/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.analysis.LinkTypeClassifier;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.Link;
import org.tweetyproject.arg.adf.syntax.LinkType;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Enumerates the complete interpretations (CO) of an ADF as k++ADF does: the
 * grounded interpretation g, followed by those models of the admissible
 * encoding of {@link SatAdmissibleAdfReasoner}, extended by the complete
 * clauses, that are strictly more informative than g. If the bound K is
 * exceeded, Γ is instead iterated to a fixpoint from every admissible
 * candidate. Skeptical acceptance is decided by g.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 * @see "k++ADF, https://bitbucket.org/andreasniskanen/k-adf"
 *
 * @author Lars Bengel
 */
public class SatCompleteAdfReasoner extends AbstractSatAdfReasoner {

	private final SatAdmissibleAdfReasoner admissible;

	private final SatGroundedAdfReasoner grounded;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatCompleteAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatCompleteAdfReasoner(Supplier<SatSolver> solvers) {
		this(solvers, SatAdmissibleAdfReasoner.DEFAULT_MAX_DEPENDENT_PARENTS);
	}

	/**
	 * @param solvers             creates a fresh solver for every call
	 * @param maxDependentParents the bound K of {@link SatAdmissibleAdfReasoner}
	 */
	public SatCompleteAdfReasoner(Supplier<SatSolver> solvers, int maxDependentParents) {
		super(solvers);
		this.admissible = new SatAdmissibleAdfReasoner(solvers, maxDependentParents);
		this.grounded = new SatGroundedAdfReasoner(solvers);
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		AbstractDialecticalFramework copy = new AbstractDialecticalFramework(adf);
		ThreeValuedInterpretation g = grounded.getModel(copy);
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		models.add(g);
		if (g.isTwoValued()) {
			return models;
		}
		Map<Link, LinkType> types = LinkTypeClassifier.classify(copy, solvers);
		boolean exact = admissible.isExact(types);
		try (SatSolver solver = solvers.get(); SatCharacteristicOperator gamma = new SatCharacteristicOperator(copy, solvers)) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(copy, solver);
			encoding.addAdmissible(types, g, exact);
			if (exact) {
				encoding.addComplete(types);
			}
			encoding.addMoreInformativeThan(g);
			int[] variables = encoding.variables();
			boolean[] witness;
			while ((witness = solver.witness(variables)) != null) {
				checkInterrupted();
				ThreeValuedInterpretation model = encoding.decode(witness);
				if (exact) {
					models.add(model);
				} else if (admissible.isModel(copy, model)) {
					ThreeValuedInterpretation next = gamma.apply(model);
					while (!next.equals(model)) {
						encoding.exclude(model);
						model = next;
						next = gamma.apply(model);
					}
					// the fixpoint may have been found before
					if (!solver.satisfiable(encoding.assumptions(model))) {
						continue;
					}
					models.add(model);
				}
				encoding.exclude(model);
			}
		}
		return models;
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		return grounded.getModel(adf);
	}

	@Override
	public Boolean query(AbstractDialecticalFramework adf, Argument argument, InferenceMode mode) {
		requireArgument(adf, argument);
		if (mode == InferenceMode.CREDULOUS) {
			return admissible.query(adf, argument, mode);
		}
		return grounded.getModel(adf).get(argument) == TruthValue.TRUE;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		try (SatCharacteristicOperator gamma = new SatCharacteristicOperator(adf, solvers)) {
			return gamma.apply(v).equals(v);
		}
	}

}

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
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Enumerates the conflict-free interpretations (CF) of an ADF with the
 * conflict-free encoding of k++ADF. Skeptical acceptance is always false, since
 * the interpretation mapping every argument to u is conflict-free.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 * @see "k++ADF, https://bitbucket.org/andreasniskanen/k-adf"
 *
 * @author Lars Bengel
 */
public class SatConflictFreeAdfReasoner extends AbstractSatAdfReasoner {

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatConflictFreeAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatConflictFreeAdfReasoner(Supplier<SatSolver> solvers) {
		super(solvers);
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		ThreeValuedInterpretation undecided = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		models.add(undecided);
		if (adf.isEmpty()) {
			return models;
		}
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(new AbstractDialecticalFramework(adf), solver);
			encoding.addConflictFree();
			encoding.addMoreInformativeThan(undecided);
			int[] variables = encoding.variables();
			boolean[] witness;
			while ((witness = solver.witness(variables)) != null) {
				checkInterrupted();
				ThreeValuedInterpretation model = encoding.decode(witness);
				models.add(model);
				encoding.exclude(model);
			}
		}
		return models;
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		return ThreeValuedInterpretation.allUndecided(adf.getNodes());
	}

	@Override
	public Boolean query(AbstractDialecticalFramework adf, Argument argument, InferenceMode mode) {
		requireArgument(adf, argument);
		if (mode == InferenceMode.SKEPTICAL) {
			return false;
		}
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(new AbstractDialecticalFramework(adf), solver);
			encoding.addConflictFree();
			return solver.satisfiable(new int[] { encoding.trueVariable(argument) });
		}
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(new AbstractDialecticalFramework(adf), solver);
			encoding.addConflictFree();
			return solver.satisfiable(encoding.assumptions(v));
		}
	}

}

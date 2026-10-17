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
import java.util.HashMap;
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
 * Enumerates the admissible interpretations (ADM) of an ADF with the
 * k-bipolar encoding of k++ADF, where k is the maximal number of dependent
 * parents of an argument. If k is not smaller than the bound K, the models of
 * the conflict-free encoding with the bipolar clauses are used as candidates
 * and each of them is verified with one SAT call. Skeptical acceptance is
 * always false, since the interpretation mapping every argument to u is
 * admissible.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 * @see "k++ADF, https://bitbucket.org/andreasniskanen/k-adf"
 *
 * @author Lars Bengel
 */
public class SatAdmissibleAdfReasoner extends AbstractSatAdfReasoner {

	/** the default bound K on the dependent parents for the k-bipolar encoding, as in k++ADF */
	public static final int DEFAULT_MAX_DEPENDENT_PARENTS = 31;

	private final int maxDependentParents;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatAdmissibleAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatAdmissibleAdfReasoner(Supplier<SatSolver> solvers) {
		this(solvers, DEFAULT_MAX_DEPENDENT_PARENTS);
	}

	/**
	 * @param solvers             creates a fresh solver for every call
	 * @param maxDependentParents the bound K, between 0 and 31
	 */
	public SatAdmissibleAdfReasoner(Supplier<SatSolver> solvers, int maxDependentParents) {
		super(solvers);
		if (maxDependentParents < 0 || maxDependentParents > 31) {
			throw new IllegalArgumentException("The bound must be between 0 and 31.");
		}
		this.maxDependentParents = maxDependentParents;
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		ThreeValuedInterpretation undecided = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		models.add(undecided);
		if (adf.isEmpty()) {
			return models;
		}
		AbstractDialecticalFramework copy = new AbstractDialecticalFramework(adf);
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(copy, solver);
			boolean exact = encode(copy, encoding);
			encoding.addMoreInformativeThan(undecided);
			int[] variables = encoding.variables();
			boolean[] witness;
			while ((witness = solver.witness(variables)) != null) {
				checkInterrupted();
				ThreeValuedInterpretation model = encoding.decode(witness);
				if (exact || isAdmissible(copy, model)) {
					models.add(model);
				}
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
		AbstractDialecticalFramework copy = new AbstractDialecticalFramework(adf);
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(copy, solver);
			boolean exact = encode(copy, encoding);
			int[] variables = encoding.variables();
			int[] accepted = { encoding.trueVariable(argument) };
			boolean[] witness;
			while ((witness = solver.witness(variables, accepted)) != null) {
				checkInterrupted();
				ThreeValuedInterpretation model = encoding.decode(witness);
				if (exact || isAdmissible(copy, model)) {
					return true;
				}
				encoding.exclude(model);
			}
		}
		return false;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		return isAdmissible(new AbstractDialecticalFramework(adf), v);
	}

	/**
	 * @param types the types of the links of an ADF
	 * @return true if the maximal number k of dependent parents of an argument is
	 *         smaller than the bound K, so that the k-bipolar encoding is used and
	 *         its models are exactly the admissible interpretations
	 */
	boolean isExact(Map<Link, LinkType> types) {
		Map<Argument, Integer> count = new HashMap<>();
		int k = 0;
		for (Map.Entry<Link, LinkType> entry : types.entrySet()) {
			if (entry.getValue() == LinkType.DEPENDENT) {
				k = Math.max(k, count.merge(entry.getKey().getChild(), 1, Integer::sum));
			}
		}
		return k < maxDependentParents;
	}

	/**
	 * Adds the k-bipolar encoding if k is smaller than the bound, and the
	 * conflict-free encoding with the bipolar clauses otherwise.
	 *
	 * @return true if the models of the encoding are exactly the admissible interpretations
	 */
	private boolean encode(AbstractDialecticalFramework adf, ThreeValuedEncoding encoding) {
		Map<Link, LinkType> types = LinkTypeClassifier.classify(adf, solvers);
		boolean exact = isExact(types);
		encoding.addAdmissible(types, ThreeValuedInterpretation.allUndecided(adf.getNodes()), exact);
		return exact;
	}

	/**
	 * @return true if no completion of v falsifies phi_a for an a with v(a) = t or
	 *         satisfies it for an a with v(a) = f
	 */
	private boolean isAdmissible(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		if (v.undecided().size() == v.size()) {
			return true;
		}
		try (SatSolver solver = solvers.get()) {
			AdfEncoding encoding = new AdfEncoding(adf, solver);
			List<Integer> violated = new ArrayList<>();
			for (Argument a : encoding.arguments()) {
				TruthValue value = v.get(a);
				if (value == TruthValue.TRUE) {
					violated.add(-encoding.condition(a));
				} else if (value == TruthValue.FALSE) {
					violated.add(encoding.condition(a));
				}
			}
			solver.add(violated.stream().mapToInt(Integer::intValue).toArray());
			return !solver.satisfiable(encoding.assumptions(v));
		}
	}

}

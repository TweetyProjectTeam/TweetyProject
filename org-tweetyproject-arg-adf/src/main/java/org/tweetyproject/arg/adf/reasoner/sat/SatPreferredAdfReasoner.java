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
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.analysis.LinkTypeClassifier;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
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
 * Enumerates the preferred interpretations (PR) of an ADF as k++ADF does:
 * admissible interpretations that are strictly more informative than the
 * grounded interpretation g are extended to ≤_i-maximal ones with a second
 * solver, each of which is then excluded together with all less informative
 * interpretations by Refine_&gt;. If there is none, g is the only preferred
 * interpretation. The admissible interpretations are encoded as in
 * {@link SatAdmissibleAdfReasoner}.
 * <p>
 * Verification checks that v is admissible and that no admissible
 * interpretation is strictly more informative. Credulous acceptance is decided
 * as for admissible interpretations. Skeptical acceptance of a is decided by g
 * if g(a) is t or f; otherwise, admissible interpretations without a_t are
 * maximised under the same restriction, and if the result cannot be extended
 * with a_t, it is a counterexample. Unlike k++ADF, skeptical acceptance also
 * handles the case where g is the only preferred interpretation.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 * @see "k++ADF, https://bitbucket.org/andreasniskanen/k-adf"
 *
 * @author Lars Bengel
 */
public class SatPreferredAdfReasoner extends AbstractAdfReasoner {

	private static final int[] NO_ASSUMPTIONS = new int[0];

	private final Supplier<SatSolver> solvers;

	private final int maxDependentParents;

	private final SatAdmissibleAdfReasoner admissible;

	private final SatGroundedAdfReasoner grounded;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatPreferredAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatPreferredAdfReasoner(Supplier<SatSolver> solvers) {
		this(solvers, SatAdmissibleAdfReasoner.DEFAULT_MAX_DEPENDENT_PARENTS);
	}

	/**
	 * @param solvers             creates a fresh solver for every call
	 * @param maxDependentParents the bound K of {@link SatAdmissibleAdfReasoner}
	 */
	public SatPreferredAdfReasoner(Supplier<SatSolver> solvers, int maxDependentParents) {
		this.solvers = solvers;
		this.maxDependentParents = maxDependentParents;
		this.admissible = new SatAdmissibleAdfReasoner(solvers, maxDependentParents);
		this.grounded = new SatGroundedAdfReasoner(solvers);
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		return models(adf, Integer.MAX_VALUE);
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		return models(adf, 1).get(0);
	}

	private List<ThreeValuedInterpretation> models(AbstractDialecticalFramework adf, int limit) {
		AbstractDialecticalFramework copy = new AbstractDialecticalFramework(adf);
		ThreeValuedInterpretation g = grounded.getModel(copy);
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		if (!g.isTwoValued()) {
			Map<Link, LinkType> types = LinkTypeClassifier.classify(copy, solvers);
			boolean exact = ThreeValuedEncoding.maxDependentParents(types) < maxDependentParents;
			try (SatSolver solver = solvers.get()) {
				ThreeValuedEncoding encoding = new ThreeValuedEncoding(copy, solver);
				encoding.addAdmissible(types, g, exact);
				encoding.addMoreInformativeThan(g);
				ThreeValuedInterpretation model;
				while (models.size() < limit && (model = next(copy, encoding, solver, exact, NO_ASSUMPTIONS)) != null) {
					if (Thread.currentThread().isInterrupted()) {
						throw new CancellationException("Enumeration interrupted.");
					}
					model = maximize(copy, types, exact, model);
					models.add(model);
					encoding.addRefineLarger(model);
				}
			}
		}
		if (models.isEmpty()) {
			models.add(g);
		}
		return models;
	}

	/**
	 * @return a preferred interpretation that is at least as informative as the
	 *         admissible interpretation v
	 */
	private ThreeValuedInterpretation maximize(AbstractDialecticalFramework adf, Map<Link, LinkType> types, boolean exact, ThreeValuedInterpretation v) {
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(adf, solver);
			encoding.addAdmissible(types, v, exact);
			ThreeValuedInterpretation maximal = v;
			while (!maximal.isTwoValued()) {
				encoding.addMoreInformativeThan(maximal);
				ThreeValuedInterpretation larger = next(adf, encoding, solver, exact, NO_ASSUMPTIONS);
				if (larger == null) {
					break;
				}
				maximal = larger;
			}
			return maximal;
		}
	}

	/**
	 * @return an admissible model of the encoding under the assumptions, or null;
	 *         if the encoding is not exact, the candidates that are not admissible
	 *         are excluded by Refine_≠
	 */
	private ThreeValuedInterpretation next(AbstractDialecticalFramework adf, ThreeValuedEncoding encoding, SatSolver solver, boolean exact, int[] assumptions) {
		int[] variables = encoding.variables();
		boolean[] witness;
		while ((witness = solver.witness(variables, assumptions)) != null) {
			ThreeValuedInterpretation candidate = encoding.decode(witness);
			if (exact || admissible.isModel(adf, candidate)) {
				return candidate;
			}
			encoding.addRefineUnequal(candidate);
		}
		return null;
	}

	@Override
	public Boolean query(AbstractDialecticalFramework adf, Argument argument, InferenceMode mode) {
		requireArgument(adf, argument);
		if (mode == InferenceMode.CREDULOUS) {
			return admissible.query(adf, argument, mode);
		}
		AbstractDialecticalFramework copy = new AbstractDialecticalFramework(adf);
		ThreeValuedInterpretation g = grounded.getModel(copy);
		if (g.get(argument).isDecided()) {
			return g.get(argument) == TruthValue.TRUE;
		}
		Map<Link, LinkType> types = LinkTypeClassifier.classify(copy, solvers);
		boolean exact = ThreeValuedEncoding.maxDependentParents(types) < maxDependentParents;
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(copy, solver);
			encoding.addAdmissible(types, g, exact);
			encoding.addMoreInformativeThan(g);
			if (next(copy, encoding, solver, exact, NO_ASSUMPTIONS) == null) {
				// g is the only preferred interpretation
				return false;
			}
			int[] notTrue = { -encoding.trueVariable(argument) };
			ThreeValuedInterpretation model;
			while ((model = next(copy, encoding, solver, exact, notTrue)) != null) {
				if (Thread.currentThread().isInterrupted()) {
					throw new CancellationException("Query interrupted.");
				}
				try (SatSolver maximizer = solvers.get()) {
					ThreeValuedEncoding maximal = new ThreeValuedEncoding(copy, maximizer);
					maximal.addAdmissible(types, model, exact);
					int at = maximal.trueVariable(argument);
					while (!model.isTwoValued()) {
						maximal.addMoreInformativeThan(model);
						ThreeValuedInterpretation larger = next(copy, maximal, maximizer, exact, new int[] { -at });
						if (larger == null) {
							break;
						}
						model = larger;
					}
					if (model.isTwoValued() || next(copy, maximal, maximizer, exact, new int[] { at }) == null) {
						return false;
					}
				}
				encoding.addRefineLarger(model);
			}
		}
		return true;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		AbstractDialecticalFramework copy = new AbstractDialecticalFramework(adf);
		if (!admissible.isModel(copy, v)) {
			return false;
		}
		if (v.isTwoValued()) {
			return true;
		}
		Map<Link, LinkType> types = LinkTypeClassifier.classify(copy, solvers);
		boolean exact = ThreeValuedEncoding.maxDependentParents(types) < maxDependentParents;
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = new ThreeValuedEncoding(copy, solver);
			encoding.addAdmissible(types, v, exact);
			encoding.addMoreInformativeThan(v);
			return next(copy, encoding, solver, exact, NO_ASSUMPTIONS) == null;
		}
	}

}

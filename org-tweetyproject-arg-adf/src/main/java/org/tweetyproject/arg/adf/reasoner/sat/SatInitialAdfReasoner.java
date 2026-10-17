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
import java.util.Arrays;
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
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Enumerates the initial models (IS) of an ADF: the non-trivial admissible
 * interpretations v such that no admissible interpretation lies strictly
 * between the trivial interpretation and v. Non-trivial admissible
 * interpretations that are not more informative than an initial model found so
 * far are reduced to ≤_i-minimal ones, each of which is then excluded together
 * with all more informative interpretations. If there is none, getModel returns
 * null and every argument is skeptically accepted.
 * <p>
 * Verification checks admissibility and then runs Algorithm 1 of Strass and
 * Wallner: for every decided argument a, starting from v[a ↦ u], each round sets
 * to u every decided argument c for which a SAT call finds a completion that
 * evaluates phi_c differently from c; if a round changes nothing and some
 * argument is still decided, a smaller non-trivial admissible interpretation is
 * found.
 *
 * @see "Lars Bengel, Matthias Thimm. 'Initial Models and Serialisability in Abstract Dialectical Frameworks.' IJCAI 2025."
 * @see "Hannes Strass, Johannes P. Wallner. 'On the Complexity of Initial Models in Abstract Dialectical Frameworks.' COMMA 2026."
 *
 * @author Lars Bengel
 */
public class SatInitialAdfReasoner extends AbstractSatAdfReasoner {

	private static final int[] NO_ASSUMPTIONS = new int[0];

	private final SatAdmissibleAdfReasoner admissible;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatInitialAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatInitialAdfReasoner(Supplier<SatSolver> solvers) {
		this(solvers, SatAdmissibleAdfReasoner.DEFAULT_MAX_DEPENDENT_PARENTS);
	}

	/**
	 * @param solvers             creates a fresh solver for every call
	 * @param maxDependentParents the bound K of {@link SatAdmissibleAdfReasoner}
	 */
	public SatInitialAdfReasoner(Supplier<SatSolver> solvers, int maxDependentParents) {
		super(solvers);
		this.admissible = new SatAdmissibleAdfReasoner(solvers, maxDependentParents);
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		return models(adf, Integer.MAX_VALUE);
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> models = models(adf, 1);
		return models.isEmpty() ? null : models.get(0);
	}

	private List<ThreeValuedInterpretation> models(AbstractDialecticalFramework adf, int limit) {
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		if (adf.isEmpty()) {
			return models;
		}
		Map<Link, LinkType> types = LinkTypeClassifier.classify(adf, solvers);
		boolean exact = admissible.isExact(types);
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = nonTrivialAdmissible(adf, solver, types, exact);
			ThreeValuedInterpretation model;
			while (models.size() < limit && (model = admissible.next(adf, encoding, solver, exact, NO_ASSUMPTIONS)) != null) {
				checkInterrupted();
				model = minimize(adf, types, exact, model);
				models.add(model);
				encoding.excludeMoreInformative(model);
			}
		}
		return models;
	}

	/**
	 * @return an initial model that is at most as informative as the non-trivial
	 *         admissible interpretation v
	 */
	private ThreeValuedInterpretation minimize(AbstractDialecticalFramework adf, Map<Link, LinkType> types, boolean exact, ThreeValuedInterpretation v) {
		try (SatSolver solver = solvers.get()) {
			ThreeValuedEncoding encoding = nonTrivialAdmissible(adf, solver, types, exact);
			ThreeValuedInterpretation minimal = v;
			while (true) {
				encoding.addLessInformativeThan(minimal);
				ThreeValuedInterpretation smaller = admissible.next(adf, encoding, solver, exact, NO_ASSUMPTIONS);
				if (smaller == null) {
					return minimal;
				}
				minimal = smaller;
			}
		}
	}

	/**
	 * @return an encoding whose models, after the check of {@link SatAdmissibleAdfReasoner#next}, are
	 *         the non-trivial admissible interpretations
	 */
	private ThreeValuedEncoding nonTrivialAdmissible(AbstractDialecticalFramework adf, SatSolver solver, Map<Link, LinkType> types, boolean exact) {
		ThreeValuedInterpretation undecided = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		ThreeValuedEncoding encoding = new ThreeValuedEncoding(adf, solver);
		encoding.addAdmissible(types, undecided, exact);
		encoding.addMoreInformativeThan(undecided);
		return encoding;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		return !decided(v).isEmpty() && admissible.isModel(adf, v) && noSmallerAdmissible(adf, v);
	}

	/**
	 * Algorithm 1 of Strass and Wallner, where the completion guessed for c is
	 * found by a SAT call.
	 *
	 * @return true if no non-trivial interpretation strictly less informative than
	 *         w is admissible
	 */
	private boolean noSmallerAdmissible(AbstractDialecticalFramework adf, ThreeValuedInterpretation w) {
		try (SatSolver solver = solvers.get()) {
			TwoValuedEncoding encoding = new TwoValuedEncoding(adf, solver);
			for (Argument a : decided(w)) {
				ThreeValuedInterpretation v = w.with(a, TruthValue.UNDECIDED);
				int rounds = decided(v).size();
				for (int j = 0; j < rounds; j++) {
					checkInterrupted();
					List<Argument> current = decided(v);
					if (current.isEmpty()) {
						break;
					}
					ThreeValuedInterpretation next = v;
					int[] assumptions = Arrays.copyOf(encoding.decided(v), current.size() + 1);
					for (Argument c : current) {
						int phi = encoding.condition(c);
						assumptions[assumptions.length - 1] = v.get(c) == TruthValue.TRUE ? -phi : phi;
						if (solver.satisfiable(assumptions)) {
							next = next.with(c, TruthValue.UNDECIDED);
						}
					}
					if (next.equals(v)) {
						return false;
					}
					v = next;
				}
			}
			return true;
		}
	}

	/** @return the arguments that v maps to t or f */
	private static List<Argument> decided(ThreeValuedInterpretation v) {
		List<Argument> result = new ArrayList<>(v.satisfied());
		result.addAll(v.unsatisfied());
		return result;
	}

}

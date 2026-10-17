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
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Enumerates the naive interpretations (NA) of an ADF as k++ADF does: every
 * model of Φ_cf is extended to a ≤_i-maximal one with a second solver, which
 * is then excluded together with all less informative interpretations by
 * Refine_&gt;. If Φ_cf has no model that is more informative than the
 * interpretation mapping every argument to u, the latter is the only naive
 * interpretation.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 * @see "k++ADF, https://bitbucket.org/andreasniskanen/k-adf (enumerate_nai)"
 *
 * @author Lars Bengel
 */
public class SatNaiveAdfReasoner extends AbstractAdfReasoner {

	private final Supplier<SatSolver> solvers;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatNaiveAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatNaiveAdfReasoner(Supplier<SatSolver> solvers) {
		this.solvers = solvers;
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
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		ThreeValuedInterpretation undecided = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		if (!adf.isEmpty()) {
			AbstractDialecticalFramework copy = new AbstractDialecticalFramework(adf);
			try (SatSolver solver = solvers.get()) {
				ConflictFreeEncoding encoding = new ConflictFreeEncoding(copy, solver);
				encoding.addMoreInformativeThan(undecided);
				int[] variables = encoding.variables();
				boolean[] witness;
				while (models.size() < limit && (witness = solver.witness(variables)) != null) {
					if (Thread.currentThread().isInterrupted()) {
						throw new CancellationException("Enumeration interrupted.");
					}
					ThreeValuedInterpretation model = maximize(copy, encoding.decode(witness));
					models.add(model);
					encoding.addRefineLarger(model);
				}
			}
		}
		if (models.isEmpty()) {
			models.add(undecided);
		}
		return models;
	}

	/**
	 * @return a ≤_i-maximal conflict-free interpretation that is at least as
	 *         informative as the conflict-free interpretation v
	 */
	private ThreeValuedInterpretation maximize(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		try (SatSolver solver = solvers.get()) {
			ConflictFreeEncoding encoding = new ConflictFreeEncoding(adf, solver);
			int[] variables = encoding.variables();
			ThreeValuedInterpretation maximal = v;
			while (!maximal.isTwoValued()) {
				encoding.addMoreInformativeThan(maximal);
				boolean[] witness = solver.witness(variables);
				if (witness == null) {
					break;
				}
				maximal = encoding.decode(witness);
			}
			return maximal;
		}
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		try (SatSolver solver = solvers.get()) {
			ConflictFreeEncoding encoding = new ConflictFreeEncoding(new AbstractDialecticalFramework(adf), solver);
			if (!solver.satisfiable(encoding.assumptions(v))) {
				return false;
			}
			if (v.isTwoValued()) {
				return true;
			}
			encoding.addMoreInformativeThan(v);
			return !solver.satisfiable();
		}
	}

}

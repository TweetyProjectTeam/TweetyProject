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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Computes the grounded interpretation (GR) of an ADF, the least fixpoint of Γ
 * (Brewka et al. 2013), as the previous implementation did: starting from the
 * interpretation that maps every argument to u, an undecided argument a becomes
 * t if phi_a has no falsifying completion and f if it has no satisfying one.
 * Since the iteration only adds information, a decided argument is fixed by a
 * unit clause, and every check is one call to an incremental solver.
 *
 * @author Lars Bengel
 */
public class SatGroundedAdfReasoner extends AbstractAdfReasoner {

	private final Supplier<SatSolver> solvers;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatGroundedAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatGroundedAdfReasoner(Supplier<SatSolver> solvers) {
		this.solvers = solvers;
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		return List.of(grounded(adf));
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		return grounded(adf);
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		return v.equals(grounded(adf));
	}

	private ThreeValuedInterpretation grounded(AbstractDialecticalFramework adf) {
		try (SatSolver solver = solvers.get()) {
			AdfEncoding encoding = new AdfEncoding(new AbstractDialecticalFramework(adf), solver);
			Map<Argument, TruthValue> values = new LinkedHashMap<>();
			List<Argument> undecided = new ArrayList<>(encoding.arguments());
			boolean changed = true;
			while (changed) {
				if (Thread.currentThread().isInterrupted()) {
					throw new CancellationException("Computation interrupted.");
				}
				changed = false;
				for (Iterator<Argument> it = undecided.iterator(); it.hasNext();) {
					Argument a = it.next();
					int x = encoding.variable(a), phi = encoding.condition(a);
					if (!solver.satisfiable(new int[] { -phi })) {
						values.put(a, TruthValue.TRUE);
						solver.add(x);
					} else if (!solver.satisfiable(new int[] { phi })) {
						values.put(a, TruthValue.FALSE);
						solver.add(-x);
					} else {
						continue;
					}
					it.remove();
					changed = true;
				}
			}
			for (Argument a : undecided) {
				values.put(a, TruthValue.UNDECIDED);
			}
			return ThreeValuedInterpretation.of(values);
		}
	}

}

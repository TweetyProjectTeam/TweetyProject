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
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Enumerates the two-valued models (MOD) of an ADF with one SAT instance
 * x_a ↔ phi_a for every argument a, blocking each model after it is found.
 *
 * @author Lars Bengel
 */
public class SatTwoValuedModelAdfReasoner extends AbstractAdfReasoner {

	private final Supplier<SatSolver> solvers;

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatTwoValuedModelAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatTwoValuedModelAdfReasoner(Supplier<SatSolver> solvers) {
		this.solvers = solvers;
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
		try (SatSolver solver = solvers.get()) {
			AdfEncoding encoding = new AdfEncoding(new AbstractDialecticalFramework(adf), solver);
			for (Argument a : encoding.arguments()) {
				int x = encoding.variable(a), phi = encoding.condition(a);
				solver.add(-x, phi);
				solver.add(x, -phi);
			}
			int[] variables = encoding.variables();
			boolean[] witness;
			while (models.size() < limit && (witness = solver.witness(variables)) != null) {
				if (Thread.currentThread().isInterrupted()) {
					throw new CancellationException("Enumeration interrupted.");
				}
				models.add(encoding.decode(witness));
				int[] blocking = new int[variables.length];
				for (int i = 0; i < variables.length; i++) {
					blocking[i] = witness[i] ? -variables[i] : variables[i];
				}
				solver.add(blocking);
			}
		}
		return models;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		if (!v.isTwoValued()) {
			return false;
		}
		for (Argument a : adf) {
			if (v.evaluate(adf.getAcceptanceCondition(a)) != v.get(a)) {
				return false;
			}
		}
		return true;
	}

}

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
package org.tweetyproject.sat.sat4j;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.tweetyproject.sat.SatSolver;

/**
 * An incremental {@link SatSolver} based on Sat4j. It is written in pure Java
 * and therefore available on every platform.
 *
 * @author Lars Bengel
 */
public final class Sat4jIncrementalSolver implements SatSolver {

	private final ISolver solver = SolverFactory.newDefault();

	/** true once a clause made the formula unsatisfiable, which Sat4j reports when the clause is added */
	private boolean inconsistent;

	private boolean closed;

	@Override
	public boolean satisfiable(int[] assumptions) {
		check();
		if (inconsistent) {
			return false;
		}
		try {
			return solver.isSatisfiable(new VecInt(assumptions.clone()));
		} catch (TimeoutException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean[] witness(int[] variables, int[] assumptions) {
		if (!satisfiable(assumptions)) {
			return null;
		}
		// the model contains the literal of variable i at index i - 1
		int[] model = solver.model();
		boolean[] values = new boolean[variables.length];
		for (int i = 0; i < variables.length; i++) {
			values[i] = model[variables[i] - 1] > 0;
		}
		return values;
	}

	@Override
	public void add(int... clause) {
		check();
		if (clause.length == 0) {
			inconsistent = true;
			return;
		}
		try {
			solver.addClause(new VecInt(clause.clone()));
		} catch (ContradictionException e) {
			inconsistent = true;
		}
	}

	@Override
	public int newVar() {
		check();
		return solver.nextFreeVarId(true);
	}

	@Override
	public void close() {
		if (!closed) {
			closed = true;
			solver.reset();
		}
	}

	private void check() {
		if (closed) {
			throw new IllegalStateException("The solver is closed.");
		}
	}

}

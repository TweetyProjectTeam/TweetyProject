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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.sat.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.IndexedSatSolverState;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public final class NativeMinisatSolver implements IncrementalSatSolver {

	private static final String DEFAULT_WIN_LIB = "/minisat.dll";
	private static final String DEFAULT_LINUX_LIB = "/minisat.so";
/**
 * NativeMinisatSolver
 */
	public NativeMinisatSolver() {
		String osName = System.getProperty("os.name").toLowerCase();
		String lib = null;
		if (osName.contains("win")) {
			lib = DEFAULT_WIN_LIB;
		} else if (osName.contains("nux")) {
			lib = DEFAULT_LINUX_LIB;
		}
		String path = getClass().getResource(lib).getPath();
		System.load(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.sat.IncrementalSatSolver#createState()
	 */
	@Override
	public SatSolverState createState() {
		return new MinisatSolverState();
	}

	private static native long init();

	private static native void delete(long handle);

	private static native int newVar(long handle);

	private static native void addClause(long handle, int lit);

	private static native void addClause(long handle, int lit1, int lit2);

	private static native void addClause(long handle, int lit1, int lit2, int lit3);

	private static native void addClause(long handle, int[] clause, int size);

	private static native void simplify(long handle);

	private static native boolean solve(long handle);

	private static native boolean solve(long handle, int assumption);

	private static native boolean solve(long handle, int assumption1, int assumption2);

	private static native boolean solve(long handle, int assumption1, int assumption2, int assumption3);

	private static native boolean solve(long handle, int[] assumptions, int size);

	/**
	 * Calls solve() by itself before returning a witness.
	 * 
	 * @param handle
	 * @return
	 */
	private static native int[] witness(long handle);

	private static native int[] witness(long handle, int[] filter);

	/**
	 * @param handle
	 * @param var
	 * @return 0 = true, 1 = false, 2 = undef
	 */
	private static native int value(long handle, int var);

	private static final class IndexedMinisatSolverState implements IndexedSatSolverState {

		private final long handle;
		
		private IndexedMinisatSolverState(long handle) {
			this.handle = init();
		}

		@Override
		public boolean satisfiable() {
			return solve(handle);
		}

		@Override
		public boolean satisfiable(int[] assumptions) {
			switch (assumptions.length) {
			case 0:
				return solve(handle);
			case 1:
				return solve(handle, assumptions[0]);
			case 2:
				return solve(handle, assumptions[0], assumptions[1]);
			case 3:
				return solve(handle, assumptions[0], assumptions[1], assumptions[2]);
			default:
				return solve(handle, assumptions, assumptions.length);
			}
		}

		@Override
		public int[] witness(int[] atoms) {
			if (satisfiable()) {
				int[] values = new int[atoms.length];
				for (int i = 0; i < values.length; i++) {
					values[i] = value(handle, atoms[i]);
					if (values[i] == 2) {
						throw new IllegalStateException("undef truth value for " + atoms[i]);
					}
				}
				return values;
			}
			return new int[0];
		}

		@Override
		public int[] witness(int[] atoms, int[] assumptions) {
			if (satisfiable(assumptions)) {
				return witness(atoms);
			}
			return new int[0];
		}

		@Override
		public void add(int[] clause) {
			switch (clause.length) {
			case 1:
				addClause(handle, clause[0]);
			case 2:
				addClause(handle, clause[0], clause[1]);
			case 3:
				addClause(handle, clause[0], clause[1], clause[2]);
			default:
				addClause(handle, clause, clause.length);
			}
		}

		@Override
		public void close() {
			delete(handle);
		}

	}

	private static final class MinisatSolverState implements SatSolverState {

		private final long handle;

		private List<Integer> assumptions;

		/**
		 * Maps the propositions to their native representation.
		 */
		private final Map<Literal, Integer> nonTransientMapping = new HashMap<Literal, Integer>();

		private Map<Literal, Integer> transientMapping = new HashMap<Literal, Integer>();

		private MinisatSolverState() {
			this.handle = init();
			// skip first var since it is 0, which leads to problems since we
			// represent negative literals as negative numbers, but we cannot
			// distinguish -0 and +0
			newVar(handle);
		}

		@Override
		public void close() {
			delete(handle);
		}

		@Override
		public Set<Literal> witness() {
			return witness(nonTransientMapping.keySet());
		}

		@Override
		public Set<Literal> witness(Collection<? extends Literal> filter) {
			if (satisfiable()) {
				Set<Literal> witness = new HashSet<>();
				for (Literal atom : filter) {
					int mapping = nonTransientMapping.get(atom);
					if (value(handle, mapping) == 0) {
						witness.add(atom);
					}
				}
				return witness;
			}

			return null;
		}

		@Override
		public boolean satisfiable() {
			transientMapping = new HashMap<>();
			if (assumptions != null) {
				boolean sat = false;
				int size = assumptions.size();
				// optimization: jni calls without arrays have less overhead
				switch (size) {
				case 1:
					sat = solve(handle, assumptions.get(0));
					break;
				case 2:
					sat = solve(handle, assumptions.get(0), assumptions.get(1));
					break;
				case 3:
					sat = solve(handle, assumptions.get(0), assumptions.get(1), assumptions.get(2));
					break;
				default:
					sat = solve(handle, assumptions.stream().mapToInt(i -> i).toArray(), size);
					break;
				}
				assumptions = null;
				return sat;
			}

			return solve(handle);
		}

		@Override
		public void assume(Literal literal) {
			int mapped = mapToNative(literal.getAtom());
			int assumption = literal.isPositive() ? mapped : -mapped;
			if (assumptions == null) {
				assumptions = new ArrayList<>();
			}
			assumptions.add(assumption);
		}

		@Override
		public boolean add(Clause clause) {
			updateState(clause);
			return true;
		}

		private int mapToNative(Literal atom) {
			Map<Literal, Integer> map = atom.isTransient() ? transientMapping : nonTransientMapping;

			if (map.containsKey(atom)) {
				return map.get(atom);
			}

			int mapping = newVar(handle);
			map.put(atom, mapping);
			return mapping;
		}

		private void updateState(Clause clause) {
			int[] nclause = new int[clause.size()];
			int i = 0;
			for (Literal literal : clause) {
				Literal atom = literal.getAtom();
				int mapped = mapToNative(atom);
				nclause[i] = literal.isPositive() ? mapped : -mapped;
				i++;
			}
			// optimization: jni calls without arrays have less overhead
			switch (nclause.length) {
			case 1:
				addClause(handle, nclause[0]);
				break;
			case 2:
				addClause(handle, nclause[0], nclause[1]);
				break;
			case 3:
				addClause(handle, nclause[0], nclause[1], nclause[2]);
				break;
			default:
				addClause(handle, nclause, nclause.length);
				break;
			}
		}
	}
}

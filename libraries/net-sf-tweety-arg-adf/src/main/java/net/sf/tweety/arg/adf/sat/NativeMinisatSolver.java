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
package net.sf.tweety.arg.adf.sat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.adf.syntax.pl.Atom;
import net.sf.tweety.arg.adf.syntax.pl.Clause;
import net.sf.tweety.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public final class NativeMinisatSolver implements IncrementalSatSolver {

	private static final String DEFAULT_WIN_LIB = "/minisat.dll";
	private static final String DEFAULT_LINUX_LIB = "/minisat.so";

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

	private static final class MinisatSolverState implements SatSolverState {

		private final long handle;

		private List<Integer> assumptions;

		/**
		 * Maps the propositions to their native representation.
		 */
		private final Map<Atom, Integer> nativeMapping = new HashMap<Atom, Integer>();

		/**
		 * The index corresponds to the native representation.
		 */
		private final List<Atom> nativeToProp = new ArrayList<>();

		private MinisatSolverState() {
			this.handle = init();
			// skip first var since it is 0, which leads to problems since we
			// represent negative literals as negative numbers, but we cannot
			// distinguish -0 and +0
			newVar(handle);
			nativeToProp.add(null); // adjust index
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.AutoCloseable#close()
		 */
		@Override
		public void close() {
			delete(handle);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sf.tweety.arg.adf.sat.SatSolverState#witness()
		 */
		@Override
		public Set<Atom> witness() {	
			int[] witness = NativeMinisatSolver.witness(handle);

			if (witness.length > 0) {
				Set<Atom> model = new HashSet<>();
				for (int i = 0; i < witness.length; i++) {
					if (witness[i] == 1) {
						model.add(nativeToProp.get(i));
					}
				}
				return model;
			}

			return null;
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.sat.SatSolverState#witness(java.util.Collection)
		 */
		@Override
		public Set<Atom> witness(Collection<Atom> filter) {
			// TODO fix
//			if (satisfiable()) {
//				int[] nfilter = new int[filter.size()];
//				int i = 0;
//				for (Atom atom : filter) {
//					nfilter[i] = nativeMapping.get(atom);
//					i++;
//				}
//				int[] witness = NativeMinisatSolver.witness(handle, nfilter);
//				Set<Atom> model = new HashSet<>();
//				for (int j = 0; j < witness.length; j++) {
//					model.add(nativeToProp.get(witness[j]));
//				}
//				return model;
//			}
			if (satisfiable()) {
				Set<Atom> witness = new HashSet<>();
				for (Atom atom : filter) {
					int mapping = nativeMapping.get(atom);
					if (value(handle, mapping) == 0) {
						witness.add(atom);
					}
				}
				return witness;
			}

			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sf.tweety.arg.adf.sat.SatSolverState#satisfiable()
		 */
		@Override
		public boolean satisfiable() {
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.sat.SatSolverState#assume(net.sf.tweety.logics.
		 * pl.syntax.Proposition, boolean)
		 */
		@Override
		public void assume(Atom atom, boolean value) {
			int mapped = mapToNative(atom);
			int assumption = value ? mapped : -mapped;
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

		private int mapToNative(Atom p) {
			if (!nativeMapping.containsKey(p)) {
				int var = newVar(handle);
				nativeMapping.put(p, var);
				nativeToProp.add(p);
			}
			return nativeMapping.get(p);
		}

		private void updateState(Clause clause) {
			int[] nclause = new int[clause.size()];
			int i = 0;
			for (Literal literal : clause) {
				Atom atom = literal.getAtom();
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

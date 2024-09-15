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

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * NativePicosatSolver class
 * @author Mathias Hofer
 *
 */
public final class NativePicosatSolver implements IncrementalSatSolver {

	private static final String DEFAULT_WIN_LIB = "/picosat.dll";
	private static final String DEFAULT_LINUX_LIB = "/picosat.so";
/**
 * NativePicosatSolver
 */
	public NativePicosatSolver() {
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.IncrementalSatSolver#createState()
	 */
	@Override
	public SatSolverState createState() {
		return new PicosatSolverState();
	}

	private static native void addClause(long handle, int lit);

	private static native void addClause(long handle, int lit1, int lit2);

	private static native void addClause(long handle, int lit1, int lit2, int lit3);

	private static native void addClause(long handle, int[] clause);

	/**
	 * Tries to avoid a copy of the given array by using GetPrimitiveArrayCritical
	 *
	 * @param handle
	 * @param buf
	 * @param size
	 */
	private static native void addArray(long handle, int[] buf, int size);

	/**
	 * Uses GetDirectBufferAddress
	 *
	 * @param handle
	 * @param buf
	 * @param size
	 */
	private static native void addBuffer(long handle, ByteBuffer buf, int size);

	private static native int[] witness(long handle);

	/*
	 * The following methods directly correspond to picosat calls as defined
	 * in picosat.h
	 */
	private static native long init();

	private static native void reset(long handle);

	private static native void add(long handle, int lit);

	private static native void addAdoLit(long handle, int lit);

	private static native void assume(long handle, int lit);

	private static native void setPropagationLimit(long handle, long limit);

	private static native boolean sat(long handle, long limit);

	private static native boolean deref(long handle, int lit);

	private static native boolean derefPartial(long handle, int lit);

	private static native boolean inconsistent(long handle);

	private static final class PicosatSolverState implements SatSolverState {

		/**
		 * Maps the propositions to their native representation.
		 */
		private final Map<Literal, Integer> nonTransientMapping = new HashMap<Literal, Integer>();

		private Map<Literal, Integer> transientMapping = new HashMap<Literal, Integer>();

		private static final int NO_DECISION_LIMIT = -1;

		/**
		 * Keeps track of the int representation of fresh propositions
		 */
		private int nextProposition = 1;

		private final long handle;

		private PicosatSolverState() {
			this.handle = init();
		}

		@Override
		public void close() {
			NativePicosatSolver.reset(handle);
		}

		private boolean isTrue(Literal p) {
			return NativePicosatSolver.deref(handle, nonTransientMapping.get(p));
		}

		private int nativeMapping(Literal atom) {
			Map<Literal, Integer> map = atom.isTransient() ? transientMapping : nonTransientMapping;

			if (map.containsKey(atom)) {
				return map.get(atom);
			}

			int mapping = nextProposition++;
			map.put(atom, mapping);
			return mapping;
		}

		private int[] nativeMapping(Clause clause) {
			int size = clause.size();
			int[] nclause = new int[size + 1];
			int i = 0;
			for (Literal lit : clause) {
				int mapped = nativeMapping(lit.getAtom());
				nclause[i] = lit.isPositive() ? mapped : -mapped;
				i++;
			}

			// 0 indicates end of clause
			nclause[size] = 0;
			return nclause;
		}

		@Override
		public boolean add(Clause clause) {
			updateState(clause);
			return true;
		}

		private void updateState(Clause clause) {
			int[] nclause = nativeMapping(clause);

			// optimization: jni calls without arrays have less overhead
			switch (clause.size()) {
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
				addClause(handle, nclause);
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see net.sf.tweety.arg.adf.sat.SatSolverState#witness()
		 */
		@Override
		public Set<Literal> witness() {
			boolean sat = satisfiable();

			if (sat) {
				Set<Literal> trues = new HashSet<>();
				for (Literal p : nonTransientMapping.keySet()) {
					if (isTrue(p)) {
						trues.add(p);
					}
				}
				return trues;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.sat.SatSolverState#witness(java.util.Collection)
		 */
		@Override
		public Set<Literal> witness(Collection<? extends Literal> filter) {
			if (satisfiable()) {
				Set<Literal> witness = new HashSet<>();
				for (Literal atom : filter) {
					int mapping = nonTransientMapping.get(atom);
					if (deref(handle, mapping)) {
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
			return NativePicosatSolver.sat(handle, NO_DECISION_LIMIT);
		}

		@Override
		public void assume(Literal literal) {
			int mapped = nativeMapping(literal.getAtom());
			int lit = literal.isPositive() ? mapped : -mapped;
			NativePicosatSolver.assume(handle, lit);
		}

	}

}

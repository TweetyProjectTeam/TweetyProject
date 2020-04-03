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

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public final class NativePicosatSolver extends IncrementalSatSolver {

	private static final String DEFAULT_WIN_LIB = "/picosat.dll";
	private static final String DEFAULT_LINUX_LIB = "/picosat.so";

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
		private Map<Proposition, Integer> propositionsToNative = new HashMap<Proposition, Integer>();
		
		private static final int NO_DECISION_LIMIT = -1;
				
		/**
		 * Keeps track of the int representation of fresh propositions
		 */
		private int nextProposition = 1;

		private long handle;
				
		private PicosatSolverState() {
			this.handle = init();
		}

		@Override
		public void close() throws Exception {			
			NativePicosatSolver.reset(handle);
		}

		private boolean isTrue(Proposition p) {
			return NativePicosatSolver.deref(handle, propositionsToNative.get(p));
		}

		@Override
		public boolean add(Collection<Disjunction> clauses) {
			for (Disjunction clause : clauses) {
				updateState(clause);
			}
			return true;
		}

		@Override
		public boolean add(Disjunction clause) {
			updateState(clause);
			return true;
		}

		private void updateState(Disjunction clause) {
			int size = clause.size();
			int[] nclause = new int[size + 1];
			for (int i = 0; i < size; i++) {
				// try to get the propositions of the given clause, ugly but works
				// Note: 1. getAtoms() causes way to much overhead
				// 2. there currently is no elegant way to deal with literals
				PlFormula lit = clause.get(i);
				Proposition p = null;
				int sign = 1;
				if (lit instanceof Proposition) {
					p = (Proposition) lit;
				} else {
					p = (Proposition) ((Negation) lit).getFormula();
					sign = -1;
				}
				
				if (!propositionsToNative.containsKey(p)) {
					propositionsToNative.put(p, nextProposition);
					nextProposition += 1;
				}
				
				int nprop = propositionsToNative.get(p);
				nclause[i] = sign * nprop;
			}
			
			// 0 indicates end of clause
			nclause[size] = 0;
			
			// optimization: jni calls without arrays have less overhead
			switch (size) {
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
		public Interpretation<PlBeliefSet, PlFormula> witness() {
			boolean sat = satisfiable();

			if (sat) {
				Collection<Proposition> trues = new LinkedList<Proposition>();
				for (Proposition p : propositionsToNative.keySet()) {
					if (isTrue(p)) {
						trues.add(p);
					}
				}
				return new PossibleWorld(trues);
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
			return NativePicosatSolver.sat(handle, NO_DECISION_LIMIT);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.sat.SatSolverState#assume(net.sf.tweety.logics.
		 * pl.syntax.Proposition, boolean)
		 */
		@Override
		public void assume(Proposition proposition, boolean value) {
			if (!propositionsToNative.containsKey(proposition)) {
				propositionsToNative.put(proposition, nextProposition);
				nextProposition += 1;
			}
			int lit = value ? propositionsToNative.get(proposition) : -propositionsToNative.get(proposition);
			NativePicosatSolver.assume(handle, lit);
		}
		
	}

}

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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
public final class NativeMinisatSolver extends IncrementalSatSolver {

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
	
	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		return getWitness(formulas) != null;
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

	private static native int[] witness(long handle);

	/**
	 * @param handle
	 * @param var
	 * @return 0 = true, 1 = false, 2 = undef
	 */
	private static native int value(long handle, int var);

	private static class MinisatSolverState implements SatSolverState {

		private long handle;

		private Integer assumption;

		/**
		 * Maps the propositions to their native representation.
		 */
		private Map<Proposition, Integer> propositionsToNative = new HashMap<Proposition, Integer>();

		private static final int INITIAL_MAPPING_SIZE = 256;

		private static final double GROWTH_FACTOR = 0.5;

		/**
		 * Use an array instead of a HashMap to avoid the recurring calculation
		 * of hashcode(). Even if a hashcode() call is relatively fast, it
		 * turned out to accumulate for instances with several thousand models
		 * for which we perform millions of native calls.
		 * <p>
		 * The faster access is bought by the resize overhead, once
		 * the array is full. This should however not occur very often and we
		 * can experiment a bit with the initial size and the growth factor.
		 */
		private Proposition[] nativeToProp = new Proposition[INITIAL_MAPPING_SIZE];

		private MinisatSolverState() {
			this.handle = init();
			// skip first var since it is 0, which leads to problems since we
			// represent negative literals as negative numbers, but we cannot
			// distinguish -0 and +0
			newVar(handle);
		}

		private void updateNativeMapping(int n, Proposition p) {
			// resize if necessary
			if (n > nativeToProp.length - 1) {
				int growth = (int) (nativeToProp.length * GROWTH_FACTOR);
				int newLength = nativeToProp.length + growth;
				nativeToProp = Arrays.copyOf(nativeToProp, newLength);
			}
			nativeToProp[n] = p;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.AutoCloseable#close()
		 */
		@Override
		public void close() throws Exception {
			delete(handle);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sf.tweety.arg.adf.sat.SatSolverState#witness()
		 */
		@Override
		public Interpretation<PlBeliefSet, PlFormula> witness() {
			int[] witness = NativeMinisatSolver.witness(handle);

			if (witness.length > 0) {
				Collection<Proposition> model = new ArrayList<>(witness.length);
				for (int i = 0; i < witness.length; i++) {
					if (witness[i] == 1) {
						model.add(nativeToProp[i]);
					}
				}
				return new PossibleWorld(model);
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
			if (assumption != null) {
				boolean sat = solve(handle, assumption);
				assumption = null;
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
		public void assume(Proposition proposition, boolean value) {
			int mapped = mapToNative(proposition);
			this.assumption = value ? mapped : -mapped;
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
		
		private int mapToNative(Proposition p) {
			if (!propositionsToNative.containsKey(p)) {
				int var = newVar(handle);
				propositionsToNative.put(p, var);
				updateNativeMapping(var, p);
			}
			return propositionsToNative.get(p);
		}

		private void updateState(Disjunction clause) {
			int size = clause.size();
			int[] nclause = new int[size];

			// try to get the propositions of the given clause, ugly but works
			// Note:
			// 1. getAtoms() causes way to much overhead
			// 2. there currently is no elegant way to deal with literals
			for (int i = 0; i < size; i++) {
				PlFormula literal = clause.get(i);
				Proposition atom = null;
				int sign = 1;
				if (literal instanceof Proposition) {
					atom = (Proposition) literal;
				} else {
					atom = (Proposition) ((Negation) literal).getFormula();
					sign = -1;
				}

				int mapped = mapToNative(atom);
				nclause[i] = sign * mapped;
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

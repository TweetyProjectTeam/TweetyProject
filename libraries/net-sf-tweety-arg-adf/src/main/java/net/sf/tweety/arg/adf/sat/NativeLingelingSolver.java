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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Experimental lingeling binding
 * 
 * @author Mathias Hofer
 *
 */
public class NativeLingelingSolver extends IncrementalSatSolver {

	private static final String DEFAULT_WIN_LIB = "/lingeling.dll";
	private static final String DEFAULT_LINUX_LIB = "/lingeling.so";

	public NativeLingelingSolver() {
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
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas) {
		try (SatSolverState state = createState()) {
			for (PlFormula f : formulas) {
				Conjunction cnf = f.toCnf();
				for (PlFormula c : cnf) {
					assert c.isClause();
					Disjunction clause = (Disjunction) c;
					state.add(clause);
				}
			}
			return state.witness();
		} catch (Exception e1) {
			// TODO how to deal with exceptions?
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		return getWitness(formulas) != null;
	}

	@Override
	public SatSolverState createState() {
		return new LingelingSolverState(init());
	}

	private native void addClause(long lgl, int[] clause);

	/*
	 * The following methods directly correspond to lingeling calls as defined
	 * in lglib.h
	 */
	private static native long init();

	private static native void release(long lgl);

	private static native void add(long lgl, int lit);

	private static native void assume(long lgl, int lit);

	private static native boolean sat(long lgl);

	private static native boolean deref(long lgl, int lit);

	private static native boolean fixed(long lgl, int lit);

	private static native boolean failed(long lgl, int lit);

	private static native boolean inconsistent(long lgl);

	private static native boolean changed(long lgl);

	private static native void reduceCache(long lgl);

	private static native void flushCache(long lgl);

	private static native void freeze(long lgl, int lit);

	private static native boolean frozen(long lgl, int lit);

	private static native void melt(long lgl, int lit);

	private static native void meltAll(long lgl);

	private static native boolean usable(long lgl, int lit);

	private static native boolean reusable(long lgl, int lit);

	private static native void reuse(long lgl, int lit);

	private static class LingelingSolverState implements SatSolverState {

		/**
		 * Maps the propositions to their native representation.
		 */
		private Map<Proposition, Integer> propositionsToNative = new HashMap<Proposition, Integer>();

		/**
		 * Contains the disjunctions which were added after the last sat call
		 * and must be added before the next sat call.
		 */
		private Set<Disjunction> stateCache = new HashSet<Disjunction>();
		
		/**
		 * Keeps track of the int representation of fresh propositions
		 */
		private int nextProposition = 1;

		private long handle;
				
		private LingelingSolverState(long handle) {
			this.handle = handle;
		}

		@Override
		public void close() throws Exception {
			NativeLingelingSolver.release(handle);
		}

		private boolean isTrue(Proposition p) {
			return NativeLingelingSolver.deref(handle, propositionsToNative.get(p));
		}

		@Override
		public boolean add(Collection<Disjunction> clauses) {
			return stateCache.addAll(clauses);
		}

		@Override
		public boolean add(Disjunction clause) {
			return stateCache.add(clause);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.sat.SatSolverState#remove(net.sf.tweety.logics.
		 * pl.syntax.Disjunction)
		 */
		@Override
		public boolean remove(Disjunction clause) {
			return stateCache.remove(clause);
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
				Set<Proposition> trues = new HashSet<Proposition>();
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
			// update native state
			for (Disjunction clause : stateCache) {
				for (Proposition p : clause.getAtoms()) {
					if (!propositionsToNative.containsKey(p)) {
						propositionsToNative.put(p, nextProposition);
						NativeLingelingSolver.freeze(handle, nextProposition);
						nextProposition += 1;
					}
				}

				// TODO typesafety
				for (PlFormula literal : clause) {
					assert literal.isLiteral();
					if (literal instanceof Negation) {
						Negation neg = (Negation) literal;
						int lit = -propositionsToNative.get(neg.getFormula());
						assert lit != 0;
						NativeLingelingSolver.add(handle, lit);
					} else if (literal instanceof Proposition) {
						int lit = propositionsToNative.get(literal);
						assert lit != 0;
						NativeLingelingSolver.add(handle, lit);
					}
				}
				NativeLingelingSolver.add(handle, 0); // end of clause
			}
			stateCache.clear();

			boolean sat = NativeLingelingSolver.sat(handle);
			return sat;
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.sat.SatSolverState#assume(net.sf.tweety.logics.pl.syntax.Proposition, boolean)
		 */
		@Override
		public void assume(Proposition proposition, boolean value) {
			if (!propositionsToNative.containsKey(proposition)) {
				propositionsToNative.put(proposition, nextProposition);
				nextProposition += 1;
			}
			int lit = value ? propositionsToNative.get(proposition) : -propositionsToNative.get(proposition);
			NativeLingelingSolver.assume(handle, lit);
			NativeLingelingSolver.freeze(handle, lit);
		}
	}
}

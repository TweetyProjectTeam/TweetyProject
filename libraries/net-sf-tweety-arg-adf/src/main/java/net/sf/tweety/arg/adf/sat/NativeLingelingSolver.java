package net.sf.tweety.arg.adf.sat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * Experimental lingeling binding
 * 
 * @author Mathias Hofer
 *
 */
public class NativeLingelingSolver extends IncrementalSatSolver {

	private static final File DEFAULT_WIN_LIB = new File("src/main/resources/lingeling.dll"); 
	
	public NativeLingelingSolver() {
		try {
			String path = DEFAULT_WIN_LIB.getCanonicalPath();
			System.load(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			return getWitness(state);			
		} catch (Exception e1) {
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
		return new LingelingSolverState(this);
	}

	@Override
	public boolean isSatisfiable(SatSolverState state) {
		return getWitness(state) != null;
	}

	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(SatSolverState state) {
		boolean sat = sat(state.getHandle());
		state.setSatCalled();
		PossibleWorld pw = null;
		if (sat) {
			pw = ((LingelingSolverState) state).getPossibleWorld();
		}
		return pw;
	}

	private native void addClause(long lgl, int[] clause);

	/*
	 * The following methods directly correspond to lingeling calls as defined in lglib.h
	 */
	private native long init();

	private native void release(long lgl);
	
	private native void add(long lgl, int lit);
	
	private native void assume(long lgl, int lit);

	private native boolean sat(long lgl);

	private native boolean deref(long lgl, int lit);
	
	private native boolean fixed(long lgl, int lit);
	
	private native boolean failed(long lgl, int lit);
	
	private native boolean inconsistent(long lgl);
	
	private native boolean changed(long lgl);
	
	private native void reduceCache(long lgl);
	
	private native void flushCache(long lgl);
	
	private native void freeze(long lgl, int lit);
	
	private native boolean frozen(long lgl, int lit);
	
	private native void melt(long lgl, int lit);
	
	private native void meltAll(long lgl);
	
	private native boolean usable(long lgl, int lit);
	
	private native boolean reusable(long lgl, int lit);
	
	private native void reuse(long lgl, int lit);

	private class LingelingSolverState implements SatSolverState {

		// map the propositions to some int representation
		private Map<Proposition, Integer> props = new HashMap<Proposition, Integer>();

		private long handle;
		
		private NativeLingelingSolver solver;
		
		/**
		 * Flag which indicates if sat was already called at least once.
		 */
		private boolean satCalled;

		public LingelingSolverState(NativeLingelingSolver solver) {
			this.solver = solver;
			this.handle = solver.init();
		}

		@Override
		public void close() throws Exception {
			solver.release(handle);
		}

		@Override
		public long getHandle() {
			return handle;
		}

		public PossibleWorld getPossibleWorld() {
			Set<Proposition> trues = new HashSet<Proposition>();
			for (Proposition p : props.keySet()) {
				if (isTrue(p)) {
					trues.add(p);
				}
			}
			return new PossibleWorld(trues);
		}

		public boolean isTrue(Proposition p) {
			return solver.deref(handle, props.get(p));
		}
		
		@Override
		public boolean add(Collection<Disjunction> clauses) {
			boolean result = false;
			for (Disjunction clause : clauses) {
				boolean added = add(clause);
				result = result || added;
			}
			return result;
		}

		@Override
		public boolean add(Disjunction clause) {
			// map the propositions to some int representation
			for (Proposition p : clause.getAtoms()) {
				if (!props.containsKey(p)) {
					props.put(p, props.size() + 1);
				}
			}
			// TODO avoid instanceof and casts
			int i = 0;
			int[] intClause = new int[clause.getLiterals().size() + 1];
			int size = intClause.length;
			boolean skip = false;
			for (PlFormula literal : clause) {
				assert literal.isLiteral();
				if (literal instanceof Negation) {
					Negation neg = (Negation) literal;
					int lit = -props.get(neg.getFormula());
					
					if (!satCalled) {
						solver.freeze(handle, lit);
					}
					
					intClause[i] = lit;
//					solver.freeze(handle, intClause[i]);
				} else if (literal instanceof Proposition) {
					int lit = props.get(literal);
					if (!satCalled) {
						solver.freeze(handle, lit);
					}
					intClause[i] = lit;
					
//					solver.freeze(handle, intClause[i]);
				} else if (literal instanceof Contradiction) {
					continue;
				} else if (literal instanceof Tautology) {
					skip = true;
					break;
				}
				i++;
				
				if (skip) break;
			}
			if (!skip) {
				solver.addClause(handle, Arrays.copyOfRange(intClause,0, size));
			}
			return !skip;
		}
		
		public void setSatCalled() {
			satCalled = true;
		}

		@Override
		public boolean remove(Disjunction clause) {
			// TODO implement
			throw new UnsupportedOperationException();
		}

	}
}

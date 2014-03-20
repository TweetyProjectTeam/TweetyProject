package net.sf.tweety.logics.pl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * Uses the Sat4j library for SAT solving.
 * @author Matthias Thimm
 *
 */
public class Sat4jEntailment extends SatSolverEntailment {

	/** The solver actually used. */
	private ISolver solver = null;

	/** Max number of variables for this solver. */
	private static final int MAXVAR = 1000000;
	/** Max number of expected clauses for this solver. */
	private static final int NBCLAUSES = 500000;
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PropositionalFormula> formulas) {
		this.solver = SolverFactory.newLight();
		this.solver.newVar(Sat4jEntailment.MAXVAR);
		this.solver.setExpectedNumberOfClauses(Sat4jEntailment.NBCLAUSES);		
		PropositionalSignature sig = new PropositionalSignature();
		for(PropositionalFormula f: formulas)
			sig.addAll(f.getAtoms());		
		Map<Proposition, Integer> prop2Idx = new HashMap<Proposition, Integer>();
		Proposition[] idx2Prop = new Proposition[sig.size()];
		int i = 0;
		for(Proposition p: sig){
			prop2Idx.put(p, i);
			idx2Prop[i++] = p;
		}
		try{
			for(PropositionalFormula f: formulas){
				Conjunction conj = f.toCnf();
				for(PropositionalFormula f2: conj){
					Disjunction disj = (Disjunction) f2;
					// first remove contradictions
					while(disj.remove(new Contradiction()));					
					int[] clause = new int[disj.size()];
					i = 0;
					boolean taut = false;
					for(PropositionalFormula f3: disj){
						if(f3 instanceof Proposition){
							clause[i++] = prop2Idx.get(f3) + 1; 
						}else if(f3 instanceof Negation){
							clause[i++] = - prop2Idx.get(((Negation)f3).getFormula()) - 1;
						}else if(f3 instanceof Tautology){
							taut = true;
							break;
						}else throw new RuntimeException("Unexpected formula type in conjunctive normal form: " + f3.getClass());
					}
					if(!taut) this.solver.addClause(new VecInt(clause));
				}
			}
			return this.solver.isSatisfiable();
		}catch(ContradictionException e){
			return false;
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

}

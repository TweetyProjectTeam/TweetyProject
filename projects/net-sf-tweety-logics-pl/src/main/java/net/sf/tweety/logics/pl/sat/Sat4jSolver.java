/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.pl.sat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.syntax.Tautology;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

/**
 * Uses the Sat4j library for SAT solving (note that currently only the light version is used).
 * @author Matthias Thimm
 *
 */
public class Sat4jSolver extends SatSolver{
	
	/** The solver actually used. */
	private ISolver solver = null;

	/** Default value for max number of variables for asolver. */
	private static final int MAXVAR = 1000000;
	/** Default value for max number of expected clauses for a solver. */
	private static final int NBCLAUSES = 500000;
	
	/** Max number of variables for this solver. */
	private int maxvar;
	/** Max number of expected clauses for this solver. */
	private int nbclauses;
	
	/**
	 * Creates a new solver with the given parameters.
	 * @param maxvar Max number of variables for this solver.
	 * @param nbclauses Max number of expected clauses for this solver.
	 */
	public Sat4jSolver(int maxvar, int nbclauses){
		this.maxvar = maxvar;
		this.nbclauses = nbclauses;
	}
	
	/**
	 * Creates a new solver with default parameters (maxvar=1000000, nbclauses=500000).
	 */
	public Sat4jSolver(){
		this(Sat4jSolver.MAXVAR, Sat4jSolver.NBCLAUSES);
	}
	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.sat.SatSolver#isSatisfiable(java.util.Collection)
	 */
	@Override
	public boolean isSatisfiable(Collection<PropositionalFormula> formulas) {
		this.solver = SolverFactory.newLight();
		this.solver.newVar(this.maxvar);
		this.solver.setExpectedNumberOfClauses(this.nbclauses);		
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.sat.SatSolver#getWitness(java.util.Collection)
	 */
	@Override
	public Interpretation getWitness(Collection<PropositionalFormula> formulas) {
		this.solver = SolverFactory.newLight();
		this.solver.newVar(this.maxvar);
		this.solver.setExpectedNumberOfClauses(this.nbclauses);		
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
			if(!this.solver.isSatisfiable())
				return null;
			int[] model = this.solver.model();
			PossibleWorld w = new PossibleWorld();
			for(i = 0; i < model.length; i++)
				if(model[i]>0)
					w.add(idx2Prop[model[i]-1]);				
			return w;
		}catch(ContradictionException e){
			return null;
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}	
}

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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.sat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Tautology;

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
	 * @see org.tweetyproject.logics.pl.sat.SatSolver#isSatisfiable(java.util.Collection)
	 */
	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		ISolver solver = SolverFactory.newDefault();
		solver.newVar(this.maxvar);
		solver.setExpectedNumberOfClauses(this.nbclauses);		
		PlSignature sig = new PlSignature();
		for(PlFormula f: formulas)
			sig.addAll(f.getAtoms());		
		Map<Proposition, Integer> prop2Idx = new HashMap<Proposition, Integer>();
		Proposition[] idx2Prop = new Proposition[sig.size()];
		int i = 0;
		for(Proposition p: sig){
			prop2Idx.put(p, i);
			idx2Prop[i++] = p;
		}
		try{
			for(PlFormula f: formulas){
				Conjunction conj;
				if(f.isClause()) {
					conj = new Conjunction();
					conj.add(f);
				}else conj = f.toCnf();
				for(PlFormula f2: conj){
					Disjunction disj = (Disjunction) f2;
					// first remove contradictions
					while(disj.remove(new Contradiction()));					
					int[] clause = new int[disj.size()];
					i = 0;
					boolean taut = false;
					for(PlFormula f3: disj){
						if(f3 instanceof Proposition){
							clause[i++] = prop2Idx.get(f3) + 1; 
						}else if(f3 instanceof Negation){
							clause[i++] = - prop2Idx.get(((Negation)f3).getFormula()) - 1;
						}else if(f3 instanceof Tautology){
							taut = true;
							break;
						}else throw new RuntimeException("Unexpected formula type in conjunctive normal form: " + f3.getClass());
					}
					if(!taut) solver.addClause(new VecInt(clause));
				}
			}
			return solver.isSatisfiable();
		}catch(ContradictionException e){
			return false;
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.sat.SatSolver#getWitness(java.util.Collection)
	 */
	@Override
	public Interpretation<PlBeliefSet,PlFormula> getWitness(Collection<PlFormula> formulas) {
		ISolver solver = SolverFactory.newLight();
		solver.newVar(this.maxvar);
		solver.setExpectedNumberOfClauses(this.nbclauses);		
		PlSignature sig = new PlSignature();
		for(PlFormula f: formulas)
			sig.addAll(f.getAtoms());		
		Map<Proposition, Integer> prop2Idx = new HashMap<Proposition, Integer>();
		Proposition[] idx2Prop = new Proposition[sig.size()];
		int i = 0;
		for(Proposition p: sig){
			prop2Idx.put(p, i);
			idx2Prop[i++] = p;
		}
		try{
			for(PlFormula f: formulas){
				Conjunction conj;
				if(f.isClause()) {
					conj = new Conjunction();
					conj.add(f);
				}else conj = f.toCnf();
				for(PlFormula f2: conj){
					Disjunction disj = (Disjunction) f2;
					// first remove contradictions
					while(disj.remove(new Contradiction()));					
					int[] clause = new int[disj.size()];
					i = 0;
					boolean taut = false;
					for(PlFormula f3: disj){
						if(f3 instanceof Proposition){
							clause[i++] = prop2Idx.get(f3) + 1; 
						}else if(f3 instanceof Negation){
							clause[i++] = - prop2Idx.get(((Negation)f3).getFormula()) - 1;
						}else if(f3 instanceof Tautology){
							taut = true;
							break;
						}else throw new RuntimeException("Unexpected formula type in conjunctive normal form: " + f3.getClass());
					}
					if(!taut) solver.addClause(new VecInt(clause));
				}
			}
			if(!solver.isSatisfiable())
				return null;
			int[] model = solver.model();
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

	@Override
	public boolean isInstalled() {
		return true;
	}	
}

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
package net.sf.tweety.math.opt.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This class implements a simple genetic optimization algorithm for solving
 * optimization problems with only box constraints on float variables.
 * 
 * @author Matthias Thimm
 */
public class SimpleGeneticOptimizationSolver extends Solver{
	
	/** The probability of changing the value of a variable in the mutation step. */
	private static final double VAR_MUTATE_PROB = 0.2; 
	/** The magnitude of changing the value of a variable in the mutation step. */
	private static final double VAR_MUTATE_STRENGTH = 0.5;
	/** The probability of taking the average value of a variable in the crossover step. */
	private static final double VAR_CROSSOVER_PROB = 0.2; 
	
	/** For randomization */
	private Random rand = new Random();
	
	/** The size of the population */
	private int populationSize;
	/** How many new individuals are created by mutation (per individual) */
	private int populationIncreaseMutation;
	/** How many new individuals are created by cross-over (per pair of individuals) */
	private int populationIncreaseCrossOver;
	/** If an iteration improves less than this value the algorithm ends. */
	private double precision;
	
	/**
	 * Compares individuals by the fitness (value of the target function)
	 */
	private class FitnessComparator implements Comparator<Map<FloatVariable,Term>>{		
		private Term minT;
		public FitnessComparator(Term t){
			this.minT = t;
		}
		@Override
		public int compare(Map<FloatVariable, Term> arg0, Map<FloatVariable, Term> arg1) {
			double val1 = this.minT.replaceAllTerms(arg0).doubleValue();
			double val2 = this.minT.replaceAllTerms(arg1).doubleValue();
			if(val1 == val2)
				return 0;
			if(val1 < val2)
				return -1;
			return 1;
		}		
	}	
	
	/**
	 * Creates a new simple genetic optimization solver.
	 * @param populationSize The size of the population
	 * @param populationIncreaseMutation How many new individuals are created by mutation (per individual)
	 * @param populationIncreaseCrossOver How many new individuals are created by cross-over (per pair of individuals)
	 * @param precision If an iteration improves less than this value the algorithm ends
	 */
	public SimpleGeneticOptimizationSolver(int populationSize, int populationIncreaseMutation, int populationIncreaseCrossOver, double precision){
		this.populationSize = populationSize;
		this.populationIncreaseMutation = populationIncreaseMutation;
		this.populationIncreaseCrossOver = populationIncreaseCrossOver;
		this.precision = precision;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve(net.sf.tweety.math.opt.ConstraintSatisfactionProblem)
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException {
		// only optimization problems
		if(!(problem instanceof OptimizationProblem))
			throw new IllegalArgumentException("Only optimization problems allowed for this solver.");
		OptimizationProblem p = (OptimizationProblem) problem; 
		// only box constraints allows
		if(!p.isEmpty())
			throw new IllegalArgumentException("Only optimization problems with box constraints on variables allowed for this solver (no other constraints.");
		return this.solve(p.getTargetFunction(), p.getType());
	}
	
	/**
	 * Mutates the given individual
	 * @param ind some individual
	 * @return a new individual
	 */
	private Map<FloatVariable,Term> mutate(Map<FloatVariable,Term> ind){
		Map<FloatVariable,Term> mutant = new HashMap<FloatVariable,Term>();
		for(FloatVariable v: ind.keySet()){
			if(this.rand.nextDouble() < SimpleGeneticOptimizationSolver.VAR_MUTATE_PROB){
				// positive or negative mutation
				if(rand.nextBoolean()){
					double val = ind.get(v).doubleValue();
					val = val + SimpleGeneticOptimizationSolver.VAR_MUTATE_STRENGTH * (v.getUpperBound() - val);
					mutant.put(v, new FloatConstant(val));
				}else{
					double val = ind.get(v).doubleValue();
					val = val - SimpleGeneticOptimizationSolver.VAR_MUTATE_STRENGTH * (val- v.getLowerBound());
					mutant.put(v, new FloatConstant(val));
				}
			}else
				mutant.put(v, ind.get(v));
		}
		return mutant;
	}
	
	/**
	 * Makes a crossover of the two individuals
	 * @param ind1 some individual
	 * @param ind2 some individual
	 * @return a new individual
	 */
	private Map<FloatVariable,Term> crossover(Map<FloatVariable,Term> ind1, Map<FloatVariable,Term> ind2){
		Map<FloatVariable,Term> child = new HashMap<FloatVariable,Term>();
		for(FloatVariable v: ind1.keySet()){
			if(this.rand.nextDouble() < SimpleGeneticOptimizationSolver.VAR_CROSSOVER_PROB){
				child.put(v, new FloatConstant((ind1.get(v).doubleValue() + ind2.get(v).doubleValue())/2));
			}else if(this.rand.nextBoolean())
				child.put(v, ind1.get(v));
			else child.put(v, ind2.get(v));
		}
		return child;
	}
	
	/**
	 * Returns the variable assignment that maximizes/minimizes the given term
	 * (which only contains variables with defined upper and lower bounds).
	 * @param t the term to be evaluated
	 * @param optimization_objective one of OptimizationProblem.MAXIMIZE, OptimizationProblem.MINIMIZE 
	 * @return the optimal variable assignment
	 * @throws GeneralMathException 
	 */
	public Map<Variable,Term> solve(Term t, int optimization_objective) throws GeneralMathException{
		List<Variable> vars = new ArrayList<Variable>(t.getVariables());
		// check for defined upper and lower bounds and float variables
		for(Variable v: vars){
			if(v.getLowerBound() > v.getUpperBound())
				throw new IllegalArgumentException("Variable " + v + " has invalid box constraints.");
			if(!(v instanceof FloatVariable))
				throw new IllegalArgumentException("Variable " + v + " is not a float variable.");
		}
		// create initial population
		Set<Map<FloatVariable,Term>> currentPopulation = new HashSet<Map<FloatVariable,Term>>();
		FloatVariable w;
		double val;
		for(int i = 0; i < this.populationSize; i++){
			Map<FloatVariable,Term> ind = new HashMap<FloatVariable,Term>();
			for(Variable v: vars){
				w = (FloatVariable) v;
				val = this.rand.nextDouble() * (w.getUpperBound() - w.getLowerBound()) + w.getLowerBound();
				ind.put(w, new FloatConstant(val));
			}
			currentPopulation.add(ind);
		}
		// we only minimize
		Term minT;
		if(optimization_objective == OptimizationProblem.MAXIMIZE)
			minT = t.mult(new FloatConstant(-1));
		else minT = t;
		// iterate
		double previous_val;
		double current_val = Double.MAX_VALUE;
		Map<FloatVariable,Term> currentBest = null;
		PriorityQueue<Map<FloatVariable,Term>> p = new PriorityQueue<Map<FloatVariable,Term>>(this.populationSize,new FitnessComparator(minT)); 
		do{
			previous_val = current_val;
			// create new population
			p.clear();
			p.addAll(currentPopulation);
			// mutate
			for(Map<FloatVariable,Term> ind: currentPopulation)
				for(int i = 0; i < this.populationIncreaseMutation; i++)
					p.add(this.mutate(ind));
			// crossover
			for(Map<FloatVariable,Term> ind1: currentPopulation)
				for(Map<FloatVariable,Term> ind2: currentPopulation)
					if(ind1 != ind2)
						for(int i = 0; i < this.populationIncreaseCrossOver; i++)
							p.add(this.crossover(ind1, ind2));
			// select best individuals
			currentBest = p.peek();
			current_val = minT.replaceAllTerms(p.peek()).doubleValue();			
			currentPopulation.clear();			
			for(int i = 0; i < this.populationSize; i++)
				currentPopulation.add(p.poll());
			
		}while(previous_val - current_val < this.precision);
		// convert map again
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		if(currentBest == null)
			throw new GeneralMathException("The optimization problem seems to be infeasible.");
		for(FloatVariable v: currentBest.keySet())
			result.put(v, currentBest.get(v));
		return result;
	}

}
package net.sf.tweety.math.opt.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.sf.tweety.math.opt.problem.CombinatoricsProblem;
import net.sf.tweety.math.term.ElementOfCombinatoricsProb;


public class SimpleGeneticOptimizationSolverCombinatorics extends CombinatoricsSolver{

	
	/** Logger */
	static private Logger log = LoggerFactory.getLogger(SimpleGeneticOptimizationSolver.class);
	
	/** The probability of changing the value of a variable in the mutation step. */
	private static final double VAR_MUTATE_PROB = 0.2; 
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
	/** The minimal number of iterations. */
	private int minIterations;
	/**the exact problem that is to  be solved*/
	private CombinatoricsProblem prob;
	
	/**
	 * Compares individuals by the fitness (value of the target function)
	 */
	public class FitnessComparator implements Comparator<ArrayList<ElementOfCombinatoricsProb>>{		

		CombinatoricsProblem prob;
		public FitnessComparator(CombinatoricsProblem prob){
			this.prob = prob;
		}
		@Override
		public int compare(ArrayList<ElementOfCombinatoricsProb> ind1, ArrayList<ElementOfCombinatoricsProb> ind2) {
			double val1 = this.prob.evaluate(ind1);
			double val2 = this.prob.evaluate(ind2);
			if(val1 == val2)
				return 0;
			if(val1 < val2)
				return -1;
			return 1;
		}		
	}	
	
	/**
	 * Creates a new simple genetic optimization solver.
	 * @param prob the instance of the combinatorics problem
	 * @param populationSize The size of the population
	 * @param populationIncreaseMutation How many new individuals are created by mutation (per individual)
	 * @param populationIncreaseCrossOver How many new individuals are created by cross-over (per pair of individuals)
	 * @param minIterations The minimal number of iterations
	 * @param precision If an iteration improves less than this value the algorithm ends
	 */
	public SimpleGeneticOptimizationSolverCombinatorics( int populationSize, int populationIncreaseMutation, int populationIncreaseCrossOver, int minIterations, double precision){
		this.populationSize = populationSize;
		this.populationIncreaseMutation = populationIncreaseMutation;
		this.populationIncreaseCrossOver = populationIncreaseCrossOver;
		this.minIterations = minIterations;
		this.precision = precision;
	}	
	

	
	/**
	 * Mutates the given individual
	 * @param ind some individual
	 * @return a new individual
	 */
	private ArrayList<ElementOfCombinatoricsProb> mutate(ArrayList<ElementOfCombinatoricsProb> ind){
		//create a mutant according to the probability
		if(rand.nextDouble() >= SimpleGeneticOptimizationSolverCombinatorics.VAR_MUTATE_PROB)
			return this.prob.createRandomNewSolution(ind);
		
		else
			return ind;
		
	}
	
	/**
	 * Makes a crossover of the two individuals
	 * @param ind1 some individual
	 * @param ind2 some individual
	 * @return a new individual
	 */
	private ArrayList<ElementOfCombinatoricsProb>  crossover(ArrayList<ElementOfCombinatoricsProb> ind1, ArrayList<ElementOfCombinatoricsProb> ind2){
		//if one of the solutions is empty, then return the other one
		if(ind1.size() == 0 && ind2.size() == 0)
			return ind1;
		else if(ind1.size() == 0)
			return ind2;
		else if(ind2.size() == 0)
			return ind1;
		//choose thesmaller of the 2 solutions 
		int sizeOfsmallerInd = ind1.size() < ind2.size() ? ind1.size() : ind2.size();
		//choose starte of the crossover 
		int changeStart = (Math.abs(rand.nextInt()) % sizeOfsmallerInd);
		//choose the length of the crossover
		int changeEnd = (int) (SimpleGeneticOptimizationSolverCombinatorics.VAR_CROSSOVER_PROB * rand.nextDouble() * sizeOfsmallerInd);
		
		ArrayList<ElementOfCombinatoricsProb> crossInd1= new ArrayList<ElementOfCombinatoricsProb>();
		ArrayList<ElementOfCombinatoricsProb> crossInd2= new ArrayList<ElementOfCombinatoricsProb>();
		//create the parts to be crossed over 
		for(int i = 0; i < changeEnd; i++)
		{
			crossInd1.add(ind1.get((changeStart + i) % ind1.size()));
			crossInd2.add(ind2.get((changeStart + i) % ind2.size()));
		}
		//Idee aus: https://www.hindawi.com/journals/cin/2017/7430125/
		ArrayList<ElementOfCombinatoricsProb> child = new ArrayList<ElementOfCombinatoricsProb>();
		int cntCross1 = 0;

		//create the child on the basis of ind1
		for(ElementOfCombinatoricsProb i : ind1)
			child.add(i);
		//copy all elements of the crossover part of ind2 (cross2) to the positions of crossover of ind1 (cross1). 
		//If elements of cross2 are already in ind1 and not in cross1, then replace these elements in ind1 with 
		for(int i = 0; i < changeEnd; i++)
		{
			//copy the elements of cross2 to the child
			child.set((i + changeStart) % ind1.size(),  crossInd2.get(i));
			//if ind1 already contains elements of cross2 replace them with elemtns from cross1
			if(ind1.contains(crossInd2.get(i)) && !crossInd1.contains(crossInd2.get(i)))
			{
				l1: for(int j = cntCross1; j < changeEnd; j++)
				{
						if(!crossInd2.contains(crossInd1.get(j)))
						{
							child.set(ind1.indexOf(crossInd2.get(i)), crossInd1.get(cntCross1));
							cntCross1++;
							break l1;
						}
						cntCross1++;
				}
			}
		}
			
		return child;
	}
	
	/**
	 * Returns the solution according the problem; problem has to be minimizing
	 * (which only contains variables with defined upper and lower bounds).
	 * @param initalSol the initial solution as a starting point
	 * @return the optimal solution found in the search
	 */
	public ArrayList<ElementOfCombinatoricsProb> solve(CombinatoricsProblem prob){
		this.prob = prob;

		// create initial population
		Set<ArrayList<ElementOfCombinatoricsProb>> currentPopulation = new HashSet<ArrayList<ElementOfCombinatoricsProb>>();
		for(int i = 0; i < this.populationSize; i++){
			ArrayList<ElementOfCombinatoricsProb> ind = this.prob.createRandomNewSolution(null);
			currentPopulation.add(ind);
		}

		// iterate
		double previous_val;
		double current_val = Double.MAX_VALUE;
		ArrayList<ElementOfCombinatoricsProb> currentBest = null;
		PriorityQueue<ArrayList<ElementOfCombinatoricsProb>> p = new PriorityQueue<ArrayList<ElementOfCombinatoricsProb>>(this.populationSize,new FitnessComparator(this.prob));
		int it = 0;
		
		do{
			previous_val = current_val;
			// create new population
			p.clear();
			p.addAll(currentPopulation);
			
			// mutate
			for(ArrayList<ElementOfCombinatoricsProb> ind: currentPopulation)
				for(int i = 0; i < this.populationIncreaseMutation; i++)
					p.add(this.mutate(ind));
			// crossover
			for(ArrayList<ElementOfCombinatoricsProb>  ind1: currentPopulation)
				for(ArrayList<ElementOfCombinatoricsProb>  ind2: currentPopulation)
					if(ind1 != ind2)
						for(int i = 0; i < this.populationIncreaseCrossOver; i++)
							p.add(this.crossover(ind1, ind2));
			// select best individuals
			currentBest = p.peek();
			
			current_val = this.prob.evaluate(p.peek());			
			currentPopulation.clear();			
			for(int i = 0; i < this.populationSize; i++)
				currentPopulation.add(p.poll());
			log.info("Optimizating... current value of target function: " + current_val);
		}while(previous_val - current_val > this.precision || it++ < this.minIterations);
		//choose the result
		ArrayList<ElementOfCombinatoricsProb> result = new ArrayList<ElementOfCombinatoricsProb>();

		result = currentBest;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		// as this is a native implementation it is always installed
		return true;
	}

}

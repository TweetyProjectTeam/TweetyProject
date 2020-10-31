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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package net.sf.tweety.math.opt.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.naming.ConfigurationException;
import isula.aco.AcoProblemSolver;
import isula.aco.Ant;
import isula.aco.AntColony;
import isula.aco.ConfigurationProvider;
import isula.aco.DaemonAction;
import isula.aco.Environment;
import isula.aco.algorithms.antsystem.OfflinePheromoneUpdate;
import isula.aco.algorithms.antsystem.PerformEvaporation;
import isula.aco.algorithms.antsystem.RandomNodeSelection;
import isula.aco.algorithms.antsystem.StartPheromoneMatrix;
import isula.aco.exception.InvalidInputException;
import net.sf.tweety.math.opt.problem.*;
import net.sf.tweety.math.term.ElementOfCombinatoricsProb;

/**
 * This class implements  the ant colony algorithm using 
 * isula (https://github.com/cptanalatriste/isula) for combinatrics problems
 * @author Sebastian Franke
 */

public class AntColonyOptimization extends CombinatoricsSolver {
	/**configuration data*/
	 public int NumberOfAnts;
     public double EvaporationRatio;
     public int NumberOfIterations;
     public double HeuristicImportance;
     public double PheromoneImportance;
	//end of configuration data
	
	
	/**for using the problem sepciifc functions*/
	protected CombinatoricsProblem problem;
	/**ajacence array representation of graph*/
	double[][] problemRepresentation;
	public AntColonyOptimization(int NumberOfAnts, double EvaporationRatio, 
			int NumberOfIterations, double HeuristicImportance, 
			double PheromoneImportance) throws IOException {
		
		
		this.NumberOfAnts = NumberOfAnts;
		this.EvaporationRatio = EvaporationRatio;
		this.NumberOfIterations = NumberOfIterations;
		this.HeuristicImportance = HeuristicImportance;
		
	}
	/**solves the problem and connects the config and initializes the rest
	 * @throws IOException */
	public ArrayList<ElementOfCombinatoricsProb> solve(CombinatoricsProblem prob) throws ConfigurationException, InvalidInputException, IOException {

		this.problem = prob;
		this.problemRepresentation = getRepresentation(problem);
			//inintialize all the data
	        ProblemConfiguration configurationProvider = new ProblemConfiguration(problemRepresentation);
	        AntColony<ElementOfCombinatoricsProb, AntCol_Environment> colony = this.getAntColony(configurationProvider);
	        AntCol_Environment environment = new AntCol_Environment(problemRepresentation);

	        AcoProblemSolver<ElementOfCombinatoricsProb, AntCol_Environment> solver = new AcoProblemSolver<>();
	        solver.initialize(environment, colony, (ConfigurationProvider) configurationProvider);
	        solver.addDaemonActions(new StartPheromoneMatrix<ElementOfCombinatoricsProb, AntCol_Environment>(),
	                new PerformEvaporation<ElementOfCombinatoricsProb, AntCol_Environment>());
	        solver.addDaemonActions(getPheromoneUpdatePolicy());       
	        solver.getAntColony().addAntPolicies(new RandomNodeSelection<ElementOfCombinatoricsProb, AntCol_Environment>());

	        solver.solveProblem();
	        //translate solution to the usual format
	        ArrayList<ElementOfCombinatoricsProb> bestSol = new ArrayList<ElementOfCombinatoricsProb>();
	        for(ElementOfCombinatoricsProb i : solver.getBestSolution())
	        	bestSol.add(i);
	        	
			return bestSol;
	}
	/**Ant colony*/
    public AntColony<ElementOfCombinatoricsProb, AntCol_Environment> getAntColony(final ConfigurationProvider configurationProvider) {
        return new AntColony<ElementOfCombinatoricsProb, AntCol_Environment>(configurationProvider.getNumberOfAnts()) {
            @Override
            protected AntCol_Ant createAnt(AntCol_Environment environment) {
                int initialReference = new Random().nextInt(environment.members.length);
                AntCol_Ant ant = null;
                try {
					ant = AntColonyOptimization.this.new AntCol_Ant(environment.members.length);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                ant.initialReference = environment.members[initialReference];
                
                return ant;
            }
        };

    }
	
    private static DaemonAction<ElementOfCombinatoricsProb, AntCol_Environment> getPheromoneUpdatePolicy() {
        return new OfflinePheromoneUpdate<ElementOfCombinatoricsProb, AntCol_Environment>() {
            @Override
            protected double getPheromoneDeposit(Ant<ElementOfCombinatoricsProb, AntCol_Environment> ant,
                                                 Integer positionInSolution,
                                                 ElementOfCombinatoricsProb solutionComponent,
                                                 AntCol_Environment environment,
                                                 ConfigurationProvider configurationProvider) {
            	return 1/ ant.getSolutionCost(environment);

            }
        };
    }

    public static double[][] getRepresentation(CombinatoricsProblem prob) throws IOException {

        return prob.getRepresentation();
    }
	/**config data*/
	class ProblemConfiguration implements ConfigurationProvider {
		private double initialPheromoneValue;
		
		public ProblemConfiguration(double[][] problemRepresentation) {
	        ArrayList<ElementOfCombinatoricsProb> randomSolution = 
	        		AntColonyOptimization.this.problem.createRandomNewSolution(null);
	        int numberOfCities = problemRepresentation.length;
	        
	        this.initialPheromoneValue = numberOfCities / AntColonyOptimization.this.problem.sumOfWeights(randomSolution);
		}
        public int getNumberOfAnts() {
            return AntColonyOptimization.this.NumberOfAnts;
        }

        public double getEvaporationRatio() {
            return AntColonyOptimization.this.EvaporationRatio;
        }

        public int getNumberOfIterations() {
            return AntColonyOptimization.this.NumberOfIterations;
        }


        public double getInitialPheromoneValue() {
            return this.initialPheromoneValue;
        }

        @Override
        public double getHeuristicImportance() {
            return AntColonyOptimization.this.HeuristicImportance;
        }

        @Override
        public double getPheromoneImportance() {
            return AntColonyOptimization.this.PheromoneImportance;
        }
		
	}
	/**environment*/
	public class AntCol_Environment extends Environment{
		/**represent elements in a given order to access the graphRepresantation better*/
		protected ElementOfCombinatoricsProb[] members = new ElementOfCombinatoricsProb[AntColonyOptimization.this.problem.elements.size()];
		public AntCol_Environment(double[][] problemRepresentation) throws InvalidInputException {
			super(problemRepresentation);			
			int i = 0;
			for(ElementOfCombinatoricsProb el: AntColonyOptimization.this.problem.elements) {
				members[i] = el;
				i++;
			}			
		}

		/**initialize the pheromone matrix with zeroes*/
		@Override
		protected double[][] createPheromoneMatrix() {
			double[][] rep = new double[problemRepresentation.length][problemRepresentation.length];

			return rep;
		}
		
	}
	/**ant*/
	class AntCol_Ant extends Ant<ElementOfCombinatoricsProb, AntCol_Environment>{
		int probSize;

	    public AntCol_Ant(int probSize) throws InterruptedException {
	        super();

	        
	        this.probSize = probSize;
	        this.setSolution(new ElementOfCombinatoricsProb[probSize]);

	    }
		/**starting point for this particular ant*/
		private ElementOfCombinatoricsProb initialReference;
		
		/**find an element in an array*/
		public <T> int find(T[] arr, T target)
		{
			if(arr == null)
				return 0;

			for(int i = 0; i < arr.length; i++) {
				if(target.equals(arr[i]))
					return i;
			}

				
			return -1;
		}
		
		boolean contains(ElementOfCombinatoricsProb[] arr, ElementOfCombinatoricsProb el) {
			for (ElementOfCombinatoricsProb i : arr) {
			    if (i == el) {
			        return true;
			    }
			}
			return false;
			
		}
	
		@Override
		public boolean isSolutionReady(AntCol_Environment environment) {
			ArrayList<ElementOfCombinatoricsProb> list = new ArrayList<ElementOfCombinatoricsProb>();
			
			for(ElementOfCombinatoricsProb el: this.getSolution()) {
				list.add(el);
			}
			return AntColonyOptimization.this.problem.isValid(list);
		}

		@Override
		public double getSolutionCost(AntCol_Environment environment) {
			ArrayList<ElementOfCombinatoricsProb> list = new ArrayList<ElementOfCombinatoricsProb>();
			
			for(ElementOfCombinatoricsProb el: environment.members) {
				list.add(el);
			}
			return AntColonyOptimization.this.problem.sumOfWeights(list);
		}

		/** return 1 as default as a means to guarantee the approval of any component, ovewrite for you problem*/
		@Override
		public Double getHeuristicValue(ElementOfCombinatoricsProb solutionComponent, Integer positionInSolution,
				AntCol_Environment environment) {
			
			
			 return AntColonyOptimization.this.problem.getHeuristicValue(solutionComponent,
						 getCurrentIndex(), this.initialReference, this.getSolution());
	        
		}

		@Override
		public List<ElementOfCombinatoricsProb> getNeighbourhood(AntCol_Environment environment) {
			ArrayList<ElementOfCombinatoricsProb> neighbors = new ArrayList<ElementOfCombinatoricsProb>();
			int lastComp = 1;
			for(ElementOfCombinatoricsProb i : this.getSolution())
			{
				if(i != null)
					lastComp++;
				else
					break;
			}
			lastComp--;
			//only add elements if they are reachable and not yet i the solution
			for(int i = 0; i < environment.getProblemRepresentation().length; i++){
					if(AntColonyOptimization.this.problem.getGraphrepresentation()[lastComp][i] == 1 &&
							 this.contains(this.getSolution(), environment.members[i]) == false){					
						neighbors.add( environment.members[i]);
					}
			}
			return neighbors;
		}

		@Override
		public Double getPheromoneTrailValue(ElementOfCombinatoricsProb solutionComponent, Integer positionInSolution,
				AntCol_Environment environment) {
			ElementOfCombinatoricsProb previousComponent = this.initialReference;
		
	        if (positionInSolution > 0) {
	            previousComponent = getSolution()[positionInSolution - 1];
	        }

	        double[][] pheromoneMatrix = environment.getPheromoneMatrix();
       

	        return pheromoneMatrix[find(environment.members, solutionComponent)][find(environment.members, previousComponent)];
			
		}

		@Override
		public void setPheromoneTrailValue(ElementOfCombinatoricsProb solutionComponent, Integer positionInSolution,
				AntCol_Environment environment, Double value) {
			ElementOfCombinatoricsProb previousComponent = this.initialReference;
	        if (positionInSolution > 0) {
	            previousComponent = getSolution()[positionInSolution - 1];
	        }

	        double[][] pheromoneMatrix = environment.getPheromoneMatrix();
	        pheromoneMatrix[find(environment.members, solutionComponent)][find(environment.members, previousComponent)] = value;
	        pheromoneMatrix[find(environment.members, previousComponent)][find(environment.members, solutionComponent)] = value;
			
		}

	    @Override
	    public void clear() {
	        super.clear();
	        int rand = new Random().nextInt(AntColonyOptimization.this.problem.elements.size());
	        int i = 0;
	        //choose random element as new startig point
	        for(ElementOfCombinatoricsProb obj : AntColonyOptimization.this.problem.elements)
	        {
	            if (rand == i)
	            	 this.initialReference = obj;
	            i++;
	        }
	    }


		
		
	}

}

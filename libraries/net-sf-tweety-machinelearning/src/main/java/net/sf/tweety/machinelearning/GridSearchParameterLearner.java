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
package net.sf.tweety.machinelearning;

/**
 * A grid-search approach for learning parameters. For each parameter with I=[l,u] being
 * the boundaries for the parameter value of a given trainer, I is divided into <code>partitions</code>
 * number of partitions. For each partition of each parameter the border points are chosen and a new
 * classifier is learned with given parameter combination. From all combinations the combination
 * where the classifier performs best is chosen. If <code>depth</code> &gt; 1, the process is iterated: after selecting
 * the best interval combination of the parameters, these intervals are again divided and the process
 * is repeated <code>depth</code> many times.
 *  
 * @author Matthias Thimm
 * @param <S> the type of observations.
 * @param <T> the type of categories.
 */
public class GridSearchParameterLearner<S extends Observation, T extends Category> extends ParameterTrainer<S,T>{

	/** The depth of the recursion. */
	private int depth;
	/** The number of partitions of each parameter interval. */
	private int partitions;
	/** The tester used for measuring the performance of each parameter combination. */
	private ClassificationTester<S,T> tester;
	
	/**
	 * Creates a new grid-search parameter learner with the given arguments.
	 * @param trainer some trainer.
	 * @param tester some classification tester for measuring performance.
	 * @param depth the depth of the recursion.
	 * @param partitions the number of partitions.
	 */
	public GridSearchParameterLearner(Trainer<S, T> trainer, ClassificationTester<S,T> tester, int depth, int partitions) {
		super(trainer);
		this.tester = tester;
		this.depth = depth;
		this.partitions = partitions;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.ParameterTrainer#learnParameters(net.sf.tweety.machinelearning.TrainingSet)
	 */
	@Override
	public ParameterSet learnParameters(TrainingSet<S, T> trainingSet) {
		Trainer<S,T> trainer = this.getTrainer();
		ParameterSet set = trainer.getParameterSet();
		int[] indices = new int[set.size()];
		for(int i = 0; i<indices.length;i++)
			indices[i]=0;
		double[] lowerBounds = new double[set.size()];
		double[] upperBounds = new double[set.size()];
		int idx = 0;
		for(TrainingParameter param: set){
			lowerBounds[idx] = param.getLowerBound();
			upperBounds[idx++] = param.getUpperBound();
		}	
		double performance;
		double maxPerformance = 0;
		int[] bestIdxs = new int[set.size()];
		for(int i = 0; i < this.depth;i++){
			do{			
				trainer.setParameterSet(this.adjustParameterSet(set, indices, lowerBounds, upperBounds));
				performance = this.tester.test(trainer, trainingSet);
				if(performance > maxPerformance){
					maxPerformance = performance;
					System.arraycopy(indices, 0, bestIdxs, 0, set.size());					
				}
			}while(!this.increment(indices, this.partitions));
			// if going into recursion, select the best indices and adjust upper/lower bounds
			if(i+1 != this.depth){
				for(int j = 0; j < set.size(); j++){
					lowerBounds[j] = lowerBounds[j]+(upperBounds[j]-lowerBounds[j])/this.partitions*(Math.max(indices[j]-1,0));
					upperBounds[j] = lowerBounds[j]+(upperBounds[j]-lowerBounds[j])/this.partitions*(Math.min(indices[j]+1,this.partitions));
					// re-init index
					indices[j] = 0;
				}
				//re-init max performance
				maxPerformance = 0;
			}
		}
		return this.adjustParameterSet(set, bestIdxs, lowerBounds, upperBounds);
	}

	/**
	 * 	Determine for all parameters of the set a new value, determined by partitioning [lowerBound,upperBound]
	 *  into this.partitions number of sub intervals and then taking the center point of the partition no. idx.
	 * @param set a parameter set
	 * @param indices indices
	 * @param lowerBounds the lower bounds
	 * @param upperBounds the upper bounds
	 * @return a new parameter set
	 */
	private ParameterSet adjustParameterSet(ParameterSet set, int[] indices, double[] lowerBounds, double[] upperBounds){
		ParameterSet newParams = new ParameterSet();
		int idx = 0;
		for(TrainingParameter param: set){								
			newParams.add(param.instantiate(lowerBounds[idx]+(upperBounds[idx]-lowerBounds[idx])/this.partitions*(indices[idx])));
			idx++;
		}
		return newParams;
	}
	
	
	/**
	 * Increments the given array of indices, e.g. (for maxIdx=5)
	 * given [0,0,0,0] it returns [1,0,0,0], given [4,2,1,4] it 
	 * returns [5,2,1,4], given [5,2,1,4] it returns [0,3,1,4], given
	 * [5,5,1,4] it returns [0,0,2,4], etc. It returns true iff
	 * an overflow occurs, e.g. if [5,5,5,5] is to be incremented.  
	 * @param indices an array of ints.
	 * @param maxIdx the max index.
	 * @return "true" iff an overflow occurs
	 */
	private boolean increment(int[] indices, int maxIdx){
		boolean carry = false;
		for(int i = 0; i < indices.length; i++){
			if(indices[i] < maxIdx){
				indices[i]++;
				return false;
			}else{
				indices[i] = 0;
				carry = true;
			}
		}		
		return carry;
	}
	
}

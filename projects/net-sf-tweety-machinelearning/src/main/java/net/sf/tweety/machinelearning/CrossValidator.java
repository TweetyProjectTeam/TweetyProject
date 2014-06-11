package net.sf.tweety.machinelearning;

import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.commons.util.Pair;

/**
 * Performs cross-validation in a classifier, i.e. it divides a given training set into
 * N parts (more precisely, for each category present in the training set, the observations
 * belonging each category are partitioned into N parts), for each i=1,...,N trains a
 * classifier on the union of all parts except i, and measures the performance on part i.
 * The final performance measure is the average on these N rounds.
 * 
 * @author Matthias Thimm
 * @param <S> the type of observation
 * @param <T> the type of category
 */
public class CrossValidator<S extends Observation, T extends Category> extends ClassificationTester<S,T> {

	/** The number of partitions for cross-validation. */
	private int fold;
	
	/**
	 * Creates a new cross-validator with the given number of partitions.
	 * @param fold the number of partitions.
	 */
	public CrossValidator(int fold){
		if(fold < 2)
			throw new IllegalArgumentException("Number of partitions must be greater or equal to 2.");
		this.fold = fold;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.ClassificationTester#test(net.sf.tweety.machinelearning.Trainer, net.sf.tweety.machinelearning.TrainingSet)
	 */
	@Override
	public double test(Trainer<S, T> trainer, TrainingSet<S, T> trainingSet) {
		List<TrainingSet<S,T>> partitions = new ArrayList<TrainingSet<S,T>>();
		for(int i = 0; i < this.fold; i++)
			partitions.add(new TrainingSet<S,T>());
		// Distribute observations of each category equally on the partitions
		for(T cat: trainingSet.getCategories()){
			int i = 0;
			for(Pair<S,T> entry: trainingSet.getObservations(cat)){
				partitions.get(i % this.fold).add(entry);
				i++;
			}
		}
		double perf = 0;
		TrainingSet<S,T> actualTrainingSet; 
		for(int i = 0; i < this.fold; i++){
			actualTrainingSet = new TrainingSet<S,T>();
			for(int j = 0; j < this.fold; j++)
				if(i != j)
					actualTrainingSet.addAll(partitions.get(j));
			Classifier c = trainer.train(actualTrainingSet);
			perf += this.test(c, partitions.get(i));
		}		
		return perf/this.fold;
	}

}

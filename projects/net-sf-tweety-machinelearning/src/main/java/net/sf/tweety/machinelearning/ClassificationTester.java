package net.sf.tweety.machinelearning;

import net.sf.tweety.commons.util.Pair;

/**
 * Classes implementing this interface provide the means to test a training mechanism 
 * for performance.
 * @author Matthias Thimm
 * @param <S> type of observations
 * @param <T> type of categories
 */
public abstract class ClassificationTester<S extends Observation, T extends Category> {
	
	/**
	 * This methods takes a trainer and a training set and returns the performance
	 * (in [0,1]) of the trained classifier on the training set (e.g. using
	 * cross-validation). The larger the value the better the trained classifier.
	 * @param trainer some trainer
	 * @param trainingSet some training set
	 * @return the performance of the trained classifier
	 */
	public abstract double test(Trainer<S,T> trainer, TrainingSet<S,T> trainingSet);
	
	/**
	 * Measures the performance of the given classifier on the given test set, i.e.
	 * every observation from the training set is classified by the classifier and
	 * its prediction is compared with the provided category. The return value is the
	 * ratio of the correctly classified observations and the total number of 
	 * observations. 
	 * @param classifier some classifier.
	 * @param trainingSet some training set.
	 */
	public double test(Classifier classifier, TrainingSet<S,T> trainingSet){
		int correctPredictions = 0;
		for(Pair<S,T> entry: trainingSet){
			if(classifier.classify(entry.getFirst()).equals(entry.getSecond()))
				correctPredictions++;
		}
		return new Double(correctPredictions)/trainingSet.size();
	}
}

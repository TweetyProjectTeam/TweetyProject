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
package org.tweetyproject.machinelearning;

import org.tweetyproject.commons.util.Pair;

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
	 * @return the performance of the given classifier 
	 */
	public double test(Classifier classifier, TrainingSet<S,T> trainingSet){
		int correctPredictions = 0;
		for(Pair<S,T> entry: trainingSet){
			if(classifier.classify(entry.getFirst()).equals(entry.getSecond()))
				correctPredictions++;
		}
		return ((double)correctPredictions)/trainingSet.size();
	}
}

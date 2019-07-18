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
 * @author Matthias Thimm
 *
 * @param <S> The type of the observations.
 * @param <T> The type of the categories.
 */
public interface Trainer<S extends Observation, T extends Category> {

	/**
	 * Trains a classifier on the given training set.
	 * @param trainingSet some training set.
	 * @return a classifier
	 */
	public Classifier train(TrainingSet<S,T> trainingSet);
	
	/**
	 * Trains a classifier on the given training set with the given parameters
	 * @param trainingSet some training set.
	 * @param params parameters for the training.
	 * @return a classifier
	 */
	public Classifier train(TrainingSet<S,T> trainingSet, ParameterSet params);
	
	/**
	 * Returns the set of parameters for this trainer.
	 * @return the set of parameters for this trainer.
	 */
	public ParameterSet getParameterSet();
	
	/** 
	 * Sets the parameters for this trainer (calling this
	 * method must ensure that the next time <code>train(TrainingSet&lt;S,T&gt; trainingSet)</code>
	 * is used it uses these parameters.
	 * @param params a parameter set.
	 * @return true if the operation was successful.
	 */
	public boolean setParameterSet(ParameterSet params);
}

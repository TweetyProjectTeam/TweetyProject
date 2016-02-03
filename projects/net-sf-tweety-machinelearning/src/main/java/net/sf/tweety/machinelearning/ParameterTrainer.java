/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.machinelearning;

/**
 * Performs a parameter training on a given trainer, i.e. explores the space
 * of the parameters to obtain the best parameters for training.
 * @author Matthias Thimm
 * @param <S> The type of observations.
 * @param <T> The type of categories.
 */
public abstract class ParameterTrainer<S extends Observation, T extends Category> implements Trainer<S,T> {

	/** The trainer for which we seek the best parameters. */
	private Trainer<S,T> trainer;
	
	/**
	 * Creates a new parameter trainer for the given trainer.
	 * @param trainer some trainer.
	 */
	public ParameterTrainer(Trainer<S,T> trainer){
		this.trainer = trainer;
	}	
	
	/**
	 * Learns the best parameters of the given trainer for the training set.
	 * @param trainingSet a training set
	 * @return the best parameters for the training set.
	 */
	public abstract ParameterSet learnParameters(TrainingSet<S, T> trainingSet);
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#train(net.sf.tweety.machinelearning.TrainingSet)
	 */
	@Override
	public Classifier train(TrainingSet<S, T> trainingSet) {
		ParameterSet set = this.learnParameters(trainingSet);
		return this.trainer.train(trainingSet,set);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#train(net.sf.tweety.machinelearning.TrainingSet, net.sf.tweety.machinelearning.ParameterSet)
	 */
	@Override
	public Classifier train(TrainingSet<S, T> trainingSet, ParameterSet params) {
		return this.train(trainingSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#getParameterSet()
	 */
	@Override
	public ParameterSet getParameterSet() {
		return new ParameterSet();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#setParameterSet(net.sf.tweety.machinelearning.ParameterSet)
	 */
	@Override
	public boolean setParameterSet(ParameterSet params) {
		// no parameters to set
		return true;
	}

	/**
	 * Returns the trainer of this parameter trainer.
	 * @return the trainer of this parameter trainer.
	 */
	protected Trainer<S,T> getTrainer(){
		return this.trainer;
	}
	
}

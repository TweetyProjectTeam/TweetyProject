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

package net.sf.tweety.machinelearning;

/**
 * A classifier classifies observations into categories.
 * @author Matthias Thimm
 */
public interface Classifier {

	/**
	 * Classifies the given observation.
	 * @param obs some observation.
	 * @return The category of the observation.
	 */
	public Category classify(Observation obs);
	
}

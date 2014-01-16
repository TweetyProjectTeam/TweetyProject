package net.sf.tweety.preferences.aggregation;

/**
 * This interface is used for 
 * @author Bastian Wolf
 *
 */
public interface WeightVector {
	
	/**
	 * returns the weight of an 
	 * @param n the given parameter to compare with
	 * @return the weight for the given parameter
	 */
	public int getWeight(int n);

}

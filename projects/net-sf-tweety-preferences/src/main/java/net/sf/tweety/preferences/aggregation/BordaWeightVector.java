package net.sf.tweety.preferences.aggregation;

/**
 * Implementation of the borda weight vector
 * N elements are weighted from 0 to n-1 depending on their ranking function rank
 * @author Bastian Wolf
 *
 */

public class BordaWeightVector implements WeightVector {

	private int size;
	
	/**
	 * constructor for the borda weight vector
	 * @param size the amount of domain elements in the preference order
	 */
	public BordaWeightVector(int size){
		this.size = size;
	}
	
	/**
	 * returns the weight based on the rank in the ranking function.
	 * e.g. the second-highest ranked element of five will get the weight
	 *(size=5)-1-(rank=1) = 5-1-1 = 3, where the highest will get weight 4 (5-1-0)
	 * and the last element will get weight 0, (5-1-4)
	 */
	@Override
	public int getWeight(int n) {
		return size-1-n;
	}

}

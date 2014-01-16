package net.sf.tweety.preferences.aggregation;

/**
 * The single peak implementation of the weight vector, where only the highest rated value in each preference order scores
 * (1,0,0,0,0,0)
 * 
 * @author bwolf
 *
 */
public class SinglePeakWeightVector implements WeightVector {
	
	/**
	 * given value of the peak element
	 */
	private int m;
	
	/**
	 * constructor for weight vector with peak value
	 * @param m the value used to determine the weight of an object
	 */
	public SinglePeakWeightVector(int m) {
		this.m = m;
	}

	/**
	 * checks, whether an element got the highest rank
	 * @return 1 if highest, 0 otherwise
	 */
	public int getWeight(int n) {
			if (n == m){
				return 1;
			}
		return 0;
	}

}

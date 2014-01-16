package net.sf.tweety.preferences.aggregation;

/**
 * 
 * @author Bastian Wolf
 *
 */
public class SingleValeWeightVector implements WeightVector {

	/**
	 * value of the least chosen element
	 */
	private int m;
	
	/**
	 * constructor setting the value for the least chosen element
	 * @param m least rank used in ranking function for the preference orders
	 */
	public SingleValeWeightVector(int m){
		this.m = m;
	}
	
	/**
	 * returns the weight of the element
	 * @return 1 if element is least ranked, 0 otherwise
	 */
	@Override
	public int getWeight(int n) {
		if (n == m){
			return 0;
		}
		return 1;
	}

}

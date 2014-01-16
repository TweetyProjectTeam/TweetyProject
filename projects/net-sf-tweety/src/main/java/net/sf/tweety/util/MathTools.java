package net.sf.tweety.util;

/**
 * This class contains some useful math tools.
 * 
 * @author Matthias Thimm
 */
public class MathTools {

	/**
	 * This method computes "n choose k". If n < 0 or k < 0
	 * the result is defined to be 0.
	 * @param n an integer.
	 * @param k an integer
	 * @return the value of "n choose k".
	 */
	public static Integer binomial(Integer n, Integer k){
		if(n < 0 || k < 0)
			return 0;
		if(n < k) return 0;
		if(k == 0) return 1;
		if(n == 0) return 0;		
		return MathTools.binomial(n-1,k-1) + MathTools.binomial(n-1,k);		
	}
	
	/**
	 * Computes the faculty of the given number.
	 * @param i an integer.
	 * @return the value 1*...*i or 1 if i==0.
	 */
	public static int faculty(int i){
		if(i < 0) return 0;
		if(i == 0) return 1;
		int result = 1;
		for(int j = 2; j <= i; j++)
			result *= j;
		return result;
	}
}

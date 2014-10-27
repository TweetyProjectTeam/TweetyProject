package net.sf.tweety.math.func;

/**
 * This is the function 1-\sum_i (1-x_i/i) used e.g in 
 * [Mu,Liu,Jin, Bell. A syntax-based approach to measuring the degree of inconsistency for belief bases. IJAR 52(7), 2011.]
 * 
 * @author Matthias Thimm
 *
 */
public class FracAggrFunction implements SimpleFunction<double[],Double>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.SimpleFunction#eval(java.lang.Object)
	 */
	@Override
	public Double eval(double[] x) {
		double result = 1;
		int i = 1;
		for(Double xi: x){
			result *= 1-xi/i;
			i++;
		}
		return 1-result;
	}

}

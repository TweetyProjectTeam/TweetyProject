package net.sf.tweety.math.func;

/**
 * The maximum function.
 * @author Matthias Thimm
 */
public class MaxFunction implements BinaryFunction<Double,Double,Double>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.BinaryFunction#eval(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double eval(Double val1, Double val2) {
		return Math.max(val1, val2);
	}

}

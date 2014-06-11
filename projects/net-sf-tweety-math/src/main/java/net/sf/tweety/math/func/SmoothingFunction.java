package net.sf.tweety.math.func;

/**
 * A function that smoothes two values with a smooting factor, i.e.
 * given a smoothing factor X and two values y1, y2 it returns X * y1 + (1-X) * y2.
 * @author Matthias Thimm
 */
public class SmoothingFunction implements BinaryFunction<Double,Double,Double>{

	/** The smoothing factor. */
	private double factor;
	
	/**
	 * Creates a new smoothing function with the given factor.
	 * @param factor some smoothing factor.
	 */
	public SmoothingFunction(double factor){
		this.factor = factor;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.BinaryFunction#eval(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Double eval(Double val1, Double val2) {
		return this.factor * val1 + (1-this.factor) * val2;
	}

}

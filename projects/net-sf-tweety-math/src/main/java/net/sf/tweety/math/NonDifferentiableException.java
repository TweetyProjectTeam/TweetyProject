package net.sf.tweety.math;

/**
 * This exception is thrown when a non-differentiable term
 * is attempted to be differentiated.
 * 
 * @author Matthias Thimm
 */
public class NonDifferentiableException extends GeneralMathException {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new NonDifferentiableException.
	 */
	public NonDifferentiableException(){
		super();
	}
	
	/**
	 * Creates a new NonDifferentiableException.
	 * @param s an error message
	 */
	public NonDifferentiableException(String s){
		super(s);
	}
}

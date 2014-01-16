package net.sf.tweety.math;

/**
 * A complex number.
 * 
 * @author Matthias Thimm
 */
public class ComplexNumber extends Number {

	/** The real part of this complex number. */
	private double realPart;
	/** The imaginary part of this complex number. */
	private double imagPart;
	
	/**
	 * Constructs a new complex number with the given real
	 * and imaginary parts.
	 * @param real the real part.
	 * @param imag the imaginary part.
	 */
	public ComplexNumber(double real, double imag){
		this.realPart = real;
		this.imagPart = imag;
	}
	
	/** For serialization. */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		return this.realPart;
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return new Float(this.realPart);
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {		
		return new Integer((int)Math.round(this.realPart));
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return Math.round(this.realPart);
	}
	
	/** Returns the real part of this complex number.
	 * @return the real part of this complex number.
	 */
	public double getRealPart(){
		return this.realPart;
	}
	
	/** Returns the imaginary part of this complex number.
	 * @return the imaginary part of this complex number.
	 */
	public double getImagPart(){
		return this.imagPart;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.realPart + "+" + this.imagPart + "i";
	}

}

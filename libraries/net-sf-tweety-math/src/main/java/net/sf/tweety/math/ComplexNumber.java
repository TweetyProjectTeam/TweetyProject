/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
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
		return (float)this.realPart;
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {		
		return (int)Math.round(this.realPart);
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

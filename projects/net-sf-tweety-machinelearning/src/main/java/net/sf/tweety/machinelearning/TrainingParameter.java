/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.machinelearning;

/**
 * A single parameter for a training method.
 * 
 * @author Matthias Thimm
 */
public class TrainingParameter {
	
	/** The identifier of the parameter. */
	private String name;
	/** The actual value of the parameter. */
	private double value;
	/** The default value of the parameter. */
	private double defaultValue;	
	/** The upper bound of the parameter. */
	private double upperBound;
	/** The lower bound of the parameter. */
	private double lowerBound;

	/**
	 * Creates a new training parameter with the given values and actual value
	 * as default value.
	 * @param name The identifier of the parameter.
	 * @param defaultValue The default value of the parameter.
	 * @param upperBound The upper bound of the parameter.
	 * @param lowerBound The lower bound of the parameter.
	 */
	public TrainingParameter(String name,  double defaultValue, double upperBound,	double lowerBound) {		
		this(name,defaultValue,defaultValue,upperBound, lowerBound);
	}
	
	/**
	 * Creates a new training parameter with the given values.
	 * @param name The identifier of the parameter.
	 * @param value The actual value of the parameter.
	 * @param defaultValue The default value of the parameter.
	 * @param lowerBound The lower bound of the parameter.
	 * @param upperBound The upper bound of the parameter. 
	 */
	public TrainingParameter(String name, double value, double defaultValue, double lowerBound,	double upperBound) {
		if(upperBound < value || upperBound < defaultValue || lowerBound > value || lowerBound > defaultValue)
			throw new IllegalArgumentException("Illegal value of default value given bounds.");
		this.name = name;
		this.value = value;
		this.defaultValue = defaultValue;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
	
	/**
	 * Instantiates a new parameter with the given value.
	 * @param value some value
	 * @return the training parameter obtained from instantiating.
	 */
	public TrainingParameter instantiate(double value){
		if(this.lowerBound <= value && this.upperBound >= value)
			return new TrainingParameter(this.name, value, this.defaultValue, this.lowerBound, this.upperBound);
		throw new IllegalArgumentException("Value outside of accepted boundaries.");
	}
	
	/**
	 * Instantiates a new parameter with the default value.
	 * @return the training parameter obtained from instantiating.
	 */
	public TrainingParameter instantiateWithDefaultValue(){
		return new TrainingParameter(this.name, this.defaultValue, this.defaultValue, this.lowerBound, this.upperBound);		
	}
	
	/**
	 * Returns the name of this parameter.
	 * @return The name of this parameter.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Returns the actual value of this parameter.
	 * @return The actual value of this parameter.
	 */
	public double getValue(){
		return this.value;
	}

	/**
	 * Returns the default value of this parameter.
	 * @return The default value of this parameter.
	 */
	public double getDefaultValue(){
		return this.defaultValue;
	}
	
	/**
	 * Returns the lower bound of this parameter.
	 * @return The lower bound of this parameter.
	 */
	public double getLowerBound(){
		return this.lowerBound;
	}
	
	/**
	 * Returns the upper bound of this parameter.
	 * @return The upper bound of this parameter.
	 */
	public double getUpperBound(){
		return this.upperBound;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "<" + this.name + "," + this.value + "," + this.defaultValue + "," + this.lowerBound + "," + this.upperBound + ">";
	}
}

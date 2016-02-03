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
package net.sf.tweety.plugin.parameter;

import net.sf.tweety.math.Interval;

/**
 * This parameter holds a number-interval of possible arguments
 * 
 * @author Bastian Wolf
 * 
 */

public class IntegerCommandParameter extends CommandParameter {

	/**
	 * all possible values for this parameter's argument
	 */
	private Interval<Integer> interval; 
	
	/**
	 * the value each instantiated needs, has to be in selections
	 */
	private Integer value;

	public IntegerCommandParameter(String id, String des) {
		super(id, des);
	}
	
	public IntegerCommandParameter(String id, String des, Interval<Integer> interval) {
		super(id, des);
		this.interval = interval;
	}
	
	public IntegerCommandParameter(String id, String des, String interval) {
		super(id, des);
		setInterval(interval);
	}

	/**
	 * sets new selection parameter
	 * @param interval
	 */
	public void setInterval(String interval){
		
	}
	
	/**
	 * returns each possible selection argument
	 * @return each possible selection argument
	 */
	public Interval<Integer> getInterval(){
		return interval;
	}
	
	/**
	 * returns the given instantiation argument value for this parameter
	 * @return the given instantiation argument value for this parameter
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * sets the instantiated parameter argument value,
	 * value has to be one of the options contained in selections
	 * @param value the value given as argument value
	 */
	public void setValue(Integer value) {
		this.value = value;
	}
	
	/**
	 * checks whether a cli input parameter argument is valid for the called command parameter
	 */
	@Override
	public boolean isValid(String s) {
		Integer in = Integer.parseInt(s);
		if(in >= interval.getLowerBound() && in <= interval.getUpperBound()){
			return true;
		}
		return false;
	}

	/**
	 * instantiates a new parameter iff the given value ist valid for this command parameter
	 */
	@Override
	public CommandParameter instantiate(String s){
		if(isValid(s)){
			IntegerCommandParameter newParameter = (IntegerCommandParameter) this.clone();
			newParameter.setValue(Integer.parseInt(s));
			return newParameter;
		}
		return null;
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 * 
	 */
	@Override
	public Object clone(){
		return new IntegerCommandParameter(this.getIdentifier(), this.getDescription(), this.getInterval());
	}

}

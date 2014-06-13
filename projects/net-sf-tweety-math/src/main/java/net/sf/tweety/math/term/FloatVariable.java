/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.math.term;

/**
 * This class models an float variable as a mathematical term.
 * @author Matthias Thimm
 */
public class FloatVariable extends Variable{
	
	/**
	 * Creates a new variable with the given name.
	 * @param name the name of this variable.
	 */
	public FloatVariable(String name){
		super(name);
	}
	
	/**
	 * Creates a new variable with the given name.
	 * @param name the name of this variable.
	 * @param isPositive whether this variables should be positive.
	 */
	public FloatVariable(String name, boolean isPositive){
		super(name,isPositive);	
	}
	
	/**
	 * Creates a new variable with the given name and bounds.
	 * @param name the name of this variable.
	 * @param lowerBound the lower bound of the variable.
	 * @param upperBound the upper bound of the variable.
	 */
	public FloatVariable(String name, double lowerBound, double upperBound){
		super(name,lowerBound,upperBound);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger(){
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Variable#hashCode()
	 */
	@Override
	public int hashCode(){
		return super.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Variable#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		return super.equals(obj);
	}
}

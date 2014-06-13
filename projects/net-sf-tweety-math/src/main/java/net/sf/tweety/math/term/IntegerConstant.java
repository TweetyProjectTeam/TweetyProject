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
 * This class encapsulates an integer as a term.
 * @author Matthias Thimm
 */
public class IntegerConstant extends Constant{
	
	/**
	 * the actual integer.
	 */	
	private int i;
	
	/**
	 * Creates a new Integer.
	 * @param i an int.
	 */
	public IntegerConstant(int i){
		this.i = i;
	}
	
	/**
	 * Get the value of this integer.
	 * @return the value of this integer.
	 */
	public int getValue(){
		return this.i;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger(){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		return String.valueOf(this.i);
	}
}

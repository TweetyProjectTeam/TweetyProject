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
 /**
 * 
 */
package net.sf.tweety.arg.aba.syntax;


import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;

/**
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * This represents a negation relation of form "not <code>formula</code> = <code>negation</code>"
 * @param <T> the type of formulas
 *
 */
public class Negation<T extends Formula> implements Formula {

	/**
	 * not <formula> = <n
	 */
	T formula, negation;
	
	
	/**
	 * Creates a new Negation
	 * @param formula	a formula
	 * @param negation	it's complement
	 */
	public Negation(T formula, T negation) {
		super();
		this.formula = formula;
		this.negation = negation;
	}
	
	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		result = prime * result + ((negation == null) ? 0 : negation.hashCode());
		return result;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Negation other = (Negation) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		if (negation == null) {
			if (other.negation != null)
				return false;
		} else if (!negation.equals(other.negation))
			return false;
		return true;
	}




	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Formula#getSignature()
	 */
	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "not "+formula + " = " + negation ;
	}
	
	

}

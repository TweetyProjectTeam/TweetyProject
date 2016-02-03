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
package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.Set;

/**
 * This class models the Tptp-Fof-ExistsQuantifiedFormula
 * Format:
 * ExistsQuantifiedFormula := <quantifier> [<variables>] : <formula>
 * @author Bastian Wolf
 */
public class TptpFofExistsQuantifiedFormula extends TptpFofQuantifiedFormula {

	/**
	 * Static exists quantifier 
	 */
	private static String quantifier = TptpFofLogicalSymbols.EXISTSQUANTIFIER();

	/**
     *	
     */
	private Set<TptpFofVariable> variables;

	/**
     *	
     */
	private TptpFofFormula formula;

	/**
	 * Constructor
	 * @param variables
	 * @param formula
	 */
	public TptpFofExistsQuantifiedFormula(Set<TptpFofVariable> variables,
			TptpFofFormula formula) {
		super(variables, formula);

	}

	/*
	 *	Getter 
	 */
	public Set<TptpFofVariable> getVariables() {
		return variables;
	}

	public String getQuantifier() {
		return quantifier;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("static-access")
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TptpFofExistsQuantifiedFormula that = (TptpFofExistsQuantifiedFormula) o;

		if (!quantifier.equals(that.quantifier))
			return false;
		if (!variables.equals(that.variables))
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = quantifier.hashCode();
		result = 31 * result + variables.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofLogicFormula#toString()
	 */
	@Override
	public String toString() {
		return quantifier + " [" + this.variables.toString() + "] : "
				+ this.formula.toString();
	}

	@Override
	public boolean isParenthesized() {
		// TODO Auto-generated method stub
		return false;
	}

}

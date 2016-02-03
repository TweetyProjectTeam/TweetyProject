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


/**
 * Negation for a given formula
 * @author Bastian Wolf
 */
public class TptpFofNegation extends TptpFofUnitaryFormula {
	/**
	 * The actual formula 
	 */
    private TptpFofFormula formula;

    /**
     * Static negation symbol
     */
    private static String negation = TptpFofLogicalSymbols.TPTP_NEGATION();

    /*
     * (non-Javadoc)
     * @see net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofFormula#toString()
     */
    @Override
    public String toString() {
        return negation + this.formula.toString();
    }

	@Override
	public boolean isParenthesized() {
		// TODO Auto-generated method stub
		return false;
	}
}

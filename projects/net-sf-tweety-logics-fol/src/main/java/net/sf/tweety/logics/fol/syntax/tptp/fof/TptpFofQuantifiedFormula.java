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
 * The abstract class for quantified formulas
 * @author Bastian Wolf
 */
public abstract class TptpFofQuantifiedFormula extends TptpFofUnitaryFormula {


    /**
     *	Variables used in this formula
     */
    private Set<TptpFofVariable> variables;


    /**
     *	the actual formula
     */
    private TptpFofFormula formula;


    /**
     * 
     * @param variables
     * @param formula
     */
	public TptpFofQuantifiedFormula(
			Set<TptpFofVariable> variables, TptpFofFormula formula) {
		super();
		this.variables = variables;
		this.formula = formula;
	}

	/*
	 * Getter
	 */

	public Set<TptpFofVariable> getVariables() {
		return variables;
	}

	public TptpFofFormula getFormula() {
		return formula;
	}
    
	/*
	 * 
	 */
	public boolean isQuantifiedFormula(){
		return true;
	}
    
}

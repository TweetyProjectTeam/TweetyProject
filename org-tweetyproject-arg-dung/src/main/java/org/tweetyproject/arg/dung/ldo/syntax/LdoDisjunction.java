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
package org.tweetyproject.arg.dung.ldo.syntax;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.logics.commons.LogicalSymbols;

/**
 * This class represents a disjunction in ldo logic.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class LdoDisjunction extends LdoAssociativeFormula {
	
	/**
	 * Creates a new disjunction with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public LdoDisjunction(Collection<? extends LdoFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Creates a new (empty) disjunction.
	 */
	public LdoDisjunction(){
		this(new HashSet<LdoFormula>());
	}
	
	/**
	 * Creates a new disjunction with the two given formulae
	 * @param first a propositional formula.
	 * @param second a propositional formula.
	 */
	public LdoDisjunction(LdoFormula first, LdoFormula second){
		this();
		this.add(first);
		this.add(second);
	}
	 
	@Override
	public LdoFormula clone() {
		return new LdoDisjunction(support.copyHelper(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public LdoDisjunction createEmptyFormula() {
		return new LdoDisjunction();
	}

	@Override
	public String getOperatorSymbol() {
		return LogicalSymbols.DISJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.TAUTOLOGY();
	}
}

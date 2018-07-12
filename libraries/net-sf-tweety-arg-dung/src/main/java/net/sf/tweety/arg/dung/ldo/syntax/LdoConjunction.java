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
package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.logics.commons.LogicalSymbols;

/**
 * This class represents a conjunction in ldo logic.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class LdoConjunction extends LdoAssociativeFormula {
		
	/**
	 * Creates a new conjunction with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public LdoConjunction(Collection<? extends LdoFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Creates a new (empty) conjunction.
	 */
	public LdoConjunction(){
		this(new HashSet<LdoFormula>());
	}
	
	/**
	 * Creates a new conjunction with the two given formulae
	 * @param first a ldo formula.
	 * @param second a ldo formula.
	 */
	public LdoConjunction(LdoFormula first, LdoFormula second){
		this();
		this.add(first);
		this.add(second);
	}	

	@SuppressWarnings("unchecked")
	@Override
	public LdoConjunction createEmptyFormula() {
		return new LdoConjunction();
	}

	@Override
	public String getOperatorSymbol() {
		return LogicalSymbols.CONJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.CONTRADICTION();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.ldo.syntax.LdoFormula#clone()
	 */
	@Override
	public LdoConjunction clone() {
		return new LdoConjunction(support.copyHelper(this));
	}

}

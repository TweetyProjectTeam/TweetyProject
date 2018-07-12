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

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.syntax.DungSignature;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;

/**
 * This class models classical negation of ldo logic.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class LdoNegation extends LdoFormula {

	/**
	 * The formula within this negation.
	 */
	private LdoFormula formula;
	
	/**
	 * Creates a new negation with the given formula.
	 * @param formula the formula within the negation.
	 */
	public LdoNegation(LdoFormula formula){
		this.formula = formula;	
	}
	
	/**
	 * Returns the formula within this negation.
	 * @return the formula within this negation.
	 */
	public LdoFormula getFormula(){
		return this.formula;
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(this.formula instanceof LdoAssociativeFormula || this.formula instanceof LdoNegation)			
			return LogicalSymbols.CLASSICAL_NEGATION() + "(" + this.formula + ")";
		return LogicalSymbols.CLASSICAL_NEGATION() + this.formula;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LdoNegation other = (LdoNegation) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

	@Override
	public Set<PropositionalPredicate> getPredicates() {
		return formula.getPredicates();
	}

	@Override
	public LdoFormula clone() {
		return new LdoNegation(formula.clone());
	}

	@Override
	public Set<LdoArgument> getAtoms() {
		return formula.getAtoms();
	}

	@Override
	public boolean isLiteral() {
		return (formula instanceof LdoArgument);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#getLiterals()
	 */
	@Override
	public Set<LdoFormula> getLiterals(){
		Set<LdoFormula> result = new HashSet<LdoFormula>();
		if(this.isLiteral())			
			result.add(this);
		return result;
	}
	
	@Override
	public DungSignature getSignature() {
		return formula.getSignature();
	}
}


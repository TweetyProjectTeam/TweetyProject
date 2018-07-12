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
package net.sf.tweety.logics.pl.syntax;

import java.util.HashSet;
import java.util.Set;

/**
 * This class captures the common functionalities of the special
 * formulas tautology and contradiction.
 * 
 * @author Matthias Thimm
 */
public abstract class SpecialFormula extends PropositionalFormula {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#collapseAssociativeFormulas()
	 */
	@Override
	public PropositionalFormula collapseAssociativeFormulas(){
		return this;
	}
	
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		return new HashSet<PropositionalPredicate>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
	 */
	@Override
	public PropositionalFormula toNnf() {
		return this;
	}
	
	@Override
	public Set<Proposition> getAtoms() {
		return new HashSet<Proposition>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#getLiterals()
	 */
	@Override
	public Set<PropositionalFormula> getLiterals(){
		return new HashSet<PropositionalFormula>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {
		Conjunction conj = new Conjunction();
		Disjunction disj = new Disjunction();
		disj.add(this);
		conj.add(disj);
		return conj;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#numberOfOccurrences(net.sf.tweety.logics.pl.syntax.Proposition)
	 */
	public int numberOfOccurrences(Proposition p){
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#replace(net.sf.tweety.logics.pl.syntax.Proposition, net.sf.tweety.logics.pl.syntax.PropositionalFormula, int)
	 */
	public PropositionalFormula replace(Proposition p, PropositionalFormula f, int i){
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#trim()
	 */
	public PropositionalFormula trim(){
		return this;
	}
}


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
package org.tweetyproject.logics.pl.syntax;

import java.util.HashSet;
import java.util.Set;

/**
 * This class captures the common functionalities of the special
 * formulas tautology and contradiction.
 * 
 * @author Matthias Thimm
 */
public abstract class SpecialFormula extends PlFormula {

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#collapseAssociativeFormulas()
	 */
	@Override
	public PlFormula collapseAssociativeFormulas(){
		return this;
	}
	
	@Override
	public Set<PlPredicate> getPredicates() {
		return new HashSet<PlPredicate>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
	 */
	@Override
	public PlFormula toNnf() {
		return this;
	}
	
	@Override
	public Set<Proposition> getAtoms() {
		return new HashSet<Proposition>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#getLiterals()
	 */
	@Override
	public Set<PlFormula> getLiterals(){
		return new HashSet<PlFormula>();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
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
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#numberOfOccurrences(org.tweetyproject.logics.pl.syntax.Proposition)
	 */
	public int numberOfOccurrences(Proposition p){
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#replace(org.tweetyproject.logics.pl.syntax.Proposition, org.tweetyproject.logics.pl.syntax.PropositionalFormula, int)
	 */
	public PlFormula replace(Proposition p, PlFormula f, int i){
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#trim()
	 */
	public PlFormula trim(){
		return this;
	}

    /** Default Constructor */
    public SpecialFormula(){}
}


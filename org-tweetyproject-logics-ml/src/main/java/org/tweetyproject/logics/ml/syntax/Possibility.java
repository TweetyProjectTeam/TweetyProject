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
package org.tweetyproject.logics.ml.syntax;

import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;

/**
 * This class models the possibility modality.
 * @author Matthias Thimm
 */
public class Possibility extends MlFormula {

	/**
	 * Creates a new possibility formula with the
	 * given inner formula.
	 * @param formula a formula, either a modal formula or a first-order formula.
	 */
	public Possibility(RelationalFormula formula){
		super(formula);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#substitute(org.tweetyproject.logics.firstorderlogic.syntax.Term, org.tweetyproject.logics.firstorderlogic.syntax.Term)
	 */
	public FolFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException{
		return new Possibility(this.getFormula().substitute(v, t));
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#toString()
	 */
	public String toString(){
		return "<>("+this.getFormula()+")";
	}

	@Override
	public FolFormula clone() {
		return new Possibility(this.getFormula().clone());
	}

	@Override
	public FolFormula toNnf() {
		throw new UnsupportedOperationException("NNF is not supported for modal formulas.");
	}

	@Override
	public RelationalFormula collapseAssociativeFormulas() {
		if (this.getFormula() instanceof FolFormula) 
			return new Possibility(((FolFormula)this.getFormula()).collapseAssociativeFormulas());
		else
			throw new IllegalArgumentException(this.getFormula() + " is not of type FolFormula");
	}

	@Override
	public boolean isDnf() {
		throw new UnsupportedOperationException("DNF is not supported for modal formulas.");
	}
}

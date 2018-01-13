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
package net.sf.tweety.logics.el.syntax;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;

/**
 * This class models the necessity modality.
 * @author Matthias Thimm
 */
public class Necessity extends ModalFormula {

	/**
	 * Creates a new necessity formula with the
	 * given inner formula
	 * @param formula a formula, either a modal formula or a first-order formula.
	 */
	public Necessity(RelationalFormula formula){
		super(formula);
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	public FolFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException{
		return new Necessity(this.getFormula().substitute(v, t));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#toString()
	 */
	public String toString(){
		return "[]("+this.getFormula()+")";
	}


	@Override
	public FolFormula clone() {
		return new Necessity(this.getFormula().clone());
	}

	@Override
	public FolFormula toNnf() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}

	@Override
	public RelationalFormula collapseAssociativeFormulas() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}

	@Override
	public boolean isDnf() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}
}

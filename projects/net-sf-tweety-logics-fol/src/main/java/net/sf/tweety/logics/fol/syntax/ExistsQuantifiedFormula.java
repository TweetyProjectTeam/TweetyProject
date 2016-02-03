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
package net.sf.tweety.logics.fol.syntax;

import java.util.*;

import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

public class ExistsQuantifiedFormula extends QuantifiedFormula {
	
	/**
	 * Creates a new exists-quantified formula with the given formula and variables.
	 * @param folFormula the formula this for-all-quantified formula ranges over.
	 * @param variables the variables of this for-all-quantified formula.
	 */
	public ExistsQuantifiedFormula(RelationalFormula folFormula, Set<Variable> variables){
		super(folFormula,variables);		
	}
	
	/**
	 * Creates a new exists-quantified formula with the given formula and variable.
	 * @param folFormula the formula this for-all-quantified formula ranges over.
	 * @param variable the variable of this for-all-quantified formula.
	 */
	public ExistsQuantifiedFormula(FolFormula folFormula, Variable variable){
		super(folFormula,variable);
	}	
	
	public ExistsQuantifiedFormula(ExistsQuantifiedFormula other) {
		super(other.getFormula(), other.getQuantifierVariables());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public ExistsQuantifiedFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException{
		if(this.getQuantifierVariables().contains(v))
			return new ExistsQuantifiedFormula(this.getFormula(),this.getQuantifierVariables());
		return new ExistsQuantifiedFormula(this.getFormula().substitute(v, t),this.getQuantifierVariables());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toNNF()
	 */
  @Override
  public FolFormula toNnf() {
    return new ExistsQuantifiedFormula( getFormula().toNnf(), getQuantifierVariables() );
  }
	
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#collapseAssociativeFormulas()
   */
  @Override
  public FolFormula collapseAssociativeFormulas() {
    return new ExistsQuantifiedFormula( this.getFormula().collapseAssociativeFormulas(), this.getQuantifierVariables() );
  }
  
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toString()
	 */
	@Override
	public String toString(){
		String s = LogicalSymbols.EXISTSQUANTIFIER() + " ";
		Iterator<Variable> it = this.getQuantifierVariables().iterator();
		if(it.hasNext())
			s += it.next();
		while(it.hasNext())
			s += "," + it.next();
		s += ": (" + this.getFormula() + ")";
		return s;
	}

	@Override
	public ExistsQuantifiedFormula clone() {
		return new ExistsQuantifiedFormula(this);
	}
}

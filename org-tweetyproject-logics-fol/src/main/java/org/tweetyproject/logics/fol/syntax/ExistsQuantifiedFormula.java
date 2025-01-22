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
package org.tweetyproject.logics.fol.syntax;

import java.util.*;

import org.tweetyproject.logics.commons.LogicalSymbols;
import org.tweetyproject.logics.commons.syntax.Functor;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.QuantifiedFormulaSupport;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;

/**
 * Exists-quantified first-order logic formula.
 * Delegates to QuantifiedFormulaSupport for shared functionalities with other quantified formulas.
 *
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public class ExistsQuantifiedFormula extends FolFormula {

	/**
	 * This helper class implements common functionalities of quantified
	 * formulas, meaning the implementation can delegate the method calls to the support
	 * class.
	 */
	protected QuantifiedFormulaSupport<FolFormula> support;

	/**
	 * Creates a new quantified folFormula with the given folFormula and variables.
	 * @param folFormula the folFormula this quantified folFormula ranges over.
	 * @param variables the variables of this quantified folFormula.
	 */
	public ExistsQuantifiedFormula(RelationalFormula folFormula, Set<Variable> variables){
		if(!(folFormula instanceof FolFormula))
			throw new IllegalArgumentException("Formula must be first-order formula.");
		support = new QuantifiedFormulaSupport<FolFormula>((FolFormula)folFormula, variables);
		if(!this.isWellFormed()) throw new IllegalArgumentException("FolFormula not well-formed.");
	}

	/**
	 * Creates a new quantified folFormula with the given folFormula and variable.
	 * @param folFormula the folFormula this quantified folFormula ranges over.
	 * @param variable the variable of this quantified folFormula.
	 */
	public ExistsQuantifiedFormula(RelationalFormula folFormula, Variable variable){
		if(!(folFormula instanceof FolFormula))
			throw new IllegalArgumentException("Formula must be first-order formula.");
		Set<Variable> variables = new HashSet<Variable>();
		variables.add(variable);
		support = new QuantifiedFormulaSupport<FolFormula>((FolFormula) folFormula, variables);
		if(!this.isWellFormed()) throw new IllegalArgumentException("FolFormula not well-formed.");
	}
	/**
	 *
	 * Formula to be constructed
	 * @param other formula to be constructed
	 */
	public ExistsQuantifiedFormula(ExistsQuantifiedFormula other) {
		if(!(other.getFormula() instanceof FolFormula))
			throw new IllegalArgumentException("Formula must be first-order formula.");
		support = new QuantifiedFormulaSupport<FolFormula>(other.getFormula(), other.getQuantifierVariables());
		if(!this.isWellFormed()) throw new IllegalArgumentException("FolFormula not well-formed.");
	}

	/**
	 * Return quantified formulas
	 * @return formulas
	 */
	public Set<FolFormula> getQuantifiedFormulas(){
		Set<FolFormula> qf = new HashSet<FolFormula>();
		if(this.getFormula() instanceof AssociativeFolFormula) {
			AssociativeFolFormula af = ((AssociativeFolFormula) this.getFormula());
			qf.addAll(af.getFormulas(ForallQuantifiedFormula.class));
			qf.addAll(af.getFormulas(ExistsQuantifiedFormula.class));
		}
		qf.add(this);
		return qf;
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
		ExistsQuantifiedFormula other = (ExistsQuantifiedFormula) obj;
		if (this.getFormula() == null) {
			if (other.getFormula() != null)
				return false;
		} else if (!this.getFormula().equals(other.getFormula()))
			return false;
		if (this.getQuantifierVariables() == null) {
			if (other.getQuantifierVariables() != null)
				return false;
		} else if (!this.getQuantifierVariables().equals(other.getQuantifierVariables()))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#toNNF()
	 */
	@Override
	public FolFormula toNnf() {
		return new ExistsQuantifiedFormula( getFormula().toNnf(), getQuantifierVariables() );
	}

	  /*
	   * (non-Javadoc)
	   * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#collapseAssociativeFormulas()
	   */
	  @Override
	  public FolFormula collapseAssociativeFormulas() {
	    return new ExistsQuantifiedFormula( (FolFormula) this.getFormula().collapseAssociativeFormulas(), this.getQuantifierVariables() );
	  }


	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.RelationalFormula#substitute(org.tweetyproject.logics.firstorderlogic.syntax.Term, org.tweetyproject.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public ExistsQuantifiedFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException{
		if(this.getQuantifierVariables().contains(v))
			return new ExistsQuantifiedFormula(this.getFormula(),this.getQuantifierVariables());
		return new ExistsQuantifiedFormula(this.getFormula().substitute(v, t),this.getQuantifierVariables());
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#toString()
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

	//-------------------------------------------------------------------------
	//	METHODS IMPLEMENTED IN QuantifiedFormulaSupport:
	//-------------------------------------------------------------------------

	/**
	 * returns formula
	 */
	public FolFormula getFormula() {
		return support.getFormula();
	}
	/**
	 * returns all variables
	 */
	public Set<Variable> getQuantifierVariables() {
		return support.getQuantifierVariables();
	}
	/**
	 * set formula
	 * @param formula formulas to be set
	 */
	public void setFormula(FolFormula formula) {
		support.setFormula(formula);
	}
	/**
	 * sets variables
	 * @param variables variables to be set
	 */
	public void setQuantifierVariables(Set<Variable> variables) {
		support.setQuantifierVariables(variables);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Set<FolAtom> getAtoms() {
		return (Set<FolAtom>) support.getAtoms();
	}

	@Override
	public Set<Functor> getFunctors() {
		return support.getFunctors();
	}


	@Override
	public boolean isDnf() {
		return support.isDnf();
	}


	@Override
	public Set<? extends Predicate> getPredicates() {
		return support.getPredicates();
	}

	@Override
	public boolean isLiteral() {
		return support.isLiteral();
	}

	@Override
	public Set<Variable> getUnboundVariables() {
		return support.getUnboundVariables();
	}

	@Override
	public boolean containsQuantifier() {
		return support.containsQuantifier();
	}

	@Override
	public boolean isWellBound() {
		return support.isWellBound();
	}

	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		return support.isWellBound(boundVariables);
	}

	@Override
	public boolean isClosed() {
		return support.isClosed();
	}

	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		return support.isClosed(boundVariables);
	}

	@Override
	public Set<Term<?>> getTerms() {
		return support.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return support.getTerms(cls);
	}
}

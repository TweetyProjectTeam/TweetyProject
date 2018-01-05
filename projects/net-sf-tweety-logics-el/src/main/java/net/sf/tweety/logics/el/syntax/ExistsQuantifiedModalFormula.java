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

import java.util.*;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.QuantifiedFormulaSupport;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.AssociativeFOLFormula;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.math.probability.Probability;

/**
 * Exists-quantified modal formula.
 * Delegates to QuantifiedFormulaSupport for shared functionalities with other quantified formulas.
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public class ExistsQuantifiedModalFormula extends FolFormula {
	
	/**
	 * This helper class implements common functionalities of quantified
	 * formulas, meaning the implementation can delegate the method calls to the support
	 * class. 
	 */
	protected QuantifiedFormulaSupport<RelationalFormula> support;

	/**
	 * Creates a new quantified relational formula with the given formula and variables.
	 * @param formula the ModalFormula or FolFormula this quantified formula ranges over.
	 * @param variables the variables of this quantified formula.
	 */
	public ExistsQuantifiedModalFormula(RelationalFormula formula, Set<Variable> variables){
		if(!(formula instanceof FolFormula || formula instanceof ModalFormula))
			throw new IllegalArgumentException("Formula must be first-order or modal formula.");
		support = new QuantifiedFormulaSupport<RelationalFormula>(formula, variables);
		if(!this.isWellFormed()) throw new IllegalArgumentException("Formula not well-formed.");
	}
	
	/**
	 * Creates a new quantified formula with the given formula and variable.
	 * @param formula the ModalFormula or FolFormula this quantified formula ranges over.
	 * @param variable the variable of this quantified formula.
	 */
	public ExistsQuantifiedModalFormula(RelationalFormula formula, Variable variable){
		if(!(formula instanceof FolFormula || formula instanceof ModalFormula))
			throw new IllegalArgumentException("Formula must be first-order or modal formula.");
		Set<Variable> variables = new HashSet<Variable>();
		variables.add(variable);
		support = new QuantifiedFormulaSupport<RelationalFormula>(formula, variables);
		if(!this.isWellFormed()) throw new IllegalArgumentException("Formula not well-formed.");
	}
	
	public ExistsQuantifiedModalFormula(ExistsQuantifiedModalFormula other) {
		if(!(other.getFormula() instanceof FolFormula || other.getFormula() instanceof ModalFormula))
			throw new IllegalArgumentException("Formula must be first-order or modal formula.");
		support = new QuantifiedFormulaSupport<RelationalFormula>(other.getFormula(), other.getQuantifierVariables());
		if(!this.isWellFormed()) throw new IllegalArgumentException("Formula not well-formed.");
	}
	
	public Set<RelationalFormula> getQuantifiedFormulas(){
		Set<RelationalFormula> qf = new HashSet<RelationalFormula>();
		if(this.getFormula() instanceof AssociativeFOLFormula) {
			AssociativeFOLFormula af = ((AssociativeFOLFormula) this.getFormula());
			qf.addAll(af.getFormulas(ForallQuantifiedModalFormula.class));
			qf.addAll(af.getFormulas(ExistsQuantifiedModalFormula.class));
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
		ExistsQuantifiedModalFormula other = (ExistsQuantifiedModalFormula) obj;
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public ExistsQuantifiedModalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException{
		if(this.getQuantifierVariables().contains(v))
			return new ExistsQuantifiedModalFormula(this.getFormula(),this.getQuantifierVariables());
		return new ExistsQuantifiedModalFormula(this.getFormula().substitute(v, t),this.getQuantifierVariables());
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
	public ExistsQuantifiedModalFormula clone() {
		return new ExistsQuantifiedModalFormula(this);
	}
	
	//-------------------------------------------------------------------------
	//	METHODS IMPLEMENTED IN QuantifiedFormulaSupport:
	//-------------------------------------------------------------------------
	
	public RelationalFormula getFormula() {
		return support.getFormula();
	}
	
	public Set<Variable> getQuantifierVariables() {
		return support.getQuantifierVariables();
	}
	
	public void setFormula(RelationalFormula formula) {
		support.setFormula(formula);
	}
	
	public void setQuantifierVariables(Set<Variable> variables) {
		support.setQuantifierVariables(variables);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<FOLAtom> getAtoms() {
		return (Set<FOLAtom>) support.getAtoms();
	}

	@Override
	public Set<Functor> getFunctors() {
		return support.getFunctors();
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

	@Override
	public Probability getUniformProbability() {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}


	@Override
	public RelationalFormula complement() {
		return new Negation(this);
	}

	@Override
	public Disjunction combineWithOr(Disjunctable f) {
		if(!(f instanceof ModalFormula || f instanceof FolFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a FolFormula or ModalFormula.");
		return new Disjunction(this,(RelationalFormula)f);
	}

	@Override
	public Conjunction combineWithAnd(Conjuctable f) {
		if(!(f instanceof ModalFormula || f instanceof FolFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a FolFormula or ModalFormula.");
		return new Conjunction(this,(RelationalFormula) f);
	}

	@Override
	public FolFormula toNnf() {
		if (this.getFormula() instanceof FolFormula) {
			return new ExistsQuantifiedModalFormula( ((FolFormula) this.getFormula()).toNnf(), getQuantifierVariables() );
		}
		else {
			throw new UnsupportedOperationException("IMPLEMENT ME");
		}

	}

	@Override
	public RelationalFormula collapseAssociativeFormulas() {
		if (this.getFormula() instanceof FolFormula) {
			 return new ExistsQuantifiedModalFormula( ((FolFormula) this.getFormula()).collapseAssociativeFormulas(), this.getQuantifierVariables() );
		}
		else {
			throw new UnsupportedOperationException("IMPLEMENT ME");
		}
	}

	@Override
	public boolean isDnf() {
		return support.isDnf();
	}
}

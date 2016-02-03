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

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * The common parent of exists and forall quantified formulas, which contains common
 * functionalities.
 * 
 * @author Matthias Thimm
 */
public abstract class QuantifiedFormula extends FolFormula {
	
	/**
	 * The folFormula this quantified folFormula ranges over. 
	 */
	private FolFormula folFormula;
	
	/**
	 * The variables of this quantified folFormula.
	 */
	private Set<Variable> quantifier_variables;
		
	/**
	 * Creates a new quantified folFormula with the given folFormula and variables.
	 * @param folFormula the folFormula this quantified folFormula ranges over.
	 * @param variables the variables of this quantified folFormula.
	 */
	public QuantifiedFormula(RelationalFormula folFormula, Set<Variable> variables){
		if(!(folFormula instanceof FolFormula))
			throw new IllegalArgumentException("Formula must be first-order formula.");
		this.folFormula = (FolFormula)folFormula;
		this.quantifier_variables = new HashSet<Variable>(variables);
		if(!this.isWellFormed()) throw new IllegalArgumentException("FolFormula not well-formed.");
	}
	
	/**
	 * Creates a new quantified folFormula with the given folFormula and variable.
	 * @param folFormula the folFormula this quantified folFormula ranges over.
	 * @param variable the variable of this quantified folFormula.
	 */
	public QuantifiedFormula(FolFormula folFormula, Variable variable){
		Set<Variable> variables = new HashSet<Variable>();
		variables.add(variable);
		this.folFormula = folFormula;
		this.quantifier_variables = variables;
		if(!this.isWellFormed()) throw new IllegalArgumentException("FolFormula not well-formed.");
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isClosed()
	 */
	@Override
	public boolean isClosed(){		
		return this.folFormula.isClosed(this.quantifier_variables);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isClosed(java.util.Set)
	 */
	@Override
	public boolean isClosed(Set<Variable> boundVariables){
		Set<Variable> variables = new HashSet<Variable>(this.quantifier_variables);
		variables.addAll(boundVariables);
		return this.folFormula.isClosed(variables);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#containsQuantifier()
	 */
	@Override
	public boolean containsQuantifier(){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isWellBound()
	 */
	@Override
	public boolean isWellBound(){
		return this.folFormula.isWellBound(this.quantifier_variables);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isWellBound(java.util.Set)
	 */
	@Override
	public boolean isWellBound(Set<Variable> boundVariables){
		Set<Variable> intersection = new HashSet<Variable>(this.quantifier_variables);
		intersection.retainAll(boundVariables);		
		if(!intersection.isEmpty()) return false;		
		Set<Variable> variables = new HashSet<Variable>(this.quantifier_variables);
		variables.addAll(boundVariables);		
		return this.folFormula.isWellBound(variables);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getPredicates()
	 */
	@Override
	public Set<? extends Predicate> getPredicates(){
		return this.folFormula.getPredicates();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getFunctors()
	 */
	@Override
	public Set<Functor> getFunctors(){
		return this.folFormula.getFunctors();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getAtoms()
	 */
	@Override
	public Set<FOLAtom> getAtoms(){
		return this.folFormula.getAtoms();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getUnboundVariables()
	 */
	@Override
	public Set<Variable> getUnboundVariables(){
	/*	Set<Variable> variables = this.getTerms(Variable.class);
		variables.removeAll(this.quantifier_variables);
		return variables;*/
		Set<Variable> variables = folFormula.getUnboundVariables();
		variables.removeAll(this.quantifier_variables);
		return variables;
	}
	
	/**
	 * Returns the folFormula this quantified folFormula ranges over
	 * @return the folFormula this quantified folFormula ranges over
	 */
	@Override
	public FolFormula getFormula(){
		return this.folFormula;
	}
	
	/**
	 * Returns the variables of this quantified folFormula.
	 * @return the variables of this quantified folFormula.
	 */
	@Override
	public Set<Variable> getQuantifierVariables(){
		return new HashSet<Variable>(this.quantifier_variables);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#getQuantifiedFormulas()
	 */
	public Set<QuantifiedFormula> getQuantifiedFormulas(){
		Set<QuantifiedFormula> qf = new HashSet<QuantifiedFormula>();
		if(this.folFormula instanceof AssociativeFOLFormula) {
			AssociativeFOLFormula af = ((AssociativeFOLFormula) this.folFormula);
			qf.addAll(af.getFormulas(ForallQuantifiedFormula.class));
			qf.addAll(af.getFormulas(ExistsQuantifiedFormula.class));
		}
		qf.add(this);
		return qf;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isDnf()
	 */
	@Override
	public boolean isDnf(){
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isLiteral()
	 */
	@Override
	public boolean isLiteral(){
		return false;
	}
	
	@Override
	public Set<Term<?>> getTerms() {
		return folFormula.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return folFormula.getTerms(cls);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((folFormula == null) ? 0 : folFormula.hashCode());
		result = prime * result
				+ ((quantifier_variables == null) ? 0 : quantifier_variables.hashCode());
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
		QuantifiedFormula other = (QuantifiedFormula) obj;
		if (folFormula == null) {
			if (other.folFormula != null)
				return false;
		} else if (!folFormula.equals(other.folFormula))
			return false;
		if (quantifier_variables == null) {
			if (other.quantifier_variables != null)
				return false;
		} else if (!quantifier_variables.equals(other.quantifier_variables))
			return false;
		return true;
	}
}

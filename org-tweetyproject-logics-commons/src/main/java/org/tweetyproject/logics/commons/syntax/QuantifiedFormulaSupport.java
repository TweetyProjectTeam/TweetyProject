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
 package org.tweetyproject.logics.commons.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;

/**
 * This class provides common functionalities for quantified formulas, i.e.
 * forall and exists quantified formulas.
 * 
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 * 
 * @param <T>	The type of the formulas which are quantified.
 */

public class QuantifiedFormulaSupport<T extends RelationalFormula> {

	/**
	 * The formula this quantified formula ranges over. 
	 */
	private T innerFormula;
	
	/**
	 * The variables of this quantified folFormula.
	 */
	private Set<Variable> quantifier_variables;
	
	public QuantifiedFormulaSupport(T formula, Set<Variable> variables) {
		this.innerFormula = formula;
		this.quantifier_variables = variables;
	}
	
	/**
	 * Returns the folFormula this quantified formula ranges over
	 * @return the folFormula this quantified formula ranges over
	 */
	public T getFormula(){
		return this.innerFormula;
	}
	
	/**
	 * Returns the variables of this quantified formula.
	 * @return the variables of this quantified formula.
	 */
	public Set<Variable> getQuantifierVariables(){
		return new HashSet<Variable>(this.quantifier_variables);
	}
	
	public void setFormula(T formula) {
		this.innerFormula = formula;
	}
	
	public void setQuantifierVariables(Set<Variable> variables) {
		this.quantifier_variables = variables;
	}
	
	public boolean isClosed(){		
		return this.innerFormula.isClosed(this.quantifier_variables);		
	}
	
	public boolean isClosed(Set<Variable> boundVariables){
		Set<Variable> variables = new HashSet<Variable>(this.quantifier_variables);
		variables.addAll(boundVariables);
		return this.innerFormula.isClosed(variables);
	}
	
	public boolean containsQuantifier(){
		return true;
	}
	
	public boolean isWellBound(){
		return this.innerFormula.isWellBound(this.quantifier_variables);
	}
	
	public boolean isWellBound(Set<Variable> boundVariables){
		Set<Variable> intersection = new HashSet<Variable>(this.quantifier_variables);
		intersection.retainAll(boundVariables);		
		if(!intersection.isEmpty()) return false;		
		Set<Variable> variables = new HashSet<Variable>(this.quantifier_variables);
		variables.addAll(boundVariables);		
		return this.innerFormula.isWellBound(variables);
	}
	
	public Set<? extends Predicate> getPredicates(){
		return this.innerFormula.getPredicates();
	}
	
	public Set<Functor> getFunctors(){
		return this.innerFormula.getFunctors();
	}
	
	public Set<? extends Atom> getAtoms(){
		return innerFormula.getAtoms();
	}
	
	public Set<Variable> getUnboundVariables(){
		Set<Variable> variables = innerFormula.getUnboundVariables();
		variables.removeAll(this.quantifier_variables);
		return variables;
	}
	
	public boolean isDnf(){
		return false;
	}
	
	public boolean isLiteral(){
		return false;
	}
	
	public Set<Term<?>> getTerms() {
		return innerFormula.getTerms();
	}
	
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return innerFormula.getTerms(cls);
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((innerFormula == null) ? 0 : innerFormula.hashCode());
		result = prime * result
				+ ((quantifier_variables == null) ? 0 : quantifier_variables.hashCode());
		return result;
	}


}

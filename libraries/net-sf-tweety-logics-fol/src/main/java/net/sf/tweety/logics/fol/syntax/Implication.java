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
package net.sf.tweety.logics.fol.syntax;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * The implication of first-order logic. 
 * 
 * @author Anna Gessler
 */
public class Implication extends FolFormula {
	
	/**
	 * The pair of formulas that are part of the implication.
	 */
	private Pair<RelationalFormula,RelationalFormula> formulas;
	

	/**
	 * Creates a new implication a=&gt;b with the two given formulas
	 * @param a a relational formula.
	 * @param b a relational formula.
	 */
	public Implication(RelationalFormula a, RelationalFormula b){
		this.formulas = new Pair<RelationalFormula,RelationalFormula>(a,b);
	}
	
	/**
	 * Creates a new implication with the given pair of formulas
	 * @param formulas a pair of formulas
	 */
	public Implication(Pair<RelationalFormula,RelationalFormula> formulas){
		this.formulas = formulas;
	}
	
	/**
	 * Returns the formulas of the implication.
	 * @return the formulas that are part of the implication
	 */
	public Pair<RelationalFormula,RelationalFormula> getFormulas() {
		return formulas;
	}
	
	/**
	 * Sets the formulas of the implication.
	 * @param formulas the formulas
	 */
	public void setFormulas(Pair<RelationalFormula,RelationalFormula> formulas) {
		this.formulas = formulas;
	}
	
	@Override
	public Set<? extends Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		predicates.addAll(this.formulas.getFirst().getPredicates());
		predicates.addAll(this.formulas.getSecond().getPredicates());
		return predicates;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Set<Variable> getUnboundVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		variables.addAll(this.formulas.getFirst().getUnboundVariables());
		variables.addAll(this.formulas.getSecond().getUnboundVariables());
		return variables;
	}

	@Override
	public boolean containsQuantifier() {
		return ( this.formulas.getFirst().containsQuantifier() || this.formulas.getSecond().containsQuantifier() );
	}

	@Override
	public boolean isWellBound() {
		return ( this.formulas.getFirst().isWellBound() && this.formulas.getSecond().isWellBound() );
	}

	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		return ( this.formulas.getFirst().isWellBound(boundVariables) && this.formulas.getSecond().isWellBound(boundVariables));
	}

	@Override
	public boolean isClosed() {
		return ( this.formulas.getFirst().isClosed() && this.formulas.getSecond().isClosed() );
	}

	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		return ( this.formulas.getFirst().isClosed(boundVariables) && this.formulas.getSecond().isClosed(boundVariables) );
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> terms = new HashSet<Term<?>>();
		terms.addAll(this.formulas.getFirst().getTerms());
		terms.addAll(this.formulas.getSecond().getTerms());
		return terms;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> terms = new HashSet<C>();
		terms.addAll(this.formulas.getFirst().getTerms(cls));
		terms.addAll(this.formulas.getSecond().getTerms(cls));
		return terms;
	}

	@Override
	public FolFormula toNnf() {
		Disjunction d = new Disjunction(new Negation(this.formulas.getFirst()),this.formulas.getSecond()); //(A=>B) <=> (!A || B)
		return d.toNnf();
	}

	@Override
	public RelationalFormula collapseAssociativeFormulas() {
		RelationalFormula first = ((FolFormula)this.formulas.getFirst()).collapseAssociativeFormulas();
		RelationalFormula second = ((FolFormula)this.formulas.getSecond()).collapseAssociativeFormulas();
		return new Implication(first,second);
	}

	@Override
	public boolean isDnf() {
		Disjunction d = new Disjunction(new Negation(this.formulas.getFirst()),this.formulas.getSecond()); //(A=>B) <=> (!A || B)
		return d.isDnf();
	}

	@Override
	public FolFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		return new Implication(this.formulas.getFirst().substitute(v, t),this.formulas.getSecond().substitute(v, t));
	}

	@Override
	public Implication clone() {
		return new Implication(this.formulas);
	}

	@Override
	public Set<? extends Atom> getAtoms() {
		Set<Atom> atoms = new HashSet<Atom>();
		atoms.addAll(this.formulas.getFirst().getAtoms());
		atoms.addAll(this.formulas.getSecond().getAtoms());
		return atoms;
	}

	@Override
	public Set<Functor> getFunctors() {
		Set<Functor> functors = new HashSet<Functor>();
		functors.addAll(this.formulas.getFirst().getFunctors());
		functors.addAll(this.formulas.getSecond().getFunctors());
		return functors;
	}

	@Override
	public String toString() {
		return "(" + this.formulas.getFirst().toString() + LogicalSymbols.IMPLICATION() + this.formulas.getSecond().toString() + ")";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formulas == null) ? 0 : formulas.hashCode());
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
		Implication other = (Implication) obj;
		if (formulas == null) {
			if (other.formulas != null)
				return false;
		} else if (!formulas.equals(other.formulas))
			return false;
		return true;
	}


}

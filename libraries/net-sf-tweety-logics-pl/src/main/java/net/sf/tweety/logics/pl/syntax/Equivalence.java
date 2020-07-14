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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.syntax;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;

/**
 * 
 * This class models equivalence of propositional logic.
 * 
 * @author Anna Gessler
 */
public class Equivalence extends PlFormula {
	
	/**
	 * The pair of formulas that are part of the equivalence.
	 */
	private Pair<PlFormula,PlFormula> formulas;
	
	/**
	 * Creates a new equivalence with the given two formulas 
	 * @param a a propositional formula
	 * @param b a propositional formula
	 */
	public Equivalence(PlFormula a, PlFormula b){
		formulas = new Pair<PlFormula,PlFormula>(a,b);
	}
	
	/**
	 * Creates a new equivalence with the given pair of formulas
	 * @param formulas a pair of formulas
	 */
	public Equivalence(Pair<PlFormula,PlFormula> formulas){
		this.formulas = formulas;
	}
	
	/**
	 * Returns the formulas of the equivalence.
	 * @return the formulas that are part of the equivalence
	 */
	public Pair<PlFormula,PlFormula> getFormulas() {
		return formulas;
	}
	
	/**
	 * Sets the formulas of the equivalence.
	 * @param formulas the formulas
	 */
	public void setFormulas(Pair<PlFormula,PlFormula> formulas) {
		this.formulas = formulas;
	}
	
	/**
	 * Sets the formulas of the equivalence.
	 * @param formula1
	 * @param formula2
	 */
	public void setFormulas(PlFormula formula1, PlFormula formula2) {
		this.formulas = new Pair<PlFormula, PlFormula>(formula1, formula2);
	}
	
	@Override
	public Set<Proposition> getAtoms() {
		Set<Proposition> result = new HashSet<Proposition>();
		result.addAll(formulas.getFirst().getAtoms());
		result.addAll(formulas.getSecond().getAtoms());
		return result;
	}

	@Override
	public Set<PlFormula> getLiterals() {
		Set<PlFormula> result = new HashSet<PlFormula>();
		result.addAll(formulas.getFirst().getLiterals());
		result.addAll(formulas.getSecond().getLiterals());
		return result;
	}

	@Override
	public PlFormula collapseAssociativeFormulas() {
		PlFormula first = this.formulas.getFirst().collapseAssociativeFormulas();
		PlFormula second = this.formulas.getSecond().collapseAssociativeFormulas();
		return new Equivalence(first,second);
	}

	@Override
	public Set<PlPredicate> getPredicates() {
		Set<PlPredicate> predicates = new HashSet<PlPredicate>();
		predicates.addAll(this.formulas.getFirst().getPredicates());
		predicates.addAll(this.formulas.getSecond().getPredicates());
		return predicates;
	}

	@Override
	public PlFormula trim() {
		PlFormula f1 = formulas.getFirst().trim();
		PlFormula f2 = formulas.getSecond().trim();
		if (f1.equals(f2))
			return new Tautology();
		return new Equivalence(f1,f2);
	}

	@Override
	public PlFormula toNnf() {
		Disjunction d1 = new Disjunction(new Negation(this.formulas.getFirst()),this.formulas.getSecond());
		Disjunction d2 = new Disjunction(new Negation(this.formulas.getSecond()),this.formulas.getFirst()); 
		return new Conjunction(d1,d2).toNnf();
	}

	@Override
	public Conjunction toCnf() {
		Disjunction d1 = new Disjunction(new Negation(this.formulas.getFirst()),this.formulas.getSecond());
		Disjunction d2 = new Disjunction(new Negation(this.formulas.getSecond()),this.formulas.getFirst()); 
		return new Conjunction(d1,d2).toCnf();
	}

	@Override
	public Set<PossibleWorld> getModels(PlSignature sig) {
		Disjunction d1 = new Disjunction(new Negation(this.formulas.getFirst()),this.formulas.getSecond());
		Disjunction d2 = new Disjunction(new Negation(this.formulas.getSecond()),this.formulas.getFirst()); 
		return (new Conjunction(d1,d2)).getModels();
	}

	@Override
	public int numberOfOccurrences(Proposition p) {
		int result = 0;
		result += formulas.getFirst().numberOfOccurrences(p);	
		result += formulas.getSecond().numberOfOccurrences(p);		
		return result;
	}

	@Override
	public PlFormula replace(Proposition p, PlFormula f, int i) {
		Pair<PlFormula,PlFormula> n = new Pair<PlFormula,PlFormula>();
		PlFormula first = formulas.getFirst();
		if (first.numberOfOccurrences(p) >= i) {
			n.setFirst(first.replace(p, f, i)); 
		} else {
			int num = first.numberOfOccurrences(p);
			PlFormula second = formulas.getSecond();
			if (num + second.numberOfOccurrences(p) >= i) 
				n.setSecond(second.replace(p, f, i-num));
		}
		return new Equivalence(n);
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
		Equivalence other = (Equivalence) obj;
		if (formulas == null) {
			if (other.formulas != null)
				return false;
		} else if (!formulas.equals(other.formulas))
			return false;
		return true;
	}

	@Override
	public Equivalence clone() {
		return new Equivalence(this.formulas);
	}
	
	@Override
	public String toString() {
		return "(" + this.formulas.getFirst().toString() + LogicalSymbols.EQUIVALENCE() + this.formulas.getSecond().toString() + ")";
	}
	
	@Override
	public PlSignature getSignature() {
		PlSignature sig = this.formulas.getFirst().getSignature();
		sig.addSignature(this.formulas.getSecond().getSignature());
		return sig;
	}

}

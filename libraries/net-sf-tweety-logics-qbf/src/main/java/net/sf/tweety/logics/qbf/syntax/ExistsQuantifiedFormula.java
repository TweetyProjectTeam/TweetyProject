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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.qbf.syntax;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlPredicate;
import net.sf.tweety.logics.pl.syntax.PlSignature;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * This class represents existential quantification for boolean formulas.
 * 
 * @author Anna Gessler
 *
 */
public class ExistsQuantifiedFormula extends PlFormula {
	/**
	 * The quantified formula.
	 */
	private PlFormula innerFormula;
	
	/**
	 * The quantified variables.
	 */
	private Set<Proposition> quantifier_variables;
	
	/**
	 * Create a new existential boolean quantification.
	 * @param f inner formula
	 * @param variables quantifier variables
	 */
	public ExistsQuantifiedFormula(PlFormula f, Set<Proposition> variables) {
		this.innerFormula = f;
		this.quantifier_variables = variables;
		//TODO check well-formed-ness
	}
	
	/**
	 * Create a new existential boolean quantification.
	 * @param f inner formula
	 * @param variable quantifier variable
	 */
	public ExistsQuantifiedFormula(PlFormula f, Proposition variable) {
		this.innerFormula = f;
		Set<Proposition> variables = new HashSet<Proposition>();
		variables.add(variable);
		this.quantifier_variables = variables;
	}
	
	/**
	 * Create a new existential boolean quantification.
	 * @param other other existential quantified formula
	 */
	public ExistsQuantifiedFormula(ExistsQuantifiedFormula other) {
		this.innerFormula = other.getFormula();
		this.quantifier_variables = other.getQuantifierVariables();
	}

	/**
	 * @return the quantifier variables (propositions)
	 */
	public Set<Proposition> getQuantifierVariables() {
		return this.quantifier_variables;
	}

	/**
	 * @return the quantified formula
	 */
	public PlFormula getFormula() {
		return this.innerFormula;
	}

	@Override
	public Set<Proposition> getAtoms() {
		return innerFormula.getAtoms();
	}

	@Override
	public Set<PlFormula> getLiterals() {
		return innerFormula.getLiterals();
	}

	@Override
	public PlFormula collapseAssociativeFormulas() {
		 return new ExistsQuantifiedFormula( this.getFormula().collapseAssociativeFormulas(), this.getQuantifierVariables() );
	}

	@Override
	public Set<PlPredicate> getPredicates() {
		return innerFormula.getPredicates();
	}

	@Override
	public PlFormula trim() {
		return new ExistsQuantifiedFormula( this.getFormula().trim(), this.getQuantifierVariables() );
	}

	@Override
	public PlFormula toNnf() {
		return new ExistsQuantifiedFormula(this.innerFormula.toNnf(),this.quantifier_variables);
	}

	/**
	 * In this case, this method returns this quantified boolean formula's 
	 * cnf kernel.
	 */
	@Override
	public Conjunction toCnf() {
		return innerFormula.toCnf();
	}

	@Override
	public Set<PossibleWorld> getModels(PlSignature sig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numberOfOccurrences(Proposition p) {
		return innerFormula.numberOfOccurrences(p);
	}

	@Override
	public PlFormula replace(Proposition p, PlFormula f, int i) {
		return new ForallQuantifiedFormula(this.innerFormula.replace(p, f, i),this.quantifier_variables);
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((innerFormula == null) ? 0 : innerFormula.hashCode());
		result = prime * result
				+ ((quantifier_variables == null) ? 0 : quantifier_variables.hashCode());
		return result;
	}

	@Override
	public PlFormula clone() {
		return new ExistsQuantifiedFormula(this);
	}
	
	@Override
	public String toString() {
		String s = LogicalSymbols.EXISTSQUANTIFIER() + " ";
		Iterator<Proposition> it = this.getQuantifierVariables().iterator();
		if(it.hasNext())
			s += it.next();
		while(it.hasNext())
			s += "," + it.next();
		s += ": (" + this.getFormula() + ")";
		return s;
	}

}

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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.dl.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * 
 * This class models a concept or role assertion in description logic, i.e. an
 * expression of the form "a : C" (a is in the extension of C) or "(a,b) : R",
 * where a,b are Individuals, C is a concept and R is a role.
 * 
 * @author Anna Gessler
 */
public class AssertionalAxiom extends DlFormula {

	private Predicate predicate;
	private List<Term<?>> arguments = new ArrayList<Term<?>>();

	/**
	 * Empty default constructor.
	 */
	public AssertionalAxiom() {
	}

	/**
	 * 
	 * Initializes a concept assertion with the given atomic concept and Individual.
	 * 
	 * @param p
	 *            AtomicConcept
	 * @param i
	 *            an Individual, term of the concept
	 */
	public AssertionalAxiom(AtomicConcept p, Individual i) {
		this.predicate = p.getPredicate();
		this.arguments.add(i);
	}

	/**
	 * 
	 * Initializes a role assertion with the given atomic role and the given two
	 * individuals.
	 * 
	 * @param p
	 *            AtomicRole
	 * @param a
	 *            an Individual
	 * @param b
	 *            an Individual
	 */
	public AssertionalAxiom(AtomicRole p, Individual a, Individual b) {
		this.predicate = p.getPredicate();
		this.arguments.add(a);
		this.arguments.add(b);
	}

	/**
	 * Initializes a role assertion with the given predicate and a list of two
	 * terms (roles are always predicates of arity 2).
	 * 
	 * @param predicate
	 *            predicate name of the atomic concept
	 * @param args
	 *            the arguments (terms) of the concept
	 */
	public AssertionalAxiom(AtomicRole p, List<Term<?>> args) {
		this.predicate = p.getPredicate();
		this.arguments = args;
	}

	/**
	 * Initializes either a concept assertion or a role
	 * assertion based on the arity of the given predicate.
	 * 
	 * @param predicate
	 *            predicate representing the concept or role name
	 * @param args
	 *            the arguments (terms) of the concept or role
	 */
	public AssertionalAxiom(Predicate predicate, List<Term<?>> arguments) {
		int arity = predicate.getArity();
		if (arity != 1 || arity != 2)
			throw new IllegalArgumentException("Arity has to be 1 or 2, arity of given predicate is " + predicate.getArity());
		if (arity != arguments.size())
			throw new IllegalArgumentException("Arity of given predicate and number of arguments given do not match.");
		this.predicate = predicate;
		this.arguments = arguments;
	}

	public Predicate getPredicate() {
		return this.getPredicate();
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		ps.add(this.predicate);
		return ps;
	}
	
	@Override
	public AssertionalAxiom clone() {
		return new AssertionalAxiom(this.predicate,this.arguments);
	}
	
	@Override
	public String toString() {
		String output = this.predicate.getName();
		if(this.arguments.size() == 0) return output;
		output += "(";
		output += this.arguments.get(0);
		for(int i = 1; i < arguments.size(); i++)
			output += ","+arguments.get(i);
		output += ")";
		return output;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;		
		if (obj == null || getClass() != obj.getClass())
			return false;
		AssertionalAxiom other = (AssertionalAxiom) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}
	
	@Override
	public DlFormula collapseAssociativeFormulas() {
		return this;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.add(this.predicate);
		sig.addAll(this.arguments);
		return sig;
	}

}

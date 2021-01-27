/*
 *  This file is part of "Tweety", a collection of Java libraries for
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
 *  Copyright 2018 The TweetyProject Project Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.dl.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.commons.syntax.Predicate;

/**
 * 
 * This class models a concept assertion in description logic, i.e. an
 * expression of the form "a : C" (a is in the extension of C) where a
 * is an Individual and C is a concept.
 * 
 * @author Anna Gessler
 */
public class ConceptAssertion extends AssertionalAxiom {

	/**
	 * The individual of this assertional axiom (= the individual that
	 * is an instance of this assertion's concept)
	 */
	private Individual individual;
	
	/**
	 * The concept or role of this assertional axiom (= the concept or role that the
	 * individuals are instances of).
	 */
	private ComplexConcept concept;
	
	/**
	 * Empty default constructor.
	 */
	public ConceptAssertion() {
	}
	
	/**
	 * 
	 * Initializes a role assertion with the given concept and Individual.
	 * 
	 * @param i
	 *            an Individual, term of the concept
	 * @param c
	 *            a (complex) concept
	 * 
	 */
	public ConceptAssertion(Individual i, ComplexConcept c) {
		this.concept = c;
		this.individual = i;
	}

	/**
	 * 
	 * Initializes a role assertion with the given atomic concept and Individual.
	 * 
	 * @param i
	 *            an Individual, term of the concept
	 * @param c
	 *            AtomicConcept
	 * 
	 */
	public ConceptAssertion(Individual i, AtomicConcept c) {
		this.concept = c;
		this.individual = i;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		ps.addAll(this.concept.getPredicates());
		return ps;
	}
	
	@Override
	public ConceptAssertion clone() {
		return new ConceptAssertion(this.individual, this.concept);
	}
	
	@Override
	public String toString() {
		return "instance " + this.individual.toString()+ " " + this.concept.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((individual == null) ? 0 : individual.hashCode());
		result = prime * result
				+ ((concept == null) ? 0 : concept.hashCode());
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
		ConceptAssertion other = (ConceptAssertion) obj;
		if (individual == null) {
			if (other.individual != null)
				return false;
		} else if (!individual.equals(other.individual)) 
			return false; 
		if (concept == null) {
			if (other.concept != null)
				return false;
		} else if (!concept.equals(other.concept))  
			return false;  
		return true;
	}
	
	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.add(this.concept);
		sig.add(this.individual);
		return sig;
	}

	/**
	 * Get the individual of this assertional axiom (= the individual that
	 * is an instance of this axiom's concept)
	 * @return the individual
	 */
	public Individual getIndividual() {
		return individual;
	}
	
	/**
	 * Get the concept of this assertional axiom (= the concept that the 
	 * individual is an instance of).
	 * @return the concept of this assertional axiom
	 */
	public ComplexConcept getConcept() {
		return concept;
	}

	@Override
	public boolean isAtomic() {
		return (this.concept instanceof AtomicConcept);
	}
}

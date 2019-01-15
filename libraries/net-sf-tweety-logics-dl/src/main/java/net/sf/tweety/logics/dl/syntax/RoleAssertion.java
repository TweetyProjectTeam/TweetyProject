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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.syntax.Predicate;

/**
 * 
 * This class models a role assertion in description logic, i.e. an
 * expression of the form  "(a,b) : R", where a,b are Individuals
 * and R is a role.
 * 
 * @author Anna Gessler
 */
public class RoleAssertion extends DlAxiom {

	/**
	 * The individuals of this assertional axiom (= the individuals that
	 * are instances of the role)
	 */
	private Pair<Individual,Individual> individuals = new Pair<Individual,Individual>();
	
	/**
	 * The role of this assertional axiom (= the role that the
	 * individuals are instances of).
	 */
	private ComplexConcept role;
	

	/**
	 * Empty default constructor.
	 */
	public RoleAssertion() {
	}
	
	/**
	 * 
	 * Initializes a role assertion with the given individuals and role.
	 * 
	 * @param a
	 *            an Individual
	 * @param b
	 *            an Individual         
	 * @param r
	 *            a role
	 * 
	 */
	public RoleAssertion(Individual a, Individual b, AtomicRole r) {
		this.role = r;
		this.individuals = new Pair<Individual,Individual>(a,b);
	}

	/**
	 * 
	 * Initializes a role assertion with the given individuals and role.
	 * 
	 * @param args
	 *            list of individuals         
	 * @param r
	 *            a role
	 * 
	 */
	public RoleAssertion(List<Individual> args, AtomicRole r) {
		if (args.size() != 2)
			throw new IllegalArgumentException("Incorrect number of arguments, has to be for 2 for a role assertion, but is " + args.size()); 
		this.role = r;
		this.individuals = new Pair<Individual,Individual>(args.get(0),args.get(1));
	}

	/**
	 * 
	 * Initializes a role assertion with the given role and individuals.
	 * 
	 * @param args
	 * 			  pair of individuals
	 * @param r
	 *            role
	 * 
	 */
	public RoleAssertion(Pair<Individual, Individual> args, ComplexConcept r) {
		this.role = r;
		this.individuals = args;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		ps.addAll(this.role.getPredicates());
		return ps;
	}
	
	@Override
	public RoleAssertion clone() {
		return new RoleAssertion(this.individuals,this.role);
	}
	
	@Override
	public String toString() {
		return "related " + individuals.getFirst().toString() + " " + individuals.getSecond().toString() + " "+ this.role.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((individuals == null) ? 0 : individuals.hashCode());
		result = prime * result
				+ ((role == null) ? 0 : role.hashCode());
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
		RoleAssertion other = (RoleAssertion) obj;
		if (individuals == null) {
			if (other.individuals != null)
				return false;
		} else if (!individuals.equals(other.individuals))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.add(this.role);
		sig.add(this.individuals.getFirst());
		sig.add(this.individuals.getSecond());
		return sig;
	}
	
	/**
	 * Get the individuals of this assertional axiom (= the individuals that
	 * are instances of the role)
	 * @return individuals
	 */
	public Pair<Individual,Individual> getIndividuals() {
		return individuals;
	}
	
	/**
	 * Get the role that the individuals of this assertional axiom are 
	 * instances of.
	 * @return role
	 */
	public ComplexConcept getRole() {
		return role;
	}

}

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
package net.sf.tweety.logics.dl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.TripleSetSignature;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class models a description logic signature. A signature for a
 * description logic consists of concept names (unary predicates, e.g.
 * "Male(X)"), role names (binary predicates, e.g. "DaughterOf(X,Y)") and
 * individuals (constants, e.g. "Alice"). 
 * 
 * @author Bastian Wolf
 * @author Anna Gessler
 *
 */
public class DlSignature extends TripleSetSignature<AtomicConcept, AtomicRole, Individual> {
	
	/**
	 * Creates an empty signature.
	 */
	public DlSignature() {
		super();
	}

	/**
	 * Creates a signature with the given concept names, role names and individuals.
	 * 
	 * @param concepts    atomic concepts of the signature
	 * @param roles       atomic roles of the signature
	 * @param individuals individuals of the signature
	 */
	public DlSignature(Set<AtomicConcept> concepts, Set<AtomicRole> roles, Set<Individual> individuals) {
		super();
		this.firstSet = concepts;
		this.secondSet = roles;
		this.thirdSet = individuals;
	}

	/**
	 * Creates a signature with the given objects (individuals, concepts, roles or
	 * formulas).
	 * 
	 * @param c a collection of items to be added.
	 * @throws IllegalArgumentException if at least one of the given objects is
	 *                                  neither an individual, a concept, a role or
	 *                                  a formula.
	 */
	public DlSignature(Collection<?> c) throws IllegalArgumentException {
		this();
		this.addAll(c);
	}
	
	/**
	 * Get the atomic concepts of the signature. A concept is an unary (arity 1)
	 * predicate.
	 */
	public Set<AtomicConcept> getConcepts() {
		return this.firstSet;
	}

	/**
	 * Get the role names of the signature. A role is a binary predicate (arity 2)
	 * consisting of two individuals.
	 */
	public Set<AtomicRole> getRoles() {
		return this.secondSet;
	}

	/**
	 * Get the individuals of the signature. An individual is a single object
	 * similar to objects used in first-order logic.
	 */

	public Set<Individual> getIndividuals() {
		return this.thirdSet;
	}

	/**
	 * @return all predicates of this signature.
	 */
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		for (AtomicConcept c : this.getConcepts())
			predicates.add(new Predicate(c.getName(), 1));
		for (AtomicRole r : this.getRoles())
			predicates.add(new Predicate(r.getName(), 2));
		return predicates;
	}

	/**
	 * Get the individual with the given name.
	 * @param s name of individual
	 * @return the individual with the given name if it is part of the signature, null otherwise
	 */
	public Individual getIndividual(String s) {
		for (Term<?> t : this.thirdSet)
			if (((Individual) t).get().equals(s))
				return (Individual) t;
		return null;
	}

	/**
	 * Get the concept with the given name.
	 * @param s name of concept
	 * @return the concept with the given name if it is part of the signature, null otherwise
	 */
	public AtomicConcept getConcept(String s) {
		for (AtomicConcept p : this.firstSet)
			if (p.getName().equals(s))
				return p;
		return null;
	}

	/**
	 * Get the role with the given name.
	 * @param s name of role
	 * @return the role with the given name if it is part of the signature, null otherwise
	 */
	public AtomicRole getRole(String s) {
		for (AtomicRole p : this.secondSet)
			if (p.getName().equals(s))
				return p;
		return null;
	}

	/**
	 * Checks whether the signature contains an Individual of the given name.
	 * 
	 * @param s the name of the Individual
	 * @return true if the the signature contains an Individual of the given name,
	 *         false otherwise
	 */
	public boolean containsIndividual(String s) {
		return this.getIndividual(s) != null;
	}

	/**
	 * Checks whether the signature contains an atomic concept of the given name.
	 * 
	 * @param s the name of the atomic concept
	 * @return true if the the signature contains an atomic concept of the given
	 *         name, false otherwise
	 */
	public boolean containsConcept(String s) {
		return this.getConcept(s) != null;
	}

	/**
	 * Checks whether the signature contains an atomic role of the given name.
	 * 
	 * @param s the name of the atomic role
	 * @return true if the the signature contains a role of the given name, false
	 *         otherwise
	 */
	public boolean containsRole(String s) {
		return this.getRole(s) != null;
	}

	/**
	 * Returns signature as string in the order individuals - concept names - role
	 * names.
	 * 
	 * @return a String
	 */
	public String toString() {
		return firstSet.toString() + ", " + secondSet.toString() + ", " + thirdSet.toString();
	}

	/**
	 * Translates this DlSignature to a FolSignature, i.e. concept names 
	 * and role names are added as predicates and individuals are added as
	 * constants.
	 * 
	 * @return the corresponding FolSignature
	 */
	public FolSignature getCorrespondingFolSignature() {
		FolSignature sig = new FolSignature();
		for (AtomicConcept c : this.getConcepts())
			sig.add(c.getPredicate());
		for (AtomicRole r : this.getRoles())
			sig.add(r.getPredicate());
		for (Individual i : this.getIndividuals())
			sig.add(new Constant(i.get()));
		return sig;
	}
	
	/**
	 * Adds single objects to this signature, iff the object is an appropriate
	 * concept, role or individual or a formula. For a formula (complex concept) all
	 * individuals, concepts and roles of this formula are added to the signature.
	 * 
	 * @param an object to be added
	 * @throws IllegalArgumentException if the object is not an individual, a
	 *                                  concept, a role or a DlFormula.
	 */
	@Override
	public void add(Object obj) throws IllegalArgumentException {
		if (obj instanceof TopConcept || obj instanceof BottomConcept)
			return;
		if (obj instanceof Individual) {
			this.thirdSet.add((Individual) obj);
			return;
		} else if (obj instanceof AtomicConcept) {
			this.firstSet.add((AtomicConcept) obj);
			return;
		} else if (obj instanceof AtomicRole) {
			this.secondSet.add((AtomicRole) obj);
			return;
		}
		else if (obj instanceof Predicate) {
			Predicate p = (Predicate) obj;
			if (p.getArity() == 1)
				this.firstSet.add(new AtomicConcept(p));
			else if (p.getArity() == 2)
				this.secondSet.add(new AtomicRole(p));
			else
				throw new IllegalArgumentException(
						"Illegal arity of " + p.getArity() + ", has to be 1 (concept name) or 2 (role name).");
		} else if (obj instanceof Constant) {
			this.thirdSet.add(new Individual((Constant) obj));
		} else if (obj instanceof ConceptAssertion) {
			ConceptAssertion d = (ConceptAssertion) obj;
			this.add(d.getConcept());
			this.add(d.getIndividual());
		} else if (obj instanceof RoleAssertion) {
			RoleAssertion d = (RoleAssertion) obj;
			this.add(d.getRole());
			this.add(d.getIndividuals().getFirst());
			this.add(d.getIndividuals().getSecond());
		} else if (obj instanceof Complement) {
			Complement c = (Complement) obj;
			this.add(c.getFormula());
		} else if (obj instanceof Union) {
			Union u = (Union) obj;
			List<ComplexConcept> formulas = u.getFormulas();
			for (ComplexConcept f : formulas)
				this.add(f);
		} else if (obj instanceof Intersection) {
			Intersection u = (Intersection) obj;
			List<ComplexConcept> formulas = u.getFormulas();
			for (ComplexConcept f : formulas)
				this.add(f);
		} else if (obj instanceof ExistentialRestriction) {
			ExistentialRestriction e = (ExistentialRestriction) obj;
			this.add(e.getFormulas().getFirst());
			this.add(e.getFormulas().getSecond());
		} else if (obj instanceof UniversalRestriction) {
			UniversalRestriction u = (UniversalRestriction) obj;
			this.add(u.getFormulas().getFirst());
			this.add(u.getFormulas().getSecond());
		} else if (obj instanceof EquivalenceAxiom) {
			EquivalenceAxiom ea = (EquivalenceAxiom) obj;
			this.add(ea.getFormulas().getFirst());
			this.add(ea.getFormulas().getSecond());
		} else
			throw new IllegalArgumentException(
					"Class " + obj.getClass() + " of parameter is unsupported and cannot be added to the signature.");
	}

	@Override
	public void remove(Object obj) {
		if (obj instanceof TopConcept || obj instanceof BottomConcept)
			return;
		if (obj instanceof Individual) {
			this.thirdSet.remove((Individual) obj);
			return;
		} else if (obj instanceof AtomicConcept) {
			this.firstSet.remove((AtomicConcept) obj);
			return;
		} else if (obj instanceof AtomicRole) {
			this.secondSet.remove((AtomicRole) obj);
			return;
		}
		else if (obj instanceof Predicate) {
			Predicate p = (Predicate) obj;
			if (p.getArity() == 1)
				this.firstSet.remove(new AtomicConcept(p));
			else if (p.getArity() == 2)
				this.secondSet.remove(new AtomicRole(p));
			else
				throw new IllegalArgumentException(
						"Illegal arity of " + p.getArity() + ", has to be 1 (concept name) or 2 (role name).");
		} else if (obj instanceof Constant) {
			this.thirdSet.remove(new Individual((Constant) obj));
		} else if (obj instanceof ConceptAssertion) {
			ConceptAssertion d = (ConceptAssertion) obj;
			this.remove(d.getConcept());
			this.remove(d.getIndividual());
		} else if (obj instanceof RoleAssertion) {
			RoleAssertion d = (RoleAssertion) obj;
			this.remove(d.getRole());
			this.remove(d.getIndividuals().getFirst());
			this.remove(d.getIndividuals().getSecond());
		} else if (obj instanceof Complement) {
			Complement c = (Complement) obj;
			this.remove(c.getFormula());
		} else if (obj instanceof Union) {
			Union u = (Union) obj;
			List<ComplexConcept> formulas = u.getFormulas();
			for (ComplexConcept f : formulas)
				this.remove(f);
		} else if (obj instanceof Intersection) {
			Intersection u = (Intersection) obj;
			List<ComplexConcept> formulas = u.getFormulas();
			for (ComplexConcept f : formulas)
				this.remove(f);
		} else if (obj instanceof ExistentialRestriction) {
			ExistentialRestriction e = (ExistentialRestriction) obj;
			this.remove(e.getFormulas().getFirst());
			this.remove(e.getFormulas().getSecond());
		} else if (obj instanceof UniversalRestriction) {
			UniversalRestriction u = (UniversalRestriction) obj;
			this.remove(u.getFormulas().getFirst());
			this.remove(u.getFormulas().getSecond());
		} else if (obj instanceof EquivalenceAxiom) {
			EquivalenceAxiom ea = (EquivalenceAxiom) obj;
			this.remove(ea.getFormulas().getFirst());
			this.remove(ea.getFormulas().getSecond());
		} else
			throw new IllegalArgumentException(
					"Class " + obj.getClass() + " of parameter is unsupported and cannot be removed from the signature.");
	}
	
	@Override
	public DlSignature clone() {
		DlSignature copy = new DlSignature();
		copy.addSignature(this);
		return copy;
	}

}

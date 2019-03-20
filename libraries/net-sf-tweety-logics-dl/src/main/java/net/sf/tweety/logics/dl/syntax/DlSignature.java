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

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;


/**
 * This class models a description logic signature. A signature for a description
 * logic consists of concept names (unary predicates, e.g. "Male(X)"), 
 * role names (binary predicates, e.g. "DaughterOf(X,Y)") 
 * and individuals (constants, e.g. "Alice").
 * <br> - Concept names:
 * 
 * @author Bastian Wolf
 * @author Anna Gessler
 *
 */
public class DlSignature extends Signature {
	/**
	 * Atomic concepts of the signature.
	 * A concept is an unary (arity 1) predicate.
	 */
	private Set<AtomicConcept> conceptNames;
	
	/**
	 * Role names of the signature.
	 * A role is a binary predicate (arity 2) consisting of two individuals.
	 */
	private Set<AtomicRole> roleNames;
	
	/**
	 * Individuals of the signature.
	 * An individual is a single object similar to objects used in first-order logic.
	 */
	private Set<Individual> individuals; 
	
	/**
	 * Creates an empty signature.
	 */
	public DlSignature() {
		this.conceptNames = new HashSet<AtomicConcept>();
		this.roleNames = new HashSet<AtomicRole>();
		this.individuals = new HashSet<Individual>();
	}
	
	/**
	 * Creates a signature with the given concept names, role names and individuals.
	 * 
	 * @param concepts atomic concepts of the signature
	 * @param roles atomic roles of the signature
	 * @param individuals individuals of the signature
	 */
	public DlSignature(Set<AtomicConcept> concepts, Set<AtomicRole> roles, Set<Individual> individuals) {
		this.conceptNames = concepts;
		this.roleNames = roles;
		this.individuals = individuals;
	}

	/**
	 * Creates a signature with the given objects (individuals,
	 * concepts, roles or formulas).
	 * 
	 * @param c a collection of items to be added.
	 * @throws IllegalArgumentException if at least one of the given objects is
	 * 	 neither an individual, a concept, a role or a formula.
	 */
	public DlSignature(Collection<?> c) throws IllegalArgumentException{
		this();
		this.addAll(c);
	}
	
	@Override
	public boolean isSubSignature(Signature other) {
		if(!(other instanceof DlSignature))
			return false;
		DlSignature o = (DlSignature) other;
		if(!o.individuals.containsAll(this.individuals)) return false;
		if(!o.conceptNames.containsAll(this.conceptNames)) return false;
		if(!o.roleNames.containsAll(this.roleNames)) return false;
		return true;
	}

	@Override
	public boolean isOverlappingSignature(Signature other){
		if(!(other instanceof DlSignature))
			return false;
		DlSignature o = (DlSignature) other;
		for(Object obj: o.individuals) if(this.individuals.contains(obj)) return true;
		for(Object obj: o.conceptNames) if(this.conceptNames.contains(obj)) return true;
		for(Object obj: o.roleNames) if(this.roleNames.contains(obj)) return true;
		return true;
	}
	
	/**
	 * Adds single objects to this signature, iff the object is
	 * an appropriate concept, role or individual or a formula. For a formula
	 * (complex concept) all individuals, concepts and roles of this formula 
	 * are added to the signature.
	 * 
	 * @param an object to be added
	 * @throws IllegalArgumentException if the object is not an individual,
	 * a concept, a role or a DlFormula.
	 */
	public void add(Object obj) throws IllegalArgumentException{
		if (obj instanceof TopConcept || obj instanceof BottomConcept)
			return;
		if (obj instanceof Individual) {
			this.individuals.add((Individual)obj);
			return;
		}
		else if (obj instanceof AtomicConcept) {
			this.conceptNames.add((AtomicConcept)obj); 
			return; }
		else if (obj instanceof AtomicRole) {
			this.roleNames.add((AtomicRole)obj);
			return;
		}
		else if (obj instanceof Predicate) {
			Predicate p = (Predicate) obj;
			if (p.getArity() == 1)
				this.conceptNames.add(new AtomicConcept(p));
			else if (p.getArity() == 2)
				this.roleNames.add(new AtomicRole(p));
			else
				throw new IllegalArgumentException("Illegal arity of " + p.getArity() +", has to be 1 (concept name) or 2 (role name).");
		}
		else if (obj instanceof Constant) {
			this.individuals.add(new Individual((Constant)obj));
		}
		else if (obj instanceof ConceptAssertion) {
			ConceptAssertion d = (ConceptAssertion) obj;
			this.add(d.getConcept());
			this.add(d.getIndividual());
		}
		else if (obj instanceof RoleAssertion) {
			RoleAssertion d = (RoleAssertion) obj;
			this.add(d.getRole());
			this.add(d.getIndividuals().getFirst());
			this.add(d.getIndividuals().getSecond());
		}
		else if (obj instanceof Complement) {
			Complement c = (Complement) obj;
			this.add(c.getFormula());
		}
		else if (obj instanceof Union) {
			Union u = (Union) obj;
			List<ComplexConcept> formulas = u.getFormulas();
			for (ComplexConcept f : formulas)
				this.add(f);
		}
		else if (obj instanceof Intersection) {
			Intersection u = (Intersection) obj;
			List<ComplexConcept> formulas = u.getFormulas();
			for (ComplexConcept f : formulas)
				this.add(f);
		}
		else if (obj instanceof ExistentialRestriction) {
			ExistentialRestriction e = (ExistentialRestriction) obj;
			this.add(e.getFormulas().getFirst());
			this.add(e.getFormulas().getSecond());
		}
		else if (obj instanceof UniversalRestriction) {
			UniversalRestriction u = (UniversalRestriction) obj;
			this.add(u.getFormulas().getFirst());
			this.add(u.getFormulas().getSecond());
		}
		else if (obj instanceof EquivalenceAxiom) {
			EquivalenceAxiom ea = (EquivalenceAxiom) obj;
			this.add(ea.getFormulas().getFirst());
			this.add(ea.getFormulas().getSecond());
		}
		else 
			throw new IllegalArgumentException("Class " + obj.getClass() + " of parameter is unsupported and cannot be added to the signature.");
	}
	
	/**
	 * Adds items of the given collection to this signature. These should be either 
	 * individuals, concepts, roles or formulas (complex concepts). 
	 * For a formula all individuals, concepts and roles of this formula are added 
	 * to the signature.
	 * 
	 * @param c the collection of items to be added
	 * @throws IllegalArgumentException if at least one of the given objects is
	 * 	 neither an individual, a concept, a role or a DlFormula.
	 */
	public void addAll(Collection<?> c) throws IllegalArgumentException{
		for(Object obj: c)
			this.add(obj);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conceptNames == null) ? 0  : conceptNames.hashCode());
		result = prime * result + ((roleNames == null) ? 0  : roleNames.hashCode());
		result = prime * result + ((individuals == null) ? 0  : individuals.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		DlSignature other = (DlSignature) obj;
		if (individuals == null) {
			if (other.individuals != null)
				return false;
		} else if (!individuals.equals(other.individuals))
			return false;
		if (conceptNames == null) {
			if (other.conceptNames != null)
				return false;
		} else if (!conceptNames.equals(other.conceptNames))
			return false;
		if (roleNames == null) {
			if (other.roleNames != null)
				return false;
		} else if (!roleNames.equals(other.roleNames))
			return false;
		return true;
	}
	

	@Override
	public void addSignature(Signature other) {
		if(!(other instanceof DlSignature))
			return;
		DlSignature dlSig = (DlSignature) other;
		this.conceptNames.addAll(dlSig.conceptNames);
		this.roleNames.addAll(dlSig.roleNames);
		this.individuals.addAll(dlSig.individuals);
	}
	

	public Set<AtomicConcept> getConcepts() {
		return this.conceptNames;
	}

	public Set<AtomicRole> getRoles() {
		return this.roleNames;
	}

	public Set<Individual> getIndividuals() {
		return this.individuals;
	}
	
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		for (AtomicConcept c : this.conceptNames)
			predicates.add(new Predicate(c.getName(),1));
		for (AtomicRole r: this.roleNames)
			predicates.add(new Predicate(r.getName(),2));
		return predicates;	
	}
	
	public Individual getIndividual(String s){
		for(Term<?> t: this.individuals)
			if(((Individual) t).get().equals(s))
				return (Individual) t;
		return null;
	}
	
	public AtomicConcept getConcept(String s){
		for(AtomicConcept p: this.conceptNames)
			if(p.getName().equals(s))
				return p;
		return null;
	}
	
	public AtomicRole getRole(String s){
		for(AtomicRole p: this.roleNames)
			if(p.getName().equals(s))
				return p;
		return null;
	}

	/**
	 * Checks whether the signature contains an
	 * Individual of the given name.
	 * @param s the name of the Individual
	 * @return true if the the signature contains an Individual of the given name, 
	 * 		   false otherwise
	 */
	public boolean containsIndividual(String s){
		return this.getIndividual(s) != null;
	}

	/**
	 * Checks whether the signature contains an
	 * atomic concept of the given name.
	 * @param s the name of the atomic concept
	 * @return true if the the signature contains an atomic concept of the given name, 
	 * 		   false otherwise
	 */
	public boolean containsConcept(String s){
		return this.getConcept(s) != null;
	}
	
	/**
	 * Checks whether the signature contains an
	 * atomic role of the given name.
	 * @param s the name of the atomic role
	 * @return true if the the signature contains a role of the given name, 
	 * 		   false otherwise
	 */
	public boolean containsRole(String s){
		return this.getRole(s) != null;
	}
	
	/**
	 * Returns signature as string in the order individuals - concept names - role names.
	 * @return a String
	 */
	public String toString() {
		return "Signature = (" + conceptNames.toString() + ",\n" + roleNames.toString() + ",\n" +individuals.toString() + ")";
	}
	
	/**
	 * Translates this DlSignature to a FolSignature
	 * and returns it, meaning concept names 
	 * and role names are added as predicates
	 * and individuals are added as constants.
	 * 
	 * @return the corresponding FolSignature
	 */
	public FolSignature getCorrespondingFolSignature() {
		FolSignature sig = new FolSignature();
		for (AtomicConcept c : this.conceptNames)
			sig.add(c.getPredicate());
		for (AtomicRole r : this.roleNames)
			sig.add(r.getPredicate());
		for (Individual i : this.individuals)
			sig.add(new Constant (i.get()));
		return sig;
	}
	
}

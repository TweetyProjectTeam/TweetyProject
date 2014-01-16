package net.sf.tweety.logics.dl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.Signature;
import net.sf.tweety.logics.commons.syntax.Concept;
import net.sf.tweety.logics.commons.syntax.Individual;
import net.sf.tweety.logics.commons.syntax.Role;


/**
 * This class models a description logic signature
 * 
 * @author Bastian Wolf
 *
 */
public class DlSignature extends Signature {

	private Set<Concept> concepts;
	private Set<Role> roles;
	private Set<Individual> individuals;
	
	/**
	 * empty constructor
	 */
	public DlSignature() {
		this.concepts = new HashSet<Concept>();
		this.roles = new HashSet<Role>();
		this.individuals = new HashSet<Individual>();
	}

	public DlSignature(Collection<?> c) throws IllegalArgumentException{
		this();
		this.addAll(c);
	}
	
	/**
	 * 
	 * @param other
	 */
	@Override
	public boolean isSubSignature(Signature other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOverlappingSignature(Signature other) {
		// TODO Auto-generated method stub
		return false;
	}

	/** TODO: to be implemented
	 * This class adds single objects to this signature, iff the object is
	 * an appropriate concept, role or individual 
	 * @param obj
	 * @throws IllegalArgumentException
	 */
	public void add(Object obj) throws IllegalArgumentException{
		if(obj instanceof Concept){
			
		}
	}
	
	public void addAll(Collection<?> c) throws IllegalArgumentException{
		for(Object obj: c){
			this.add(obj);
		}
	}
	
	public Set<Concept> getConcepts() {
		return concepts;
	}

	public void setConcepts(Set<Concept> concepts) {
		this.concepts = concepts;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Individual> getIndividuals() {
		return individuals;
	}

	public void setIndividuals(Set<Individual> individuals) {
		this.individuals = individuals;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((concepts == null) ? 0  : concepts.hashCode());
		result = prime * result + ((roles == null) ? 0  : roles.hashCode());
		result = prime * result + ((individuals == null) ? 0  : individuals.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addSignature(Signature other) {
		if(!(other instanceof DlSignature))
			return;
		DlSignature dlSig = (DlSignature) other;
		this.concepts.addAll(dlSig.concepts);
		this.roles.addAll(dlSig.roles);
		this.individuals.addAll(dlSig.individuals);
	}

}

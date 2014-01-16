package net.sf.tweety.logics.cl;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class models a belief set on conditional logic, i.e. a set of conditionals.
 * 
 * @author Matthias Thimm
 *
 */
public class ClBeliefSet extends BeliefSet<Conditional> {
	
	/**
	 * Creates a new (empty) conditional belief set.
	 */
	public ClBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new conditional belief set with the given collection of
	 * conditionals.
	 * @param conditionals a collection of conditionals.
	 */
	public ClBeliefSet(Collection<? extends Conditional> conditionals){
		super(conditionals);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	@Override
	public PropositionalSignature getSignature(){
		PropositionalSignature sig = new PropositionalSignature();
		for(Formula f: this){
			Conditional c = (Conditional) f;
			sig.addAll(c.getPremise().iterator().next().getAtoms());
			sig.addAll(c.getConclusion().getAtoms());
		}
		return sig;
	}

	@Override
	public ClBeliefSet clone(){
		ClBeliefSet copy = new ClBeliefSet();
		Iterator<Conditional> i = this.iterator();
		while(i.hasNext()){
			copy.add(i.next());
		}
		return copy;
	}
}

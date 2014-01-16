package net.sf.tweety.logics.pcl;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.syntax.*;


/**
 * This class models a belief set on probabilistic conditional logic, i.e. a set of
 * probabilistic conditionals.
 * 
 * @author Matthias Thimm
 *
 */
public class PclBeliefSet extends BeliefSet<ProbabilisticConditional> {

	/**
	 * Creates a new (empty) conditional belief set.
	 */
	public PclBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new conditional belief set with the given collection of
	 * conditionals.
	 * @param conditionals a collection of conditionals.
	 */
	public PclBeliefSet(Collection<? extends ProbabilisticConditional> conditionals){
		super(conditionals);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefSet#getSignature()
	 */
	@Override
	public Signature getSignature() {
		PropositionalSignature sig = new PropositionalSignature();
		for(ProbabilisticConditional c: this)
			sig.addAll(((PropositionalSignature)c.getSignature()));			
		return sig;
	}	
	
}

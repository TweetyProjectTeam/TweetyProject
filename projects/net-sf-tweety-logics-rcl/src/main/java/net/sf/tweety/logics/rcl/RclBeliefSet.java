package net.sf.tweety.logics.rcl;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.rcl.syntax.*;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class models a belief set on relational conditional logic, i.e. a set of relational conditionals.
 * 
 * @author Matthias Thimm
 *
 */
public class RclBeliefSet extends BeliefSet<RelationalConditional> {
	
	/**
	 * Creates a new (empty) conditional belief set.
	 */
	public RclBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new relational conditional belief set with the given collection of
	 * relational conditionals.
	 * @param conditionals a collection of relational conditionals.
	 */
	public RclBeliefSet(Collection<? extends RelationalConditional> conditionals){
		super(conditionals);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature(){
		FolSignature sig = new FolSignature();
		for(Formula f: this){
			RelationalConditional c = (RelationalConditional) f;
			sig.addAll(c.getTerms(Constant.class));
			sig.addAll(c.getFunctors());
			sig.addAll(c.getPredicates());			
		}
		return sig;
	}

}

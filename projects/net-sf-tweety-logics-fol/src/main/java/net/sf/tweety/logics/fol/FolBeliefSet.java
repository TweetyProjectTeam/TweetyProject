package net.sf.tweety.logics.fol;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.fol.syntax.*;


/**
 * This class models a first-order knowledge base, i.e. a set of formulas
 * in first-order logic.
 * @author Matthias Thimm
 *
 */
public class FolBeliefSet extends BeliefSet<FolFormula>{
	
	/**
	 * Creates a new and empty first-order knowledge base.
	 */
	public FolBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new first-order knowledge base with the given set of formulas.
	 * @param formulas
	 */
	public FolBeliefSet(Set<FolFormula> formulas){
		super(formulas);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature(){
		FolSignature sig = new FolSignature();
		sig.addAll(this);
		return sig;
	}
}

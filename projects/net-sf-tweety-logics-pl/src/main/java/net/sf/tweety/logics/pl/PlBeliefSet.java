package net.sf.tweety.logics.pl;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.util.SetTools;

/**
 * This class represents a knowledge base of propositional formulae.
 * 
 * @author Matthias Thimm
 *
 */
public class PlBeliefSet extends BeliefSet<PropositionalFormula> {

	/**
	 * Creates a new (empty) knowledge base.
	 */
	public PlBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new knowledge base with the given
	 * set of formulas.
	 * @param formulas a set of formulas.
	 */
	public PlBeliefSet(Collection<? extends PropositionalFormula> formulas){
		super(formulas);
	}
	
	/** Checks whether this belief set is consistent.
	 * @return "true" if this belief set is consistent.
	 */
	public boolean isConsistent(){
		return !new ClassicalInference(this, new ClassicalEntailment()).query(new Contradiction()).getAnswerBoolean();
	}
	
	/** 
	 * Returns the set of minimal inconsistent subsets of this set.
	 * @return the set of minimal inconsistent subsets of this set.
	 */
	public Set<PlBeliefSet> getMinimalInconsistentSubsets(){
		if(this.isConsistent()) return new HashSet<PlBeliefSet>();
		SetTools<PropositionalFormula> setTools = new SetTools<PropositionalFormula>();
		Set<PlBeliefSet> minInconSets = new HashSet<PlBeliefSet>();
		for(int card = 1; card <= this.size(); card++){
			Set<Set<PropositionalFormula>> sets = setTools.subsets(this, card);
			for(Set<PropositionalFormula> set: sets){
				// test if we already have a subset in minInconSets
				boolean properSet = true;
				for(PlBeliefSet set2: minInconSets)
					if(set.containsAll(set2)){
						properSet = false;
						break;
					}
				if(!properSet) continue;
				// check for consistency
				PlBeliefSet candidate = new PlBeliefSet(set);
				if(!candidate.isConsistent())
					minInconSets.add(candidate);
			}
		}
		return minInconSets;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		PropositionalSignature signature = new PropositionalSignature();
		for(Formula f: this)
			signature.addAll(((PropositionalFormula)f).getAtoms());
		return signature;
	}
	
	/**
     * This method returns this belief set in conjunctive normal form (CNF).
     * A formula is in CNF iff it is a conjunction of disjunctions and in NNF.
     * @return the formula in CNF.
     */
	public Conjunction toCnf(){
		Conjunction conj = new Conjunction();
		for(PropositionalFormula f: this)
			conj.add(f);
		return conj.toCnf();
	}

}

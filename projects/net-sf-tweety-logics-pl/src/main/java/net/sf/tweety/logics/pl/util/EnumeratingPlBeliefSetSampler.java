package net.sf.tweety.logics.pl.util;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.tweety.commons.BeliefBaseSampler;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This sampler enumerates all possible propositional belief bases of the given signature.
 * It does so by taking all subsets of the set of interpretations as the models of some
 * formula and combines all these formulas in all ways.
 * 
 * @author Matthias Thimm
 *
 */
public class EnumeratingPlBeliefSetSampler extends BeliefBaseSampler<PlBeliefSet> {

	/** The currently used min length. */
	private int currentMinLength = -1;
	/** The currently used max length. */
	private int currentMaxLength = -1;
	/** The current length */
	private int currentLength = -1;
	
	/** All possible worlds */
	private List<PossibleWorld> allWorlds;
	/** The current indices of the worlds that construct the formulas. */
	private BitSet indices;
	
	/**
	 * Creates a new sampler for the given signature
	 * @param signature some signature
	 */
	public EnumeratingPlBeliefSetSampler(Signature signature) {
		super(signature);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet randomSample(int minLength, int maxLength) {
		if(this.currentMaxLength != maxLength || this.currentMinLength != minLength){
			this.currentMaxLength = maxLength;
			this.currentMinLength = minLength;
			this.currentLength = this.currentMinLength;
			this.allWorlds = new LinkedList<PossibleWorld>(PossibleWorld.getAllPossibleWorlds((PropositionalSignature)this.getSignature()));
			this.indices = new BitSet(this.currentLength * this.allWorlds.size());						
		}		
		if(this.indices == null){
			if(this.currentLength < this.currentMaxLength){
				this.currentLength++;
				this.indices = new BitSet(this.currentLength * this.allWorlds.size());
			}else throw new NoSuchElementException("All belief bases have been generated");
		}
		return this.next();
	}
	
	/**
	 * Returns the next belief set.
	 * @return the next belief set.
	 */
	private PlBeliefSet next(){
		Proposition a = ((PropositionalSignature)this.getSignature()).iterator().next();
		PropositionalFormula contr = a.combineWithAnd(new Negation(a));		
		PlBeliefSet result = new PlBeliefSet();
		int size = this.allWorlds.size();		
		for(int i = 0; i < this.currentLength; i++){
			// we have to ensure that appearing formulas are not syntactically equivalent.
			PropositionalFormula p = contr.combineWithAnd(new Proposition("XSA"+i));
			for(int j = 0; j < size; j++){
				if(this.indices.get(i*size + j))
					p = p.combineWithOr(this.allWorlds.get(j).getCompleteConjunction((PropositionalSignature)this.getSignature()));				
			}
			result.add(p);
		}
		this.indices = this.increment(this.indices);
		return result;
	}
	
	/** Increments the given bit set, returns null
	 * if an overflow happens.
	 * @param bitSet some bit set.
	 * @return the incremented bit set
	 */
	private BitSet increment(BitSet bitSet){
		boolean carry = true, tmp;
		int i = 0;
		while(carry){
			tmp = carry;
			carry = carry && bitSet.get(i);
			bitSet.set(i, tmp ^ bitSet.get(i));
			i++;
		}
		if(this.currentLength * this.allWorlds.size() < i)
			return null;
		return bitSet;
	}
}

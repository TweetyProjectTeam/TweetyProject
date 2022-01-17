package org.tweetyproject.logics.translators.adfconditional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.InterpretationIterator;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;

import org.tweetyproject.logics.translators.adfconditional.FourValuedWorld.TruthValue;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * This class iterates over all 4-valued interpretations of a given signature.
 * Inspired by class "PossibleWorldIterator" for the use of 4-valued Logic
 * 
 * @author Jonas Schumacher
 */
public class FourValuedWorldIterator implements InterpretationIterator<PlFormula,PlBeliefSet,FourValuedWorld>{

	private PlSignature sig = null;
	
	private List<Proposition> set;
	
	private GeneralValuedBitSet currentItem;
	
	// 4-valued means there are values among [0, 1, 2, 3]
	private final int maxValue = 3;
/**
 * FourValuedWorldIterator
 */
	public FourValuedWorldIterator(){
	}
	
	/**
	 * Creates new iterator for the given signature.
	 * @param sig some signature
	 */
	public FourValuedWorldIterator(PlSignature sig){
		this.set = new ArrayList<Proposition>(sig.toCollection());
		this.currentItem = new GeneralValuedBitSet(set.size(), maxValue);
		this.sig = sig;
	}
	
	@Override
	public boolean hasNext() {
		return this.currentItem != null;
	}

	@Override
	public FourValuedWorld next() {
		if(!this.hasNext()) {
			throw new NoSuchElementException();
		}
		
		FourValuedWorld nextWorld = new FourValuedWorld();
		
		// Iterate over all propositions
		for(int i = 0; i < this.set.size(); i++) {	
			int value = this.currentItem.get(i);
			switch(value) {
			case 0:
				nextWorld.set(this.set.get(i),TruthValue.UNDECIDED);
				break;
			case 1:
				nextWorld.set(this.set.get(i),TruthValue.FALSE);
				break;
			case 2:
				nextWorld.set(this.set.get(i),TruthValue.TRUE);
				break;
			case 3:
				nextWorld.set(this.set.get(i),TruthValue.INCONSISTENT);
				break;
			default:
				throw new IllegalArgumentException("Only values below 4 are supported.");
			}
		}
		this.currentItem = this.increment(this.currentItem);
		return nextWorld;
	}

	/** Increments the given bit set from left to right, 
	 * returns null if an overflow happens.
	 * @param bitSet some bit set.
	 * @return the incremented bit set
	 */
	private GeneralValuedBitSet increment(GeneralValuedBitSet bitSet){
		// carry = true = need to place +1 from last round
		// carry = false = nothing to carry: the last +1 could already be integrated
		boolean carry = true;
		
		int i = 0;
		while(carry){
			
			// Everything strictly below 3 can take the carry: 			
			if (bitSet.get(i) <= maxValue-1) {
				carry = false;
				bitSet.set(i, bitSet.get(i) + 1);
			}
			// Only if the bit is already set to 3, the carry remains and we try to increment the next position
			else {
				carry = true;
				bitSet.set(i, 0);
				// If even the last bit cannot take the carry, the iteration is done and we return null
				if (i == this.set.size()-1) {
					return null;
				}
			}
			
			// Start next iteration
			i++;
		}
		
		return bitSet;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.InterpretationIterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("This operation is not supported");		
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.InterpretationIterator#reset()
	 */
	@Override
	public InterpretationIterator<PlFormula,PlBeliefSet,FourValuedWorld> reset() {
		return new FourValuedWorldIterator(this.sig);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.InterpretationIterator#reset(org.tweetyproject.commons.Signature)
	 */
	@Override
	public InterpretationIterator<PlFormula,PlBeliefSet,FourValuedWorld> reset(Signature sig){
		if(!(sig instanceof PlSignature))
			throw new IllegalArgumentException("Signature of type 'PropositionalSignature' expected.");
		return new FourValuedWorldIterator((PlSignature)sig);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.InterpretationIterator#reset(java.util.Collection)
	 */
	@Override
	public InterpretationIterator<PlFormula,PlBeliefSet,FourValuedWorld> reset(Collection<? extends Formula> formulas){
		PlSignature sig = new PlSignature();
		for(Formula f: formulas){
			if(!(f instanceof PlFormula))
				throw new IllegalArgumentException("Formula of type 'PropositionalFormula' expected.");
			sig.add(((PlFormula)f).getSignature());
		}	
		return this.reset((Signature)sig);
	}
}

package org.tweetyproject.logics.translators.adfcl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.tweetyproject.arg.adf.util.ThreeValuedBitSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.InterpretationIterator;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;

import org.tweetyproject.logics.translators.adfcl.PriestWorldAdapted.TruthValue;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;


/**
 * This class iterates effectively over all interpretation sets worlds of a given signature.
 * 
 * Adapted from class "PossibleWorldIterator" for the use of Three Valued Logic
 * 
 * @author Jonas Schumacher
 *
 */
public class PriestWorldIterator implements InterpretationIterator<PlFormula,PlBeliefSet,PriestWorldAdapted>{

	/** The signature used for creating possible worlds. */
	private PlSignature sig = null;
	
	private List<Proposition> set;
	
	private ThreeValuedBitSet currentItem;

	/**
	 * Creates new iterator.
	 */
	public PriestWorldIterator(){		
	}
	
	/**
	 * Creates new iterator for the given signature.
	 * @param sig some signature
	 */
	public PriestWorldIterator(PlSignature sig){
		this.set = new ArrayList<Proposition>(sig.toCollection());
		this.currentItem = new ThreeValuedBitSet(set.size());
		this.sig = sig;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.InterpretationIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.currentItem != null;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.InterpretationIterator#next()
	 */
	@Override
	public PriestWorldAdapted next() {
		if(!this.hasNext())
			throw new NoSuchElementException();
		
		PriestWorldAdapted next_pw = new PriestWorldAdapted();
		
		// Iterate over all propositions
		for(int i = 0; i < this.set.size(); i++) {
				Boolean bool = this.currentItem.get(i);
				if (bool == null) {
					next_pw.set(this.set.get(i),TruthValue.BOTH);
				}
				else if (bool == true) {
					next_pw.set(this.set.get(i),TruthValue.TRUE);
				} 
				else {
					next_pw.set(this.set.get(i),TruthValue.FALSE);
				}
			}
		this.currentItem = this.increment(this.currentItem);
		return next_pw;
	}

	/** Increments the given bit set, 
	 * returns null if an overflow happens.
	 * @param bitSet some bit set.
	 * @return the incremented bit set
	 */
	private ThreeValuedBitSet increment(ThreeValuedBitSet bitSet){
		// carry = true = need to place +1 from last round
		// carry = false = kein Übertrag >> das +1 konnte bereits "integriert" werden
		boolean carry = true;
		
		int i = 0;
		while(carry){
			
			// Null und False vertragen den Carry, in dem sie eins hochsetzen			
			if (bitSet.get(i) == null) {
				carry = false;
				bitSet.set(i, false);
			}
			else if (bitSet.get(i) == false) {
				carry = false;
				bitSet.set(i, true);
			}
			// nur wenn das bit auf "true" steht, dann bleibt der carry erhalten
			else {
				carry = true;
				bitSet.set(i, null);
				// Wenn selbst das letzte Bits geflippt wurde, dann 
				// steht jetzt alles auf "u", ich bin einmal durch und breche ab!
				if (i == this.set.size()-1) {
					return null;
				}
			}
			
			// zähle counter hoch
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
	public InterpretationIterator<PlFormula,PlBeliefSet,PriestWorldAdapted> reset() {
		return new PriestWorldIterator(this.sig);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.InterpretationIterator#reset(org.tweetyproject.commons.Signature)
	 */
	@Override
	public InterpretationIterator<PlFormula,PlBeliefSet,PriestWorldAdapted> reset(Signature sig){
		if(!(sig instanceof PlSignature))
			throw new IllegalArgumentException("Signature of type 'PropositionalSignature' expected.");
		return new PriestWorldIterator((PlSignature)sig);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.InterpretationIterator#reset(java.util.Collection)
	 */
	@Override
	public InterpretationIterator<PlFormula,PlBeliefSet,PriestWorldAdapted> reset(Collection<? extends Formula> formulas){
		PlSignature sig = new PlSignature();
		for(Formula f: formulas){
			if(!(f instanceof PlFormula))
				throw new IllegalArgumentException("Formula of type 'PropositionalFormula' expected.");
			sig.add(((PlFormula)f).getSignature());
		}	
		return this.reset((Signature)sig);
	}
}

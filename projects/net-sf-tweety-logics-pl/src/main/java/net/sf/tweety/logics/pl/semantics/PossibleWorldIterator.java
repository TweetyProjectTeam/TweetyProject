package net.sf.tweety.logics.pl.semantics;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.tweety.Interpretation;
import net.sf.tweety.InterpretationIterator;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.util.DefaultSubsetIterator;
import net.sf.tweety.util.SubsetIterator;


/**
 * Iterates effectively over all interpretation sets worlds of a given signature.
 * 
 * @author Matthias Thimm
 *
 */
public class PossibleWorldIterator implements InterpretationIterator{

	/** The signature used for creating possible worlds. */
	private PropositionalSignature sig;
	
	/** Used for iterating over subsets of propositions. */
	private SubsetIterator<Proposition> it;
	
	/**
	 * Creates new iterator for the given signature.
	 * @param sig some signature
	 */
	public PossibleWorldIterator(PropositionalSignature sig){
		this.sig = sig;
		this.it = new DefaultSubsetIterator<Proposition>(new HashSet<Proposition>(sig));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.InterpretationIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.it.hasNext();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.InterpretationIterator#next()
	 */
	@Override
	public Interpretation next() {
		if(!this.it.hasNext())
			throw new NoSuchElementException();
		Set<Proposition> s = this.it.next();
		return new PossibleWorld(s);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.InterpretationIterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("This operation is not supported");		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.InterpretationIterator#reset()
	 */
	@Override
	public InterpretationIterator reset() {
		return new PossibleWorldIterator(this.sig);
	}

}

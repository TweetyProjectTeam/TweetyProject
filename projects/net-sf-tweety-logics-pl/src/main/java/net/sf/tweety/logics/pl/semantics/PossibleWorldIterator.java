/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.InterpretationIterator;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;


/**
 * Iterates effectively over all interpretation sets worlds of a given signature.
 * 
 * @author Matthias Thimm
 *
 */
public class PossibleWorldIterator implements InterpretationIterator<PossibleWorld>{

	/** The signature used for creating possible worlds. */
	private PropositionalSignature sig = null;
	
	/** Used for iterating over subsets of propositions. */
	private SubsetIterator<Proposition> it = null;

	/**
	 * Creates new iterator for the given signature.
	 * @param sig some signature
	 */
	public PossibleWorldIterator(){		
	}
	
	/**
	 * Creates new iterator for the given signature.
	 * @param sig some signature
	 */
	public PossibleWorldIterator(PropositionalSignature sig){
		this();
		this.sig = sig;
		this.it = new DefaultSubsetIterator<Proposition>(new HashSet<Proposition>(sig));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.InterpretationIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.it != null && this.it.hasNext();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.InterpretationIterator#next()
	 */
	@Override
	public PossibleWorld next() {
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
	public InterpretationIterator<PossibleWorld> reset() {
		return new PossibleWorldIterator(this.sig);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.InterpretationIterator#reset(net.sf.tweety.commons.Signature)
	 */
	@Override
	public InterpretationIterator<PossibleWorld> reset(Signature sig){
		if(!(sig instanceof PropositionalSignature))
			throw new IllegalArgumentException("Signature of type 'PropositionalSignature' expected.");
		return new PossibleWorldIterator((PropositionalSignature)sig);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.InterpretationIterator#reset(java.util.Collection)
	 */
	@Override
	public InterpretationIterator<PossibleWorld> reset(Collection<? extends Formula> formulas){
		PropositionalSignature sig = new PropositionalSignature();
		for(Formula f: formulas){
			if(!(f instanceof PropositionalFormula))
				throw new IllegalArgumentException("Formula of type 'PropositionalFormula' expected.");
			sig.add(((PropositionalFormula)f).getSignature());
		}	
		return this.reset((Signature)sig);
	}
}

/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.InterpretationIterator;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.DefaultSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;


/**
 * Iterates effectively over all interpretation sets worlds of a given signature.
 * 
 * @author Matthias Thimm
 *
 */
public class PossibleWorldIterator implements InterpretationIterator<PlFormula,PlBeliefSet,PossibleWorld>{

	/** The signature used for creating possible worlds. */
	private PlSignature sig = null;
	
	/** Used for iterating over subsets of propositions. */
	private SubsetIterator<Proposition> it = null;

	/**
	 * Creates new iterator.
	 */
	public PossibleWorldIterator(){		
	}
	
	/**
	 * Creates new iterator for the given signature.
	 * @param sig some signature
	 */
	public PossibleWorldIterator(PlSignature sig){
		this();
		this.sig = sig;
		this.it = new DefaultSubsetIterator<Proposition>(new HashSet<Proposition>(sig.toCollection()));
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.InterpretationIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.it != null && this.it.hasNext();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.InterpretationIterator#next()
	 */
	@Override
	public PossibleWorld next() {
		if(!this.it.hasNext())
			throw new NoSuchElementException();
		Set<Proposition> s = this.it.next();
		return new PossibleWorld(s);
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
	public InterpretationIterator<PlFormula,PlBeliefSet,PossibleWorld> reset() {
		return new PossibleWorldIterator(this.sig);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.InterpretationIterator#reset(org.tweetyproject.commons.Signature)
	 */
	@Override
	public InterpretationIterator<PlFormula,PlBeliefSet,PossibleWorld> reset(Signature sig){
		if(!(sig instanceof PlSignature))
			throw new IllegalArgumentException("Signature of type 'PropositionalSignature' expected.");
		return new PossibleWorldIterator((PlSignature)sig);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.InterpretationIterator#reset(java.util.Collection)
	 */
	@Override
	public InterpretationIterator<PlFormula,PlBeliefSet,PossibleWorld> reset(Collection<? extends Formula> formulas){
		PlSignature sig = new PlSignature();
		for(Formula f: formulas){
			if(!(f instanceof PlFormula))
				throw new IllegalArgumentException("Formula of type 'PropositionalFormula' expected.");
			sig.add(((PlFormula)f).getSignature());
		}	
		return this.reset((Signature)sig);
	}
}

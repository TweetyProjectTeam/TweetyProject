/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.pl.semantics;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.InterpretationIterator;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;


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

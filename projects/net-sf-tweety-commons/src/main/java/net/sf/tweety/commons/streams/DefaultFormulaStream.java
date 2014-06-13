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
package net.sf.tweety.commons.streams;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sf.tweety.commons.Formula;

/**
 * This class models a default stream on the formulas of a given collection.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 */
public class DefaultFormulaStream<S extends Formula> implements FormulaStream<S>{

	/** The collection of formulas. */
	private Collection<S> formulas;
	
	/** Whether this stream is never-ending (formulas are repeated once through).*/
	private boolean neverending; 
	
	/** The actual iterator. */
	private Iterator<S> it;
	
	/**
	 * Creates a new default stream with the given formulas that ends after all formulas
	 * have been streamed.
	 * @param formulas a collection of formulas.
	 */
	public DefaultFormulaStream(Collection<S> formulas){
		this(formulas, false);
	}
	
	/**
	 * Creates a new default stream with the given formulas.
	 * @param formulas a collection of formulas.
	 * @param neverending whether this stream is never-ending (formulas are repeated once through).
	 */
	public DefaultFormulaStream(Collection<S> formulas, boolean neverending){
		this.formulas = formulas;
		this.neverending = neverending;
		this.it = formulas.iterator();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.streams.FormulaStream#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.it.hasNext() || this.neverending;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.streams.FormulaStream#next()
	 */
	@Override
	public S next() {
		if(!this.it.hasNext() && !this.neverending)
			throw new NoSuchElementException();
		if(!this.it.hasNext())
			this.it = this.formulas.iterator();
		if(!this.it.hasNext())
			throw new NoSuchElementException();
		return this.it.next();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.streams.FormulaStream#remove()
	 */
	@Override
	public void remove() {
		this.it.remove();		
	}
}

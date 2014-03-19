package net.sf.tweety.streams;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sf.tweety.Formula;

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

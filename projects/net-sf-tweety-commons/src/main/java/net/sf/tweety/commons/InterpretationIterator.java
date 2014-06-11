package net.sf.tweety.commons;

import java.util.Iterator;

/**
 * An iterator over interpretations.
 * @author Matthias Thimm
 */
public interface InterpretationIterator extends Iterator<Interpretation>{

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext();

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Interpretation next();

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove();
	
	/**
	 * Initializes a new reseted iterator. 
	 * @return a reseted iterator.
	 */
	public InterpretationIterator reset();	
	
}

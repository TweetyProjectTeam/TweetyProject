package net.sf.tweety.commons.util;

import java.util.Iterator;
import java.util.Set;

/**
 * Iterates over all subsets of a given set.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The elements of the set
 */
public abstract class SubsetIterator<T> implements Iterator<Set<T>>{

	/** The set this iterator is iterating over. */
	private Set<T> set;
		
	/** Creates a new subset iterator for the given set.
	 * @param set some set.
	 */
	public SubsetIterator(Set<T> set){
		this.set = set;
	}
	
	/**
	 * Returns the set this iterator is iterating over. 
	 * @return The set this iterator is iterating over. 
	 */
	protected Set<T> getSet(){
		return this.set;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("This operation is not supported by this class.");		
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public abstract boolean hasNext();

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public abstract Set<T> next();
	
}

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
package org.tweetyproject.commons.util;

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

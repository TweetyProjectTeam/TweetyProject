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
package net.sf.tweety.commons.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Iterates over all subsets of a given set. This iterator first returns the empty
 * set, then all 1-element subsets, then all 2-element subsets,... 
 * 
 * @author Matthias Thimm
 *
 * @param <T> The element class which is iterated.
 */
public class IncreasingSubsetIterator<T> extends SubsetIterator<T> {

	/** The actual set in a list. */
	private List<T> set;
	
	/** The indices of the generated subsets. */
	private int[] indices;
	
	/** The current size of the subsets generated. */
	private int currentSize;
	
	/** For hasNext(). */
	private boolean hasNext;
	
	/** Creates a new subset iterator for the given set.
	 * @param set some set.
	 */
	public IncreasingSubsetIterator(Set<T> set) {
		super(set);
		this.set = new ArrayList<T>(set);
		this.indices = new int[set.size()];
		this.currentSize = 0;
		this.hasNext = true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.util.SubsetIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.hasNext;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.util.SubsetIterator#next()
	 */
	@Override
	public Set<T> next() {		
		Set<T> result = new HashSet<T>();
		for(int i = 0; i < this.currentSize; i++){
			result.add(this.set.get(this.indices[i]));
		}
		if(this.currentSize != this.set.size())
			this.increment();
		else this.hasNext = false;
		return result;
	}
	
	/**
	 * Increments the indices.
	 */
	private void increment(){
		if(this.currentSize == 0){
			this.currentSize = 1;
			this.indices[0] = 0;
		}else this.increment(0);	
	}
	
	/**
	 * Increments the indices.
	 * @param lvl the level
	 * @return the new index
	 */
	private int increment(int lvl){
		if(lvl >= this.currentSize){
			this.indices[lvl] = 0;
			this.currentSize++;	
		}else if(this.indices[lvl] < this.set.size()-lvl-1){			
			this.indices[lvl]++; 
		}else{
			this.indices[lvl] = this.increment(lvl+1) + 1;  
		}		
		return this.indices[lvl];
	}
}

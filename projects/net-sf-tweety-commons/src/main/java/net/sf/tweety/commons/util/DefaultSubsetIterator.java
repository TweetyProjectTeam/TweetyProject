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
package net.sf.tweety.commons.util;

import java.util.*;

/**
 * Iterates over all subsets of a given sets. The order is given by the increment of a bitset as follows.
 * Let {1,2,3,4} a set where a subsets should be enumerated. Then a bitset 0000 of the same length is
 * initialized. At every next() operation the bitset is incremented in the standard way (0000->0001,...
 * 0101->0110,...) and the set is returned which contains exactly the elements at the indices with 1 in the
 * bitset.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The element class which is iterated.
 */
public class DefaultSubsetIterator<T> extends SubsetIterator<T> {

	/** The set over which subsets are iterated. */
	private List<T> set;
		
	/** The number of the current item as a bit set. */
	private BitSet currentItem;
		
	/** Creates a new subset iterator for the given set.
	 * @param set some set.
	 */
	public DefaultSubsetIterator(Set<T> set){
		super(set);
		this.set = new ArrayList<T>(set);
		this.currentItem = new BitSet(set.size());
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.currentItem != null;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Set<T> next() {
		if(this.currentItem == null)
			throw new NoSuchElementException("No more elements");
		Set<T> result = new HashSet<T>();
		for(int i = 0; i < this.set.size(); i++)
			if(this.currentItem.length() > i && this.currentItem.get(i))
				result.add(this.set.get(i));
		this.currentItem = this.increment(this.currentItem);
		return result;
	}

	/** Increments the given bit set, returns null
	 * if an overflow happens.
	 * @param bitSet some bit set.
	 * @return the incremented bit set
	 */
	private BitSet increment(BitSet bitSet){
		boolean carry = true, tmp;
		int i = 0;
		while(carry){
			tmp = carry;
			carry = carry && bitSet.get(i);
			bitSet.set(i, tmp ^ bitSet.get(i));
			i++;
		}
		if(this.set.size() < i)
			return null;
		return bitSet;
	}
	


}

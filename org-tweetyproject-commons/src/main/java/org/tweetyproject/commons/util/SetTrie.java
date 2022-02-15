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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a general implementation of a set-trie as defined in
 * [Iztok Savnik: Index Data Structure for Fast Subset and Superset Queries. CD-ARES 2013: 134-148].
 * 
 * It is a data structure for storing sets that supports fast queries regarding
 * subset containment (removal not allowed though). 
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of elements in the sets of the trie
 */
public class SetTrie<T extends Comparable<T>> {
	private class SetTrieNode{
		private boolean marked;
		private Map<T,SetTrieNode> children;
		
		public SetTrieNode(SetTrieNode parent) {
			this.marked = false;
			this.children = new HashMap<>();
		}		
		
		public boolean add(List<T> set, int index, boolean onlyForSubsetTests) {
			if(index >= set.size()) {
				if(this.marked)
					return false;				
				this.marked = true;
				if(onlyForSubsetTests)
					// no children needed
					this.children = new HashMap<>();
				return true;
			}else {
				if(!this.children.containsKey(set.get(index)))
					this.children.put(set.get(index), new SetTrieNode(this));
				return this.children.get(set.get(index)).add(set, index+1, onlyForSubsetTests);
			}
		}
		
		public boolean contains(List<T> set, int index) {
			if(index >= set.size()) {
				return this.marked;
			}else {
				if(!this.children.containsKey(set.get(index)))
					return false;
				return this.children.get(set.get(index)).contains(set, index+1);
			}
		}		
		
		public boolean containsSubsetOf(List<T> set, int index) {
			if(this.marked)
				return true;
			if(index >= set.size())
				return false;
			if(this.children.containsKey(set.get(index)) && this.children.get(set.get(index)).containsSubsetOf(set, index+1))
				return true;
			return this.containsSubsetOf(set, index+1);
		}
		
		public int actualSize() {
			int n = 0;
			if(this.marked)
				n++;
			for(SetTrieNode node: this.children.values())
				n += node.actualSize();
			return n;
		}
	}
	
	private SetTrieNode root;
	private boolean onlyForSubsetTests;
	private int size;
	
	/**
	 * Creates a new set-trie
	 */
	public SetTrie() {
		this(false);
	}
	
	/**
	 * Creates a new set-trie
	 * @param onlyForSubsetTests if set to true, then
	 * this trie does not store all sets, but (basically)
	 * only minimal ones. Whenever a set is added and there
	 * is already a superset of that set contained, the larger
	 * is deleted.
	 */
	public SetTrie(boolean onlyForSubsetTests) {
		this.root = new SetTrieNode(null);
		this.onlyForSubsetTests = onlyForSubsetTests;
		this.size = 0;
	}
	
	public int size() {
		return this.size;
	}
	
	public int actualSize() {
		return this.root.actualSize();	
	}
	
	/**
	 * Inserts the given set into this set-trie
	 * @param set some set
	 * @return "true" if indeed a set has been added
	 */
	public boolean add(Collection<T> set) {
		if(this.onlyForSubsetTests && this.containsSubsetOf(set))
			return false;
		List<T> sorted = new ArrayList<T>(set);
		Collections.sort(sorted);		
		boolean reply = this.root.add(sorted,0,this.onlyForSubsetTests);
		if(reply)
			this.size++;
		return reply;
	}
	
	/**
	 * Checks whether the given set is contained in this set-trie.
	 * @param set some set
	 * @return "true" iff the given set is contained in this set-trie.
	 */
	public boolean contains(Collection<T> set) {
		List<T> sorted = new ArrayList<T>(set);
		Collections.sort(sorted);
		return this.root.contains(sorted, 0);
	}
	
	/**
	 * Checks whether there is a subset of the given
	 * set contained.
	 * @param set some set
	 * @return true if there is a subset of the given set
	 */
	public boolean containsSubsetOf(Collection<T> set) {
		List<T> sorted = new ArrayList<T>(set);
		Collections.sort(sorted);
		return this.root.containsSubsetOf(sorted, 0);
	}
}

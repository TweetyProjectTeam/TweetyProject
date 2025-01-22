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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class implements a set-trie, a data structure for storing sets efficiently.
 * It supports fast queries for subset containment and can be configured to store only
 * minimal sets when used for subset testing. The implementation is based on the set-trie
 * as defined in the paper:
 * <br>
 * Iztok Savnik: "Index Data Structure for Fast Subset and Superset Queries." CD-ARES 2013: 134-148.
 * <br><br>
 * The set-trie allows the addition of sets and supports queries for checking whether a specific
 * set or a subset of a given set is contained within the trie.
 *
 * Note that the removal of sets is not supported in this implementation.
 *
 * @param <T> The type of elements in the sets stored in the trie. Elements must be comparable or a comparator must be provided.
 *
 * @author Matthias Thimm
 */
public class SetTrie<T> {

    /**
     * This class represents a node in the set-trie. Each node contains a map of child nodes and
     * can be marked to indicate that a complete set ends at this node.
     */
    private class SetTrieNode {
        private boolean marked;
        private Map<T, SetTrieNode> children;

        /**
         * Creates a new node in the set-trie.
         *
         * @param parent The parent node of this node (unused in the current implementation).
         */
        public SetTrieNode(SetTrieNode parent) {
            this.marked = false;
            this.children = new HashMap<>();
        }
        
        /**
         * Returns the set of all sets represented by this node
         * @return the set of all sets represented by this node
         */
        public Collection<Collection<T>> sets(){
        	Collection<Collection<T>> sets = new HashSet<>();
        	for(T child: this.children.keySet()) {
        		Collection<Collection<T>> sub_sets = this.children.get(child).sets();
        		for(Collection<T> set: sub_sets) {
        			set.add(child);
        			sets.add(set);
        		}
        	}
        	if(this.marked)
        		sets.add(new HashSet<>());
        	return sets;
        }

        /**
         * Adds a sorted set to the trie starting at this node.
         *
         * @param set The list of sorted elements to add.
         * @param index The current index in the list of elements.
         * @param onlyForSubsetTests If true, the trie only stores minimal sets.
         * @return true if a new set was added, false if the set already exists.
         */
        public boolean add(List<T> set, int index, boolean onlyForSubsetTests) {
            if (index >= set.size()) {
                if (this.marked) {
                    return false;
                }
                this.marked = true;
                if (onlyForSubsetTests) {
                    this.children = new HashMap<>();  // clear children if only testing subsets
                }
                return true;
            } else {
                this.children.putIfAbsent(set.get(index), new SetTrieNode(this));
                return this.children.get(set.get(index)).add(set, index + 1, onlyForSubsetTests);
            }
        }

        /**
         * Checks whether a given sorted set is contained in the trie starting at this node.
         *
         * @param set The list of sorted elements to check.
         * @param index The current index in the list of elements.
         * @return true if the set is contained, false otherwise.
         */
        public boolean contains(List<T> set, int index) {
            if (index >= set.size()) {
                return this.marked;
            }
            return this.children.containsKey(set.get(index)) && this.children.get(set.get(index)).contains(set, index + 1);
        }
        
        /**
         * Checks if this node contains a set with the given element.
         * @param elem some element
         * @return true iff this node contains a set with the given element.
         */
        public boolean containsElement(T elem) {
        	if(this.children.keySet().contains(elem))
        		return true;
        	for(T child: this.children.keySet())
        		if(this.children.get(child).containsElement(elem))
        			return true;
        	return false;
        }

        /**
         * Checks whether the trie contains a subset of the given sorted set starting at this node.
         *
         * @param set The list of sorted elements to check for subset containment.
         * @param index The current index in the list of elements.
         * @return true if a subset is found, false otherwise.
         */
        public boolean containsSubsetOf(List<T> set, int index) {
            if (this.marked) {
                return true;
            }
            if (index >= set.size()) {
                return false;
            }
            if (this.children.containsKey(set.get(index)) && this.children.get(set.get(index)).containsSubsetOf(set, index + 1)) {
                return true;
            }
            return this.containsSubsetOf(set, index + 1);
        }

        /**
         * Computes the number of sets stored in the trie.
         *
         * @return The number of sets stored in the trie.
         */
        public int actualSize() {
            int n = this.marked ? 1 : 0;
            for (SetTrieNode node : this.children.values()) {
                n += node.actualSize();
            }
            return n;
        }

        /**
         * Computes the total number of nodes in the trie.
         *
         * @return The total number of nodes in the trie.
         */
        public int numberOfNodes() {
            int n = 1;
            for (SetTrieNode node : this.children.values()) {
                n += node.numberOfNodes();
            }
            return n;
        }       
    }

    private SetTrieNode root;
    private boolean onlyForSubsetTests;
    private int size;
    private Comparator<T> comp;

    /**
     * Creates a new empty set-trie.
     */
    public SetTrie() {
        this(false,null);
    }

    /**
     * Creates a new empty set-trie, optionally configured for minimal subset storage.
     *
     * @param onlyForSubsetTests If true, the trie only stores minimal sets. Sets that have supersets in the trie
     *                           will not be added, and supersets will be replaced by smaller sets.
     */
    public SetTrie(boolean onlyForSubsetTests) {
        this(onlyForSubsetTests, null);
    }
    
    /**
     * Creates a new empty set-trie, where elements are compared wrt. the given comparator
     *
     * @param comp a comparator for elements of type T.
     */
    public SetTrie(Comparator<T> comp) {
    	this(false,comp);
    }
    
    /**
     * Creates a new empty set-trie, where elements are compared wrt. the given comparator, optionally configured for minimal subset storage.
     *
     * @param comp a comparator for elements of type T.
     * @param onlyForSubsetTests If true, the trie only stores minimal sets. Sets that have subsets in the trie
     *                           will not be added, and supersets will be replaced by smaller sets.
     */
    public SetTrie(boolean onlyForSubsetTests, Comparator<T> comp) {
    	this.root = new SetTrieNode(null);
        this.onlyForSubsetTests = onlyForSubsetTests;
        this.size = 0;
        // if no comparator is provided, we use the natural order of the type
        // if the type does not implement Comparable, this will give runtime
        // errors
        if(comp == null) {
        	this.comp = new Comparator<T>(){
				@SuppressWarnings("unchecked")
				@Override
				public int compare(T o1, T o2) {
					return ((Comparable<T>)o1).compareTo(o2);
				}};
        }else this.comp = comp;
    }

    /**
     * Returns the number of sets currently stored in the trie.
     *
     * @return The number of sets stored in the trie.
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns the number of sets actually stored in the trie (i.e., the number of unique sets).
     *
     * @return The number of sets stored in the trie.
     */
    public int actualSize() {
        return this.root.actualSize();
    }

    /**
     * Returns the total number of nodes in the trie, which includes all intermediate nodes.
     *
     * @return The total number of nodes in the trie.
     */
    public int numberOfNodes() {
        return this.root.numberOfNodes();
    }
    
    /**
     * Adds a set to the trie.
     *
     * @param set The set to add.
     * @return true if the set was successfully added, false if the set was already in the trie.
     */
    public boolean add(Collection<T> set) {
        if (this.onlyForSubsetTests && this.containsSubsetOf(set)) {
            return false;
        }
        List<T> sorted = new ArrayList<>(set);
        Collections.sort(sorted,this.comp);
        
        boolean added = this.root.add(sorted, 0, this.onlyForSubsetTests);
        if (added) {
            this.size++;
        }
        return added;
    }

    /**
     * Checks if a set is contained in the trie.
     *
     * @param set The set to check for.
     * @return true if the set is contained in the trie, false otherwise.
     */
    public boolean contains(Collection<T> set) {
        List<T> sorted = new ArrayList<>(set);
        Collections.sort(sorted,this.comp);
        return this.root.contains(sorted, 0);
    }

    /**
     * Checks if the trie contains a subset of the given set.
     *
     * @param set The set to check for subset containment.
     * @return true if a subset of the given set is contained in the trie, false otherwise.
     */
    public boolean containsSubsetOf(Collection<T> set) {
        List<T> sorted = new ArrayList<>(set);
        Collections.sort(sorted,this.comp);
        return this.root.containsSubsetOf(sorted, 0);
    }
    
    /**
     * Checks if the trie contains a set with the given element.
     * @param elem some element
     * @return true iff the trie contains a set with the given element.
     */
    public boolean containsElement(T elem) {
    	return this.root.containsElement(elem);
    }
    
    /**
     * Returns the set of all sets represented by this set trie
     * @return the set of all sets represented by this set trie
     */
    public Collection<Collection<T>> sets(){
    	Collection<Collection<T>> sets = this.root.sets();
    	if(this.root.marked)
    		sets.add(new HashSet<>());
    	return sets;
    }
}

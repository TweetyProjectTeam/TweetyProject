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
package org.tweetyproject.machinelearning.assoc;

import java.util.*;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.commons.util.SetTools;

/**
 * Implements the an FP-Tree for the FP-Growth Algorithm for frequent pattern mining, cf.
 * [Jiawei Han, Jian Pei, Yiwen Yin. Mining frequent patterns without candidate generation.
 * ACM SIGMOD Record, Volume 29, Issue 2, June 2000 pp 1–12]
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of items
 */
public class FrequentPatternTree<T extends Object> {
	private static int next_id = 1;
	// minimal support (absolute) of this tree
	private int minsupport_abs;
	// the root of the tree
	private FrequentPatternTreeNode<T> root;
	// the header table
	private List<Pair<T,Integer>> items = new ArrayList<>();	
	private List<FrequentPatternTreeNode<T>> items_first_node = new ArrayList<>();
	private List<FrequentPatternTreeNode<T>> items_last_node = new ArrayList<>();
	private Map<T,Integer> indexOf = new HashMap<>();
	
	public class FrequentPatternTreeNode<S extends Object>{		
		private int id;
		private S item;
		private int freq_abs;
		
		private FrequentPatternTreeNode<S> parent;
		private Map<S,FrequentPatternTreeNode<S>> children = new HashMap<>();		
		private FrequentPatternTreeNode<S> next_node = null;
		
		/**
		 * Creates a new tree node for the given item with the 
		 * given frequency of the path up to this item.
		 * @param item some item
		 * @param freq_abs the frequency of the path up to this item (as absolute value).
		 */
		public FrequentPatternTreeNode(S item, int freq_abs, FrequentPatternTreeNode<S> parent) {
			this.id = FrequentPatternTree.next_id++;
			this.item = item;
			this.freq_abs = freq_abs;
			this.parent = parent;
		}
		
		/**
		 * Returns the child for the given item (if it does not exist, null is returned)
		 * and increments the freq
		 * @param item some item
		 * @return the child for the given item (if it does not exist, null is returned)
		 */
		public FrequentPatternTreeNode<S> getChild(S item) {
			if(this.children.containsKey(item))				
				return this.children.get(item);
			return null;
		}
		
		/**
		 * Adds the child to this node
		 * @param node some node
		 */
		public void addChild(FrequentPatternTreeNode<S> node) {
			this.children.put(node.item, node);
		}
		
		@Override
		public String toString() {
			return this.toString(0);
		}
		
		/**
		 * Checks whether the tree rooted at this node
		 * is a single path
		 * @return "true" if the tree rooted at this node
		 * is a single path
		 */
		public boolean isSinglePath() {
			if(this.children.size() == 0)
				return true;
			if(this.children.size() > 1)
				return false;
			return this.children.values().iterator().next().isSinglePath();
		}
		
		/**
		 * Collects all items in this tree and returns them.
		 * @return a collection of all items appearing in this tree
		 */
		public Collection<S> getAllItems(){
			Collection<S> items = new HashSet<S>();
			if(this.item != null)
				items.add(this.item);
			for(FrequentPatternTreeNode<S> child: this.children.values())
				items.addAll(child.getAllItems());
			return items;
		}
		
		/**
		 * Returns a string representation of the node
		 * with <code>2*indent</code> leading white spaces
		 * @param indent some indent
		 * @return a string representation of the node
		 */
		public String toString(int indent) {
			String s = "";
			for(int i = 0; i < indent; i++) s+=" ";
			s += this.item + ":" + this.freq_abs + " (#" + this.id + ")";
			if(this.next_node != null)
				s += " -> " + this.next_node.id;
			s+= "\n";
			for(FrequentPatternTreeNode<S> n: this.children.values())
				s += n.toString(indent + 2);			
			return s;
		}
	}
	
	private class PairComparator implements Comparator<Pair<T,Integer>>{
		@Override
		public int compare(Pair<T, Integer> o1, Pair<T, Integer> o2) {
			return -1 * o1.getSecond().compareTo(o2.getSecond());
		}	
	}
	
	private class ItemComparator implements Comparator<T>{
		private Map<T,Integer> indexOf;
		public ItemComparator(Map<T,Integer> indexOf) {
			this.indexOf = indexOf;
		}
		@Override
		public int compare(T o1, T o2) {
			return this.indexOf.get(o1).compareTo(this.indexOf.get(o2));
		}	
	}
	
	/**
	 * Creates an FP-Tree for the given database with the given
	 * minimal support.
	 * @param database some set of transactions
	 * @param minsupport minimal support
	 */
	public FrequentPatternTree(Collection<Collection<T>> database, double minsupport) {
		this(FrequentPatternTree.toWeightedDatabase(database), (int) Math.ceil(minsupport * database.size()));
	}		
	
	/**
	 * Creates an FP-Tree for the given database with the given
	 * minimal support (as absolute value).
	 * @param database some set of transactions
	 * @param minsupport_abs minimal support (as absolute value)
	 */
	private FrequentPatternTree(Collection<Pair<Collection<T>,Integer>> database, int minsupport_abs) {
		this.minsupport_abs = minsupport_abs;		
		// gather frequent items		
		Map<T,Integer> supp_abs = new HashMap<>();
		for(Pair<Collection<T>,Integer> t: database) 
			for(T item: t.getFirst()) {
				if(!supp_abs.containsKey(item))
					supp_abs.put(item, 0);
				supp_abs.put(item, supp_abs.get(item)+t.getSecond());
			}
		// extract frequent items		
		Collection<T> freq_items = new HashSet<>(); 
		for(T item: supp_abs.keySet())
			if(supp_abs.get(item) >= this.minsupport_abs) {
				this.items.add(new Pair<>(item,supp_abs.get(item)));
				freq_items.add(item);
			}
		// sort this.items in descending order
		this.items.sort(new PairComparator());
		// BEGIN just to get the same tree as in the paper
		//Pair<T,Integer> a = this.items.get(0);
		//this.items.set(0, this.items.get(1));
		//this.items.set(1, a);
		//System.out.println(this.items);
		// END just to get the same tree as in the paper
		// initialise header table
		for(int i = 0; i < this.items.size(); i++) {
			this.items_first_node.add(null);
			this.items_last_node.add(null);
		}
		//create tree
		for(int i = 0; i < this.items.size(); i++)
			this.indexOf.put(this.items.get(i).getFirst(), i);
		ItemComparator icomp = new ItemComparator(this.indexOf);
		this.root = new FrequentPatternTreeNode<T>(null, 0, null);
		// add transaction data
		for(Pair<Collection<T>,Integer> t: database) {
			List<T> freq_trans = new ArrayList<>();
			// add all frequent items
			for(T item: t.getFirst())
				if(freq_items.contains(item))
					freq_trans.add(item);
			// sort list
			freq_trans.sort(icomp);
			this.insert_tree(freq_trans, root, t.getSecond());
		}		
		//System.out.println(root);
		//for(int i = 0; i < this.items_first_node.size(); i++)
		//	if(this.items_first_node.get(i) == null)
		//		System.out.println(this.items.get(i).getFirst() + ":null");
		//	else System.out.println(this.items.get(i).getFirst() + ":" + this.items_first_node.get(i).id);
		//System.exit(0);
	}
	
	/**
	 * Converts the given (unweighted) data base to weighted data base
	 * where every transaction has weight 1.
	 * @param database some data base
	 * @return the weighted version of the database
	 */
	private static <R extends Object> Collection<Pair<Collection<R>,Integer>> toWeightedDatabase(Collection<Collection<R>> database) {
		Collection<Pair<Collection<R>,Integer>> weighted_database = new LinkedList<>();
		for(Collection<R> t: database)
			weighted_database.add(new Pair<>(t,1));
		return weighted_database;
	}
	
	/**
	 * Inserts the given transaction (which only lists frequent items ordered by frequency) into the tree
	 * @param freq_trans a transaction (which only lists frequent items ordered by frequency)
	 * @param node the current node of the tree
	 * @param indexOf maps items to the index in the order
	 * @param weigth the multiplicity of the transaction
	 */
	private void insert_tree(List<T> freq_trans, FrequentPatternTreeNode<T> node, int weight) {
		while(freq_trans.size() != 0) {
			T next_item = freq_trans.get(0);
			freq_trans.remove(0);
			FrequentPatternTreeNode<T> next_node = node.getChild(next_item);
			if(next_node == null) {
				next_node = new FrequentPatternTreeNode<T>(next_item, 0, node);
				node.addChild(next_node);
				// update header table
				if(this.items_first_node.get(this.indexOf.get(next_item)) == null) {
					this.items_first_node.set(this.indexOf.get(next_item),next_node);
					this.items_last_node.set(this.indexOf.get(next_item),next_node);
				}else {
					this.items_last_node.get(this.indexOf.get(next_item)).next_node = next_node;
					this.items_last_node.set(this.indexOf.get(next_item),next_node);
				}
			}
			next_node.freq_abs += weight;
			node = next_node;	
		}
	}

	/**
	 * Extracts all frequent patterns from this tree.
	 * @return the set of all frequent patterns from this tree 
	 */
	public Collection<Collection<T>> extractFrequentPatterns(){
		return this.extractFrequentPatterns(new HashSet<>());
	}
	
	/**
	 * Extracts all frequent patterns from this tree plus <code>prefix</code>.
	 * @param prefix items to be added to each set.
	 * @return the set of all frequent patterns from this tree plus <code>prefix</code>. 
	 */
	public Collection<Collection<T>> extractFrequentPatterns(Collection<T> prefix){
		Collection<Collection<T>> result = new HashSet<Collection<T>>();
		if(this.root.isSinglePath()) {
			//generate all subsets of all items in this tree
			for(Set<T> subset: new SetTools<T>().subsets(this.root.getAllItems())) {
				if(subset.isEmpty())
					continue;
				subset.addAll(prefix);
				result.add(subset);
			}	
			return result;
		}else {			
			for(int i = this.items.size()-1; i >= 0; i--) {
				T a = this.items.get(i).getFirst();				
				// add singleton (plus prefix)
				Collection<T> singleton = new HashSet<>();
				singleton.add(a);
				singleton.addAll(prefix);
				result.add(singleton);
				// recursive call
				Collection<Pair<Collection<T>,Integer>> condBase = new LinkedList<>();
				FrequentPatternTreeNode<T> node = items_first_node.get(this.indexOf.get(a));
				while(node != null) {
					List<T> t = new LinkedList<T>();
					int weight = node.freq_abs;
					FrequentPatternTreeNode<T> sub_node = node.parent;
					while(sub_node.item != null) {
						t.add(0, sub_node.item);
						sub_node = sub_node.parent;
					}
					if(t.size() > 0)
						condBase.add(new Pair<>(t,weight));
					node = node.next_node;
				}
				if(condBase.size() > 0) {
					Collection<T> new_prefix = new HashSet<>(prefix);
					new_prefix.add(a);
					result.addAll(new FrequentPatternTree<T>(condBase,this.minsupport_abs).extractFrequentPatterns(new_prefix));
				}
			}
			return result;
		}
	}
	
}

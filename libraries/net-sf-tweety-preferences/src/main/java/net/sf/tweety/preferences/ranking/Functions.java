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
package net.sf.tweety.preferences.ranking;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.sf.tweety.preferences.PreferenceOrder;

/**
 * An abstract class as superclass for two different ranking/leveling functions used to rank elements from preference orders
 * 
 * @author Bastian Wolf
 *
 * @param <T>
 */

public abstract class Functions<T> extends HashMap<T, Integer> implements
Map<T, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * returns a string representation for this ranking function
	 */
	public String toString() {
		String s = "{";
		int count = 1;
		for (Entry<T, Integer> e : this.entrySet()) {

			if (count < this.entrySet().size())
				s += e.toString() + ", ";
			else
				s += e.toString();
			count++;
		}
		s += "}";

		return s;
	}

	/**
	 * returns the ranking function
	 * 
	 * @return ranking function
	 */
	public Map<T, Integer> getLevelingFunction() {
		return this;
	}
	
	/**
	 * this method returns a preference order made out of an ranking function
	 * 
	 * @return a preference order out of a given ranking function
	 */
	public abstract PreferenceOrder<T> generatePreferenceOrder();
	
	

	/**
	 * checks whether the key is present in the entry-set of the map
	 */
	@Override
	public boolean containsKey(Object key) {
		for (Entry<T, Integer> o : this.entrySet()) {
			if (o.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks whether the value is present in the entry-set of the map
	 */
	@Override
	public boolean containsValue(Object value) {
		Iterator<Entry<T, Integer>> temp = this.entrySet().iterator();
		if (temp.hasNext()) {
			while (temp.hasNext()) {
				if (temp.next().getValue().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns the value to a given key
	 * 
	 * @return the value if present, null otherwise (but value.equals(null) is
	 *         possible)
	 */
	@Override
	public Integer get(Object key) {
		Iterator<Entry<T, Integer>> temp = this.entrySet().iterator();
		if (temp.hasNext()) {
			while (temp.hasNext()) {
				Entry<T, Integer> e = temp.next();
				if (e.getKey().toString().equals(key.toString())) {
					return e.getValue();
				}
			}
		}
		return null;
	}

	
	/**
	 * returns a collection containing all values of the map
	 */
	@Override
	public Collection<Integer> values() {
		Set<Integer> v = new HashSet<Integer>();
		Iterator<Entry<T, Integer>> temp = this.entrySet().iterator();
		if (temp.hasNext()) {
			while (temp.hasNext()) {
				Entry<T, Integer> e = temp.next();
				v.add(e.getValue());
			}
		}
		return v;
	}

	/**
	 * returns an entry set of all elements in this ranking function with the same rank (= value)
	 * @param val the rank of the elements searched
	 * @return an entry set of all elements in this ranking function with the same rank (= value)
	 */
	public Set<Entry<T, Integer>> getElementsByValue(int val){
		Set<Entry<T, Integer>> temp = new HashSet<Entry<T,Integer>>();
		if(this.containsValue(val)){
			for(Entry<T, Integer> e : this.entrySet()){
				if(e.getValue().equals(val)){
					temp.add(e);
				}
			}
		}
		return temp;
	}
	
	
	/**
	 * returns a set of predecessor elements for the given entry
	 * @param element the given entry 
	 * @return a set of predecessor elements for the given entry
	 */
	public Set<Entry<T, Integer>> getPredecessors(Entry<T, Integer> element){
		return getElementsByValue(element.getValue()-1);
	}
	
	
	/**
	 * returns a set of successor elements for the given entry
	 * @param element the given entry 
	 * @return a set of successor elements for the given entry
	 */
	public Set<Entry<T, Integer>> getSuccessors(Entry<T, Integer> element){
		return getElementsByValue(element.getValue()+1);
	}

	/**
	 * weakens the given element in the function
	 * @param element the element being weakened
	 */
	public abstract void weakenElement(T element);
	
	/**
	 * strengthens the given element in the function
	 * @param element the element being strengthened 
	 */
	public abstract void strengthenElement(T element);
	
}

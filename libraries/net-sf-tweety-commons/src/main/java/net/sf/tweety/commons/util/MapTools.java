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

import java.util.*;

/**
 * This class provides some utility functions for maps.
 * @author Matthias Thimm
 *
 * @param <E> Domain class of the maps
 * @param <F> Range class of the maps
 */
public class MapTools<E,F> {

	/**
	 * Computes the complete set of maps from E to F such that the following
	 * condition holds. For every map "m" in the result the set m.keySet()
	 * is equal to the union of all sets S with S in relations.keySet() and each
	 * element "e" in m.keySet() is mapped to an element "f" such that "f" in
	 * relations.get(S) with "e" in S. For example the map:<br>
	 * {a,b}   =&gt; {1,2}<br>
	 * {c,d}   =&gt; {3}<br>
	 * {f}     =&gt; {4,5}<br>
	 * yields the set of maps:<br>
	 * a=&gt;1, b=&gt;1, c=&gt;3, d=&gt;3, f=&gt;4<br>
	 * a=&gt;1, b=&gt;1, c=&gt;3, d=&gt;3, f=&gt;5<br>
	 * a=&gt;1, b=&gt;2, c=&gt;3, d=&gt;3, f=&gt;4<br>
	 * a=&gt;1, b=&gt;2, c=&gt;3, d=&gt;3, f=&gt;5<br>
	 * a=&gt;2, b=&gt;1, c=&gt;3, d=&gt;3, f=&gt;4<br>
	 * a=&gt;2, b=&gt;1, c=&gt;3, d=&gt;3, f=&gt;5<br>
	 * a=&gt;2, b=&gt;2, c=&gt;3, d=&gt;3, f=&gt;4<br>
	 * a=&gt;2, b=&gt;2, c=&gt;3, d=&gt;3, f=&gt;5<br>
	 * @param relations a map from sets of E to sets of F.
	 * @return a set of maps from E to F.
	 */
	public Set<Map<E,F>> allMaps(Map<Set<E>,Set<F>> relations){
		Set<Set<Map<E,F>>> maps = new HashSet<Set<Map<E,F>>>();
		for(Set<E> e : relations.keySet())
			maps.add(this.allMaps(e,relations.get(e)));
		Collection<Set<Map<E,F>>> permutations = new SetTools<Map<E,F>>().permutations(maps);
		Set<Map<E,F>> result = new HashSet<Map<E,F>>();
		for(Set<Map<E,F>> mapSet: permutations){
			result.add(this.combine(mapSet));
		}	
		return result;
	}
	
	/**
	 * Computes the complete set of maps from E to F such that the following
	 * condition holds. For every map "m" in the result the set m.keySet()
	 * is equal to relations.keySet() and each
	 * element "e" in m.keySet() is mapped to an element "f" such that "f" in
	 * relations.get(S) with "e" in S. For example the map:<br>
	 * a   =&gt; {1,2}<br>
	 * b   =&gt; {3}<br>
	 * c   =&gt; {4,5}<br>
	 * yields the set of maps:<br>
	 * a=&gt;1, b=&gt;3, c=&gt;4<br>
	 * a=&gt;1, b=&gt;3, c=&gt;5<br>
	 * a=&gt;2, b=&gt;3, c=&gt;4<br>
	 * a=&gt;2, b=&gt;3, c=&gt;5<br>
	 * @param relations a map from sets of E to sets of F.
	 * @return a set of maps from E to F.
	 */
	public Set<Map<E,F>> allMapsSingleSource(Map<E,Set<F>> relations){
		Set<Map<E,F>> result = new HashSet<Map<E,F>>();
		result.add(new HashMap<E,F>());
		for(E key: relations.keySet()){
			Set<Map<E,F>> newResult = new HashSet<Map<E,F>>();
			for(Map<E,F> map: result){
				for(F val: relations.get(key)){
					Map<E,F> newMap = new HashMap<E,F>(map);
					newMap.put(key, val);
					newResult.add(newMap);
				}
			}
			result = newResult;
		}	
		return result;
	}
	
	/** Computes all bijections from E to F.
	 * E and F have to be of the same cardinality.
	 * @param domain some set.
	 * @param range some set.
	 * @return all bijections from E to F.
	 */
	public Set<Map<E,F>> allBijections(Collection<E> domain, Collection<F> range){
		if(domain.size() != range.size())
			throw new IllegalArgumentException("Domain and range have to be of the same cardinality");
		Set<Map<E,F>> result = new HashSet<Map<E,F>>();
		if(domain.size() == 1){
			Map<E,F> newMap = new HashMap<E,F>();
			newMap.put(domain.iterator().next(), range.iterator().next());
			result.add(newMap);
			return result;
		}
		E elem = domain.iterator().next();
		Set<E> newDomain = new HashSet<E>(domain);
		newDomain.remove(elem);
		for(F elem2: range){			
			Set<F> newRange = new HashSet<F>(range);
			newRange.remove(elem2);
			Set<Map<E,F>> subResult = this.allBijections(newDomain, newRange);
			for(Map<E,F> map: subResult){
				map.put(elem, elem2);
				result.add(map);
			}			
		}		
		return result;
	}
	
	/**
	 * This methods computes all maps from domain to range.
	 * @param domain a set of elements.
	 * @param range a set of elements
	 * @return a set of maps, where every map maps any element of domain to an
	 * 		element of range.
	 */
	public Set<Map<E,F>> allMaps(Set<? extends E> domain, Set<? extends F> range){
		Set<Map<E,F>> allMaps = new HashSet<Map<E,F>>();
		Stack<Pair<Map<E,F>,Stack<E>>> stack = new Stack<Pair<Map<E,F>,Stack<E>>>();
		Pair<Map<E,F>,Stack<E>> elem = new Pair<Map<E,F>,Stack<E>>();
		elem.setFirst(new HashMap<E,F>());
		elem.setSecond(new Stack<E>());
		elem.getSecond().addAll(domain);
		stack.push(elem);
		while(!stack.isEmpty()){
			elem = stack.pop();
			if(elem.getSecond().isEmpty()){
				allMaps.add(elem.getFirst());
			}else{
				E domelem = elem.getSecond().pop();
				for(F image: range){
					Map<E,F> newMap = new HashMap<E,F>(elem.getFirst());
					newMap.put(domelem, image);
					Stack<E> newStack = new Stack<E>();
					newStack.addAll(elem.getSecond());
					stack.push(new Pair<Map<E,F>,Stack<E>>(newMap,newStack));					
				}
			}
		}		
		return allMaps;
	}
	
	/**
	 * Combines all maps in singleMaps to one maps containing
	 * every assignment of each map in singleMaps.
	 * @param singleMaps the set of maps to be combined.
	 * @return a single map.
	 * @throws IllegalArgumentException if one key is used
	 *  	in more than one map of singleMaps.
	 */
	public Map<E,F> combine(Set<Map<E,F>> singleMaps)throws IllegalArgumentException{
		Map<E,F> result = new HashMap<E,F>();
		for(Map<E,F> map: singleMaps){
			for(E key: map.keySet()){
				if(result.containsKey(key))
					throw new IllegalArgumentException("Value of key " + key + " is ambiguous.");
				result.put(key, map.get(key));
			}
		}
		return result;
	}
	
	/**
	 * Checks whether the given map is injective, i.e. whether no two different keys
	 * are assigned the same value.
	 * @param map a map
	 * @return "true" iff the given map is injective.
	 */
	public static boolean isInjective(Map<? extends Object,? extends Object> map){
		for(Object key1: map.keySet())
			for(Object key2: map.keySet())
				if(key1 != key2)
					if(map.get(key1).equals(map.get(key2)))
						return false;
		return true;
	}
}

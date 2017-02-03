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
 * This class provides some methods for set operations.
 * @author Matthias Thimm
 */
public class SetTools<E> {

	/**
	 * This method computes all subsets of the given set of elements
	 * of class "E".
	 * @param elements a set of elements of class "E".
	 * @return all subsets of "elements".
	 */
	public Set<Set<E>> subsets(Collection<? extends E> elements){
		Set<Set<E>> subsets = new HashSet<Set<E>>();		
		if(elements.size() == 0){
			subsets.add(new HashSet<E>());
		}else{
			E element = elements.iterator().next();
			Set<E> remainingElements = new HashSet<E>(elements);
			remainingElements.remove(element);
			Set<Set<E>> subsubsets = this.subsets(remainingElements);
			for(Set<E> subsubset: subsubsets){
				subsets.add(new HashSet<E>(subsubset));
				subsubset.add(element);
				subsets.add(new HashSet<E>(subsubset));				
			}				
		}
		return subsets;
	}	
	
	/**
	 * This method computes all subsets of the given set of elements
	 * of class "E" with the given size.
	 * @param elements a set of elements of class "E".
	 * @param size some int.
	 * @return all subsets of "elements" of the given size.
	 */
	public Set<Set<E>> subsets(Collection<? extends E> elements, int size){
		if(size < 0)
			throw new IllegalArgumentException("Size must be at least zero.");
		Set<Set<E>> subsets = new HashSet<Set<E>>();		
		if(size == 0){
			subsets.add(new HashSet<E>());
			return subsets;
		}		
		if(elements.size() < size)
			return subsets;		
		if(elements.size() == size){
			subsets.add(new HashSet<E>(elements));
			return subsets;
		}			
		if(size == 1){
			for(E e: elements){
				Set<E> set = new HashSet<E>();
				set.add(e);
				subsets.add(set);
			}
			return subsets;				
		}
		E element = elements.iterator().next();
		Set<E> remainingElements = new HashSet<E>(elements);
		remainingElements.remove(element);
		Set<Set<E>> subsubsets = this.subsets(remainingElements,size-1);
		for(Set<E> subsubset: subsubsets){
			subsubset.add(element);
			subsets.add(new HashSet<E>(subsubset));				
		}
		subsubsets = this.subsets(remainingElements,size);
		for(Set<E> subsubset: subsubsets)		
			subsets.add(new HashSet<E>(subsubset));		
		return subsets;
	}	
	
	/**
	 * Computes all permutations of elements in partitions as follows.
	 * For any set A in the result and any set B in partitions it holds,
	 * that exactly one element of B is in A. For example<br>
	 * permutations({{a,b},{c,d,e},{f}})<br>
	 * equals to<br>
	 * {{a,c,f},{b,c,f},{a,d,f},{b,d,f},{a,e,f},{b,e,f}}
	 * @param partitions a set of sets of E.
	 * @return a set of sets of E.
	 */
	public Set<Set<E>> permutations(Set<Set<E>> partitions){		
		if(partitions.size() == 0){
			partitions.add(new HashSet<E>());
			return partitions;
		}
		Set<Set<E>> result = new HashSet<Set<E>>();
		Set<E> set = partitions.iterator().next();
		Set<Set<E>> remaining = new HashSet<Set<E>>(partitions);
		remaining.remove(set);
		Set<Set<E>> subresult = this.permutations(remaining);
		for(Set<E> subresultset: subresult){
			for(E item: set){
				Set<E> newSet = new HashSet<E>();
				newSet.addAll(subresultset);
				newSet.add(item);
				result.add(newSet);				
			}
		}			
		return result;						
	}		
	
	/**
	 * Computes the set of irreducible hitting sets of "sets". A hitting set
	 * H is a set that has a non-empty intersection with every set in "sets".
	 * H is irreducible if no proper subset of H is a hitting set.
	 * @param sets a set of sets
	 * @return the set of all irreducible hitting sets of "sets"
	 */
	public Set<Set<E>> irreducibleHittingSets(Set<Set<E>> sets){
		// naive implementation, should be revised at some time
		Set<Set<E>> result;
		// if there is no set to hit, there are no hitting sets
		if(sets.size() == 0)
			return new HashSet<Set<E>>();;
		// if there is only one set to hit, every element of that set
		// forms a hitting set
		if(sets.size() == 1){
			result = new HashSet<Set<E>>();
			Set<E> h;
			for(E e: sets.iterator().next()){
				h = new HashSet<E>();
				h.add(e);
				result.add(h);
			}
			return result;	
		}
		// if more than one set is to be hit, we recursively build up hitting sets
		Set<E> current = sets.iterator().next();
		Set<Set<E>> new_sets = new HashSet<Set<E>>();
		new_sets.addAll(sets);
		new_sets.remove(current);
		// recursively solve the problem 
		result = irreducibleHittingSets(new_sets);
		// now check whether the current set is already hit; if not add some element
		Set<E> tmp;
		Set<Set<E>> new_result = new HashSet<Set<E>>();
		for(Set<E> h: result){
			tmp = new HashSet<E>();
			tmp.addAll(current);
			tmp.retainAll(h);
			if(tmp.size() == 0){
				for(E e: current){
					tmp= new HashSet<E>();
					tmp.addAll(h);
					tmp.add(e);
					new_result.add(tmp);
				}
			}else new_result.add(h);
		}
		// check for irreducibility
		result.clear();
		for(Set<E> h: new_result){
			result.add(h);
			for(Set<E> h2: new_result){
				if(h != h2)
					if(h.containsAll(h2)){
						result.remove(h);
						break;
					}
			}
		}
		return result;
	}
	
	/** Checks whether the given set of sets has an empty intersection
	 * @param sets some set of sets
	 * @return true iff the all sets have an empty intersection.
	 */
	public boolean hasEmptyIntersection(Set<Set<E>> sets){
		Set<E> i = new HashSet<E>();
		i.addAll(sets.iterator().next());
		for(Set<E> s: sets)
			i.retainAll(s);
		return i.isEmpty();		
	}
	
	/**
	 * Returns the union of the set of sets.
	 * @param sets some set of sets
	 * @return the union of the set.
	 */
	public Set<E> getUnion(Set<Set<E>> sets){
		Set<E> result = new HashSet<E>();
		for(Set<E> s: sets)
			result.addAll(s);
		return result;
	}
		
	/**
	 * Computes every bipartition of the given set, e.g. for
	 * a set {a,b,c,d,e,f} this method returns a set containing for example
	 * {{a,b,c,d,e},{f}} and {{a,b,c,},{d,e,f}} and {{a,b,c,d,e,f},{}}
	 * and {{a,d,e},{b,c,f}}.   
	 * @param set a set of E
	 * @return the set of all bipartitions of the given set.
	 */
	public Set<Set<Set<E>>> getBipartitions(Set<E> set){
		Set<Set<E>> subsets = this.subsets(set);
		Set<Set<Set<E>>> bipartitions = new HashSet<Set<Set<E>>>();
		for(Set<E> partition1: subsets){
			Set<E> partition2 = new HashSet<E>(set);
			partition2.removeAll(partition1);
			Set<Set<E>> bipartition = new HashSet<Set<E>>();
			bipartition.add(partition1);
			bipartition.add(partition2);
			bipartitions.add(bipartition);
		}
		return bipartitions;
	}
	
	/**
	 * Returns the symmetric difference of the two sets s and t, i.e.
	 * it returns (s \cup t) \setminus (s \cap t).
	 * @param s some set
	 * @param t some set
	 * @return the symmetric difference of the two sets
	 */
	public Set<E> symmetricDifference(Collection<E> s, Collection<E> t){
		Set<E> result = new HashSet<E>();
		Set<E> isec = new HashSet<E>(s);
		isec.retainAll(t);
		result.addAll(s);
		result.addAll(t);
		result.removeAll(isec);
		return result;
	}
	
	/**
	 * Returns all independent sets of the given cardinality of the given set of sets.
	 * A set M={M1,...,Mk} is an independent set of N={N1,...,Nl} if M\subseteq N and
	 * for all i,j, i\neq j, Mi\cap Mj=\emptyset.  <br/>
	 * This method uses a brute force approach to determine these sets.
	 * 
	 * @param sets a set of sets
	 * @return all independent sets of the given cardinality of the given set of sets
	 */
	public Set<Set<Collection<E>>> independentSets(Set<Collection<E>> sets, int cardinality){
		Set<Set<Collection<E>>> result = new HashSet<>();
		Set<Set<Collection<E>>> candidates = new SetTools<Collection<E>>().subsets(sets, cardinality);
		for(Set<Collection<E>> candidate: candidates)
			if(this.isIndependent(candidate))
				result.add(candidate);
		return result;
	}
	
	/**
	 * Checks whether the given set of sets is independent, i.e. whether
	 * all pairs of sets are disjoint.
	 * @param set a set of sets
	 * @return "true" if the given set of sets is independent.
	 */
	public boolean isIndependent(Set<Collection<E>> set){
		for(Collection<E> s1: set)
			for(Collection<E> s2: set)
				if(s1 != s2)
					for(E elem: s1)
						if(s2.contains(elem))
							return false;
		return true;
	}
}

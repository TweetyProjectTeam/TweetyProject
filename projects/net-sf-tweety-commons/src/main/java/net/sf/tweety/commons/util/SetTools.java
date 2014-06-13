/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
}

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
package org.tweetyproject.logics.commons.analysis;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.commons.Formula;

/**
 * A simple approach to compute minimal inconsistent subsets and maximal
 * consistent subsets by exhaustive search.
 *  
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas.
 */
public class NaiveMusEnumerator<S extends Formula> extends AbstractMusEnumerator<S>{
	
	/** Used for making consistency checks. */
	private BeliefSetConsistencyTester<S> tester;
	
	/**
	 * Creates a new naive MusEnumerator that uses the given consistency tester.
	 * @param tester some consistency tester
	 */
	public NaiveMusEnumerator(BeliefSetConsistencyTester<S> tester){
		this.tester = tester;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.MusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	public Collection<Collection<S>> minimalInconsistentSubsets(Collection<S> beliefSet){
		if(this.tester.isConsistent(beliefSet))
			return new HashSet<Collection<S>>();
		Collection<Collection<S>> result = new HashSet<Collection<S>>();
		Collection<Collection<S>> candidates = new HashSet<Collection<S>>();
		Collection<Collection<S>> new_candidates;
		Collection<Collection<S>> tmp = new HashSet<Collection<S>>();
		boolean m;
		// start with singletons
		Collection<S> candidate;
		for(S f: beliefSet){
			candidate = new HashSet<S>();
			candidate.add(f);
			candidates.add(candidate);
		}		
		while(!candidates.isEmpty()){
			new_candidates = new HashSet<Collection<S>>();
			for(Collection<S> cand: candidates)
				if(!this.tester.isConsistent(cand)){
					//remove super sets erroneously added
					//and check for smaller mis
					tmp.clear();
					m = true;
					for(Collection<S> mi: result){						
						if(mi.containsAll(cand))
							tmp.add(mi);
						if(cand.containsAll(mi)){
							m = false;
							break;
						}
					}
					result.removeAll(tmp);
					if(m) result.add(cand);
				}
				else new_candidates.add(cand);
			new_candidates = this.merge(new_candidates);
			// remove candidates that already contain a minimal inconsistent subset
			candidates = new HashSet<Collection<S>>();
			boolean contains;
			for(Collection<S> cand: new_candidates){
				contains = false;
				for(Collection<S> mi: result){					
					if(cand.containsAll(mi)){
						contains = true;
						break;
					}						
				}
				if(!contains)
					candidates.add(cand);
			}
		}		
		return result;
	}
	
	/** Auxiliary method that combines every two collections into one.
	 * @param formulas a set of sets of formulas
	 * @return a set of sets of formulas
	 */
	private Collection<Collection<S>> merge(Collection<Collection<S>> formulas){
		 Collection<Collection<S>> result = new HashSet<Collection<S>>();
		 Collection<S> merged;
		 for(Collection<S> f1: formulas)
			 for(Collection<S> f2: formulas)
				 if(f1 != f2){
					 merged = new HashSet<S>();
					 merged.addAll(f1);
					 merged.addAll(f2);
					 result.add(merged);
				 }
		 return result;
	}
	
	//Deprecated, slower version to compute minimal inconsistent subsets
	//public Collection<Collection<S>> minimalInconsistentSubsets(T beliefSet){
	//	Collection<Collection<S>> result = new HashSet<Collection<S>>();
	//	if(this.isConsistent(beliefSet))
	//		return result;
	//	Stack<Collection<S>> subsets = new Stack<Collection<S>>();
	//	subsets.addAll(new SetTools<S>().subsets(beliefSet));
	//	while(!subsets.isEmpty()){
	//		Collection<S> subset = subsets.pop();
	//		if(!this.isConsistent(subset)){
	//			// remove all super sets of subset from result
	//			Collection<Collection<S>> toBeRemoved = new HashSet<Collection<S>>();
	//			for(Collection<S> set: result)
	//				if(set.containsAll(subset))
	//					toBeRemoved.add(set);
	//			result.removeAll(toBeRemoved);
	//			// remove all super sets of subset from the stack
	//			toBeRemoved = new HashSet<Collection<S>>();
	//			for(Collection<S> set: subsets)
	//				if(set.containsAll(subset))
	//					toBeRemoved.add(set);
	//			subsets.removeAll(toBeRemoved);			
	//			result.add(subset);
	//		}
	//	}
	//	return result;
	//}
}

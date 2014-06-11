package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.Formula;

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
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#minimalInconsistentSubsets(java.util.Collection)
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
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#maximalConsistentSubsets(java.util.Collection)
	 */
	public Collection<Collection<S>> maximalConsistentSubsets(Collection<S> beliefSet){
		// remove a formula from every minimal inconsistent subset
		Collection<Collection<S>> minIncon = this.minimalInconsistentSubsets(beliefSet);
		Collection<Collection<S>> result = this.maximalConsistentSubsets(beliefSet, minIncon, new HashSet<S>());
		// check if there are still some non-maximal consistent subsets
		Collection<Collection<S>> toBeRemoved = new HashSet<Collection<S>>();
		for(Collection<S> s: result){
			boolean remove = false;
			for(Collection<S> s2 : result){
				if(s != s2 && s2.containsAll(s)){
					remove = true;
					break;
				}						
			}
			if(remove) toBeRemoved.add(s);
		}
		result.removeAll(toBeRemoved);
		return result;
	}
	
	/**
	 * Auxiliary method for determining the maximal consistent subsets. Removes one formula
	 * from some minIncon and recursively calls the same method.
	 * @param beliefSet some belief sets
	 * @param minIncon the (remaining) minimal inconsistent subsets that have to be considered. 
	 * @param toBeRemoved the formulas that will be removed at the end.
	 * @return the maximal consistent subsets of the given
	 *  belief set.
	 */
	private Collection<Collection<S>> maximalConsistentSubsets(Collection<S> beliefSet, Collection<Collection<S>> minIncon, Collection<S> toBeRemoved){
		// if there are no remaining maximal inconsistent subsets we are finished
		if(minIncon.isEmpty()){
			Collection<Collection<S>> result = new HashSet<Collection<S>>();
			Collection<S> newBeliefSet = new HashSet<S>();
			newBeliefSet.addAll(beliefSet);
			newBeliefSet.removeAll(toBeRemoved);
			result.add(newBeliefSet);
			return result;			
		}		
		// select one minimal inconsistent subset
		Collection<S> mi = minIncon.iterator().next();
		Collection<Collection<S>> newMinIncon = new HashSet<Collection<S>>();
		newMinIncon.addAll(minIncon);
		newMinIncon.remove(mi);
		// if one formula from mi has already been removed then skip removing another one
		boolean nonEmptyCut = false;
		for(S f: mi){
			if(toBeRemoved.contains(f)){
				nonEmptyCut = true;
				break;
			}				
		}		
		if(nonEmptyCut)
			return this.maximalConsistentSubsets(beliefSet, newMinIncon, toBeRemoved);
		// consider removing some formula from mi
		Collection<Collection<S>> result = new HashSet<Collection<S>>();
		for(S f: mi){
			Collection<S> newToBeRemoved = new HashSet<S>();
			newToBeRemoved.addAll(toBeRemoved);
			newToBeRemoved.add(f);
			result.addAll(this.maximalConsistentSubsets(beliefSet, minIncon, newToBeRemoved));
		}
		return result;
	}
}

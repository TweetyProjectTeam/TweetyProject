package net.sf.tweety.logics.commons.analysis;

import java.util.*;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;
import net.sf.tweety.util.*;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent and providing
 * the minimal inconsistent subsets. 
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractBeliefSetConsistencyTester<S extends Formula,T extends BeliefSet<S>> implements BeliefSetConsistencyTester<S,T> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#minimalInconsistentSubsets(net.sf.tweety.BeliefSet)
	 */
	public Collection<Collection<S>> minimalInconsistentSubsets(T beliefSet){
		Collection<Collection<S>> result = new HashSet<Collection<S>>();
		if(this.isConsistent(beliefSet))
			return result;
		Stack<Collection<S>> subsets = new Stack<Collection<S>>();
		subsets.addAll(new SetTools<S>().subsets(beliefSet));
		while(!subsets.isEmpty()){
			Collection<S> subset = subsets.pop();
			if(!this.isConsistent(subset)){
				// remove all super sets of subset from result
				Collection<Collection<S>> toBeRemoved = new HashSet<Collection<S>>();
				for(Collection<S> set: result)
					if(set.containsAll(subset))
						toBeRemoved.add(set);
				result.removeAll(toBeRemoved);
				// remove all super sets of subset from the stack
				toBeRemoved = new HashSet<Collection<S>>();
				for(Collection<S> set: subsets)
					if(set.containsAll(subset))
						toBeRemoved.add(set);
				subsets.removeAll(toBeRemoved);			
				result.add(subset);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#maximalConsistentSubsets(net.sf.tweety.BeliefSet)
	 */
	public Collection<Collection<S>> maximalConsistentSubsets(T beliefSet){
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
	private Collection<Collection<S>> maximalConsistentSubsets(T beliefSet, Collection<Collection<S>> minIncon, Collection<S> toBeRemoved){
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
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyTester#isConsistent(net.sf.tweety.BeliefBase)
	 */
	@Override
	public boolean isConsistent(T beliefSet){
		return this.isConsistent((Collection<S>) beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	public abstract boolean isConsistent(Collection<S> formulas);
}
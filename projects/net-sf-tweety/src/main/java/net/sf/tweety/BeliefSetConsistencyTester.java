package net.sf.tweety;

import java.util.*;

import net.sf.tweety.util.*;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent and providing
 * the minimal inconsistent subsets. 
 * 
 * @author Matthias Thimm
 */
public abstract class BeliefSetConsistencyTester implements ConsistencyTester {

	/**
	 * This method returns the minimal inconsistent subsets of the given
	 * belief set.<br>
	 * TODO: make this method efficient
	 * @param beliefSet a belief set
	 * @return the minimal inconsistent subsets of the given
	 *  belief set.
	 */
	public Set<Set<Formula>> minimalInconsistentSubsets(BeliefSet<? extends Formula> beliefSet){
		Set<Set<Formula>> result = new HashSet<Set<Formula>>();
		if(this.isConsistent(beliefSet))
			return result;
		Stack<Set<Formula>> subsets = new Stack<Set<Formula>>();
		subsets.addAll(new SetTools<Formula>().subsets(beliefSet));
		while(!subsets.isEmpty()){
			Set<Formula> subset = subsets.pop();
			if(!this.isConsistent(subset)){
				// remove all super sets of subset from result
				Set<Set<Formula>> toBeRemoved = new HashSet<Set<Formula>>();
				for(Set<Formula> set: result)
					if(set.containsAll(subset))
						toBeRemoved.add(set);
				result.removeAll(toBeRemoved);
				// remove all super sets of subset from the stack
				toBeRemoved = new HashSet<Set<Formula>>();
				for(Set<Formula> set: subsets)
					if(set.containsAll(subset))
						toBeRemoved.add(set);
				subsets.removeAll(toBeRemoved);			
				result.add(subset);
			}
		}
		return result;
	}
	
	/**
	 * This method returns the maximal consistent subsets of the given
	 * belief set.<br>
	 * TODO: make this method efficient
	 * @param beliefSet a belief set
	 * @return the maximal consistent subsets of the given
	 *  belief set.
	 */
	public Set<Set<Formula>> maximalConsistentSubsets(BeliefSet<? extends Formula> beliefSet){
		// remove a formula from every minimal inconsistent subset
		Set<Set<Formula>> minIncon = this.minimalInconsistentSubsets(beliefSet);
		Set<Set<Formula>> result = this.maximalConsistentSubsets(beliefSet, minIncon, new HashSet<Formula>());
		// check if there are still some non-maximal consistent subsets
		Set<Set<Formula>> toBeRemoved = new HashSet<Set<Formula>>();
		for(Set<Formula> s: result){
			boolean remove = false;
			for(Set<Formula> s2 : result){
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
	private Set<Set<Formula>> maximalConsistentSubsets(BeliefSet<? extends Formula> beliefSet, Set<Set<Formula>> minIncon, Set<Formula> toBeRemoved){
		// if there are no remaining maximal inconsistent subsets we are finished
		if(minIncon.isEmpty()){
			Set<Set<Formula>> result = new HashSet<Set<Formula>>();
			Set<Formula> newBeliefSet = new HashSet<Formula>();
			newBeliefSet.addAll(beliefSet);
			newBeliefSet.removeAll(toBeRemoved);
			result.add(newBeliefSet);
			return result;			
		}		
		// select one minimal inconsistent subset
		Set<Formula> mi = minIncon.iterator().next();
		Set<Set<Formula>> newMinIncon = new HashSet<Set<Formula>>();
		newMinIncon.addAll(minIncon);
		newMinIncon.remove(mi);
		// if one formula from mi has already been removed then skip removing another one
		boolean nonEmptyCut = false;
		for(Formula f: mi){
			if(toBeRemoved.contains(f)){
				nonEmptyCut = true;
				break;
			}				
		}		
		if(nonEmptyCut)
			return this.maximalConsistentSubsets(beliefSet, newMinIncon, toBeRemoved);
		// consider removing some formula from mi
		Set<Set<Formula>> result = new HashSet<Set<Formula>>();
		for(Formula f: mi){
			Set<Formula> newToBeRemoved = new HashSet<Formula>();
			newToBeRemoved.addAll(toBeRemoved);
			newToBeRemoved.add(f);
			result.addAll(this.maximalConsistentSubsets(beliefSet, minIncon, newToBeRemoved));
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.ConsistencyTester#isConsistent(net.sf.tweety.BeliefBase)
	 */
	@Override
	public abstract boolean isConsistent(BeliefBase beliefBase);
	
	/**
	 * Checks whether the given set of beliefs (formulas) is consistent
	 * @param beliefs a set of formulas.
	 * @return "true" iff the given set is consistent.
	 */
	public abstract boolean isConsistent(Set<? extends Formula> beliefs);

}

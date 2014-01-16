package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class represents a multiple indicator utility function, i.e. a function
 * that ranks a set of propositions to 1 if this function's focal set
 * is part of the set, and 0 otherwise.
 * 
 * @author Matthias Thimm
 *
 */
public class MultipleIndicatorUtilityFunction implements UtilityFunction {

	/**
	 * The focal set of this function.
	 */
	private Set<Proposition> focalSet;
	
	/**
	 * Creates a new multiple indicator utility function for the given focal set.
	 * @param focalSet a collection of propositions.
	 */
	public MultipleIndicatorUtilityFunction(Collection<? extends Proposition> focalSet){
		this.focalSet = new HashSet<Proposition>(focalSet);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sas.UtilityFunction#rank(java.util.Collection)
	 */
	@Override
	public int rank(Collection<? extends Proposition> propositions) {
		if(propositions.containsAll(this.focalSet)) return 1;
		return 0;
	}

}

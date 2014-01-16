package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.logics.pl.syntax.*;


/**
 * This class represents a counting utility function, i.e. a function
 * that ranks a set of propositions to the number of common propositions
 * with this function's focal set.
 * 
 * @author Matthias Thimm
 *
 */
public class CountingUtilityFunction implements UtilityFunction {

	/**
	 * The focal set of this function.
	 */
	private Set<Proposition> focalSet;
	
	/**
	 * Creates a new counting utility function for the given focal set.
	 * @param focalSet a collection of propositions.
	 */
	public CountingUtilityFunction(Collection<? extends Proposition> focalSet){
		this.focalSet = new HashSet<Proposition>(focalSet);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sas.UtilityFunction#rank(java.util.Collection)
	 */
	@Override
	public int rank(Collection<? extends Proposition> propositions) {
		Set<Proposition> s = new HashSet<Proposition>(propositions);
		s.retainAll(this.focalSet);
		return s.size();
	}

}

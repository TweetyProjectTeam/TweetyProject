package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.logics.pl.syntax.*;

/**
 * This interface models an utility function, i.e. a 
 * function that maps sets of propositions to an integer and
 * thus ranking sets of propositions.
 * 
 * @author Matthias Thimm
 */
public interface UtilityFunction {

	/**
	 * Rank the given collection of propositions. A set S is preferred to
	 * a set T if rank(S)>rank(T).
	 * @param propositions a collection of propositions.
	 * @return the rank of the given collection.
	 */
	public int rank(Collection<? extends Proposition> propositions);
}

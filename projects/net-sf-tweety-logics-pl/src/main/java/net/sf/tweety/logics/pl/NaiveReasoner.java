package net.sf.tweety.logics.pl;

import net.sf.tweety.commons.BeliefBase;

/**
 * Naive classical inference  (checks all interpretations for satisfiability).
 * 
 * @author Matthias Thimm
 */
public class NaiveReasoner extends ClassicalInference {

	public NaiveReasoner(BeliefBase beliefBase) {
		super(beliefBase, new ClassicalEntailment());
	}

}

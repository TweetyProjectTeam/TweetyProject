package net.sf.tweety.logics.pl;

import net.sf.tweety.BeliefBase;

/**
 * Reasoner based on Sat4j.
 * 
 * @author Matthias Thimm
 */
public class Sat4jReasoner extends ClassicalInference {
	
	public Sat4jReasoner(BeliefBase beliefBase) {
		super(beliefBase, new Sat4jEntailment());
	}
}

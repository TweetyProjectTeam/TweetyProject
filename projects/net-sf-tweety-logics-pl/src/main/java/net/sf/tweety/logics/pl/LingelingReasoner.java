package net.sf.tweety.logics.pl;

import net.sf.tweety.BeliefBase;

/**
 * Reasoner based on Lingeling.
 * 
 * @author Matthias Thimm
 */
public class LingelingReasoner extends ClassicalInference{
	public LingelingReasoner(BeliefBase beliefBase, String binaryLocation) {
		super(beliefBase, new LingelingEntailment(binaryLocation));
	}
}

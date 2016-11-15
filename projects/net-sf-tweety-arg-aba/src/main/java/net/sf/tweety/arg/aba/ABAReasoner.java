/**
 * 
 */
package net.sf.tweety.arg.aba;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;

/**
 * @author Nils Geilen
 *
 */
public class ABAReasoner extends Reasoner {

	int semantics, inferencetype;
	
	public ABAReasoner(BeliefBase beliefBase, int semantics, int inferencetype) {
		super(beliefBase);
		this.semantics = semantics;
		this.inferencetype = inferencetype;
		if (! (beliefBase instanceof ABATheory))
			throw new IllegalArgumentException("Knowledge base of type ABATheory<?> expected");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		ABATheory<?> abat = (ABATheory<?>)getKnowledgeBase();
		DungTheory dt = abat.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getReasonerForSemantics(dt, semantics, inferencetype);
		return aer.query(query);
	}

}

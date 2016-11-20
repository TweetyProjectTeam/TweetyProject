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
 *	This class models a reasoner over ABA formulae
 */
public class ABAReasoner extends Reasoner {

	int semantics, inferencetype;
	
	/**
	 * Creates a new instance
	 * @param beliefBase	an ABATheory
	 * @param semantics	an indicator for the used semantics (c.f. net.sf.tweety.arg.dung.semantics.Semantics)
	 * @param inferencetype	an indicator for the used inference (c.f. net.sf.tweety.arg.dung.semantics.Semantics)
	 */
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

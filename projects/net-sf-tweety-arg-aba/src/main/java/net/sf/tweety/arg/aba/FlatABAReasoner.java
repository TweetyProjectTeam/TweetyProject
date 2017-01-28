/**
 * 
 */
package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;

/**
 * @author Nils Geilen 
 * This class models a reasoner over ABA formulae
 * Can only be used with flat ABA theories because
 * only those can be transformed into Dung frameworks
 */
public class FlatABAReasoner extends Reasoner {

	int semantics, inferencetype;

	/**
	 * Creates a new instance
	 * 
	 * @param beliefBase
	 *            an ABATheory
	 * @param semantics
	 *            an indicator for the used semantics (c.f.
	 *            net.sf.tweety.arg.dung.semantics.Semantics)
	 * @param inferencetype
	 *            an indicator for the used inference (c.f.
	 *            net.sf.tweety.arg.dung.semantics.Semantics)
	 */
	public FlatABAReasoner(BeliefBase beliefBase, int semantics, int inferencetype) {
		super(beliefBase);
		this.semantics = semantics;
		this.inferencetype = inferencetype;
		if (!(beliefBase instanceof ABATheory))
			throw new IllegalArgumentException("Knowledge base of type ABATheory<?> expected");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		Argument arg;
		if (query instanceof Assumption)
			arg = new Argument(((Assumption<?>) query).getConclusion().toString());
		else
			throw new RuntimeException("ABAReasoner.query expects input of class Assumption");
		ABATheory<?> abat = (ABATheory<?>) getKnowledgeBase();
		DungTheory dt = abat.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getReasonerForSemantics(dt, semantics, inferencetype);
		return aer.query(arg);
	}

	public Collection<Collection<Assumption<?>>> getExtensions() {
		ABATheory<?> abat = (ABATheory<?>) getKnowledgeBase();
		DungTheory dt = abat.asDungTheory();
		AbstractExtensionReasoner aer = AbstractExtensionReasoner.getReasonerForSemantics(dt, semantics, inferencetype);
		Collection<Collection<Assumption<?>>> result = new HashSet<>();
		for (Extension ext : aer.getExtensions()) {
			Collection<Assumption<?>> abaext = new HashSet<>();
			for (Argument arg : ext) {
				for (Assumption<?> ass : abat.getAssumptions()) {
					if (ass.toString().equals(arg.toString())) {
						abaext.add(ass);
						break;
					}
				}
			}
			result.add(abaext);
		}
		return result;
	}

}

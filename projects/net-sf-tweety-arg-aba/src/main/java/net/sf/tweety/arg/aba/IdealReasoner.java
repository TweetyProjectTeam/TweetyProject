/**
 * 
 */
package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 * This reasoner for ABA theories performs inference on the ideal extension.
 * @param <T>	the language of the underlying ABA theory
 */
public class IdealReasoner<T extends Formula> extends GeneralABAReasoner<T> {

	/**
	 * Creates a new ideal reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public IdealReasoner(BeliefBase beliefBase, int inferenceType) {
		super(beliefBase, inferenceType);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.GeneralABAReasoner#computeExtensions()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Collection<Assumption<T>>> computeExtensions() {
		ABATheory<T> abat = (ABATheory<T>)getKnowledgeBase();
		Collection<Collection<Assumption<T>>> prefexts = new PreferredReasoner<T>(abat, Semantics.CREDULOUS_INFERENCE).computeExtensions();
		Iterator<Collection<Assumption<T>>> iter = prefexts.iterator();
		Collection<Assumption<T>> intersec = iter.hasNext() ? iter.next() : new HashSet<Assumption<T>>();
		while (iter.hasNext()) {
			intersec.retainAll(iter.next());
		}
		
		Collection<Collection<Assumption<T>>>result = new HashSet<>();
		Collection<Collection<Assumption<T>>> exts = abat.getAllAdmissbleExtensions();
		for(Collection<Assumption<T>> ext : exts) {
			if (intersec.containsAll(ext))
				result.add(new HashSet<>(ext));
		}
		
		Collection<Collection<Assumption<T>>>result2 = new HashSet<>();
		l:for(Collection<Assumption<T>> ext : result) {
			for(Collection<Assumption<T>> ext2 : result) {
				if (ext2!=ext&&ext2.containsAll(ext))
					continue l;
			}
			result2.add(new HashSet<>(ext));
		}
		return result2;
	}
}
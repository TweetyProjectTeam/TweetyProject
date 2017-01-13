/**
 * 
 */
package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *
 */
public class GroundedReasoner<T extends Formula> extends GeneralABAReasoner<T> {
	
	
	

	public GroundedReasoner(BeliefBase beliefBase, int inferenceType) {
		super(beliefBase, inferenceType);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.GeneralABAReasoner#computeExtensions()
	 */
	@Override
	public Collection<Collection<Assumption<T>>> computeExtensions() {
		ABATheory<T> abat = (ABATheory<T>)getKnowledgeBase();
		Collection<Collection<Assumption<T>>> complete_exts = new CompleteReasoner(abat, getInferenceType()).computeExtensions();
		Iterator<Collection<Assumption<T>>> iter = complete_exts.iterator();
		Collection<Assumption<T>> ext = iter.hasNext() ? iter.next() : new HashSet<Assumption<T>>();
		while (iter.hasNext()) {
			ext.retainAll(iter.next());
		}
		
		
		Collection<Collection<Assumption<T>>>result = new HashSet<>();
		result.add(ext);
		return result;
	}

	
	

}

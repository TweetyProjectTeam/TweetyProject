package net.sf.tweety.beliefdynamics;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.Formula;

/**
 * Implements the revision method with two belief bases by delegating the processing
 * to the revision method bases on an ordered list of belief bases. It acts as base
 * class for revision approaches which support the revision of multiple belief bases
 * in one step.
 * 
 * @author Tim Janus
 *
 * @param <T> The type of the belief base
 */
public abstract class CredibilityRevisionNonIterative<T extends Formula> 
	extends CredibilityRevision<T>{

	@Override
	public Collection<T> revise(Collection<T> beliefBase1, Collection<T> beliefBase2) {
		List<Collection<T>> param = new LinkedList<Collection<T>>();
		param.add(beliefBase1);
		param.add(beliefBase2);
		return revise(param);
	}

}

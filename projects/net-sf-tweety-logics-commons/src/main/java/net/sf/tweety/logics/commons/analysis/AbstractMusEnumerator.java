package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * Abstract implementation for MUes enumerators.
 * 
 * @author Matthias Thimm
 *
 * @param <S> the type of formulas
 */
public abstract class AbstractMusEnumerator<S extends Formula> implements MusEnumerator<S> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public abstract Collection<Collection<S>> minimalInconsistentSubsets(Collection<S> formulas);

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#maximalConsistentSubsets(java.util.Collection)
	 */
	@Override
	public abstract Collection<Collection<S>> maximalConsistentSubsets(Collection<S> formulas);

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(net.sf.tweety.BeliefSet)
	 */
	@Override
	public boolean isConsistent(BeliefSet<S> beliefSet){
		return this.isConsistent((Collection<S>) beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(net.sf.tweety.Formula)
	 */
	@Override
	public boolean isConsistent(S formula){
		Collection<S> c = new HashSet<S>();
		c.add(formula);
		return this.isConsistent(c);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<S> formulas){
		return this.minimalInconsistentSubsets(formulas).isEmpty();
	}
}

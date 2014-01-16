package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.Formula;

/**
 * This class represents the set of remainder sets constructed
 * from a belief base.
 *  
 * @author Sebastian Homann
 *
 * @param <T> the type of formulas these remainder sets are based upon
 */
public abstract class RemainderSets<T extends Formula> extends HashSet<Collection<T>> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns the belief base that seeded this remainder set. 
	 * @return a belief base
	 */
	public abstract Collection<T> getSourceBeliefBase();
}

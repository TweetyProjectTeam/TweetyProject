package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;

import net.sf.tweety.Formula;

/**
 * This interface represents an consolidation operator for belief bases
 * as defined in [KKI12].
 * 
 * @author Sebastian Homann
 *
 * @param <T> The type of formulas this consolidation operator works on.
 */
public interface ConsolidationOperator<T extends Formula> {
	
	/**
	 * Returns a consolidation of set p, i.e. a consistent subset of p. 
	 * @param p a belief base
	 * @return the consolidated belief base
	 */
	public Collection<T> consolidate(Collection<T> p);
}

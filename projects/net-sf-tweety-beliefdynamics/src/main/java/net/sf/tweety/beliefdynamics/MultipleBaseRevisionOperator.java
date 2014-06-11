package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This is the interface for a classic multiple belief base revision operator, ie. an
 * operator that takes some set of formulas and another set of formulas and revises
 * the former by the latter. 
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public abstract class MultipleBaseRevisionOperator<T extends Formula> implements BaseRevisionOperator<T> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.BaseRevisionOperator#revise(java.util.Collection, net.sf.tweety.Formula)
	 */
	public Collection<T> revise(Collection<T> base, T formula) {
		Set<T> formulas = new HashSet<T>();
		formulas.add(formula);
		return this.revise(base, formulas);
	}

	/**
	 * Revises the first collection of formulas by the second collection of formulas.
	 * @param base some collection of formulas.
	 * @param formulas some formulas.
	 * @return the revised collection.
	 */
	public abstract Collection<T> revise(Collection<T> base, Collection<T> formulas);
	
}

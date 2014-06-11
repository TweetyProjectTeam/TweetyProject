package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This is the interface for a classic multiple belief base contraction operator, ie. an
 * operator that takes some set of formulas and another set of formulas and contracts
 * the former by the latter. 
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public abstract class MultipleBaseContractionOperator<T extends Formula> implements BaseContractionOperator<T> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.BaseContractionOperator#contract(java.util.Collection, net.sf.tweety.Formula)
	 */
	@Override
	public Collection<T> contract(Collection<T> base, T formula) {
		Set<T> formulas = new HashSet<T>();
		formulas.add(formula);
		return this.contract(base, formulas);
	}

	/**
	 * Contracts the first collection of formulas by the second collection of formulas.
	 * @param base some collection of formulas.
	 * @param formulas some formulas.
	 * @return the contracted collection.
	 */
	public abstract Collection<T> contract(Collection<T> base, Collection<T> formulas);
	
}

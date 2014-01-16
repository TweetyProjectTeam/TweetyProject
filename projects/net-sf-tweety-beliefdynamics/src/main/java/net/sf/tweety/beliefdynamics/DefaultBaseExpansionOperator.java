package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.*;

/**
 * This class implements the default base expansion operator, ie. an operator
 * that returns the union of a set of formulas and a formula.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public class DefaultBaseExpansionOperator<T extends Formula> implements BaseExpansionOperator<T> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.BaseExpansionOperator#expand(java.util.Collection, net.sf.tweety.Formula)
	 */
	@Override
	public Collection<T> expand(Collection<T> base, T formula) {
		Set<T> expandedCollection = new HashSet<T>();
		expandedCollection.addAll(base);
		expandedCollection.add(formula);
		return expandedCollection;
	}
}

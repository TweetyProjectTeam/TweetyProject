package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This class implements the default multiple base expansion operator, ie. an operator
 * that returns the union of the sets of formulas
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public class DefaultMultipleBaseExpansionOperator<T extends Formula> extends MultipleBaseExpansionOperator<T> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseExpansionOperator#expand(java.util.Collection, java.util.Collection)
	 */
	public Collection<T> expand(Collection<T> base, Collection<T> formulas){
		Set<T> expandedCollection = new HashSet<T>();
		expandedCollection.addAll(base);
		expandedCollection.addAll(formulas);
		return expandedCollection;		
	}
}

package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class represents an indicator utility function, i.e. a function
 * that ranks a set of propositions to 1 if this function's focal element
 * is part of the set, and 0 otherwise.
 * 
 * @author Matthias Thimm
 *
 */
public class IndicatorUtilityFunction implements UtilityFunction{

	/**
	 * The focal element of this function.
	 */
	private Proposition focalElement;
	
	/**
	 * Creates a new indicator utility function for the given focal element.
	 * @param focalElement a proposition.
	 */
	public IndicatorUtilityFunction(Proposition focalElement){
		this.focalElement = focalElement;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sas.UtilityFunction#rank(java.util.Collection)
	 */
	@Override
	public int rank(Collection<? extends Proposition> propositions) {
		if(propositions.contains(this.focalElement)) return 1;
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((focalElement == null) ? 0 : focalElement.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndicatorUtilityFunction other = (IndicatorUtilityFunction) obj;
		if (focalElement == null) {
			if (other.focalElement != null)
				return false;
		} else if (!focalElement.equals(other.focalElement))
			return false;
		return true;
	}

}

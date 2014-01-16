package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.*;

/**
 * This is the interface for a classic belief base contraction operator, ie. an
 * operator that takes some set of formulas and a single formula and contracts
 * the former by the latter. 
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public interface BaseContractionOperator<T extends Formula> {

	/**
	 * Contracts the given collection of formulas by the given formula.
	 * @param base some collection of formulas.
	 * @param formula a formula
	 * @return the contracted collection.
	 */
	public Collection<T> contract(Collection<T> base, T formula);	
}

package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;
import net.sf.tweety.Interpretation;

/**
 * Provides methods for returning some model (if it exists) of 
 * a set of formulas.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas.
 */
public interface ConsistencyWitnessProvider<S extends Formula> {

	/**
	 * If the collection of formulas is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the formulas or null.
	 */
	public Interpretation getWitness(Collection<S> formulas);
	
	/**
	 * If the formula is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the formula or null.
	 */
	public Interpretation getWitness(S formula);
	
	/**
	 * If the belief set is consistent this method
	 * returns some model of it or, if it is inconsistent, null.
	 * @return some model of the belief set or null.
	 */
	public Interpretation getWitness(BeliefSet<S> bs);
}

package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.Formula;

/**
 * Classes extending this abstract class represent inconsistency measures
 * on belief sets.
 * 
 * @author Matthias Thimm
 * @param <S> The type of formulas this measure supports.
 * @param <T> The type of belief sets this measure supports.
 */
public abstract class BeliefSetInconsistencyMeasure<S extends Formula> implements InconsistencyMeasure<BeliefSet<S>> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.BeliefBase)
	 */
	public Double inconsistencyMeasure(BeliefSet<S> beliefBase){
		return this.inconsistencyMeasure((Collection<S>) beliefBase);
	}
	
	/**
	 * This method measures the inconsistency of the given set of formulas.
	 * @param formulas a collection of formulas.
	 * @return a Double indicating the degree of inconsistency.
	 */
	public abstract Double inconsistencyMeasure(Collection<S> formulas);
}

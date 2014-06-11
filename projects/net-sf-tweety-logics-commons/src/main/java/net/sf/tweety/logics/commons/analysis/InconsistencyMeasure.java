package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.commons.BeliefBase;

/**
 * Classes implementing this interface represent inconsistency measures
 * on belief bases.
 * 
 * @author Matthias Thimm
 * @param <T> The type of belief bases this measure supports.
 */
public interface InconsistencyMeasure<T extends BeliefBase> {

	/** Tolerance. */
	public static final double MEASURE_TOLERANCE = 0.005;
	
	/**
	 * This method measures the inconsistency of the given belief base.
	 * @param beliefBase a belief base.
	 * @return a Double indicating the degree of inconsistency.
	 */
	public Double inconsistencyMeasure(T beliefBase);	
}

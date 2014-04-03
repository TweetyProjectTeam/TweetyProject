package net.sf.tweety.logics.commons.analysis;

import java.util.*;

import net.sf.tweety.*;

/**
 * This class models the MI^C inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class MicInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new drastic inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public MicInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		double value = 0; 
		for(Collection<S> minInconSet: this.enumerator.minimalInconsistentSubsets(formulas)){			
			value += ( 1 / new Double(minInconSet.size()) );
		}
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "mic";
	}
}

package org.tweetyproject.logics.bpm.analysis;

import org.tweetyproject.logics.bpm.syntax.BpmnModel;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;


// Let P a BPMN Model and n a natural number
// the inconsistency value of P w.r.t n is defined as 
// the ratio of all terminated sequences of activities with length smaller or equal to n 
// divided by the total number of possible sequences of length smaller of equal to n.
// the inconsistency value of P is defined as the inconsistency value for n -> infinity.
public class IntederminateInconsistencyMeasure2 implements BpmnInconsistencyMeasure{

	private final int iterations = 10^6;
	
	/**
	 * the BPMN model for which the inconsistency value is to find
	 */
	private BpmnModel processModel;
	
	@Override
	public Double inconsistencyMeasure(BpmnModel processModel) {
		// 
		return null;
	}
}


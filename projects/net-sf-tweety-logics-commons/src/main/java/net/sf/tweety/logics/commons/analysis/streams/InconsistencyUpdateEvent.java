package net.sf.tweety.logics.commons.analysis.streams;

import net.sf.tweety.Formula;

/**
 * An event that is thrown when an inconsistency value
 * has been updated. 
 * 
 * @author Matthias Thimm
 */
public class InconsistencyUpdateEvent {

	protected StreamBasedInconsistencyMeasure<?,?> measure;
	protected InconsistencyMeasurementProcess<?> process;
	protected Double inconsistencyValue;
	protected Formula f;
	
	/**
	 * Creates a new event with the given parameters.
	 * @param measure
	 * @param process
	 * @param inconsistencyValue
	 * @param f
	 */
	public InconsistencyUpdateEvent(StreamBasedInconsistencyMeasure<?,?> measure, InconsistencyMeasurementProcess<?> process, Double inconsistencyValue, Formula f){
		this.measure = measure;
		this.process = process;
		this.inconsistencyValue = inconsistencyValue;
		this.f = f;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "InconsistencyUpdateEvent: <" + this.inconsistencyValue + "," + this.process + "," + this.f + "," + this.measure + ">";
	}
}

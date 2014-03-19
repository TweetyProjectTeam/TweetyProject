package net.sf.tweety.logics.commons.analysis.streams;

import net.sf.tweety.Formula;
import net.sf.tweety.streams.FormulaStream;

/**
 * The actual process of an inconsistency measure on streams.
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas.
 */
public abstract class InconsistencyMeasurementProcess<S extends Formula> extends Thread {
	
	/** The stream.*/
	private FormulaStream<S> stream;
	/** Whether execution should be aborted. */
	private boolean abort; 
	/** The current inconsistency value.*/
	private Double iValue;
	/** The measure from where this process has been dispatched. */
	private StreamBasedInconsistencyMeasure<S,?> parent;
	
	/**
	 * Creates a new process for the given stream.
	 */
	public InconsistencyMeasurementProcess(){ }
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while(!this.abort && this.stream.hasNext()){
			S f = this.stream.next();
			this.iValue = this.update(f);
			this.parent.fireInconsistencyUpdateEvent(new InconsistencyUpdateEvent(this.parent, this, this.iValue, f));
		}
	}
	
	/**
	 * Initialization statements.
	 * @param stream some formula stream.
	 * @param parent the  measure from where this process has been dispatched.
	 */
	protected void init(FormulaStream<S> stream, StreamBasedInconsistencyMeasure<S,?> parent){
		this.abort = false;
		this.stream = stream;
		this.iValue = 0d;
		this.parent = parent;
		this.init();
	}
	
	/**
	 * Additional initialization statements are put here.
	 */
	protected abstract void init();
	
	/**
	 * Updates the inconsistency value with the new formula.
	 * @param formula some formula.
	 * @return the current inconsistency value.
	 */
	protected abstract double update(S formula);
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#toString()
	 */
	public abstract String toString();
	
	/**
	 * Aborts the measurement of a stream.
	 */
	public void abort(){
		this.abort = true;
	}
	
	/**
	 * Returns the current inconsistency value of this stream processing or 
	 * the last value if the stream processing has finalized.
	 * @return the current inconsistency value.
	 */
	public Double getInconsistencyValue(){
		return this.iValue;
	}
}

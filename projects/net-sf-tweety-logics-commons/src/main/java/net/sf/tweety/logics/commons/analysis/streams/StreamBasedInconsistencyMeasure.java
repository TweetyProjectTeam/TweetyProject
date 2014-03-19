package net.sf.tweety.logics.commons.analysis.streams;

import net.sf.tweety.BeliefBase;
import net.sf.tweety.Formula;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.streams.FormulaStream;

/**
 * General interface for inconsistency measures working on streams.
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 * @param <T> The type of belief bases 
 */
public interface StreamBasedInconsistencyMeasure<S extends Formula,T extends BeliefBase> extends InconsistencyMeasure<T>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.BeliefBase)
	 */
	@Override
	public Double inconsistencyMeasure(T beliefBase);
	
	/**
	 * Processes the formulas in the given stream one after the other.
	 * After each update an event of type "InconsistencyUpdateEvent"
	 * is created and listeners are notified. This method creates and
	 * starts a new object of type "InconsistencyMeasurementProcess"
	 * that processes the stream in a separate thread, and returns it.
	 * The current value of inconsistency can be retrieved by the "getInconsistencyValue()"
	 * of that process and measurement can be aborted by calling its "abort()" method. 
	 * @param stream some formula stream. 
	 * @return an inconsistency measurement process.
	 */
	public InconsistencyMeasurementProcess<S> getInconsistencyMeasureProcess(FormulaStream<S> stream);
	
	/**
	 * Add a listener to this measure.
	 * @param listener the listener to be added.
	 */
	public void addInconsistencyListener(InconsistencyListener listener);
	
	/**
	 * Remove a listener from this measure.
	 * @param listener the listener to be removed.
	 */
	public void removeInconsistencyListener(InconsistencyListener listener);
	
	/**
	 * This method is called by inconsistency measurement processes to dispatch
	 * events to listeners.
	 * @param evt some event to be fired.
	 */
	void fireInconsistencyUpdateEvent(InconsistencyUpdateEvent evt);
}

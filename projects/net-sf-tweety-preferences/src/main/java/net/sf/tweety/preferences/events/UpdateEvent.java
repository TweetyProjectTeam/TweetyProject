package net.sf.tweety.preferences.events;

import java.util.EventObject;

import net.sf.tweety.preferences.PreferenceOrder;

/**
 * The class for event objects used in dynamic preference aggregation
 * 
 * @author Bastian Wolf
 * 
 * @param <T>
 */

public class UpdateEvent<T> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PreferenceOrder<T> result;

	/**
	 * constructor for an update containing the aggregation result
	 * 
	 * @param source where the event occurred 
	 * @param result of the occurring event
	 */
	public UpdateEvent(Object source, PreferenceOrder<T> result) {
		super(source);
		this.result = result;
	}

	/**
	 * Sets the result in for this update event
	 * @param result of this update event
	 */
	public boolean setResult(PreferenceOrder<T> result) {
		if (result != null && !result.isEmpty()) {
			this.result = result;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns the result for this update event
	 * @return the result for this update event
	 */
	public PreferenceOrder<T> getResult() {
		return this.result;
	}

}

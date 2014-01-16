package net.sf.tweety.preferences.aggregation;

import java.util.List;

import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.events.UpdateListener;
import net.sf.tweety.preferences.update.Update;

/**
 * This interface is meant to be used for the dynamic aggregation of some generic preference orders
 * 
 * @author Bastian Wolf
 * 
 * @param <T>
 *            generic preference order type
 */

public interface DynamicPreferenceAggregator<T>{
	
	/**
	 * Abstract class for implementation of dynamic preference aggregation
	 * 
	 * @param input the array of preference orders to be aggregated
	 * @return the final result as a preference order
	 */
	public PreferenceOrder<T> aggregate(List<PreferenceOrder<T>> input);
	
	/**
	 * This update stream is going to be used for dynamic changes in a preferences orders
	 * Input: ArrayList(Quadruple(PreferenceOrder, Number of Operations, Operation, Element))
	 * e.g: weakening element b in PO test 2 times needs quadruple like this:
	 * Quadruple(test, 2, WEAKEN, b)
	 * 
	 * Possible Structures: ArrayList, Queue
	 * 
	 * Empty initialization, update() if stream is not empty
	 */
	public PreferenceOrder<T> update(Update<T> update, List<PreferenceOrder<T>> input);

	
	/**
	 * The add-method for listeners for a dynamic preference aggregator
	 */
	 public void addListener(UpdateListener<T> listener);
	 
	 
	 
	 /**
	 * The remove-method for listeners for a dynamic preference aggregator
	 */
	 public void removeListener(UpdateListener<T> listener);
}

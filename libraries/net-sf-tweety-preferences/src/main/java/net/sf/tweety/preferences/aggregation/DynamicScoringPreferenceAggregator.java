/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.preferences.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.tweety.preferences.Operation;
import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.events.UpdateEvent;
import net.sf.tweety.preferences.events.UpdateListener;
import net.sf.tweety.preferences.ranking.LevelingFunction;
import net.sf.tweety.preferences.update.Update;
import net.sf.tweety.preferences.update.UpdateStream;

/**
 * This Demo-class provides a basic implementation similar to the
 * ScoringPreferenceAggregator but dynamic aggregation instead of static
 * 
 * @author Bastian Wolf
 * 
 * @param <T>
 */

public abstract class DynamicScoringPreferenceAggregator<T> implements
		DynamicPreferenceAggregator<T> {

	/**
	 * The weight vector for the aggregator given via the constructor
	 */
	private WeightVector v;

	/**
	 * The input list of preference orders
	 */
	//private List<PreferenceOrder<T>> input;

	/**
	 * The list containing the update listeners
	 */
	private ArrayList<UpdateListener<T>> _listeners;

	/**
	 * Constructor with given weight vector
	 * 
	 * @param v
	 *            the weight vector
	 */
	public DynamicScoringPreferenceAggregator(WeightVector v) {
		this._listeners = new ArrayList<UpdateListener<T>>();
		this.v = v;
	}

	/**
	 * This method aggregates the given preference orders according to the
	 * WeightVector used within construction
	 */
	public PreferenceOrder<T> aggregate(List<PreferenceOrder<T>> input) {
		// PreferenceOrder<T> tempPO = new PreferenceOrder<T>();

		
		// "this.input" is never used!
		//this.input = input;
		Map<T, Integer> elem = new HashMap<T, Integer>();

		// all single elements are store in one HashMap
		// note that every input-po only consists of the exact same domain
		// elements
		if (!input.isEmpty()) {

			ListIterator<PreferenceOrder<T>> it = input.listIterator();
			if (it.hasNext()) {
				PreferenceOrder<T> tPO = it.next();

				for (T e : tPO.getDomainElements()) {

					if (!elem.containsKey(e)) {
						elem.put(e, 0);
					} else {
						continue;
					}

				}
				while (it.hasNext()) {
					PreferenceOrder<T> checkPO = it.next();
					for (T e : checkPO.getDomainElements()) {
						if (!elem.containsKey(e)) {
							// TODO Exception handling for null pointer
							// exception
							System.out.println("Invalid preference order used");
						}
					}
				}
			}

		}

		// for each element in each po the weight vector value is
		// requested
		// and
		// subtracted from the current value in the HashMap
		ListIterator<PreferenceOrder<T>> it2 = input.listIterator();
		while (it2.hasNext()) {
			PreferenceOrder<T> tPO = it2.next();
			Map<T, Integer> temp = tPO.getLevelingFunction();
			for (Entry<T, Integer> e : temp.entrySet()) {
				T t = e.getKey();
				Integer i = e.getValue();
				int val = v.getWeight(i);
				elem.put(e.getKey(), elem.get(t.toString()) - val);
			}
		}

		// finally a temporary ranking function is created an generates the
		// aggregated preference order

		LevelingFunction<T> tempRF = new LevelingFunction<T>();
		tempRF.putAll(elem);

		return tempRF.generatePreferenceOrder();

	}

	/**
	 * The update-method for dynamically changing the input for preference
	 * aggregation
	 * 
	 * @param update
	 *            the update element containing the changes to be applied
	 */
	public PreferenceOrder<T> update(Update<T> update,
			List<PreferenceOrder<T>> input) throws IndexOutOfBoundsException,
			NullPointerException {

		// get the list-index of the po to be changed
		int i = update.getPreferenceOrderIndex();
		PreferenceOrder<T> po = input.get(i);

//		int i = -1;

//		for (PreferenceOrder<T> in : input) {
//			if (po.compareEqualityWith(in)) {
//				i = input.indexOf(po);
//				break;
//			}
//		}

		Operation op = update.getOperation();
		T element = update.getElement();

		if (po.getDomainElements().contains(element)) {
			int amount = update.getAmount();
			if (op == Operation.WEAKEN) {
				while (amount > 0) {
					po.weakenElementInLF(element);
					input.set(i, po);
					amount--;
				}
			} else if (op == Operation.STRENGTHEN) {
				while (amount > 0) {

					po.strengthenElementInLF(element);
					input.set(i, po);
					amount--;
				}
			}
		}

		// aggregate the updated preference orders into a new result
		PreferenceOrder<T> result = aggregate(input);

		// firing a new event for every update to every listener using this
		// result
		UpdateEvent<T> event = new UpdateEvent<T>(this, result);
		fireEvent(event);

		// return the newly aggregated result
		return result;
	}

	/**
	 * this method extends the update-functionality with input-streams
	 * consisting of Update-elements
	 * 
	 * @param stream
	 *            the input stream with the Update-elements
	 * @return the newly aggregated preference order after all updates are
	 *         applied
	 */
	public PreferenceOrder<T> update(UpdateStream<T> stream,
			List<PreferenceOrder<T>> input) {
		PreferenceOrder<T> temp = new PreferenceOrder<T>();

		while (!(stream.isEmpty())) {
			Update<T> up = stream.next();
			temp = update(up, input);
		}

		return temp;
	}

	/**
	 * Fires an event every time a change occurred
	 * 
	 * @param event
	 */
	private void fireEvent(UpdateEvent<T> event) {

		Iterator<UpdateListener<T>> i = _listeners.iterator();

		while (i.hasNext()) {
			(i.next()).eventOccurred(event);
		}
	}

	/**
	 * adds a listener to this dynamic preference aggregator
	 */
	@Override
	public synchronized void addListener(UpdateListener<T> listener) {
		_listeners.add(listener);

	}

	/**
	 * removes a listener from this dynamic preference aggregator
	 */
	@Override
	public synchronized void removeListener(UpdateListener<T> listener) {
		_listeners.remove(listener);

	}

}

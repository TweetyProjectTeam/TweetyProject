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
package net.sf.tweety.preferences.events;

import java.util.EventObject;

import net.sf.tweety.preferences.PreferenceOrder;

/**
 * The class for event objects used in dynamic preference aggregation
 * 
 * @author Bastian Wolf
 * 
 * @param <T> generic preference order type
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
	 * @return true iff the operation was successful
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

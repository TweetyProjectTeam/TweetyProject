/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.preferences.events;

import java.util.EventListener;
//Event-Listener

/**
 * The interface for UpdateListener used for dynamic preference aggregation
 * 
 * @author Bastian Wolf
 *
 * @param <T> the generic element's type
 */

public interface UpdateListener<T> extends EventListener {
	
	/**
	 * UpdateListener is informed as soon as an update occurs
	 * @param e the update event observed by the listener
	 */
	void eventOccurred(UpdateEvent<T> e);
	
}

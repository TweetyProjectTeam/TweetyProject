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
package org.tweetyproject.preferences.events;

/**
 * This exemplary class implements a simple printer for update events writing its result into the console
 * 
 * @author Bastian Wolf
 *
 * @param <T> the generic type
 */

public class UpdatePrinter<T> implements UpdateListener<T> {

	
	/**
	 * This method is called every time an update occurs
	 */
	public void eventOccurred(UpdateEvent<T> e) {
	
	System.out.println("Updated aggregation result: "+ e.getResult());
	System.out.println(e.getResult().getLevelingFunction());
	System.out.println(e.getResult().getLevelingFunction().getRankingFunction());	
	}

}

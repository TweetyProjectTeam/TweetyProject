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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.analysis;

import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This interface represents a measure of inconsistency for a Dungian argumentation framework.
 * It provides a method to calculate the inconsistency measure of a given argumentation framework.
  * @author Timothy Gillespie
 * @param <T> the type of DungTheory
 */
public interface InconsistencyMeasure<T extends DungTheory> {

	/**
	 * Calculates the inconsistency measure of a given Dungian argumentation framework.
	 *
	 * @param argumentationFramework the argumentation framework to measure
	 * @return the inconsistency measure as a Double value
	 */
	public Double inconsistencyMeasure(T argumentationFramework);

}

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
 * Calculates the drastic inconsistency measure of the given argumentation framework.It implements the
 * InconsistencyMeasure interface.
 * @author Timothy Gillespie
 * @param <T> the type of DungTheory
 */
public class DrasticInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {
	/**
	 * Calculates the inconsistency measure based on the drastic inconsistency
	 * measure.
	 *
	 * @param argumentationFramework the argumentation framework to calculate the
	 *                               inconsistency measure for
	 * @return The inconsistency measure of the argumentation framework.
	 *         Returns 0.0 if the argumentation framework has no attacks,
	 *         otherwise returns 1.0 indicating inconsistency.
	 */
	@Override
	public Double inconsistencyMeasure(T argumentationFramework) {
		if (argumentationFramework.getAttacks().size() == 0)
			return 0d;
		return 1d;
	}
}

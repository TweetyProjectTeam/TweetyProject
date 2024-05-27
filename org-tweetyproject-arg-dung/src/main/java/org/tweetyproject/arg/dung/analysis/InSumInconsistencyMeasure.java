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
 * This class implements an inconsistency measure for Dung's argumentation frameworks
 * based on the number of attacks within the framework. The inconsistency measure quantifies
 * how much conflict is present within the argumentation framework, assuming that more attacks
 * indicate higher inconsistency. It extends the generic InconsistencyMeasure interface to
 * provide this functionality for any Dung theory.
 * 
 * @param <T> the type of Dung theories used, extending the DungTheory class
 * @author Timothy Gillespie
 */
public class InSumInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	/**
     * Calculates the inconsistency measure of the given Dung theory argumentation framework.
     * The measure is calculated as the number of attack relations present in the framework,
     * under the assumption that more attacks indicate a higher level of inconsistency.
     *
     * @param argumentationFramework the Dung theory argumentation framework to measure for inconsistency
     * @return Double the calculated inconsistency measure based on the number of attacks
     */
	public Double inconsistencyMeasure(T argumentationFramework) {
		// Correct since the attack class extends the directed edge class
		return (double) argumentationFramework.getAttacks().size();
	}
}

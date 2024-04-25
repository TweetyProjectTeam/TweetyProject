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

import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Implements an inconsistency measure for Dung's argumentation frameworks based on the weighted sum of
 * incoming attacks to each argument. This measure evaluates the intensity of conflict around individual
 * arguments by considering the number of attackers each argument has. The contribution of each argument
 * to the overall inconsistency score is inversely proportional to the number of its attackers, under the
 * assumption that an argument with fewer attackers represents a more critical inconsistency point.
 * 
 * @param <T> the type of Dung theories used, extending DungTheory
 * @author Timothy Gillespie
 */
public class WeightedInSumInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {

	/**
     * Calculates the inconsistency measure of a given Dung theory argumentation framework by evaluating
     * the weighted sum of incoming attacks for each argument. The measure computes the sum of 1 divided
     * by the number of attackers for each argument.
     *
     * @param argumentationFramework the Dung theory argumentation framework to measure for inconsistency
     * @return Double the calculated inconsistency measure, where a higher score indicates a higher level
     *         of inconsistency.
     */
	public Double inconsistencyMeasure(T argumentationFramework) {
		
		Double weightedInSum = 0d;
		Collection<Argument> arguments = argumentationFramework.getNodes();
		for (Argument singleArgument : arguments) {
			Collection<Argument> attackers = argumentationFramework.getParents(singleArgument);
			int inCount = attackers.size();
		
			if(inCount > 0) weightedInSum += (1.0 / inCount);
		}
		
		return weightedInSum;
	}
}

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
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.arg.dung.syntax.Argument;
import java.util.Collection;



/**
 * Implements an inconsistency measure for Dung's argumentation frameworks based on the weighted count
 * of connected components within the framework. This measure calculates inconsistency by evaluating
 * the size of each component, assuming that larger isolated groups (components) in the argumentation
 * framework contribute more significantly to its inconsistency. The inconsistency score is the sum
 * of the squares of the sizes of the components, minus one for each component.
 * 
 * @param <T> the type of Dung theories used, extending DungTheory
 * @author Timothy Gillespie
 */
public class WeightedComponentCountInconsistencyMeasure<T extends DungTheory> implements InconsistencyMeasure<T> {
	
	/**
     * Calculates the inconsistency measure of the given Dung theory argumentation framework based on
     * the weighted count of its components. Each component contributes to the inconsistency score
     * based on the square of its size minus one. This method aggregates the scores from all components
     * to produce a final measure of inconsistency.
     *
     * @param argumentationFramework the Dung theory argumentation framework to measure for inconsistency
     * @return Double the calculated inconsistency measure, where larger numbers indicate greater inconsistency
     */
	@Override
	public Double inconsistencyMeasure(T argumentationFramework) {
		
		Double weightedComponentCount = 0d;
		
		Collection<Graph<Argument>> components = argumentationFramework.getComponents();
		
		for(Graph<Argument> singleComponent : components) {
			Double intermediateValue = singleComponent.getNodes().size() - 1.0;
			Double componentSizeWeighted = intermediateValue * intermediateValue;
			weightedComponentCount += componentSizeWeighted;
		}
		
		return weightedComponentCount;
	}

}

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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.logics.bpm.syntax.BpmnModel;
import org.tweetyproject.logics.bpm.syntax.BpmnNode;
import org.tweetyproject.logics.bpm.syntax.EndEvent;
import org.tweetyproject.logics.bpm.syntax.StartEvent;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;
import org.tweetyproject.logics.petri.syntax.PetriNet;
import org.tweetyproject.logics.petri.syntax.reachability_graph.Marking;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkingEdge;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkovWalk;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.probability.ProbabilityFunction;

/**
 * A BPMN model has an IndeterminateInconsistency value of 1 if there is an activity in this model 
 * a) that is reachable from a start event via the model's sequence flow
 * b) from where no end event can possibly be reached, 
 * and 0 otherwise
 * @author Benedikt Knopp
 */
public class DeadEndInconsistencyMeasure implements BpmnInconsistencyMeasure{

	/**
	 * the ReachabilityGraph for which the inconsistency value is to find
	 */
	private ReachabilityGraph reachabilityGraph;
	
	/**
	 * the transition matrix of the reachability graph, based on the graph's probability function
	 */
	private Matrix transitionMatrix;
	
	
	/**
	 * DeadEndInconsistencyMeasure
	 */
	public DeadEndInconsistencyMeasure() {}
	
	@Override
	public Double inconsistencyMeasure(ReachabilityGraph reachabilityGraph) {
		this.reachabilityGraph = reachabilityGraph;
		MarkovWalk randomWalk = new MarkovWalk(reachabilityGraph);
		randomWalk.initializeWalk();
		randomWalk.performWalk();
		Matrix limit = randomWalk.getCurrentState();
		return calculateInconsistencyValue(limit);
	} 
	
	private Double calculateInconsistencyValue(Matrix limit) {
		double inconsistencyValue = 0;
		for(int i = 0; i < limit.getYDimension(); i++) {
			Marking marking = reachabilityGraph.getNodes().get(i);
			double markingProbability = limit.getEntry(0, i).doubleValue();
			int nonFinals = marking.getSumOfTokensAtNonFinalPlaces();
			inconsistencyValue += markingProbability*nonFinals;
		}
		return inconsistencyValue;
	}
	
}

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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkovWalk;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.matrix.Matrix;

/**
 * @author Benedikt Knopp
 */
public class UnfairnessMeasure implements BpmnInconsistencyMeasure {
	
	/**
	 * the ReachabilityGraph for which the inconsistency value is to find
	 */
	private ReachabilityGraph reachabilityGraph;
	/**
	 * the Markov Walk that determines the inconsistency value
	 */
	private MarkovWalk markovWalk;
	/**
	 * the inconsistency value, calculated after performing the Markov walk
	 */
	private double inconsistencyValue;
	/**
	 * the responsibilities of individual transitions for the global inconsistency value, calculated after performing the Markov walk
	 */
	private Map<Transition, Double> transitionCulpabilities = new HashMap<>();
	
	@Override
	public Double inconsistencyMeasure(ReachabilityGraph reachabilityGraph) {
		if(!reachabilityGraph.getPetriNet().checkShortCircuit()) {
			throw new IllegalStateException("The given Petri Net is not short-circuited");
		}
		this.reachabilityGraph = reachabilityGraph;
		this.markovWalk = new MarkovWalk(reachabilityGraph);
		markovWalk.initializeWalk();
		markovWalk.performShortCircuitWalk();
		calculateInconsistencyValue(markovWalk);
		return this.inconsistencyValue;
	}
	
	/**
	 * calculates the inconsistency and culpabilities after performing the Markov walk
	 * @param markovWalk the exhaustively performed Markov walk
	 */
	private void calculateInconsistencyValue(MarkovWalk markovWalk) {
		Matrix normalizedControlVector = markovWalk.getNormalizedControlVector();
		List<Transition> transitions = reachabilityGraph.getPetriNet().getTransitions();
		double entry;
		double culpability;
		int dimension = normalizedControlVector.getXDimension();
		double mean = 1/(dimension+0d);
		inconsistencyValue = 0;
		for(int j = 0; j < dimension; j++) {
			Transition transition = transitions.get(j);
			entry = normalizedControlVector.getEntry(j,0).simplify().doubleValue();
			culpability = Math.pow(entry-mean, 2);
			transitionCulpabilities.put(transition, culpability);
			inconsistencyValue += culpability;
		}
	}

	@Override
	/**
	 * Build and return some strings that describe the graph and its inconsistency and culpability values 
	 * This comprises 1) an ordered list of transitions with their normalized control vector entries after exhaustively performing 
	 * the Markov walk up to convergence of the mean state, as well as the transition's culpability
	 * and 2) the calculated global inconsistency value
	 * @return the info strings
	 */	
	public List<String> getInfoStrings() {
		List<String> infoStrings = new ArrayList<String>();
		List<Transition> transitions = reachabilityGraph.getPetriNet().getTransitions();
		String infoString = "";
		
		Matrix normalizedControlVector = markovWalk.getNormalizedControlVector();
		int dimension = transitions.size();
		double mean = Math.round(1/(0.0d+dimension) * 10000.0)/10000.0;
		infoString += "<br>Mean: " + mean  + " (for " + dimension + " transitions)";
		infoString += "<br>Transitions / Normalized Control Vector Entries / Culpabilities ";
		infoStrings.add(infoString);
		for(int j = 0; j < dimension; j++) {
			Transition transition = transitions.get(j);
			infoString = "";
			infoString += transition.getName() + " / ";
			infoString += Math.round(normalizedControlVector.getEntry(j, 0).doubleValue() * 10000.0) / 10000.0 + " / ";
			infoString += Math.round(transitionCulpabilities.get(transition) * 10000.0) / 10000.0;			
			infoStrings.add(infoString);
		}
		
		double roundOff = Math.round(this.inconsistencyValue * 100.0) / 100.0;
		infoStrings.add("<br><i>---Unfairness inconsistency: " + roundOff + "---</i>");
		return infoStrings;
	}

}

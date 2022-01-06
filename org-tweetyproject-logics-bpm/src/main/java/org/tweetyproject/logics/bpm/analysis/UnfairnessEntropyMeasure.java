package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkovWalk;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.matrix.Matrix;

public class UnfairnessEntropyMeasure implements BpmnInconsistencyMeasure {
	/**
	 * the ReachabilityGraph for which the inconsistency value is to find
	 */
	private ReachabilityGraph reachabilityGraph;
	/**
	 * the Markov Walk that determines the inconsistency value
	 */
	private MarkovWalk markovWalk;
	private double inconsistencyValue;
	
	
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

	private void calculateInconsistencyValue(MarkovWalk markovWalk) {
		Matrix normalizedControlVector = markovWalk.getNormalizedControlVector();
		double entry;
		double delta;
		int dimension = normalizedControlVector.getXDimension();
		inconsistencyValue = 1;
		for(int j = 0; j < dimension; j++) {
			entry = normalizedControlVector.getEntry(j,0).simplify().doubleValue();
			delta = entry == 0.0d? 0.0d : entry*Math.log(entry)/Math.log(dimension);
			inconsistencyValue += delta;
		}
	}

	@Override
	public List<String> getInfoStrings() {
		List<String> infoStrings = new ArrayList<String>();
		List<Transition> transitions = reachabilityGraph.getPetriNet().getTransitions();
		String infoString = "";
		
		Matrix normalizedControlVector = markovWalk.getNormalizedControlVector();
		int dimension = transitions.size();
		double mean = Math.round(1/(0.0d+dimension) * 100.0)/100.0;
		infoString += "<br>Mean: " + mean  + " (for " + dimension + " transitions)";
		infoString += "<br>Transitions / Normalized Control Vector Entries";
		infoStrings.add(infoString);
		for(int j = 0; j < dimension; j++) {
			Transition transition = transitions.get(j);
			infoString = "";
			infoString += transition.getName() + " / ";
			infoString += Math.round(normalizedControlVector.getEntry(j, 0).doubleValue() * 100.0) / 100.0;
			infoStrings.add(infoString);
		}
		double roundOff = Math.round(this.inconsistencyValue * 100.0) / 100.0;
		infoStrings.add("<br><i>---Entropy-based Unfairness inconsistency: " + roundOff + "---</i>");
		return infoStrings;
	}

}

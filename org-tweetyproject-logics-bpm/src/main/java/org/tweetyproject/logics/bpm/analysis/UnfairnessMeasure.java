package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkovWalk;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.matrix.Matrix;

public class UnfairnessMeasure implements BpmnInconsistencyMeasure {
	
	/**
	 * the ReachabilityGraph for which the inconsistency value is to find
	 */
	private ReachabilityGraph reachabilityGraph;
	/**
	 * the Markov Walk that determines the inconsistency value
	 */
	private MarkovWalk markovWalk;
	private double inconsistencyValue;
	
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

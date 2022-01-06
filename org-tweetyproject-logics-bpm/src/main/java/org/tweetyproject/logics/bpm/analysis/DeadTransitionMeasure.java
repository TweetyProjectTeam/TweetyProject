package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkovWalk;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.matrix.Matrix;


public class DeadTransitionMeasure implements BpmnInconsistencyMeasure {

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
		return calculateInconsistencyValue(markovWalk);
	}
	
	private double calculateInconsistencyValue(MarkovWalk markovWalk) {
		Matrix controlVector = markovWalk.getControlVector();
		int zeros = 0;
		double entry;
		int dimension = controlVector.getXDimension();
		List<Transition> transitions = reachabilityGraph.getPetriNet().getTransitions();
		Set<Transition> deadTransitions = new HashSet<>();
		for(int j = 0; j < dimension; j++) {
			entry = controlVector.getEntry(j,0).simplify().doubleValue();
			if(Math.abs(entry) < MEASURE_TOLERANCE) {
				zeros++;
				deadTransitions.add(transitions.get(j));
			}
		}
		for(Transition deadTransition : deadTransitions) {
			this.transitionCulpabilities.put(deadTransition, 1/(zeros+0d));
		}
		inconsistencyValue = (zeros + 0d)/(dimension + 0d);
		return inconsistencyValue;
	}

	@Override
	public List<String> getInfoStrings() {
		List<String> infoStrings = new ArrayList<String>();
		List<Transition> transitions = reachabilityGraph.getPetriNet().getTransitions();
		String infoString = "";
		
		Matrix controlVector = markovWalk.getControlVector();
		int dimension = transitions.size();
		infoString += "<br>Transitions/ Control Vector Entries / Culpabilities: ";
		infoStrings.add(infoString);
		for(int j = 0; j < dimension; j++) {
			Transition transition = transitions.get(j);
			infoString = "";
			infoString += transition.getName() + " / ";
			infoString += Math.round(controlVector.getEntry(j, 0).doubleValue() * 100.0) / 100.0 + " / ";
			infoString += transitionCulpabilities.keySet().contains(transition) ? 
					Math.round(transitionCulpabilities.get(transition) * 100.0) / 100.0 : "0.0";
			infoStrings.add(infoString);
		}
		
		double roundOff = Math.round(inconsistencyValue * 100.0) / 100.0;
		infoStrings.add("<br><i>---Dead Transition Inconsistency: " + roundOff + "---</i>");
		return infoStrings;
	}
	
	
	
}


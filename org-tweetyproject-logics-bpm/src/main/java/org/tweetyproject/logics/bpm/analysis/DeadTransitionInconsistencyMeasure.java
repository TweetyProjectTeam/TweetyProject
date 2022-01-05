package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkovWalk;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.matrix.Matrix;


public class DeadTransitionInconsistencyMeasure implements BpmnInconsistencyMeasure {

	/**
	 * the ReachabilityGraph for which the inconsistency value is to find
	 */
	private ReachabilityGraph reachabilityGraph;
	/**
	 * the Markov Walk that determines the inconsistency value
	 */
	private MarkovWalk markovWalk;
	private double inconsistencyValue;
	
	/**
	 * a lower limit to specify when a control vector entry should be considered as zero
	 */
	private final double TOLERANCE = 0.001;
	

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
		for(int j = 0; j < dimension; j++) {
			entry = controlVector.getEntry(j,0).simplify().doubleValue();
			if(Math.abs(entry) < TOLERANCE) {
				zeros++;
			}
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
		infoString += "<br>Transitions/ Control Vector Entries: ";
		infoStrings.add(infoString);
		for(int j = 0; j < dimension; j++) {
			infoString = "";
			infoString += transitions.get(j).getName() + ":    ";
			infoString += Math.round(controlVector.getEntry(j, 0).doubleValue() * 100.0) / 100.0;
			infoStrings.add(infoString);
		}
		
		double roundOff = Math.round(inconsistencyValue * 100.0) / 100.0;
		infoStrings.add("<br><i>---Dead Transition Inconsistency: " + roundOff + "---</i>");
		return infoStrings;
	}
	
	
	
}


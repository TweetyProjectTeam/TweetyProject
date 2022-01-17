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
package org.tweetyproject.logics.petri.syntax.reachability_graph;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.Set;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.petri.syntax.PetriNet;
import org.tweetyproject.logics.petri.syntax.Transition;

/**
 * 
 * @author Matthias Thimm
 *
 */
public class ReachabilityGraphParser extends Parser{

	/**
	 * the source Petri net
	 */
	private PetriNet petriNet;
	/**
	 * the reachability graph being constructed
	 */
	private ReachabilityGraph reachabilityGraph;
	/**
	 * current node during construction
	 */
	private Marking currentMarking;
	/**
	 * true iff the construction has finished
	 */
	private boolean constructed = false;
	
	/**
	 * 
	 * @param petriNet a Petri net with some designated initial markings
	 */
	public ReachabilityGraphParser(PetriNet petriNet) {
		this.petriNet = petriNet;
	}
	
	/**
	 * Construct the reachability graph for the Petri net
	 */
	public void construct() {
		if(constructed) {
			throw new IllegalStateException("Graph already constructed.");
		}
		try {
			reachabilityGraph = new ReachabilityGraph(petriNet);
			Set<Marking> initialMarkings = petriNet.getInitialMarkings();
			for(Marking initialMarking : initialMarkings) {
				currentMarking = initialMarking;
				// set the token distribution in the net according to the marking
				petriNet.setState(initialMarking);
				if(!reachabilityGraph.hasMarking(currentMarking)) {
					reachabilityGraph.add(currentMarking);					
				}
				search();
			}
			reachabilityGraph.sortMarkings();
			constructed = true;
		} catch (Exception e) {
			this.reachabilityGraph = null;
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the reachability graph after parsing
	 * @return the reachability graph if the graph had been parsed before
	 * @throws IllegalStateException if the reachability graph has not been parsed yet
	 */
	public ReachabilityGraph get() throws IllegalStateException {
		if(!this.constructed) {
			throw new IllegalStateException("The reachability graph has not been constructed yet.");
		}
		return reachabilityGraph;
	}
	
	/**
	 * Search depth-first recursively for possible follow-up markings to the current marking
	 */
	private void search() {
		Set<Transition> enabledTransitions = petriNet.getEnabledTransitions();
		if(enabledTransitions.isEmpty()) {
			// add empty self-loop (with silent transition)
			Transition transition = petriNet.createEmptyTransition(currentMarking);
			addEdge(currentMarking, currentMarking, transition);
			return;
		}
		Marking newMarking;
		Marking temp;
		for(Transition transition : enabledTransitions) {
			temp = currentMarking;
			transition.fire();			
			newMarking = petriNet.getMarking();
			// add edge to new marking and also check if we have been there before
			boolean hasLoop = addEdge(currentMarking, newMarking, transition);
			if(!hasLoop) {
				// search only if that marking is new
				// otherwise, the search at that marking has been called before
				currentMarking = newMarking;
				search();								
			}
			// reset states to continue search for other markings
			transition.revertFire();
			currentMarking = temp;
		}
	}
	
	/**
	 * Add an edge to the reachability graph 
	 * and add the target marking to the graph if it does not already exist in the graph
	 * @param source the source of the edge
	 * @param target the target of the edge
	 * @param transition the transition leading to the respective change of markings
	 * @return true iff the target node already existed in this reachability graph
	 */
	private boolean addEdge(Marking source, Marking target, Transition transition) {
		boolean hasLoop = false;
		// check if this marking has already been reached on the current path
		Optional<Marking> loopEntry = reachabilityGraph.getMarking(target);
		if(loopEntry.isPresent()) {
			target = loopEntry.get();
			hasLoop = true;
		} else {
			reachabilityGraph.add(target);
		}
		MarkingEdge markingEdge = new MarkingEdge(source, target, transition);
		reachabilityGraph.add(markingEdge);
		return hasLoop;
	}

	@Override
	public BeliefBase parseBeliefBase(Reader reader) throws IOException, ParserException {
		throw new IllegalStateException("Method not supported");
	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		throw new IllegalStateException("Method not supported");
	}
}

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
package org.tweetyproject.logics.bpm.parser.bpmn_to_petri;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.bpm.syntax.BpmnModel;
import org.tweetyproject.logics.bpm.syntax.BpmnModel.BpmnNodeType;
import org.tweetyproject.logics.bpm.syntax.BpmnNode;
import org.tweetyproject.logics.bpm.syntax.InclusiveGateway;
import org.tweetyproject.logics.petri.syntax.Ark;
import org.tweetyproject.logics.petri.syntax.PetriNet;
import org.tweetyproject.logics.petri.syntax.Place;
import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.logics.petri.syntax.reachability_graph.Marking;

/**
 * A class to map a BPMN model to a Petri net
 * 
 * @author Benedikt Knopp
 */
public class PetriNetParser extends Parser {

	/**
	 * the source model
	 */
	private BpmnModel bpmnModel;
	/**
	 * the target net
	 */
	private PetriNet petriNet;
	/**
	 * true iff, for this instance, the parsing has been completed
	 */
	private boolean constructed = false;
	/**
	 * the nodes of the model sorted by their type (event, activity etc.)
	 */
	private Map<BpmnNodeType, Set<BpmnNode>> sortedNodes;
	/**
	 * places associated with single bpmn nodes 
	 */
	private Map<BpmnNode, Place> placesMap = new HashMap<>();
	/**
	 * transitions associated with single bpmn nodes 
	 */
	private Map<BpmnNode, Transition> transitionMap = new HashMap<>();
	/**
	 * specify if places corresponding to start events should get an initial token
	 */
	private boolean provideInitialTokensAtStartEvents = false;
	
	/**
	 * Create a new instance
	 * @param bpmnModel the BPMN model that is to be parsed
	 */
	public PetriNetParser(BpmnModel bpmnModel) {
		this.bpmnModel = bpmnModel;
	}
	
	/**
	 * Specify if the BPMN start events should get an initial token in the
	 * corresponding places of parsed Petri net
	 * @param provide true iff the places should get an initial token
	 */
	public void setProvideInitialTokensAtStartEvents(boolean provide) {
		this.provideInitialTokensAtStartEvents = provide;
	}

	/**
	 * Construct the Petri net 
	 * @throws IllegalStateException if the net has already been constructed
	 */
	public void construct() throws IllegalStateException {
		if (constructed) {
			throw new IllegalStateException("Graph already constructed.");
		}
		try {
			petriNet = new PetriNet();
			sortedNodes = bpmnModel.getSortedNodes();
			parseNodes();
			parseEdges();
			setInitialMarkings();
			constructed = true;
		} catch (Exception e) {
			petriNet = null;
			e.printStackTrace();
		}
	}



	/**
	 * retrieve the constructed Petri net
	 * @return the Petri net
	 * @throws IllegalStateException if the Petri net has not been constructed yet
	 */
	public PetriNet get() throws IllegalStateException {
		if (!constructed) {
			throw new IllegalStateException("Graph not constructed.");
		}
		return petriNet;
	}

	/**
	 * build nodes of the Petri net based on BPMN nodes
	 * with respect to behaviour of different types of BPMN nodes
	 */
	private void parseNodes() {
		for (BpmnNode startEvent : sortedNodes.get(BpmnNodeType.START_EVENT)) {
			parseStartEvent(startEvent);
		}
		for (BpmnNode endEvent : sortedNodes.get(BpmnNodeType.END_EVENT)) {
			parseEndEvent(endEvent, true);
		}
		for (BpmnNode event : sortedNodes.get(BpmnNodeType.EVENT)) {
			parseEvent(event);
		}
		for (BpmnNode activity : sortedNodes.get(BpmnNodeType.ACTIVITY)) {
			parseActivity(activity);
		}
		for (BpmnNode exclusiveGateway : sortedNodes.get(BpmnNodeType.EXCLUSIVE_GATEWAY)) {
			parseExclusiveGateway(exclusiveGateway);
		}
		for (BpmnNode inclusiveGateway : sortedNodes.get(BpmnNodeType.INCLUSIVE_GATEWAY)) {
			parseInclusiveGateway(inclusiveGateway);
		}		
	}
	
	/**
	 * build nodes and edges of the Petri net based on adjacencies between BPMN nodes
	 * with respect to behaviour of different types of BPMN nodes
	 */
	private void parseEdges() {
		// treat events, activities, exclusive gateways
		Set<BpmnNode> standardNodes = new HashSet<>();
		standardNodes.addAll(sortedNodes.get(BpmnNodeType.START_EVENT));
		standardNodes.addAll(sortedNodes.get(BpmnNodeType.END_EVENT));
		standardNodes.addAll(sortedNodes.get(BpmnNodeType.EVENT));
		standardNodes.addAll(sortedNodes.get(BpmnNodeType.ACTIVITY));
		standardNodes.addAll(sortedNodes.get(BpmnNodeType.EXCLUSIVE_GATEWAY));
		// treat inclusive gateways
		Set<BpmnNode> inclusiveGateways = new HashSet<>();	
		inclusiveGateways.addAll(sortedNodes.get(BpmnNodeType.INCLUSIVE_GATEWAY));
		
		for(BpmnNode node : standardNodes) {
			Place sourcePlace = placesMap.get(node);
			Set<BpmnNode> successors = bpmnModel.getSequenceFlowSuccessors(node);
			for(BpmnNode successor : successors) {
				
				Transition transition = createTransition(node, successor);					
				
				Place targetPlace;
				if(InclusiveGateway.class.isAssignableFrom(successor.getClass())) {
					// create buffer place before inclusive gateway transition fires
					// also connect this place to that transition
					targetPlace = createPlace(node, successor);
					Transition gatewayTransition = transitionMap.get(successor);
					createArk(targetPlace, gatewayTransition);
				} else {
					targetPlace = placesMap.get(successor);
				}
				createArk(sourcePlace, transition);
				createArk(transition, targetPlace);
				
			}
		}
		
		for(BpmnNode node : inclusiveGateways) {
			Transition transition = transitionMap.get(node);
			Set<BpmnNode> successors = bpmnModel.getSequenceFlowSuccessors(node);
			for(BpmnNode successor : successors) {
				Place targetPlace = placesMap.get(successor);
				createArk(transition, targetPlace);
			}
		}

	}
	
	/**
	 * Conduct steps to parse a BPMN start event to elements of the Petri net
	 * @param startEvent the node to parse
	 */
	private void parseStartEvent(BpmnNode startEvent) {
		Place place = createPlace(startEvent);
		if(provideInitialTokensAtStartEvents) {
			place.addTokens(1);
			place.setInitial();
		}
		
	}
	
	/**
	 * Conduct steps to parse a BPMN end event to elements of the Petri net
	 * @param endEvent the node to parse
	 * @param addFinalTransition true iff the Petri net should receive a transition that fixes the final state,
	 * i.e. a transition with exactly one outgoing and one incoming ark to and from the final place corresponding to the endEvent
	 */
	private void parseEndEvent(BpmnNode endEvent, boolean addFinalTransition) {
		Place place = createPlace(endEvent);
		place.setFinal();
		if(!addFinalTransition) {
			return;
		}
		Transition finalTransition = createTransition(endEvent);
		finalTransition.setFinal();
		createArk(place, finalTransition);
		createArk(finalTransition, place);
	}
	
	/**
	 * Conduct steps to parse a BPMN (intermediate-) event to elements of the Petri net
	 * @param event the node to parse
	 */
	private void parseEvent(BpmnNode event) {
		createPlace(event);
	}
	
	/**
	 * Conduct steps to parse a BPMN activity to elements of the Petri net
	 * @param activity the node to parse
	 */
	private void parseActivity(BpmnNode activity) {
		createPlace(activity);
	}
	
	/**
	 * Conduct steps to parse a BPMN exclusive (XOR) gateway to elements of the Petri net
	 * @param exclusiveGateway the node to parse
	 */
	private void parseExclusiveGateway(BpmnNode exclusiveGateway) {
		createPlace(exclusiveGateway);
	}
	
	/**
	 * Conduct steps to parse a BPMN inclusive (AND) gateway to elements of the Petri net
	 * @param inclusiveGateway the node to parse
	 */
	private void parseInclusiveGateway(BpmnNode inclusiveGateway) {
		createTransition(inclusiveGateway);
	}
	
	/**
	 * Create a place associated with a node of the BPMN model
	 * @param node the associated node
	 */
	private Place createPlace(BpmnNode node) {
		String id = "p_" + node.getId();
		String name = node.getName() != null ? ("p_" + node.getName()) : id;
		Place place = new Place(id, name);
		petriNet.add(place);
		placesMap.put(node, place);
		return place;
	}
	
	/**
	 * Create a place that is associated with two nodes of the BPMN model
	 * e.g. the preset of an inclusive gateway transition
	 * @param nodeA the first associated node
	 * @param nodeB the second associated node
	 */
	private Place createPlace(BpmnNode nodeA, BpmnNode nodeB) {
		String id = "p_" + nodeA.getId() + "_" + nodeB.getId();
		String name = "p_" 
				+ (nodeA.getName() != null ? nodeA.getName() : nodeA.getId()) 
				+ "_" 
				+ (nodeB.getName() != null ? nodeB.getName() : nodeB.getId());
		Place place = new Place(id, name);
		petriNet.add(place);
		return place;
	}
	
	/**
	 * Create a transition that is associated with one node of the BPMN model
	 * e.g. the transition for an inclusive gateway
	 * @param node the associated node
	 * @return the created transition
	 */
	private Transition createTransition(BpmnNode node) {
		String id = "t_" + node.getId();
		String name = node.getName() != null ? ("t_" + node.getName()) : id;
		Transition transition = new Transition(id, name);
		transitionMap.put(node, transition);
		petriNet.add(transition);
		return transition;
	}
	
	/**
	 * Create a transition associated with two nodes of the BPMN model,
	 * e.g. between two activities
	 * @param nodeA the first associated node
	 * @param nodeB the second associated node
	 * @return the created transition
	 */
	private Transition createTransition(BpmnNode nodeA, BpmnNode nodeB) {
		String id = "t_" + nodeA.getId() + "_" + nodeB.getId();
		String name = "t_" 
				+ (nodeA.getName() != null ? nodeA.getName() : nodeA.getId()) 
				+ "_" 
				+ (nodeB.getName() != null ? nodeB.getName() : nodeB.getId());
		Transition transition = new Transition(id, name);
		petriNet.add(transition);
		return transition;
	}
	
	/**
	 * create a new ark from a place to a transition and add to Petri net
	 * @param place the place
	 * @param transition the transition
	 * @return the ark
	 */
	private Ark createArk(Place place, Transition transition) {
		Ark ark = new Ark(place, transition);
		transition.addIncomingArk(ark);
		petriNet.add(ark);
		return ark;
	}
	
	/**
	 * create a new ark from a transition to a place and add to Petri net
	 * @param transition the transition
	 * @param place the place
	 * @return the ark
	 */
	private Ark createArk(Transition transition, Place place) {
		Ark ark = new Ark(transition, place);
		transition.addOutgoingArk(ark);
		petriNet.add(ark);
		return ark;
	}
	
	/**
	 * identify the initial markings after constructing all places
	 */
	private void setInitialMarkings() {
		List<Place> places = this.petriNet.getPlaces();
		for(Place place : places) {
			if(!place.isInitial()) {
				continue;
			}
			Marking initialMarking = new Marking(places);
			initialMarking.putTokens(place, 1);
			this.petriNet.addInitialMarking(initialMarking);
		}
	}


	@Override
	public BeliefBase parseBeliefBase(Reader reader) throws IOException, ParserException {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		throw new UnsupportedOperationException("Method not implemented");
	}

}

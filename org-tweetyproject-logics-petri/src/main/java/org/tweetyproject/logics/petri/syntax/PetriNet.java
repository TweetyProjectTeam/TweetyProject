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
package org.tweetyproject.logics.petri.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.logics.petri.syntax.reachability_graph.Marking;
import org.tweetyproject.math.matrix.Matrix;

/**
 * A class to represent a Petri net
 * @author Benedikt Knopp, Matthias Thimm
 */
public class PetriNet implements Graph<PetriNetNode>{

	/**
	 * the places in this Petri net
	 */
	private List<Place> places = new ArrayList<>();
	
	/**
	 * the places in this Petri net
	 */
	private List<Transition> transitions = new ArrayList<>();
	
	/**
	 * the edges in this Petri net
	 */
	private Set <Edge<PetriNetNode>> edges = new HashSet<>();
	
	/**
	 * (optional) some designated initial markings (usually one)
	 */
	private Set <Marking> initialMarkings = new HashSet<>();

	
	@Override
	public boolean add(PetriNetNode node) {
		throw new UnsupportedOperationException("Method not implemented.");
	}
	
	/**
	 * Add a place to the Petri net
	 * @param place the place
	 * @return true iff adding the place was successful
	 */
	public boolean add(Place place) {
		return places.add(place);
	}
	
	/**
	 * Add a transition to the Petri net
	 * @param transition the transition
	 * @return true iff adding the transition was successful
	 */
	public boolean add(Transition transition) {
		return transitions.add(transition);
	}

	/**
	 * 
	 * @param edge add
	 * @return add
	 */
	public boolean add(Edge<PetriNetNode> edge) {
		return edges.add(edge);
	}

	@Override
	public Set<PetriNetNode> getNodes() {
		Set<PetriNetNode> nodes = new HashSet<>();
		nodes.addAll(this.places);
		nodes.addAll(this.transitions);
		return nodes;
	}

	@Override
	public int getNumberOfNodes() {
		return this.places.size() + this.transitions.size();
	}
	
	@Override
	public int getNumberOfEdges() {
		return this.edges.size();
	}	

	@Override
	public boolean areAdjacent(PetriNetNode a, PetriNetNode b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Edge<PetriNetNode> getEdge(PetriNetNode a, PetriNetNode b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Edge<PetriNetNode>> getEdges() {
		return edges;
	}

	/**
	 * @return the places
	 */
	public List<Place> getPlaces() {
		return places;
	}
	
	/**
	 * @return the transitions
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}


	/**
	 * @param places the places to set
	 */
	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	/**
	 * @return the initialMarkings
	 */
	public Set<Marking> getInitialMarkings() {
		return initialMarkings;
	}

	@Override
	public Iterator<PetriNetNode> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<PetriNetNode> getChildren(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<PetriNetNode> getParents(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsDirectedPath(PetriNetNode node1, PetriNetNode node2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<PetriNetNode> getNeighbors(PetriNetNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<PetriNetNode> getComplementGraph(int selfloops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Collection<PetriNetNode>> getStronglyConnectedComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Graph<PetriNetNode>> getSubgraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<PetriNetNode> getRestriction(Collection<PetriNetNode> nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSelfLoops() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 
	 * @param transition fire
	 */
	public void fire(Transition transition) {
		
	}

	/**
	 * Get the marking of this graph, based on the current token distribution at the places
	 * @return the current marking
	 */
	public Marking getMarking() {
		Marking marking = new Marking();
		for(Place place : this.places) {
			marking.putTokens(place, place.getTokens());
		}
		return marking;
	}

	/**
	 * Retrieve the transitions that are enabled in the current state (token distribution) of the net 
	 * @return the enabled transitions
	 */
	public Set<Transition> getEnabledTransitions() {
		Set<Transition> enabledTransitions = this.transitions.stream().filter(t -> t.canFire())
				.collect(Collectors.toSet());
		return enabledTransitions;
	}

	/**
	 * add a designated initial marking
	 * @param initialMarking the initial marking
	 * @return true iff adding the marking was successful
	 */
	public boolean addInitialMarking(Marking initialMarking) {
		return this.initialMarkings.add(initialMarking);
	}

	/**
	 * Check whether the marking is an initial marking
	 * @param marking the marking
	 * @return true iff the marking is initial
	 */
	public boolean isInitial(Marking marking) {
		// TODO Auto-generated method stub
		return this.initialMarkings.contains(marking)
				|| this.initialMarkings.stream().anyMatch(m -> m.equals(marking));
	}

	/**
	 * Set the state (token distribution) according to the provided marking
	 * @param marking the marking
	 */
	public void setState(Marking marking) {
		this.getPlaces().forEach(place -> {
			place.setTokens(marking.getTokens(place));
		});
	}

	@Override
	public boolean add(GeneralEdge<PetriNetNode> edge) {
		if(edge instanceof Edge)
			return this.add((Edge<PetriNetNode>) edge);
		return false;
	}

	/**
	 * Add a transition which is enabled at the specified marking and does not change the marking
	 * @param marking the marking
	 */
	public Transition createEmptyTransition(Marking marking) {
		Transition transition = new Transition(marking.getId(), "eps" );
		for(Place place : this.places) {
			int tokensAtPlace = marking.getTokens(place);
			if(tokensAtPlace == 0) {
				continue;
			}
			Edge<PetriNetNode> ark1 = new Ark(place, transition, tokensAtPlace);
			Edge<PetriNetNode> ark2 = new Ark(transition, place, tokensAtPlace);
			this.edges.add(ark1);
			this.edges.add(ark2);
		}
		this.transitions.add(transition);
		return transition;
	}

	/**
	 * Transform the Petri net to the short-circuited version, where the final place and initial place are linked via 
	 * a transition
	 * @throws IllegalStateException if the transformation is not possible due to violations of syntactical requirements
	 */
	public void transformToShortCircuit() throws IllegalStateException {
		if(this.checkShortCircuit()) {
			return;
		}
		List<Place> finalPlaces = places.stream().filter(
				place -> place.isFinal()
				).collect(Collectors.toList());
		List<Place> initialPlaces = places.stream().filter(
				place -> place.isInitial()
				).collect(Collectors.toList());
		// filter out the final transition (should be exactly one, but unchecked)
		transitions = transitions.stream().filter(
				transition -> !transition.isFinal()
				).collect(Collectors.toList());
		// filter out edges involving the final transition and the final place
		edges = edges.stream().filter(
				edge -> {
					return !edge.getNodeA().isFinal() || !edge.getNodeB().isFinal();
				}).collect(Collectors.toSet());

		// add and wire short circuit transition
		Transition circuitTransition = new Transition("shortCircuit","shortCircuit");
		transitions.add(circuitTransition);
		Place initialPlace = initialPlaces.get(0);
		Place finalPlace = finalPlaces.get(0);
		Ark circuitEdge1 = new Ark(finalPlace, circuitTransition);
		Ark circuitEdge2 = new Ark(circuitTransition, initialPlace);
		circuitTransition.addIncomingArk(circuitEdge1);
		circuitTransition.addOutgoingArk(circuitEdge2);
		edges.add(circuitEdge1);
		edges.add(circuitEdge2);
	}

	/**
	 * @param edges the arks (edges)
	 */
	public void setEdges(Set<Edge<PetriNetNode>> edges) {
		this.edges = edges;
	}

	/**
	 * @param transitions the transitions
	 */
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * check if this Petri net is short-circuited
	 * @return true if this Petri net is short-circuited
	 * @throws IllegalStateException if the number of initial and final places in this net is not equal to 1
	 */
	public boolean checkShortCircuit() throws IllegalStateException {	
		int numberOfInitialPlaces = 0;
		int numberOfFinalPlaces = 0;
		for(Place place: places) {
			if(place.isInitial()) {
				numberOfInitialPlaces++;
			}
			if(place.isFinal()) {
				numberOfFinalPlaces++;
			}
		}
		if(numberOfInitialPlaces != 1 || numberOfFinalPlaces != 1) {
			throw new IllegalStateException("No or more than one initial places or final places were found");
		}
		Transition initialPlaceIncomingTransition = null, finalPlaceOutgoingTransition = null;
		int initialPlaceIncomingArks = 0;
		int finalPlaceOutgoingArks = 0;
		for(Edge<PetriNetNode> edge : edges) {
			PetriNetNode nodeA = edge.getNodeA();
			PetriNetNode nodeB = edge.getNodeB();
			if( nodeB instanceof Place && ((Place) nodeB).isInitial() ) {
				initialPlaceIncomingArks++;
				initialPlaceIncomingTransition = (Transition) nodeA;
			}
			if( nodeA instanceof Place && ((Place) nodeA).isFinal() ) {
				finalPlaceOutgoingArks++;
				finalPlaceOutgoingTransition = (Transition) nodeB;
			}
		}
		return initialPlaceIncomingArks == 1 
				&& finalPlaceOutgoingArks == 1  
				&& initialPlaceIncomingTransition.equals(finalPlaceOutgoingTransition);
	}


}

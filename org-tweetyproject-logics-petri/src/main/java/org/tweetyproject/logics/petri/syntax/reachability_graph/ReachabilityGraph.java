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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.logics.petri.syntax.PetriNet;
import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.probability.Probability;
import org.tweetyproject.math.probability.ProbabilityFunction;
import org.tweetyproject.math.term.FloatConstant;

/**
 * A class to describe the graph of reachability between possible markings of a Petri net
 * @author Benedikt Knopp
 */
public class ReachabilityGraph implements Graph<Marking>, BeliefBase {

	/**
	 * remember the underlying Petri net
	 */
	private PetriNet petriNet;

	/**
	 * the markings of the Petri net described by this graph
	 */
	private List<Marking> markings = new ArrayList<>();

	/**
	 * the direct reachabilities between markings
	 */
	private List<MarkingEdge> edges = new ArrayList<>();

	/**
	 * some user-specified function that describes how likely switches between markings are to happen
	 */
	ProbabilityFunction<MarkingEdge> probabilityFunction;

	/**
	 * Create a new instance
	 * @param petriNet the underlying Petri net
	 */
	public ReachabilityGraph(PetriNet petriNet) {
		this.petriNet = petriNet;
	}

	@Override
	public boolean add(Marking node) {
		if(node.getId() == null) {
			String id = getNewNodeDefaultId();
			node.setId(id);
		}
		return markings.add(node);
	}

	/**
	 * fetch a unique identifier for a newly created marking
	 * @return
	 */
	private String getNewNodeDefaultId() {
		return "M" + String.valueOf(markings.size());
	}

	/**
	 * add a marking edge to this graph
	 * @param edge the edge
	 * @return true iff adding the edge was successful
	 */
	public boolean add(MarkingEdge edge) {
		return edges.add(edge);
	}

	@Override
	public boolean add(GeneralEdge<Marking> edge) {
		return false;
	}

	@Override
	public List<Marking> getNodes() {
		return markings;
	}

	@Override
	public int getNumberOfNodes() {
		return markings.size();
	}

	/**
	 * the number of edges in this graph
	 * @return the number of edges in this graph
	 */
	public int getNumberOfEdges() {
		return edges.size();
	}

	@Override
	public boolean areAdjacent(Marking a, Marking b) {
		return edges.stream().anyMatch(e -> e.getNodeA().equals(a) && e.getNodeB().equals(b));
	}

	@Override
	public Edge<Marking> getEdge(Marking a, Marking b) {
		Optional<MarkingEdge> edge_o = edges.stream().filter( e -> {
			return e.getNodeA().equals(a) && e.getNodeB().equals(b);
		}).findAny();
		if(edge_o.isPresent() == false) {

			return null;
		}
		return edge_o.get();
	}

	@Override
	public List<MarkingEdge> getEdges() {
		return edges;
	}

	/**
	 * Return the petriNet
	 * @return the petriNet
	 */
	public PetriNet getPetriNet() {
		return petriNet;
	}

	/**
	 * Setter petriNet
	 * @param petriNet the petriNet to set
	 */
	public void setPetriNet(PetriNet petriNet) {
		this.petriNet = petriNet;
	}

	/**
	 * the probability function of this graph, which is null for default
	 * @return the probabilityFunction
	 */
	public ProbabilityFunction<MarkingEdge> getProbabilityFunction() {
		return probabilityFunction;
	}

	/**
	 * sets the probability function of this graph
	 * @param probabilityFunction the probability function to set
	 */
	public void setProbabilityFunction(ProbabilityFunction<MarkingEdge> probabilityFunction) {
		this.probabilityFunction = probabilityFunction;
	}

	/**
	 * initializes a probability function over the edges of this graph according to
	 * Random Walk, i.e. all outgoing edges of one particular node are assigned to
	 * the same probability and the sum of these probabilities equals to 1.
	 */
	public void initializeDefaultProbabilityFunction() {
		probabilityFunction = new ProbabilityFunction<MarkingEdge>();
		for(Marking marking : this.getNodes()) {
			Set<MarkingEdge> successorEdges = this.getOutgoing(marking);
			int numberOfSuccessors = successorEdges.size();
			double p = 1/Double.valueOf(numberOfSuccessors);
			Probability probability = new Probability(p);
			successorEdges.forEach(edge -> {
				probabilityFunction.put(edge, probability);
			});
		}
	}

	/**
	 * Initialize a probability function over the edges of this graph according to
	 * a stochastic walk. The sum of probabilities of outgoing edges at each node
	 * equals to 1, and for multiple outgoing edges, probabilities are randomized.
	 */
	public void initializeRandomProbabilityFunction() {
		probabilityFunction = new ProbabilityFunction<MarkingEdge>();
		for(Marking marking : this.getNodes()) {
			List<MarkingEdge> successorEdges = this.getOutgoing(marking)
					.stream().collect(Collectors.toList());
			int numberOfSuccessors = successorEdges.size();
			double remaining = 1.0d;
			MarkingEdge edge;
			int index = 0;
			while(index < numberOfSuccessors - 1){
				edge = successorEdges.get(index);
				double rand = Math.random();
				double probabilityValue = remaining * rand;
				probabilityFunction.put(edge, new Probability(probabilityValue));
				remaining = remaining - probabilityValue;
				index++;
			}
			// last edge gets remaining probability
			edge = successorEdges.get(index);
			probabilityFunction.put(edge, new Probability(remaining));
		}
	}

	/**
	 * Initialize a probability function over the edges of this graph according to
	 * a stochastic walk. For multiple outgoing edges, exactly one edge receives a probability of 1
	 * and all other edges a probability of 0. This may turn some transitions dead.
	 */
	public void initializeIrregularProbabilityFunction() {
		probabilityFunction = new ProbabilityFunction<MarkingEdge>();
		for(Marking marking : this.getNodes()) {
			List<MarkingEdge> successorEdges = this.getOutgoing(marking)
					.stream().collect(Collectors.toList());
			int numberOfSuccessors = successorEdges.size();
			int activeEdgeIndex = (int) Math.round((numberOfSuccessors-1)*Math.random());
			MarkingEdge edge;
			double probabilityValue;
			int index = 0;
			while(index < numberOfSuccessors ){
				edge = successorEdges.get(index);
				probabilityValue = index == activeEdgeIndex ? 1.0 : 0.0;
				probabilityFunction.put(edge, new Probability(probabilityValue));
				index++;
			}
		}
	}

	/**
	 * specifies if the probability function of this graph is
	 * 1. initialized, and 2. assigns for each marking a total
	 * (summed up) probability of 1 for all successor edges.
	 * This implies that each marking needs to have at least one successor marking.
	 * @return true iff the probability function is valid
	 */
	public boolean hasValidProbabilityFunction() {
		if(probabilityFunction == null) {
			return false;
		}
		for(Marking marking : this.getNodes()) {
			Set<MarkingEdge> successorEdges = this.getOutgoing(marking);
			Double probabilitySum = probabilityFunction.entrySet().stream().filter(entry -> {
				// get only the probabilities of successor edges
				return successorEdges.contains(entry.getKey());
			}).map(entry -> {
				return entry.getValue().getValue();
			}).reduce(0d, Double::sum);
			if(!probabilitySum.equals(1.0d)) {
				return false;
			};
		}
		return true;
	};

	/**
	 * fix the sorting to prepare linear algebra analysis
	 * in particular, assert a fixed ordering for index-based matrix/vector-access
	 */
	public void sortMarkings() {
		this.markings.sort(Marking::compareTo);
	}

	/**
	 * Retrieve a matrix that specifies for each pair of markings how likely a transition between these
	 * markings is going to happen, based on the probability function that describes this graph.
	 * @return the transition matrix
	 * @throws IllegalStateException iff the probability function of this graph is invalid
	 */
	public Matrix getTransitionMatrix() throws IllegalStateException {
		if(!this.hasValidProbabilityFunction()) {
			throw new IllegalStateException("The probability function of this graph is invalid.");
		}
		int n = this.getNumberOfNodes();
		Matrix transitionMatrix = new Matrix(n, n);
		this.edges.forEach(markingEdge -> {
			Marking markingA = markingEdge.getNodeA();
			Marking markingB = markingEdge.getNodeB();
			int i = markings.indexOf(markingA);
			int j = markings.indexOf(markingB);
			Probability p = probabilityFunction.get(markingEdge);
			transitionMatrix.setEntry(j, i, new FloatConstant(p.getValue()));
		});
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(transitionMatrix.getEntry(i, j) == null) {
					transitionMatrix.setEntry(i, j, new FloatConstant(0));
				}
			}
		}
		return transitionMatrix;
	}

	/**
	 * Retrieve a matrix that specifies for each pair of marking and transition how likely that transition
	 * is going to occur at that marking, based on the probability function that describes this graph.
	 * @return the control matrix
	 * @throws IllegalStateException iff the probability function of this graph is invalid
	 */
	public Matrix getControlMatrix() {
		if(!this.hasValidProbabilityFunction()) {
			throw new IllegalStateException("The probability function of this graph is invalid.");
		}
		List<Transition> transitions = this.getPetriNet().getTransitions();
		int n = this.getNumberOfNodes();
		int m = transitions.size();
		Matrix controlMatrix = new Matrix(m,n);
		this.edges.forEach(markingEdge -> {
			Marking marking = markingEdge.getNodeA();
			Transition transition = markingEdge.getTransition();
			int i = markings.indexOf(marking);
			int j = transitions.indexOf(transition);
			Probability p = probabilityFunction.get(markingEdge);
			controlMatrix.setEntry(j, i, new FloatConstant(p.getValue()));
		});
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				if(controlMatrix.getEntry(j, i) == null) {
					controlMatrix.setEntry(j, i, new FloatConstant(0));
				}
			}
		}
		return controlMatrix;
	}


	@Override
	public Iterator<Marking> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Marking> getChildren(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Marking> getParents(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsDirectedPath(Marking node1, Marking node2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Marking> getNeighbors(Marking node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<Marking> getComplementGraph(int selfloops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Collection<Marking>> getConnectedComponents() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<Collection<Marking>> getStronglyConnectedComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Graph<Marking>> getSubgraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<Marking> getRestriction(Collection<Marking> nodes) {
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
		return false;
	}

	/**
	 * Determine if the specified marking is present as a node in the graph
	 * @param marking the marking
	 * @return true if the marking is present
	 */
	public boolean hasMarking(Marking marking) {
		return this.getMarking(marking).isPresent();
	}

	/**
	 * Retrieve, if present, the marking in this graph that equals the query marking
	 * in the sense that both markings describe the same token distribution over places
	 * @param marking the query marking
	 * @return the marking that equals the query marking, or null if not present
	 */
	public Optional<Marking> getMarking(Marking marking) {
		return markings.stream().filter(m -> m.equals(marking)).findAny();
	}

	/**
	 * For one particular marking, retrieve all outgoing edges
	 * @param marking the marking of interest
	 * @return the sequence flow successors
	 */
	public Set<MarkingEdge> getOutgoing(Marking marking){
		return this.edges.stream()
				.filter(edge -> edge.getNodeA() != null && edge.getNodeA().equals(marking))
				.collect(Collectors.toSet());
	}

	/**
	 * check if all nodes (markings) of this graph describe the same Petri net, i.e. the same set of places
	 * @return true iff all markings describe the same set of places
	 */
	public boolean checkConsistency() {
		if(markings.size() < 2) {
			return true;
		}
		Marking firstMarking = markings.get(0);
		for(int index = 1; index < markings.size(); index++) {
			final Marking marking = markings.get(index);
			if( marking.getPlaces().stream().anyMatch(place -> !firstMarking.getPlaces().contains(place))
			 || firstMarking.getPlaces().stream().anyMatch(place -> !marking.getPlaces().contains(place))
			) {
				return false;
			}
		}
		return true;

	}

	@Override
	public Signature getMinimalSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Retrieve the designated initial markings
	 * @return getInitialMarkings
	 */
	public Set<Marking> getInitialMarkings() {
		return this.petriNet.getInitialMarkings();
	}

	/**
	 * Check whether a marking is a designated initial marking
	 * @param marking the marking to check
	 * @return true iff the marking is initial
	 */
	public boolean isInitial(Marking marking) {
		return this.petriNet.isInitial(marking);
	}

	/**
	 * Retrieve the markings
	 * @return markings
	 */
	public List<Marking> getMarkings() {
		return this.markings;
	}


}

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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.syntax;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.math.matrix.Matrix;

/**
 * A class to represent a BPMN Model
 * @author Benedikt Knopp, Matthias Thimm
 */
public class BpmnModel implements Graph<BpmnNode>, BeliefBase{

	/**
	 * (sub) processes of this BPMN model
	 */
	private Set<Process> processes = new HashSet<>();

	/**
	 * the nodes in this BPMN model
	 */
	private Set<BpmnNode> nodes = new HashSet<>();

	/**
	 * the edges in this BPMN model
	 */
	private Set <Edge<BpmnNode>> edges = new HashSet<>();

	@Override
	public Signature getMinimalSignature() {
		return null;
	}


	@Override
	public boolean add(BpmnNode node) {
		return this.nodes.add(node);
	}

	@Override
	public boolean add(GeneralEdge<BpmnNode> edge) {
		return this.edges.add((Edge) edge);
	}

	/**
	 * Add a process
	 * @param process the proces to add
	 * @return status
	 */
	public boolean add(Process process) {
		return this.processes.add(process);
	}

	@Override
	public Set<BpmnNode> getNodes() {
		return this.nodes;
	}

	@Override
	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	@Override
	public int getNumberOfEdges() {
		return this.edges.size();
	}

	@Override
	public boolean areAdjacent(BpmnNode a, BpmnNode b) {
		return false;
	}

	@Override
	public Edge<BpmnNode> getEdge(BpmnNode a, BpmnNode b) {
		return null;
	}

	@Override
	public Set<Edge<BpmnNode>> getEdges() {
		return this.edges;
	}

	@Override
	public Iterator<BpmnNode> iterator() {
		return null;
	}

	@Override
	public boolean contains(Object obj) {
		return false;
	}

	@Override
	public Collection<BpmnNode> getChildren(Node node) {
		return null;
	}

	@Override
	public Collection<BpmnNode> getParents(Node node) {
		return null;
	}

	@Override
	public boolean existsDirectedPath(BpmnNode node1, BpmnNode node2) {
		return false;
	}

	@Override
	public Collection<BpmnNode> getNeighbors(BpmnNode node) {
		return null;
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		return null;
	}

	@Override
	public Graph<BpmnNode> getComplementGraph(int selfloops) {
		return null;
	}

	@Override
	public Collection<Collection<BpmnNode>> getConnectedComponents() {
		return null;
	}
	
	@Override
	public Collection<Collection<BpmnNode>> getStronglyConnectedComponents() {
		return null;
	}

	@Override
	public Collection<Graph<BpmnNode>> getSubgraphs() {
		return null;
	}

	@Override
	public Graph<BpmnNode> getRestriction(Collection<BpmnNode> nodes) {
		return null;
	}

	@Override
	public boolean hasSelfLoops() {
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		return false;
	}

	/**
	 * retrieve all nodes in this BPMN model that are Activities
	 * @return the model's Activities
	 */
	public Set<Activity> getActivities(){
		Set<Activity> activities = new HashSet<>();
		for(BpmnNode node : nodes) {
			if(isInstanceOf(node, Activity.class)) {
				activities.add((Activity) node);
			}
		}
		return activities;
	}

	/**
	 * retrieve all edges in this BPMN model that are Sequence Flows
	 * @return the model's Sequence Flows
	 */
	public Set<SequenceFlow> getSequenceFlows(){
		Set<SequenceFlow> sequenceFlows = new HashSet<>();
		for(Edge<BpmnNode> edge : edges) {
			if(isInstanceOf(edge, SequenceFlow.class)) {
				sequenceFlows.add((SequenceFlow) edge);
			}
		}
		return sequenceFlows;
	}

	/**
	 * Retrieve all nodes in this model that are instances of a certain class
	 * @param c the class of interest
	 * @return the nodes that are instances of class c
	 */
	public Set<BpmnNode> getNodesOfType(Class<?> c) {
		return this.nodes.stream()
				.filter(node -> isInstanceOf(node, c))
				//.map(node -> (c) node)
				.collect(Collectors.toSet());
	}

	/**
	 * For one particular nodes, retrieve all successors in the sequence flow of the BPMN model
	 * @param node the node of interest
	 * @return the sequence flow successors
	 */
	public Set<BpmnNode> getSequenceFlowSuccessors(BpmnNode node){
		return this.edges.stream()
				.filter(edge -> isInstanceOf(edge, SequenceFlow.class)
							&& edge.getNodeA() != null
							&& edge.getNodeA().equals(node))
				.map(edge -> edge.getNodeB())
				.collect(Collectors.toSet());
	}

	/**
	 * Determine whether an object is an instance of a particular class
	 * @param object the object
	 * @param theClass the class
	 * @return true, iff the object is an instance of that class
	 */
	private boolean isInstanceOf(Object object, Class<?> theClass) {
		return theClass.isAssignableFrom(object.getClass());
	}
	/**
	 *
	 * @author Matthias Thimm
	 *
	 */
	public enum BpmnNodeType {
		/** START_EVENT */
		START_EVENT,
		/** END_EVENT */
		END_EVENT,
		/** EVENT */
		EVENT,
		/** ACTIVITY */
		ACTIVITY,
		/** EXCLUSIVE_GATEWAY */
		EXCLUSIVE_GATEWAY,
		/** INCLUSIVE_GATEWAY */
		 INCLUSIVE_GATEWAY
	}

	/**
	 *
	 * Return getSortedNodes
	 * @return getSortedNodes
	 */
	public Map<BpmnNodeType, Set<BpmnNode>> getSortedNodes() {
		Map<BpmnNodeType, Set<BpmnNode>> sortedNodes = new HashMap<>();
		for(BpmnNodeType type : BpmnNodeType.values()) {
			sortedNodes.put(type, new HashSet<>());
		}
		nodes.forEach( node -> {
			BpmnNodeType type = getType(node);
			sortedNodes.get(type).add(node);
		});
		return sortedNodes;
	}

	private BpmnNodeType getType(BpmnNode node) {
		if(StartEvent.class.isAssignableFrom(node.getClass())) {
			return BpmnNodeType.START_EVENT;
		}
		if(EndEvent.class.isAssignableFrom(node.getClass())) {
			return BpmnNodeType.END_EVENT;
		}
		if(Event.class.isAssignableFrom(node.getClass())) {
			return BpmnNodeType.EVENT;
		}
		if(Activity.class.isAssignableFrom(node.getClass())) {
			return BpmnNodeType.ACTIVITY;
		}
		if(ExclusiveGateway.class.isAssignableFrom(node.getClass())) {
			return BpmnNodeType.EXCLUSIVE_GATEWAY;
		}
		if(InclusiveGateway.class.isAssignableFrom(node.getClass())) {
			return BpmnNodeType.INCLUSIVE_GATEWAY;
		}
		return null;
	}


    /** Default Constructor */
    public BpmnModel(){}
}

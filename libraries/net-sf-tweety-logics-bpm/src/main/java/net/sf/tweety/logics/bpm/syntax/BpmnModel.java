package net.sf.tweety.logics.bpm.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.graphs.Edge;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.Node;
import net.sf.tweety.math.matrix.Matrix;

public class BpmnModel implements Graph<BpmnNode>, BeliefBase{

	private Set<Process> processes = new HashSet<>();
	private Set<BpmnNode> nodes = new HashSet<>();
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
	public boolean add(Edge<BpmnNode> edge) {
		return this.edges.add(edge);
	}
	
	public boolean add(Process process) {
		return this.processes.add(process);
	}

	@Override
	public Set<BpmnNode> getNodes() {
		return this.nodes;
	}

	@Override
	public int getNumberOfNodes() {
		return 0;
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
	
	public Set<Activity> getActivities(){
		Set<Activity> activities = new HashSet<>();
		for(BpmnNode node : nodes) {
			if(isInstanceOf(node, Activity.class)) {
				activities.add((Activity) node);
			}
		}
		return activities;
	}
	
	public Set<SequenceFlow> getSequenceFlows(){
		Set<SequenceFlow> sequenceFlows = new HashSet<>();
		for(Edge<BpmnNode> edge : edges) {
			if(isInstanceOf(edge, SequenceFlow.class)) {
				sequenceFlows.add((SequenceFlow) edge);
			}
		}
		return sequenceFlows;
	}
	
	public Set<BpmnNode> getNodesOfType(Class<?> c) {
		return this.nodes.stream()
				.filter(node -> isInstanceOf(node, c))
				//.map(node -> (c) node)
				.collect(Collectors.toSet());
	}
	
	public Set<BpmnNode> getSequenceFlowSuccessors(BpmnNode node){
		return this.edges.stream()
				.filter(edge -> isInstanceOf(edge, SequenceFlow.class)
							&& edge.getNodeA() != null 
							&& edge.getNodeA().equals(node))
				.map(edge -> edge.getNodeB())
				.collect(Collectors.toSet());
	}
	
	private boolean isInstanceOf(Object object, Class<?> theClass) {
		return theClass.isAssignableFrom(object.getClass());
	}

}

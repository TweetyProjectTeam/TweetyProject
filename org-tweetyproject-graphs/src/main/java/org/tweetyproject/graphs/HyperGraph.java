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

package org.tweetyproject.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.math.matrix.Matrix;


/**
 * This class implements a simple directed hypergraph
 *
 * @param <T> A Node
 * @author Sebastian Franke, Matthias Thimm
 *
 */

public class HyperGraph<T extends Node> implements Graph<T>{

	/** The set of nodes */
	protected Set<T> nodes;

	/** The set of edges */
	protected Set<HyperDirEdge<T>> edges;
	/**
	 * constructor
	 */
	public HyperGraph(){
		this.nodes = new HashSet<T>();
		this.edges = new HashSet<HyperDirEdge<T>>();
	}

	@Override
	public boolean add(T node) {
		return this.nodes.add((T) node);
	}

	/**
	 * Add Edge.
	 * Return whether the operation was successful
	 * @param edge an edge
	 * @return whether the operation was successful
	 */
	public boolean add(HyperDirEdge<T> edge) {
		for(T e: edge.getNodeA())
			if(!this.nodes.contains(e))
				throw new IllegalArgumentException("The edge connects node that are not in this graph.");

		if (!this.nodes.contains(edge.getNodeB()))
			throw new IllegalArgumentException("The edge connects node that are not in this graph.");
		return this.edges.add((HyperDirEdge<T>) edge);
	}

	@Override
	public Collection<T> getNodes() {
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
	public boolean areAdjacent(Node a, Node b) {
		for(HyperDirEdge<T> e : this.edges) {
			if((e.getNodeA().contains(a) && e.getNodeB().equals(b)) ||
					(e.getNodeA().contains(b) && e.getNodeB().equals(a))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Edge<T> getEdge(Node a, Node b) {
		System.err.println("an edge in a hypergraph is comprised of a set of Elements in Node A and an Element in Node B");
		return null;
	}
	/**
	 * Get a directed edge.
	 * @param node1 a set of nodes (attacker)
	 * @param b a node (attacked)
	 * @return a directed Hyper Edge
	 */
	public HyperDirEdge<T> getDirEdge(Set<T> node1, Node b) {
		for(HyperDirEdge<T> e : this.edges) {
			if(e.getNodeA().equals(node1) && e.getNodeB().equals(b))
				return e;
		}

		return null;
	}

	@Override
	public Collection<HyperDirEdge<T>> getEdges() {
		return this.edges;
	}

	@Override
	public Iterator<T> iterator() {
		return this.nodes.iterator();
	}

	@Override
	public boolean contains(Object obj) {
		if(obj instanceof Node)
			if(this.nodes.contains((Node) obj)){
				return true;
			}
			else {
				return false;
			}
		if(obj instanceof HyperDirEdge)
			if(this.edges.contains(obj)){
				return true;
			}
			else {
				return false;
			}
		return false;
	}

	@Override
	public Collection<T> getChildren(Node node) {
		System.err.println("an edge in a hypergraph is comprised of a set of Elements in Node A and an Element in Node B."
				+ "Please choose a set of Nodes to find the set's children");
		return null;
	}
	/**
	 *
	 * Return the children of the node
	 * @param node a node
	 * @return the children of the node
	 */
	public Collection<T> getChildren(Set<T> node) {
		HashSet<T> result = new HashSet<T>();
		for(HyperDirEdge<T> e : this.edges) {
			if(e.getNodeA().equals(node)) {
				result.add(e.getNodeB());
			}
		}
		return result;

	}

	/**
	 * returns all parents without taking indivdual attacks into account
	 */
	@Override
	public Collection<T> getParents(Node node) {
		HashSet<T> result = new HashSet<T>();
		for(HyperDirEdge<T> e : this.edges) {
			if(e.getNodeB().equals(node)) {
				result.addAll(e.getNodeA());
			}
		}
		return result;
	}

	/**
	 *
	 * Checks if there is a direct path from node 1 to node 2
	 * @param hyperGraph a hypergraph
	 * @param node1 1st node
	 * @param node2 2nd node
	 * @return checks if there is a direct path from node 1 to node 2
	 */
	public boolean existsDirectedPath(HyperGraph<T> hyperGraph, T node1, T node2) {
		if (!hyperGraph.getNodes().contains(node1) || !hyperGraph.getNodes().contains(node2))
			throw new IllegalArgumentException("The nodes are not in this graph.");
		if (node1.equals(node2))
			return true;
		// we perform a DFS.
		Stack<T> stack = new Stack<T>();
		Collection<T> visited = new HashSet<T>();
		stack.add((T) node1);
		while (!stack.isEmpty()) {
			T node = stack.pop();
			visited.add(node);
			if (node.equals(node2))
				return true;
			stack.addAll(hyperGraph.getChildren(node));
			stack.removeAll(visited);
		}
		return false;
	}

	@Override
	public boolean existsDirectedPath(T node1, T node2) {
		return this.existsDirectedPath(this, node1, node2);
	}



	@Override
	public Collection<T> getNeighbors(Node node) {
		HashSet<T> result = new HashSet<T>();
		for(HyperDirEdge<T> a : this.edges) {
			if(a.getNodeB().equals(node)) {
				result.addAll(a.getNodeA());
			}
			if(a.getNodeA().contains(node)) {
				result.add(a.getNodeB());
			}
		}
		return result;
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		// A matrix representation o a hypergraph is not known to me
		return null;
	}

	/**
	 * Return a powerset.
	 * @param originalSet original set
	 * @return the powerset of @param originalSet
	 */
	public Set<Set<T>> powerSet(Set<T> originalSet) {
	    HashSet<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new HashSet<T>());
	        return sets;
	    }
	    ArrayList<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    HashSet<T> rest = new HashSet<T>(list.subList(1, list.size()));
	    for (Set<T> set : powerSet(rest)) {
	        Set<T> newSet = new HashSet<T>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }

	    return sets;
	}


	@Override
	public HyperGraph<T> getComplementGraph(int selfloops) {
		//very inefficient
		Set<Set<T>> myPowerSet = new HashSet<Set<T>>();
		myPowerSet = powerSet(this.nodes);

		HyperGraph<T> comp = new HyperGraph<T>();
		for (T node : this.nodes)
			comp.add(node);
		//iterate over powerset and add every edge that is not in the original graph
		//this can make the String represtnattion extremly log
		//and it may not be able to be shown in 1 line with the toString() method
		for (Set<T> node1 : myPowerSet)
			for (T node2 : this.nodes)
				if (node1.contains(node2)) {
					if (selfloops == HyperGraph.INVERT_SELFLOOPS) {
						if (this.getDirEdge(node1, node2) != null)
							comp.add(new HyperDirEdge<T>(node1, node2));
					} else if (selfloops == HyperGraph.IGNORE_SELFLOOPS) {
						if (this.getDirEdge(node1, node2) != null)
							comp.add(new HyperDirEdge<T>(node1, node2));
					}
				} else if (this.getDirEdge(node1, node2) == null) {
					comp.add(new HyperDirEdge<T>(node1, node2));
				}


		return comp;
	}

	@Override
	public Collection<Collection<T>> getConnectedComponents() {
		// TODO Auto-generated method stub
		//algorithm yet to be implemented, not important for the next time
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	@Override
	public Collection<Collection<T>> getStronglyConnectedComponents() {
		// TODO Auto-generated method stub
		//algorithm yet to be implemented, not important for the next time
		throw new UnsupportedOperationException("not yet implemented");
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.GeneralGraph#getSubgraphs()
	 */
	public Collection<Graph<T>> getSubgraphs() {
		return this.getSubgraphs(this);
	}

	/**
	 * Returns the set of sub graphs of the given graph.
	 * @param g a graph
	 *
	 * @return the set of sub graphs of the given graph.
	 */
	public Collection<Graph<T>> getSubgraphs(HyperGraph<T> g) {

		// not very efficient but will do for now
		Collection<Graph<T>> result = new HashSet<Graph<T>>();
		Set<Set<T>> subNodes = new SetTools<T>().subsets(g.getNodes());
		for (Set<T> nodes : subNodes) {
			Set<Set<HyperDirEdge<T>>> edges = new SetTools<HyperDirEdge<T>>()
					.subsets((Set<HyperDirEdge<T>>) g.getRestriction(nodes).getEdges());
			for (Set<HyperDirEdge<T>> es : edges) {
				HyperGraph<T> newg = new HyperGraph<T>();
				newg.nodes.addAll(nodes);
				newg.edges.addAll(es);
				result.add(newg);
			}
		}

		return result;
	}

	@Override
	public HyperGraph<T> getRestriction(Collection<T> nodes) {
		HyperGraph<T> graph = new HyperGraph<T>();
		graph.nodes.addAll(nodes);
		for (HyperDirEdge<T> e : this.edges)
			if (nodes.containsAll(e.getNodeA()) && nodes.contains(e.getNodeB()))
				graph.add(e);
		return graph;
	}

	@Override
	public boolean hasSelfLoops() {
		for (T node1 : this.nodes)
			if (this.areAdjacent(node1, node1))
				return true;
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		return false;
	}

	@Override
	public boolean add(GeneralEdge<T> edge) {
		return this.add((HyperDirEdge<T>) edge);
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "<" + this.nodes.toString() + "," + this.edges.toString() + ">";
	}


}

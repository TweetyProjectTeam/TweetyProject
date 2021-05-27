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
 * @author Sebastian Franke
 *
 */

public class HyperGraph<T extends Node> implements Graph{
	
	/** The set of nodes */
	protected Set<T> nodes;

	/** The set of edges */
	protected Set<HyperDirEdge<T>> edges;
	
	public HyperGraph(){
		this.nodes = new HashSet<T>();
		this.edges = new HashSet<HyperDirEdge<T>>();
	}

	@Override
	public boolean add(Node node) {
		return this.nodes.add((T) node);
	}


	public boolean add(HyperDirEdge<T> edge) {
		for(T e: edge.getNodeA())
			if(!this.nodes.contains(e))
				throw new IllegalArgumentException("The edge connects node that are not in this graph.");
		
		if (!this.nodes.contains(edge.getNodeB()))
			throw new IllegalArgumentException("The edge connects node that are not in this graph.");
		return this.edges.add((HyperDirEdge<T>) edge);
	}

	@Override
	public Collection getNodes() {
		return this.nodes;
	}

	@Override
	public int getNumberOfNodes() {
		return this.nodes.size();
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
	public Edge getEdge(Node a, Node b) {
		System.err.println("an edge in a hypergraph is comprised of a set of Elements in Node A and an Element in Node B");
		return null;
	}
	
	public HyperDirEdge getDirEdge(Set<T> node1, Node b) {
		for(HyperDirEdge e : this.edges) {
			if(e.getNodeA().equals(node1) && e.getNodeB().equals(b))
				return e;
		}

		return null;
	}

	@Override
	public Collection getEdges() {
		return this.edges;
	}

	@Override
	public Iterator iterator() {
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
			if(this.edges.contains((HyperDirEdge) obj)){
				return true;
			}
			else {
				return false;
			}
		return false;
	}

	@Override
	public Collection getChildren(Node node) {
		System.err.println("an edge in a hypergraph is comprised of a set of Elements in Node A and an Element in Node B."
				+ "Please choose a set of Nodes to find the set's children");
		return null;
	}
	
	public Collection getChildren(Set<Node> node) {
		HashSet<Node> result = new HashSet<Node>();
		for(HyperDirEdge e : this.edges) {
			if(e.getNodeA().equals(node)) {
				result.add(e.getNodeB());
			}
		}
		return result;
		
	}

	@Override
	public Collection getParents(Node node) {
		HashSet<Set<Node>> result = new HashSet<Set<Node>>();
		for(HyperDirEdge e : this.edges) {
			if(e.getNodeB().equals(node)) {
				result.add(e.getNodeA());
			}
		}
		return result;
	}

	
	public static <S extends Node> boolean existsDirectedPath(HyperGraph<S> hyperGraph, Node node1, Node node2) {
		if (!hyperGraph.getNodes().contains(node1) || !hyperGraph.getNodes().contains(node2))
			throw new IllegalArgumentException("The nodes are not in this graph.");
		if (node1.equals(node2))
			return true;
		// we perform a DFS.
		Stack<S> stack = new Stack<S>();
		Collection<S> visited = new HashSet<S>();
		stack.add((S) node1);
		while (!stack.isEmpty()) {
			S node = stack.pop();
			visited.add(node);
			if (node.equals(node2))
				return true;
			stack.addAll(hyperGraph.getChildren(node));
			stack.removeAll(visited);
		}
		return false;
	}
	
	@Override
	public boolean existsDirectedPath(Node node1, Node node2) {
		return this.existsDirectedPath(this, node1, node2);
	}
	


	@Override
	public Collection getNeighbors(Node node) {
		HashSet<Node> result = new HashSet<Node>();
		for(HyperDirEdge a : this.edges) {
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
	 * 
	 * 
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
	public HyperGraph getComplementGraph(int selfloops) {
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
	public Collection getStronglyConnectedComponents() {
		// TODO Auto-generated method stub
		//algorithm yet to be implemented, not important for the next time
		return null;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.graphs.Graph#getSubgraphs()
	 */
	public Collection<Graph<T>> getSubgraphs() {
		return this.<T>getSubgraphs(this);
	}
	
	/**
	 * Returns the set of sub graphs of the given graph.
	 * @param g a graph
	 * @param <S> the type of nodes
	 * 
	 * @return the set of sub graphs of the given graph.
	 */
	public Collection getSubgraphs(HyperGraph<T> g) {
		
		// not very efficient but will do for now
		Collection<HyperGraph<T>> result = new HashSet<HyperGraph<T>>();
		Set<Set<T>> subNodes = new SetTools<T>().subsets(g.getNodes());
		for (Set<T> nodes : subNodes) {
			@SuppressWarnings("unchecked")
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
	public HyperGraph getRestriction(Collection nodes) {
		HyperGraph<T> graph = new HyperGraph<T>();
		graph.nodes.addAll(nodes);
		for (HyperDirEdge<T> e : this.edges)
			if (nodes.contains(e.getNodeA()) && nodes.contains(e.getNodeB()))
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
	public boolean add(GeneralEdge edge) {
		return this.add((HyperDirEdge) edge);
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

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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.graphs;

import java.util.*;

import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.math.matrix.Matrix;
import net.sf.tweety.math.term.IntegerConstant;

/**
 * Instance of this class represent graphs with nodes of type T
 * 
 * @author Matthias Thimm
 * 
 * @param <T> The type of the node.
 */
public class DefaultGraph<T extends Node> implements Graph<T> {

	/** The set of nodes */
	private Set<T> nodes;

	/** The set of edges */
	private Set<Edge<T>> edges;

	/**
	 * Creates an empty graph.
	 */
	public DefaultGraph() {
		this.nodes = new HashSet<T>();
		this.edges = new HashSet<Edge<T>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#add(net.sf.tweety.graphs.Node)
	 */
	public boolean add(T node) {
		return this.nodes.add(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#add(net.sf.tweety.graphs.Edge)
	 */
	public boolean add(Edge<T> edge) {
		if (!this.nodes.contains(edge.getNodeA()) || !this.nodes.contains(edge.getNodeB()))
			throw new IllegalArgumentException("The edge connects node that are not in this graph.");
		return this.edges.add(edge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getNodes()
	 */
	public Collection<T> getNodes() {
		return this.nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getNumberOfNodes()
	 */
	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getEdges()
	 */
	public Collection<Edge<T>> getEdges() {
		return this.edges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.nodes.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#contains(java.lang.Object)
	 */
	public boolean contains(Object obj) {
		return this.nodes.contains(obj) || this.edges.contains(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getNeighbors(net.sf.tweety.graphs.Node)
	 */
	public Collection<T> getNeighbors(T node) {
		if (!this.nodes.contains(node))
			throw new IllegalArgumentException("The node is not in this graph.");
		Set<T> neighbors = new HashSet<T>();
		for (Edge<T> edge : this.edges) {
			if (edge.getNodeA() == node)
				neighbors.add(edge.getNodeB());
			else if (edge.getNodeB() == node)
				neighbors.add(edge.getNodeA());
		}
		return neighbors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getChildren(net.sf.tweety.graphs.Node)
	 */
	public Collection<T> getChildren(Node node) {
		if (!this.nodes.contains(node))
			throw new IllegalArgumentException("The node is not in this graph.");
		Set<T> children = new HashSet<T>();
		for (Edge<T> edge : this.edges) {
			if (edge.getNodeA() == node)
				children.add(edge.getNodeB());
			else if (edge.getNodeB() == node && (edge instanceof UndirectedEdge))
				children.add(edge.getNodeA());
		}
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getParents(net.sf.tweety.graphs.Node)
	 */
	public Collection<T> getParents(Node node) {
		if (!this.nodes.contains(node))
			throw new IllegalArgumentException("The node is not in this graph.");
		Set<T> parents = new HashSet<T>();
		for (Edge<T> edge : this.edges) {
			if (edge.getNodeB() == node)
				parents.add(edge.getNodeA());
			else if (edge.getNodeA() == node && (edge instanceof UndirectedEdge))
				parents.add(edge.getNodeB());
		}
		return parents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#areAdjacent(net.sf.tweety.graphs.Node,
	 * net.sf.tweety.graphs.Node)
	 */
	public boolean areAdjacent(T a, T b) {
		return this.getEdge(a, b) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getEdge(net.sf.tweety.graphs.Node,
	 * net.sf.tweety.graphs.Node)
	 */
	@Override
	public Edge<T> getEdge(T a, T b) {
		for (Edge<T> edge : this.edges) {
			if (edge.getNodeA().equals(a) || (edge instanceof UndirectedEdge && edge.getNodeB().equals(a)))
				if (edge.getNodeB().equals(b) || (edge instanceof UndirectedEdge && edge.getNodeA().equals(b)))
					return edge;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#existsDirectedPath(net.sf.tweety.graphs.Node,
	 * net.sf.tweety.graphs.Node)
	 */
	public boolean existsDirectedPath(T node1, T node2) {
		return DefaultGraph.existsDirectedPath(this, node1, node2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getAdjancyMatrix()
	 */
	public Matrix getAdjacencyMatrix() {
		Matrix m = new Matrix(this.getNumberOfNodes(), this.getNumberOfNodes());
		int i = 0, j;
		for (T a : this.nodes) {
			j = 0;
			for (T b : this.nodes) {
				m.setEntry(i, j, new IntegerConstant(this.areAdjacent(a, b) ? 1 : 0));
				j++;
			}
			i++;
		}
		return m;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "<" + this.nodes + "," + this.edges + ">";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getComplementGraph(int)
	 */
	@Override
	public Graph<T> getComplementGraph(int selfloops) {
		Graph<T> comp = new DefaultGraph<T>();
		for (T node : this)
			comp.add(node);
		for (T node1 : this)
			for (T node2 : this)
				if (node1 == node2) {
					if (selfloops == Graph.INVERT_SELFLOOPS) {
						if (!this.areAdjacent(node1, node2))
							comp.add(new DirectedEdge<T>(node1, node2));
					} else if (selfloops == Graph.IGNORE_SELFLOOPS) {
						if (this.areAdjacent(node1, node2))
							comp.add(new DirectedEdge<T>(node1, node2));
					}
				} else if (!this.areAdjacent(node1, node2))
					comp.add(new DirectedEdge<T>(node1, node2));
		return comp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#hasSelfLoops()
	 */
	@Override
	public boolean hasSelfLoops() {
		for (T node1 : this)
			if (this.areAdjacent(node1, node1))
				return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#isWeightedGraph()
	 */
	@Override
	public boolean isWeightedGraph() {
		for (Edge<T> e : this.edges)
			if (!(e instanceof WeightedEdge))
				return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getStronglyConnectedComponents()
	 */
	@Override
	public Collection<Collection<T>> getStronglyConnectedComponents() {
		return DefaultGraph.<T>getStronglyConnectedComponents(this);
	}

	/**
	 * Main method for computing the strongly connected components using Tarjan's
	 * algorithm.
	 * 
	 * @param idx     the current node index
	 * @param v       the current node
	 * @param stack   the stack of nodes that need to be visited
	 * @param sccs    the set of SCCs that is computed
	 * @param g       the graph
	 * @param index   an index map for the vertices
	 * @param lowlink the lowlink map for the vertices
	 * @return
	 */
	private static <S extends Node> int getStronglyConnectedComponentsRec(int idx, S v, Stack<S> stack,
			Collection<Collection<S>> sccs, Graph<S> g, Map<S, Integer> index, Map<S, Integer> lowlink) {
		index.put(v, idx);
		lowlink.put(v, idx);
		idx++;
		stack.push(v);
		for (S w : g.getChildren(v)) {
			if (!index.containsKey(w)) {
				idx = getStronglyConnectedComponentsRec(idx, w, stack, sccs, g, index, lowlink);
				lowlink.put(v, Math.min(lowlink.get(v), lowlink.get(w)));
			} else if (stack.contains(w)) {
				lowlink.put(v, Math.min(lowlink.get(v), index.get(w)));
			}
		}
		if (lowlink.get(v).equals(index.get(v))) {
			Collection<S> scc = new HashSet<S>();
			S w;
			do {
				w = stack.pop();
				scc.add(w);
			} while (!v.equals(w));
			sccs.add(scc);
		}
		return idx;
	}

	/**
	 * Returns the strongly connected components of the given graph. A set of nodes
	 * is strongly connected, if there is a path from each node to each other. A set
	 * of nodes is called strongly connected component if it is strongly connected
	 * and maximal with respect to set inclusion. The strongly connected components
	 * are computed using Tarjan's algorithm
	 * 
	 * @param g some graph
	 * @return the strongly connected components of the graph.
	 */
	public static <S extends Node> Collection<Collection<S>> getStronglyConnectedComponents(Graph<S> g) {
		int idx = 0;
		Stack<S> stack = new Stack<S>();
		Collection<Collection<S>> sccs = new HashSet<Collection<S>>();
		Map<S, Integer> index = new HashMap<S, Integer>();
		Map<S, Integer> lowlink = new HashMap<S, Integer>();
		for (S v : g) {
			if (!index.containsKey(v))
				idx = getStronglyConnectedComponentsRec(idx, v, stack, sccs, g, index, lowlink);
		}
		return sccs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getSubgraphs()
	 */
	public Collection<Graph<T>> getSubgraphs() {
		return DefaultGraph.<T>getSubgraphs(this);
	}

	/**
	 * Returns the set of sub graphs of the given graph.
	 * 
	 * @return the set of sub graphs of the given graph.
	 */
	public static <S extends Node> Collection<Graph<S>> getSubgraphs(Graph<S> g) {
		// not very efficient but will do for now
		Collection<Graph<S>> result = new HashSet<Graph<S>>();
		Set<Set<S>> subNodes = new SetTools<S>().subsets(g.getNodes());
		for (Set<S> nodes : subNodes) {
			@SuppressWarnings("unchecked")
			Set<Set<Edge<S>>> edges = new SetTools<Edge<S>>()
					.subsets((Set<Edge<S>>) g.getRestriction(nodes).getEdges());
			for (Set<Edge<S>> es : edges) {
				DefaultGraph<S> newg = new DefaultGraph<S>();
				newg.nodes.addAll(nodes);
				newg.edges.addAll(es);
				result.add(newg);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.graphs.Graph#getRestriction(java.util.Collection)
	 */
	@Override
	public DefaultGraph<T> getRestriction(Collection<T> nodes) {
		DefaultGraph<T> graph = new DefaultGraph<T>();
		graph.nodes.addAll(nodes);
		for (Edge<T> e : this.edges)
			if (nodes.contains(e.getNodeA()) && nodes.contains(e.getNodeB()))
				graph.add(e);
		return graph;
	}

	/**
	 * Checks whether there is a (directed) path from node1 to node2 in the given
	 * graph.
	 * 
	 * @param g     some graph.
	 * @param node1 some node.
	 * @param node2 some node.
	 * @return "true" if there is a directed path from node1 to node2.
	 */
	public static <S extends Node> boolean existsDirectedPath(Graph<S> g, S node1, S node2) {
		if (!g.getNodes().contains(node1) || !g.getNodes().contains(node2))
			throw new IllegalArgumentException("The nodes are not in this graph.");
		if (node1.equals(node2))
			return true;
		// we perform a DFS.
		Stack<S> stack = new Stack<S>();
		Collection<S> visited = new HashSet<S>();
		stack.add(node1);
		while (!stack.isEmpty()) {
			S node = stack.pop();
			visited.add(node);
			if (node.equals(node2))
				return true;
			stack.addAll(g.getChildren(node));
			stack.removeAll(visited);
		}
		return false;
	}

	/**
	 * Checks whether there is at least one cycle in the given graph.
	 * 
	 * @param g some graph
	 * @return "true" if there is a cycle in the graph, "false" if the graph is acyclic
	 */
	public static <S extends Node> boolean containsCycle(Graph<S> g) {
		Map<Node, Integer> states = new HashMap<Node, Integer>();
		for (Node n : g) 
			if (containsBackEdge(n,states,g))
				return true;
		return false;

	}

	/**
	 * Helper method for detecting cycles using depth-first search.
	 * 
	 * @param parent      current node
	 * @param states map of states of nodes
	 * @param g      some graph
	 * @return "true" if a back edge (cycle) was found in the DFS step, false
	 *         otherwise
	 */
	public static <S extends Node> boolean containsBackEdge(Node parent, Map<Node, Integer> states,Graph<S> g) {
		final int OPEN = 0; 					// node has been visited but not all of its edges
		final int CLOSED = 1; 					// node and its edges have been fully explored
		states.put(parent, OPEN);
		for (Node child_node : g.getChildren(parent)) {
			if (!states.containsKey(child_node)) {		//found unvisited node, apply DFS recursively
				if (containsBackEdge(child_node,states,g))
					return true;
			} else if (states.get(child_node) == OPEN) 	//found back edge
				return true;
		}
		states.put(parent, CLOSED);
		return false;
	}
}

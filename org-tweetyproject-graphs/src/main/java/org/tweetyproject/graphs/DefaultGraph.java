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
package org.tweetyproject.graphs;

import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.term.IntegerConstant;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Instance of this class represent graphs with nodes of type T
 *
 * @author Matthias Thimm
 *
 * @param <T> The type of the node.
 */
public class DefaultGraph<T extends Node> implements Graph<T> {

	/** The set of nodes */
	protected Set<T> nodes;

	/** The set of edges */
	protected Set<Edge<T>> edges;

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
	 * @see org.tweetyproject.graphs.Graph#add(org.tweetyproject.graphs.Node)
	 */
	public boolean add(T node) {
		return this.nodes.add(node);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#add(org.tweetyproject.graphs.Edge)
	 */
	/**
	 *
	 * Return whether the operation was successful
	 * @param edge an edge
	 * @return whether the operation was successful
	 */
	public boolean add(Edge<T> edge) {
		if (!this.nodes.contains(edge.getNodeA()) || !this.nodes.contains(edge.getNodeB()))
			throw new IllegalArgumentException("The edge connects node that are not in this graph.");
		return this.edges.add(edge);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#getNodes()
	 */
	public Collection<T> getNodes() {
		return this.nodes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#getNumberOfNodes()
	 */
	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	@Override
	public int getNumberOfEdges() {
		return this.edges.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#getEdges()
	 */
	public Collection<Edge<T>> getEdges() {
		return this.edges;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.nodes.iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#contains(java.lang.Object)
	 */
	public boolean contains(Object obj) {
		return this.nodes.contains(obj) || this.edges.contains(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#getNeighbors(org.tweetyproject.graphs.Node)
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
	 * @see org.tweetyproject.graphs.Graph#getChildren(org.tweetyproject.graphs.Node)
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
	 * @see org.tweetyproject.graphs.Graph#getParents(org.tweetyproject.graphs.Node)
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
	 * @see org.tweetyproject.graphs.Graph#areAdjacent(org.tweetyproject.graphs.Node,
	 * org.tweetyproject.graphs.Node)
	 */
	public boolean areAdjacent(T a, T b) {
		return this.getEdge(a, b) != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#getEdge(org.tweetyproject.graphs.Node,
	 * org.tweetyproject.graphs.Node)
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
	 * @see org.tweetyproject.graphs.Graph#existsDirectedPath(org.tweetyproject.graphs.Node,
	 * org.tweetyproject.graphs.Node)
	 */
	public boolean existsDirectedPath(T node1, T node2) {
		return DefaultGraph.existsDirectedPath(this, node1, node2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#getAdjancyMatrix()
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
	 * @see org.tweetyproject.graphs.Graph#getComplementGraph(int)
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
	 * @see org.tweetyproject.graphs.Graph#hasSelfLoops()
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
	 * @see org.tweetyproject.graphs.Graph#isWeightedGraph()
	 */
	@Override
	public boolean isWeightedGraph() {
		for (Edge<T> e : this.edges)
			if (!(e instanceof WeightedEdge))
				return false;
		return true;
	}

	@Override
	public Collection<Collection<T>> getConnectedComponents() {
		return DefaultGraph.<T>getConnectedComponents(this);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.graphs.Graph#getStronglyConnectedComponents()
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
	 * @return the updated idx
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
	 * Returns the set of (simple) connected components of this graph.
	 * A set of nodes is connected, if there is some path (ignoring edge
	 * directions) from each node to each other. It is a connected component
	 * if it is connected and maximal wrt. set inclusion.
	 * @param <S> a node
	 * @param g some graph
	 * @return the connected components of the graph
	 */
	public static <S extends Node> Collection<Collection<S>> getConnectedComponents(Graph<S> g) {
		Collection<S> remaining = new HashSet<>(g.getNodes());
		Collection<Collection<S>> components = new HashSet<>();
		while(!remaining.isEmpty()) {
			Collection<S> component = new HashSet<>();
			Stack<S> stack = new Stack<S>();
			S node = remaining.iterator().next();
			stack.push(node);
			while(!stack.isEmpty()) {
				node = stack.pop();
				if(component.contains(node))
					continue;
				component.add(node);
				remaining.remove(node);
				stack.addAll(g.getChildren(node));
				stack.addAll(g.getParents(node));				
			}
			components.add(component);
		}
		return components;
	}
	
	/**
	 * 	 * Returns the strongly connected components of the given graph. A set of nodes
	 * is strongly connected, if there is a path from each node to each other. A set
	 * of nodes is called strongly connected component if it is strongly connected
	 * and maximal with respect to set inclusion. The strongly connected components
	 * are computed using Tarjan's algorithm
	 *
	 * @param <S> a Node
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
	 * @see org.tweetyproject.graphs.GeneralGraph#getSubgraphs()
	 */
	public Collection<Graph<T>> getSubgraphs() {
		return DefaultGraph.<T>getSubgraphs(this);
	}

	/**
	 * Returns the set of sub graphs of the given graph.
	 * @param g a graph
	 * @param <S> the type of nodes
	 *
	 * @return the set of sub graphs of the given graph.
	 */
	public static <S extends Node> Collection<Graph<S>> getSubgraphs(GeneralGraph<S> g) {
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
	 * @see org.tweetyproject.graphs.Graph#getRestriction(java.util.Collection)
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
	 * @param <S> a Node
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
	 * @param <S> a Node
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
	 * @param <S> a Node
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

	/**
	 * Finds the cycles of an graph order-sensitively, excluding self-loops (cycles of length one).
	 * @param <S> a Node
	 * @param g The graph to be searched.
	 * @return An stack of nodes with the order indicating the direction of the cycle (assuming an directed graph).
	 */
	public static <S extends Node> Set<Stack<S>> getCyclesExcludingSelfLoops(Graph<S> g) {
		Set<Stack<S>> results = new HashSet<Stack<S>>();
		results.addAll(DefaultGraph.getCyclesIncludingSelfLoops(g));
		Collection<? extends GeneralEdge<? extends S>> edges = g.getEdges();

		// removing all self-loops
		for(GeneralEdge<? extends S> singleEdge : edges) {
			if(((Edge<? extends S>) singleEdge).getNodeA().equals(((Edge<? extends S>) singleEdge).getNodeB())) {
				Stack<S> removeFromResults = new Stack<S>();
				removeFromResults.push(((Edge<? extends S>) singleEdge).getNodeA());
				removeFromResults.push(((Edge<? extends S>) singleEdge).getNodeA());
				results.remove(removeFromResults);
			}
		}

		return results;
	}

	/**
	 * Finds the cycles of an graph order-sensitively, including self-loops (cycles of length one).
	 * @param <S> a Node
	 * @param g The graph to be searched.
	 * @return An stack of nodes with the order indicating the direction of the cycle (assuming an directed graph).
	 */
	public static <S extends Node> Set<Stack<S>> getCyclesIncludingSelfLoops(Graph<S> g) {
		// early out for performance reason
		if(!DefaultGraph.containsCycle(g))
			return new HashSet<Stack<S>>();

		// Adapted version of Johnson's Algorithm described in
		// "Find All The elementary Circuits Of A Directed Graph" by D. B. Johnson (1975)
		Map<S, Set<S>> ag = new HashMap<S, Set<S>>();
		Map<S, Set<S>> b = new HashMap<S, Set<S>>();
		Map<S, Boolean> blocked = new HashMap<S, Boolean>();
		S s;
		Stack<S> stack = new Stack<S>();
		Set<Stack<S>> results = new HashSet<Stack<S>>();

		Collection<Collection<S>> stronglyConnectedComponents = DefaultGraph.getStronglyConnectedComponents(g);
		for(Collection<S> singleComponent : stronglyConnectedComponents) {
			for(S node : singleComponent) {
				Collection<S> children = g.getChildren(node);
				// Filtering out children who are not in the same SCC
				// SCC := Strongly Connected Component
				Set<S> childrenInSCC = children.stream().filter(child -> singleComponent.contains(child)).collect(Collectors.toSet());
				ag.put(node, childrenInSCC);
			}
		}

		for(Collection<S> singleComponent : stronglyConnectedComponents) {
			Iterator<S> componentIterator = singleComponent.iterator();
			while(componentIterator.hasNext()) {
				s = componentIterator.next();
				for(S i : singleComponent) {
					blocked.put(i, false);
					b.put(i, new HashSet<S>());
				}

				DefaultGraph.circuit(s, stack, blocked, ag, b, s, results);
			}
		}


		return results;


	}

	// Helper function for getCycles
	private static <S extends Node> void unblock(S u, Map<S, Boolean> blocked, Map<S, Set<S>> b){
		blocked.put(u, false);
		for(S w : b.get(u)) {
			b.remove(w);
			if(blocked.get(w))
				DefaultGraph.unblock(w, blocked, b);
		}
	}

	// Helper function for getCycles
	private static <S extends Node> Boolean circuit(S v, Stack<S> stack, Map<S, Boolean> blocked, Map<S, Set<S>> ak, Map<S, Set<S>> b, S s, Set<Stack<S>> results) {
		Boolean f = false;
		stack.push(v);
		blocked.put(v, true);
		for (S w : ak.get(v)) {
			if(w == s) {
				Stack<S> singleResult = new Stack<S>();
				singleResult.addAll(stack);
				singleResult.push(s);
				results.add(singleResult);
				f = true;
			} else if(!blocked.get(w))
				if(DefaultGraph.circuit(w, stack, blocked, ak, b, s, results))
					f = true;
		}

		if(f)
			unblock(v, blocked, b);
		else
			for(S w : ak.get(v))
				b.get(w).add(v);

		stack.pop();
		return f;
	}


	/**
	 * Finds all components of a graph.
	 * @param <S> a Node
	 * @param g The graph which components should be found.
	 * @return A collection of the components as separate graphs.
	 */
	public static <S extends Node> Collection<Graph<S>> getComponents(Graph<S> g) {
		// implementation via BFS adapted to directed graphs
		Collection<Graph<S>> components = new HashSet<Graph<S>>();
		Stack<S> notVisited = new Stack<S>();
		notVisited.addAll(g.getNodes());

		while(!notVisited.isEmpty()) {
			Graph<S> singleComponent = new DefaultGraph<S>();
			S startNode = notVisited.pop();

			Queue<S> queue = new LinkedList<S>();
			queue.add(startNode);

			while(!queue.isEmpty()) {
				S currentNode = queue.poll();
				singleComponent.add(currentNode);

				Collection<S> parentNodes = g.getParents(currentNode);
				Collection<S> childNodes = g.getChildren(currentNode);

				Collection<S> adjacentNodes = new HashSet<S>();
				adjacentNodes.addAll(parentNodes);
				adjacentNodes.addAll(childNodes);
				Collection<S> filteredAdjacentNodes = adjacentNodes.stream().filter(node -> notVisited.contains(node)).collect(Collectors.toList());

				queue.addAll(filteredAdjacentNodes);
				notVisited.removeAll(filteredAdjacentNodes);

				for(S singleParentNode : parentNodes) {
					singleComponent.add(singleParentNode);
					singleComponent.add(g.getEdge(singleParentNode, currentNode));
				}

				for(S singleChildNode : childNodes) {
					singleComponent.add(singleChildNode);
					singleComponent.add(g.getEdge(currentNode, singleChildNode));
				}

			}

			components.add(singleComponent);

		}

		return components;
	}


	/**
	 * Finds all induced subgraphs.
	 * @param <S> a Node
	 * @param g The graph which induced subgraphs should be found.
	 * @return A collection of graphs representing the induced subgraphs.
	 */
	public static <S extends Node> Collection<Graph<S>> getInducedSubgraphs(Graph<S> g) {
		Collection<Graph<S>> resultingInducedSubgraphs = new HashSet<Graph<S>>();

		Set<Set<S>> subsetOfNodes = new SetTools<S>().subsets(g.getNodes());

		for(Set<S> singleSubset : subsetOfNodes) {
			Graph<S> singleInducedSubgraph = new DefaultGraph<S>();
			for(S singleNode : singleSubset)
				singleInducedSubgraph.add(singleNode);

			@SuppressWarnings("unchecked")
			Collection<Edge<S>> edges = (Collection<Edge<S>>) g.getEdges();

			// Leave only those edges where both nodeA and nodeB are contained in the induced graph
			Collection<Edge<S>> filteredEdges = edges.stream().filter(edge -> singleSubset.contains(edge.getNodeA()) && singleSubset.contains(edge.getNodeB())).collect(Collectors.toList());

			for(Edge<S> singleEdge : filteredEdges)
				singleInducedSubgraph.add(g.getEdge(singleEdge.getNodeA(), singleEdge.getNodeB()));

			resultingInducedSubgraphs.add(singleInducedSubgraph);
		}

		return resultingInducedSubgraphs;
	}

	/**
	 * checks whether the given graph is bipartite or not
	 * the algorithm starts at a random node and colors adjacent nodes in alternating colors
	 * if two adjacent nodes have the same color, the graph is no bipartite
	 * NOTE: This method only works if the given graph is connected
	 * @param g a graph
	 * @param <S> the type of nodes
	 * @return "true" if g is bipartite
	 */
	public static <S extends Node> boolean isBipartite(Graph<S> g) {
		Map<S, Integer> colors = new HashMap<>();

		// select a node to start
		S startNode = g.iterator().next();
		colors.put(startNode, 1);

		LinkedList<S> q = new LinkedList<>();
		q.add(startNode);

		// iterate the graph starting at node startNode
		while (q.size() != 0) {
			S node = q.poll();
			Integer color_node = colors.get(node);

			// if there is a self loop, then the graph is not bipartite
			if (g.areAdjacent(node, node)) {
				return false;
			}

			for (S neighbor: g.getNeighbors(node)) {
				Integer ret = colors.putIfAbsent(neighbor, 1 - color_node);
				// if neighbor had no color yet, set color and add to queue
				if (ret == null) {
					q.add(neighbor);
				}
				// if neighbor has the same color as node, the graph is not bipartite
				else if (ret.equals(color_node)){
					return false;
				}
			}
		}
		return true;

	}

	@Override
	public boolean add(GeneralEdge<T> edge) {
		if(edge instanceof  Edge<?>)
			return this.add((Edge<T>) edge);
		else
			return false;
	}



}

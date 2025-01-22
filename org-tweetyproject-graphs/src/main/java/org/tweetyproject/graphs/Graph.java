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

import java.util.Collection;
import java.util.Iterator;

import org.tweetyproject.math.matrix.Matrix;


/**
 * Common interface for graphs with
 * nodes of type T
 * 
 * @author Matthias Thimm
 * 
 * @param <T> The type of the node.
 */
public interface Graph<T extends Node> extends GeneralGraph<T>{

	/** When inverting a graph, ignore self loops (don't add and don't remove) */
	public static final int IGNORE_SELFLOOPS = 1;
	/** When inverting a graph, deal with self loops like ordinary edges (add if not present and remove if present) */
	public static final int INVERT_SELFLOOPS = 2;
	/** When inverting a graph, simple remove self loops, but don't add new ones. */
	public static final int REMOVE_SELFLOOPS = 3;
	
	/**
	 * Adds the given node to this graph.
	 * @param node some node.
	 * @return "true" iff the edge has been added successfully.
	 */
	public boolean add(T node);
	
	/**
	 * Adds the given edge to this graph. If at least one
	 * of the nodes the given edge connects is not in the
	 * graph, an illegal argument exception is thrown.
	 * @param edge some edge.
	 * @return "true" iff the edge has been added successfully.
	 */
	public boolean add(GeneralEdge<T> edge);
	
	/**
	 * Returns the nodes of this graph.
	 * @return the nodes of this graph.
	 */
	public Collection<T> getNodes();
	
	/**
	 * Returns the number of nodes in this graph.
	 * @return the number of nodes in this graph.
	 */
	public int getNumberOfNodes();
	
	/**
	 * Returns the number of edges in this graph.
	 * @return the number of edges in this graph.
	 */
	public int getNumberOfEdges();
	
	/**
	 * Returns "true" iff the two nodes are connected by a directed edge
	 * from a to b or an undirected edge.
	 * @param a some node
	 * @param b some node
	 * @return "true" iff the two nodes are connected by a directed edge
	 * from a to b or an undirected edge.
	 */
	public boolean areAdjacent(T a, T b);

	/**
	 * Returns the corresponding edge (a,b) if a and b are adjacent.
	 * Otherwise it returns null.
	 * @param a some node
	 * @param b some node
	 * @return the edge (a,b) or null.
	 */
	public GeneralEdge<T> getEdge(T a, T b);
	
	/**
	 * Returns the edges of this graph.
	 * @return the edges of this graph.
	 */
	public Collection<? extends GeneralEdge<? extends T>> getEdges();

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator();

	/** 
	 * Returns "true" when this graph contains the given
	 * node or edge.
	 * @param obj an object
	 * @return "true" if this graph contains the given
	 * node or edge.
	 */
	public boolean contains(Object obj);
	
	/**
	 * Returns the set of children (node connected via an undirected edge or a directed edge
	 * where the given node is the parent) of the given node.
	 * @param node some node (must be in the graph).
	 * @return the set of children of the given node.
	 */
	public Collection<T> getChildren(Node node);
	
	/**
	 * Returns the set of parents (node connected via an undirected edge or a directed edge
	 * where the given node is the child) of the given node.
	 * @param node some node (must be in the graph).
	 * @return the set of parents of the given node.
	 */
	public Collection<T> getParents(Node node);
	
	/**
	 * Checks whether there is a (directed) path from node1 to node2.
	 * @param node1 some node.
	 * @param node2 some node.
	 * @return "true" if there is a directed path from node1 to node2.
	 */
	public boolean existsDirectedPath(T node1, T node2);
	
	/**
	 * Returns the set of neighbors of the given node.
	 * @param node some node (must be in the graph).
	 * @return the set of neighbors of the given node.
	 */
	public Collection<T> getNeighbors(T node);
	
	/**
	 * Returns the adjacency matrix of this graph (the order
	 * of the nodes is the same as returned by "iterator()").
	 * @return the adjacency matrix of this graph
	 */
	public Matrix getAdjacencyMatrix();
	
	/**
	 * Returns the complement graph of this graph, i.e. the graph
	 * on the same set of vertices as this graph that connects two
	 * vertices v and w with an edge if and only if v and w are not
	 * connected in this graph.
	 * 
	 * @param selfloops Indicates how to deal with selfloops:<br>
	 * 	IGNORE_SELFLOOPS - ignore self loops (don't add and don't remove)<br> 
	 *  INVERT_SELFLOOPS - deal with self loops like ordinary edges (add if not present and remove if present)<br>	
	 *  REMOVE_SELFLOOPS - simple remove self loops, but don't add new ones.<br>	
	 *  
	 * @return the complement graph of this graph.
	 */
	public Graph<T> getComplementGraph(int selfloops);	
	
	/**
	 * Returns the set of (simple) connected components of this graph.
	 * A set of nodes is connected, if there is some path (ignoring edge
	 * directions) from each node to each other. It is a connected component
	 * if it is connected and maximal wrt. set inclusion. 
	 * @return the connected components of this graph.
	 */
	public Collection<Collection<T>> getConnectedComponents();
	
	/**
	 * Returns the strongly connected components of this graph. A set
	 * of nodes is strongly connected, if there is a path from each
	 * node to each other. A set of nodes is called strongly connected
	 * component if it is strongly connected and maximal with respect
	 * to set inclusion.
	 * @return the strongly connected components of this graph.
	 */
	public Collection<Collection<T>> getStronglyConnectedComponents();
		
	/**
	 * Returns the set of sub graphs of this graph.
	 * @return the set of sub graphs of this graph.
	 */
	public Collection<Graph<T>> getSubgraphs();
	


	/**
	 * Returns "true" iff the graph has a self loop (an edge from a node to itself).
	 * @return  "true" iff the graph has a self loop (an edge from a node to itself).
	 */
	public boolean hasSelfLoops();
	
	/**
	 * Checks whether this graph only contains weighted edges.
	 * @return "true" if all edges are weighted in this graph.
	 */
	public boolean isWeightedGraph();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString();
}

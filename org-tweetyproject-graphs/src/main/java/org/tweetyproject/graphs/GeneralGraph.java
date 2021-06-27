package org.tweetyproject.graphs;

import java.util.Collection;

public interface GeneralGraph<T extends Node> extends Iterable<T> {
	
	/**
	 * Returns copy of this graph consisting only of the given 
	 * nodes and all corresponding edges. 
	 * @param nodes a set of nodes
	 * @return a graph.
	 */
	public GeneralGraph<T> getRestriction(Collection<T> nodes);
	
	/**
	 * Returns the nodes of this graph.
	 * @return the nodes of this graph.
	 */
	public Collection<T> getNodes();
	
	
	/**
	 * Returns the edges of this graph.
	 * @return the edges of this graph.
	 */
	public Collection<? extends GeneralEdge<? extends T>> getEdges();
	

}

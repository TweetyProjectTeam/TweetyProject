package net.sf.tweety.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * 
 * @author Nils Geilen
 * a node in a Digraph
 * @param <T> type of the node's lable
 */
public class DigraphNode<T> {
	
	/** the node's label */
	private T value;
	/** nodes with edges from/to this node */
	private Collection<DigraphNode<T>> parents = new ArrayList<>(),
			children = new ArrayList<>();
	/** graph can contain several nodes with the same label if uniq == false */
	private boolean uniq;
	/** the graph this node is part of */
	private final Digraph<T> graph;
	
	DigraphNode(Digraph<T> graph,T value, boolean unique) {
		super();
		this.graph = graph;
		this.value = value;
		uniq = unique;
		parents = uniq ? new HashSet<>() : new ArrayList<>();
		children = uniq ? new HashSet<>() : new ArrayList<>();
	}
	
	/**
	 * 
	 * @return the number of edges pointing to this node
	 */
	public int inDegree() {
		return parents.size();
	}
	
	/**
	 * 
	 * @return the umber of edges pointing away from this node
	 */
	public int outDegree() {
		return children.size();
	}

	/**
	 * 
	 * @return the node's lable
	 */
	public T getValue() {
		return value;
	}

	/**
	 * a parent is a node n, that has an edge leading from n to this node
	 * @return all of this node's parents
	 */
	public Collection<DigraphNode<T>> getParents() {
		return parents;
	}
	
	/**
	 * a parent is a node n, that has an edge leading from n to this node
	 * @return one of this node's parents
	 */
	public DigraphNode<T> getParent() {
		return parents.iterator().next();
	}

	/**
	 * a child is a node n, that has an edge leading from this node to n 
	 * @return all of the nodes children
	 */
	public Collection<DigraphNode<T>> getChildren() {
		return children;
	}
	
	/**
	 * adds an edge from this node to another node
	 * @param to the node the adge points to
	 */
	public void addEdge(DigraphNode<T> to){
		if(this.graph != to.graph)
			throw new RuntimeException("DigraphNode: Tried to establish an edge to a node in another graph.");
		this.children.add(to);
		to.parents.add(this);
	}
	
	/**
	 * roots have an indegree of 0
	 * @return whether this node is the root of a tree
	 */
	public boolean isRoot(){
		return parents.isEmpty();
	}
	
	/**
	 * leafs have an outdegree of 0
	 * @return whether this node is a leaf of a tree
	 */
	public boolean isLeaf(){
		return children.isEmpty();
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if(!this.uniq)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		DigraphNode other = (DigraphNode) obj;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DigraphNode [value=" + value + "]";
	}

}

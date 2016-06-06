package net.sf.tweety.commons.util;

import java.util.ArrayList;
import java.util.Collection;

public class DigraphNode<T> {
	
	private T value;
	private Collection<DigraphNode<T>> parents = new ArrayList<>(),
			children = new ArrayList<>();
	
	DigraphNode(T value) {
		super();
		this.value = value;
	}
	
	public int inDegree() {
		return parents.size();
	}
	
	public int outDegree() {
		return children.size();
	}

	public T getValue() {
		return value;
	}

	public Iterable<DigraphNode<T>> getParents() {
		return parents;
	}

	public Iterable<DigraphNode<T>> getChildren() {
		return children;
	}
	
	public void addEdge(DigraphNode<T> to){
		this.children.add(to);
		to.parents.add(this);
	}

}

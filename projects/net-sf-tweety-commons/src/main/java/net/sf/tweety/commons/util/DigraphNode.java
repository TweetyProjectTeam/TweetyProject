package net.sf.tweety.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class DigraphNode<T> {
	
	private T value;
	private Collection<DigraphNode<T>> parents = new ArrayList<>(),
			children = new ArrayList<>();
	private boolean uniq;
	
	DigraphNode(T value, boolean unique) {
		super();
		this.value = value;
		uniq = unique;
		parents = uniq ? new HashSet<>() : new ArrayList<>();
		children = uniq ? new HashSet<>() : new ArrayList<>();
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
	
	public boolean isRoot(){
		return parents.isEmpty();
	}
	
	public boolean isLeaf(){
		return children.isEmpty();
	}
	
	public DigraphNode<T> getParent() {
		return parents.iterator().next();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DigraphNode other = (DigraphNode) obj;
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

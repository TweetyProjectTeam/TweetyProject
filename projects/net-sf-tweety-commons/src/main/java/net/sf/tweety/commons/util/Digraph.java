package net.sf.tweety.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Digraph<T> implements Iterable<DigraphNode<T>> {
	
	
	private Collection<DigraphNode<T>> nodes = new ArrayList<>();
	
	public DigraphNode<T> addNode(T val){
		DigraphNode<T> n = new DigraphNode<T>(val);
		nodes.add(n);
		return n;
	}
	
	public Collection<T> getValues() {
		Collection<T> result = new ArrayList<>();
		for(DigraphNode<T> n:nodes)
			result.add(n.getValue());
		return result;
	}

	@Override
	public Iterator<DigraphNode<T>> iterator() {
		return nodes.iterator();
	}


	


}

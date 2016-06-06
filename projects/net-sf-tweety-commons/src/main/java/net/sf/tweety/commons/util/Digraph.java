package net.sf.tweety.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Digraph<T> implements Iterable<DigraphNode<T>> {
	
	
	private Collection<DigraphNode<T>> nodes = new ArrayList<>();
	
	DigraphNode<T> addNode(T val){
		DigraphNode<T> n = new DigraphNode<T>(val);
		nodes.add(n);
		return n;
	}

	@Override
	public Iterator<DigraphNode<T>> iterator() {
		return nodes.iterator();
	}


	


}

package net.sf.tweety.commons.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Digraph<T> implements Iterable<DigraphNode<T>> {
	
	
	private Collection<DigraphNode<T>> nodes;
	boolean uniq;
	
	
	public Digraph(boolean unique) {
		uniq=unique;
		nodes = uniq ? new HashSet<>() : new ArrayList<>();
	}
	
	public DigraphNode<T> addNode(T val){
		if(uniq) {
			for(DigraphNode<T> n:nodes)
				if(n.getValue().equals(val))
					return n;
		}
		DigraphNode<T> n = new DigraphNode<T>(val, uniq);
		nodes.add(n);
		return n;
	}
	
	public int size(){
		return nodes.size();
	}
	
	public int numberOfEdges() {
		int sum =0;
		for(DigraphNode<T> n:this)
			sum+=n.outDegree();
		return sum;
	}
	
	public Collection<DigraphNode<T>> getLeafs() {
		Collection<DigraphNode<T>> result = new ArrayList<>();
		for(DigraphNode<T> n:nodes)
			if(n.isLeaf())
				result.add(n);
		return result;
	}
	
	public Collection<T> getValues() {
		Collection<T> result = new ArrayList<>();
		for(DigraphNode<T> n:nodes)
			result.add(n.getValue());
		return result;
	}
	
	public void printTrees(OutputStream os) {
		for(DigraphNode<T> n:nodes)
			if(n.isRoot())
				printTree(os, n);
	}
	
	public void printTree(OutputStream os, DigraphNode<T> root) {
		PrintStream ps = new PrintStream(os);
		printNode(ps, root, 0);
	}
	
	private void printNode(PrintStream os, DigraphNode<T> node, int tab) {
		for(int i=0; i<tab;i++)
			os.print("\t");
		os.println(node.getValue());
		for(DigraphNode<T> child:node.getChildren())
			printNode(os, child, tab+1);
	}

	@Override
	public Iterator<DigraphNode<T>> iterator() {
		return nodes.iterator();
	}


	


}

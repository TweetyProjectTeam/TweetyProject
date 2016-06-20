package net.sf.tweety.commons.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * @author Nils Geilen
 * An representation of directed, unweighted Graphs
 * @param <T> type of the nodes' labels
 */

public class Digraph<T> implements Iterable<DigraphNode<T>> {
	
	/** the grsph's nodes */
	private Collection<DigraphNode<T>> nodes;
	
	/** if uniq is true there can only be one single node labelled with the same instance of T */
	private final boolean uniq;
	
	public Digraph() {
		uniq = true;
	}
	
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
		DigraphNode<T> n = new DigraphNode<T>(this,val, uniq);
		nodes.add(n);
		return n;
	}
	
	/**
	 * 
	 * @return the number of nodes in this graph
	 */
	public int numberOfNodes(){
		return nodes.size();
	}
	
	
	/**
	 * 
	 * @return the number of Edges in the graph
	 */
	public int numberOfEdges() {
		int sum =0;
		for(DigraphNode<T> n:this)
			sum+=n.outDegree();
		return sum;
	}
	
	/**
	 * leaves are nodes with an outdegree of 0
	 * @return list of all leaves
	 */
	public Collection<DigraphNode<T>> getLeafs() {
		Collection<DigraphNode<T>> result = new ArrayList<>();
		for(DigraphNode<T> n:nodes)
			if(n.isLeaf())
				result.add(n);
		return result;
	}
	
	/**
	 * roots are nodes with an indegree of 0
	 * @return list of all roots
	 */
	public Collection<DigraphNode<T>> getRoots() {
		Collection<DigraphNode<T>> result = new ArrayList<>();
		for(DigraphNode<T> n:nodes)
			if(n.isRoot())
				result.add(n);
		return result;
	}
	
	/**
	 * 
	 * @return a list of all nodes' values
	 */
	public Collection<T> getValues() {
		Collection<T> result = new ArrayList<>();
		for(DigraphNode<T> n:nodes)
			result.add(n.getValue());
		return result;
	}
	
	/**
	 * prints a tree for every root node in the graph
	 * @param os
	 */
	public void printTrees(OutputStream os) {
		for(DigraphNode<T> n:getRoots())
				printTree(os, n);
	}
	
	/**
	 * prints a tree
	 * @param os
	 * @param root the root of the tree
	 */
	public void printTree(OutputStream os, DigraphNode<T> root) {
		PrintStream ps = new PrintStream(os);
		printNode(ps, root, 0);
	}
	
	/**
	 * prints a node
	 * @param os
	 * @param node to be printed
	 * @param tab indention level
	 */
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

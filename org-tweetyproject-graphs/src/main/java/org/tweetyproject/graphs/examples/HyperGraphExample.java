package org.tweetyproject.graphs.examples;

import java.util.HashSet;

import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.DirectedEdge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.HyperDirEdge;
import org.tweetyproject.graphs.HyperGraph;
import org.tweetyproject.graphs.SimpleNode;
import org.tweetyproject.graphs.util.GraphUtil;
import org.tweetyproject.math.ComplexNumber;

public class HyperGraphExample {
	public void run(){

	}
	

	
	public static void main(String[] args){
		Graph<SimpleNode> g = new HyperGraph<SimpleNode>();
		SimpleNode[] nodes = new SimpleNode[11];
		nodes[0] = new SimpleNode("A");
		nodes[1] = new SimpleNode("B");
		nodes[2] = new SimpleNode("C");
		nodes[3] = new SimpleNode("D");
		nodes[4] = new SimpleNode("E");
		nodes[5] = new SimpleNode("F");
		nodes[6] = new SimpleNode("G");
		nodes[7] = new SimpleNode("H");
		nodes[8] = new SimpleNode("I");
		nodes[9] = new SimpleNode("J");
		nodes[10] = new SimpleNode("K");
		for(SimpleNode n: nodes)
			g.add(n);
		HashSet<SimpleNode> a1 = new HashSet<SimpleNode>(); 
		a1.add(nodes[1]);
		a1.add(nodes[2]);
		a1.add(nodes[3]);
		HashSet<SimpleNode> a2 = new HashSet<SimpleNode>(); 
		a2.add(nodes[1]);
		a2.add(nodes[2]);
		a2.add(nodes[4]);
		HashSet<SimpleNode> a3 = new HashSet<SimpleNode>(); 
		a3.add(nodes[6]);
		a3.add(nodes[7]);
		a3.add(nodes[3]);
		g.add(new HyperDirEdge(a1, nodes[0]));
		g.add(new HyperDirEdge(a2, nodes[10]));
		g.add(new HyperDirEdge(a3, nodes[4]));
		
		Graph<SimpleNode> tmp = g.getComplementGraph(0);
		//System.out.println(g.toString());
		System.out.println(tmp.toString());
	}	
}

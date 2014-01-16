package net.sf.tweety.graphs.test;

import net.sf.tweety.graphs.DefaultGraph;
import net.sf.tweety.graphs.DirectedEdge;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.Node;
import net.sf.tweety.graphs.util.GraphUtil;
import net.sf.tweety.math.ComplexNumber;

public class GraphTest {
	
	public class SimpleNode implements Node{
		private String name;
		public SimpleNode(String s){
			this.name = s;
		}
		public String toString(){
			return this.name;
		}
		
	}
	
	public void run(){
		Graph<SimpleNode> g = new DefaultGraph<SimpleNode>();
		SimpleNode[] nodes = new SimpleNode[11];
		nodes[0] = new GraphTest.SimpleNode("A");
		nodes[1] = new GraphTest.SimpleNode("B");
		nodes[2] = new GraphTest.SimpleNode("C");
		nodes[3] = new GraphTest.SimpleNode("D");
		nodes[4] = new GraphTest.SimpleNode("E");
		nodes[5] = new GraphTest.SimpleNode("F");
		nodes[6] = new GraphTest.SimpleNode("G");
		nodes[7] = new GraphTest.SimpleNode("H");
		nodes[8] = new GraphTest.SimpleNode("I");
		nodes[9] = new GraphTest.SimpleNode("J");
		nodes[10] = new GraphTest.SimpleNode("K");
		for(SimpleNode n: nodes)
			g.add(n);
		g.add(new DirectedEdge<SimpleNode>(nodes[1],nodes[2]));
		g.add(new DirectedEdge<SimpleNode>(nodes[2],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[3],nodes[0]));
		g.add(new DirectedEdge<SimpleNode>(nodes[3],nodes[1]));		
		g.add(new DirectedEdge<SimpleNode>(nodes[4],nodes[3]));
		g.add(new DirectedEdge<SimpleNode>(nodes[4],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[4],nodes[5]));		
		g.add(new DirectedEdge<SimpleNode>(nodes[5],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[5],nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[6],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[6],nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[7],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[7],nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[8],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[8],nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[9],nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[10],nodes[4]));
		
		for(SimpleNode n: nodes){
			System.out.println(n + "\t" + GraphUtil.pageRank(g, n, 0.85, 0.00001));
		}
	}
	
	public void run2(){
		Graph<SimpleNode> g = new DefaultGraph<SimpleNode>();
		SimpleNode[] nodes = new SimpleNode[5];
		nodes[0] = new GraphTest.SimpleNode("A");
		nodes[1] = new GraphTest.SimpleNode("B");
		nodes[2] = new GraphTest.SimpleNode("C");
		nodes[3] = new GraphTest.SimpleNode("D");
		nodes[4] = new GraphTest.SimpleNode("E");
		for(SimpleNode n: nodes)
			g.add(n);
		g.add(new DirectedEdge<SimpleNode>(nodes[0],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[1],nodes[2]));
		g.add(new DirectedEdge<SimpleNode>(nodes[2],nodes[3]));
		//g.add(new DirectedEdge<SimpleNode>(nodes[3],nodes[0]));
		//g.add(new DirectedEdge<SimpleNode>(nodes[4],nodes[4]));
		
//		g.add(new DirectedEdge<SimpleNode>(nodes[1],nodes[0]));
//		g.add(new DirectedEdge<SimpleNode>(nodes[1],nodes[2]));
//		g.add(new DirectedEdge<SimpleNode>(nodes[2],nodes[3]));
//		g.add(new DirectedEdge<SimpleNode>(nodes[3],nodes[4]));
//		g.add(new DirectedEdge<SimpleNode>(nodes[4],nodes[3]));
//		g.add(new DirectedEdge<SimpleNode>(nodes[4],nodes[2]));
		
		System.out.println(g);
		System.out.println();
		for(ComplexNumber d: GraphUtil.eigenvalues(g))
			System.out.println(d);
	}
	
	public static void main(String[] args){
		new GraphTest().run2();		
	}	
}

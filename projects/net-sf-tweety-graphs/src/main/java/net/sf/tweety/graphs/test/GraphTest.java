/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.graphs.test;

import net.sf.tweety.graphs.DefaultGraph;
import net.sf.tweety.graphs.DirectedEdge;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.SimpleNode;
import net.sf.tweety.graphs.util.GraphUtil;
import net.sf.tweety.math.ComplexNumber;

public class GraphTest {
	
	public void run(){
		Graph<SimpleNode> g = new DefaultGraph<SimpleNode>();
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
		nodes[0] = new SimpleNode("A");
		nodes[1] = new SimpleNode("B");
		nodes[2] = new SimpleNode("C");
		nodes[3] = new SimpleNode("D");
		nodes[4] = new SimpleNode("E");
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
		new GraphTest().run();		
	}	
}

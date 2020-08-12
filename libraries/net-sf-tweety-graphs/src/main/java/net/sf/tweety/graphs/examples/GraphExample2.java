/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.graphs.examples;

import net.sf.tweety.graphs.DefaultGraph;
import net.sf.tweety.graphs.DirectedEdge;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.SimpleNode;
import net.sf.tweety.graphs.util.GraphUtil;

/**
 * Another example that shows how to construct graphs programmatically.
 */
public class GraphExample2 {
	public static void main(String[] args) {
		Graph<SimpleNode> g = new DefaultGraph<SimpleNode>();
		SimpleNode[] nodes = new SimpleNode[6];
		nodes[0] = new SimpleNode("A");
		nodes[1] = new SimpleNode("B");
		nodes[2] = new SimpleNode("C");
		nodes[3] = new SimpleNode("D");		
		nodes[4] = new SimpleNode("E");
		nodes[5] = new SimpleNode("F");
		for(SimpleNode n: nodes)
			g.add(n);
		g.add(new DirectedEdge<SimpleNode>(nodes[0],nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[1],nodes[2]));
		g.add(new DirectedEdge<SimpleNode>(nodes[2],nodes[3]));
		g.add(new DirectedEdge<SimpleNode>(nodes[3],nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[4],nodes[5]));
		g.add(new DirectedEdge<SimpleNode>(nodes[5],nodes[1]));
		
		System.out.println(GraphUtil.betweennessCentralityNormalised(g));
	}
}

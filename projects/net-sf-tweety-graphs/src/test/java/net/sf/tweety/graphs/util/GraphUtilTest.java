/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.graphs.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sf.tweety.graphs.DefaultGraph;
import net.sf.tweety.graphs.DirectedEdge;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.SimpleNode;

public class GraphUtilTest {
	
	@Test
	public void testEnumerationChordlessCircuits1() {
		Graph<SimpleNode> g = new DefaultGraph<SimpleNode>();
		SimpleNode[] nodes = new SimpleNode[10];
		for(int i = 0; i < 10; i++){
			nodes[i] = new SimpleNode("a"+i);
			g.add(nodes[i]);
		}
		g.add(new DirectedEdge<SimpleNode>(nodes[0], nodes[1]));
		g.add(new DirectedEdge<SimpleNode>(nodes[1], nodes[2]));
		g.add(new DirectedEdge<SimpleNode>(nodes[2], nodes[0]));
		g.add(new DirectedEdge<SimpleNode>(nodes[0], nodes[3]));
		g.add(new DirectedEdge<SimpleNode>(nodes[3], nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[4], nodes[2]));
		g.add(new DirectedEdge<SimpleNode>(nodes[4], nodes[5]));
		g.add(new DirectedEdge<SimpleNode>(nodes[5], nodes[6]));
		g.add(new DirectedEdge<SimpleNode>(nodes[6], nodes[7]));
		g.add(new DirectedEdge<SimpleNode>(nodes[7], nodes[4]));
		g.add(new DirectedEdge<SimpleNode>(nodes[2], nodes[8]));
		g.add(new DirectedEdge<SimpleNode>(nodes[2], nodes[7]));
		g.add(new DirectedEdge<SimpleNode>(nodes[9], nodes[9]));
		
		System.out.println(GraphUtil.enumerateChordlessCircuits(g));
		assertEquals(GraphUtil.enumerateChordlessCircuits(g).size(),4);
		
	}
}

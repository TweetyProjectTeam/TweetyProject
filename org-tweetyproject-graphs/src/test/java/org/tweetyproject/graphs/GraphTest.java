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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.graphs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GraphTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getAncestorsOfNode() {
        var graph = new SimpleGraph<>();
        var nodeA = new SimpleNode("a");
        var nodeB = new SimpleNode("b");
        var nodeC = new SimpleNode("c");
        graph.addAll(List.of(nodeA, nodeB, nodeC));
        graph.addAllEdges(List.of(
                new DirectedEdge<>(nodeA, nodeB),
                new DirectedEdge<>(nodeB, nodeC))
        );

        var ancestors = graph.getAncestors(nodeC);

        assertEquals(Set.of(nodeA, nodeB), ancestors);
    }

    @Test
    public void getAncestorsOfNodeWithCycle() {
        var graph = new SimpleGraph<>();
        var nodeA = new SimpleNode("a");
        var nodeB = new SimpleNode("b");
        var nodeC = new SimpleNode("c");
        graph.addAll(List.of(nodeA, nodeB, nodeC));
        graph.addAllEdges(List.of(
                new DirectedEdge<>(nodeA, nodeB),
                new DirectedEdge<>(nodeB, nodeC),
                new DirectedEdge<>(nodeC, nodeA))
        );

        var ancestors = graph.getAncestors(nodeC);

        assertEquals(Set.of(nodeA, nodeB, nodeC), ancestors);
    }

    @Test
    public void getAncestorsOfNodeThrowWithNodeNotInGraph() {
        var graph = new SimpleGraph<>();
        SimpleNode node = new SimpleNode("aNode");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The node is not in this graph.");
        graph.getAncestors(node);
    }

    @Test
    public void getAncestorsOfNodesInSeparateStronglyConnectedComponents() {
        var graph = new SimpleGraph<>();
        var nodeA = new SimpleNode("a");
        var nodeB = new SimpleNode("b");
        var nodeC = new SimpleNode("c");
        graph.addAll(List.of(nodeA, nodeB, nodeC));
        graph.addAllEdges(List.of(
                new DirectedEdge<>(nodeA, nodeB),
                new DirectedEdge<>(nodeB, nodeC))
        );
        var nodeL = new SimpleNode("l");
        var nodeM = new SimpleNode("m");
        var nodeN = new SimpleNode("n");
        graph.addAll(List.of(nodeL, nodeM, nodeN));
        graph.addAllEdges(List.of(
                new DirectedEdge<>(nodeL, nodeM),
                new DirectedEdge<>(nodeM, nodeN))
        );

        var ancestors = graph.getAncestors(List.of(nodeC, nodeN));

        assertEquals(Set.of(nodeA, nodeB, nodeL, nodeM), ancestors);
    }

    @Test
    public void getAncestorsOfNodesInSameStronglyConnectedComponents() {
        var graph = new SimpleGraph<>();
        var nodeA = new SimpleNode("a");
        var nodeB = new SimpleNode("b");
        var nodeC = new SimpleNode("c");
        var nodeD = new SimpleNode("d");
        var nodeE = new SimpleNode("e");
        var nodeF = new SimpleNode("f");
        graph.addAll(List.of(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF));
        graph.addAllEdges(List.of(
                new DirectedEdge<>(nodeA, nodeB),
                new DirectedEdge<>(nodeB, nodeC),
                new DirectedEdge<>(nodeB, nodeD),
                new DirectedEdge<>(nodeB, nodeD),
                new DirectedEdge<>(nodeD, nodeE),
                new DirectedEdge<>(nodeE, nodeF)
        ));

        var ancestors = graph.getAncestors(List.of(nodeB, nodeE));

        assertEquals(3, ancestors.size());
        assertEquals(Set.of(nodeA, nodeB, nodeD), ancestors);
    }
}
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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.graphs.examples;

import java.util.HashSet;

import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.HyperDirEdge;
import org.tweetyproject.graphs.HyperGraph;
import org.tweetyproject.graphs.SimpleNode;
/**
 * This class provides an example of creating and working with a hypergraph.
 * A hypergraph is a generalized graph where edges, called hyperedges, can connect
 * more than two vertices (nodes). This example demonstrates adding nodes and hyperedges
 * to the hypergraph, and generating its complement graph.
 *
 * @author Sebastian Franke
 */
public class HyperGraphExample {

    /**
     * The main method that runs the hypergraph example.
     * It creates a hypergraph, adds nodes and directed hyperedges to it,
     * and generates the complement of the hypergraph. The result of the complement graph
     * is printed to the console.
     *
     * @param args Command-line arguments (not used in this example).
     */
    public static void main(String[] args) {
        // Create a new hypergraph with nodes of type SimpleNode
        Graph<SimpleNode> g = new HyperGraph<SimpleNode>();

        // Initialize an array of SimpleNode objects
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

        // Add nodes to the hypergraph
        for (SimpleNode n : nodes)
            g.add(n);

        // Create sets of nodes to form hyperedges
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

        // Add directed hyperedges to the hypergraph
        g.add(new HyperDirEdge<SimpleNode>(a1, nodes[0]));
        g.add(new HyperDirEdge<SimpleNode>(a2, nodes[10]));
        g.add(new HyperDirEdge<SimpleNode>(a3, nodes[4]));

        // Generate and print the complement of the hypergraph
        Graph<SimpleNode> tmp = g.getComplementGraph(0);
        System.out.println(tmp.toString());
    }
}


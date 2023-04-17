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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.graphs;

import java.rmi.NoSuchObjectException;
import java.util.*;

/**
 * An extended version of the DefaultGraph which supports removing of nodes as well as
 * some utility functions for d-separation
 *
 * @param <T> the type of the node
 *
 * @author Lars Bengel
 */
public class SimpleGraph<T extends Node> extends DefaultGraph<T> implements Graph<T> {

    /**
     * create a copy of the given graph
     * @param graph some graph
     */
    public SimpleGraph(Graph<T> graph) {
        super();
        this.addAll(new HashSet<>(graph.getNodes()));
        this.addAllEdges(new HashSet<>(((DefaultGraph<T>) graph).getEdges()));
    }

    /**
     * create a new SimpleGraph instance
     */
    public SimpleGraph() {
        super();
    }

    
    /**
     * add all nodes to the graph
     * @param c a collection of nodes
     * @return true iff all nodes are added
     */
    public boolean addAll(Collection<? extends T> c) {
        boolean result = true;
        for(T t: c){
            boolean sub = this.add(t);
            result = result && sub;
        }
        return result;
    }

    /**
     * add all edges to the graph
     * @param c a collection of edges
     * @return true iff all edges are added
     */
    public boolean addAllEdges(Collection<Edge<T>> c) {
        boolean result = false;
        for(Edge<T> e: c)
            result |= this.add(e);
        return result;
    }

    /**
     * remove the given node from the graph
     * @param node some node
     * @return true iff the structure has been changed
     */
    public boolean remove(T node) {
        boolean result = true;
        for (Edge<T> edge: new HashSet<>(this.getEdges())) {
            if (node == edge.getNodeA() || node == edge.getNodeB())
            result &= this.remove(edge);
        }
        result &= this.nodes.remove(node);
        return result;
    }

    /**
     * remove the given edge from the graph
     * @param edge some edge
     * @return true iff the structure has changed
     */
    public boolean remove(Edge<T> edge) {
        return this.edges.remove(edge);
    }

    /**
     * Compute whether nodesA and nodesB are disconnected and thus d-separated in the  graph
     * @param nodesA a set of arguments
     * @param nodesB a set of arguments
     * @return true iff argsA and argsB are disconnected in the graph
     */
    public boolean areDisconnected(Collection<? extends T> nodesA, Collection<? extends T> nodesB) {
        for (T a: nodesA) {
            for (T b: nodesB) {
                // if there is a path from any node in nodesA to any node in nodesB, the sets are connected
                // since we assume all edges to be undirected, this method works just fine
                if (this.existsDirectedPath(a, b)) {
                    return false;
                }
            }
        }
        // no connection found
        return true;
    }

    /**
     * convert the graph into a undirected graph
     * @return the undirected graph
     */
    public SimpleGraph<T> toUndirectedGraph() {
        SimpleGraph<T> undirectedGraph = new SimpleGraph<>();
        undirectedGraph.addAll(this.getNodes());
        // replace all edges with undirected edges
        for (Edge<T> edge: this.getEdges()) {
            undirectedGraph.add(new UndirectedEdge<>(edge.getNodeA(), edge.getNodeB()));
        }

        return undirectedGraph;
    }
    
    /**
	 *  Adds a graph as a subgraph
	 * 
	 * @param superExit Node of the this graph, under which the new graph will be anchored
	 * @param subGraph Graph, which will be added to the super-graph
	 * @param subEntry Node of the subgraph, which will be connected to the super-graph
	 * @param label Label of the newly created edge, from the superExit node to the subRoot node
     * @throws NoSuchObjectException Thrown if superExit is not a node of this graph
	 */
    public boolean addSubGraph(T superExit,
			SimpleGraph<T> subGraph, T subEntry, String label ) throws NoSuchObjectException {
    	if(!this.getNodes().contains(superExit)) throw new NoSuchObjectException(superExit.toString() + " is not a node of this graph");
		this.addAll(subGraph.getNodes());
		this.addAllEdges( (Collection<Edge<T>>) subGraph.getEdges());
		return this.add(new DirectedEdge<T>(superExit,subEntry,label));
	}
}

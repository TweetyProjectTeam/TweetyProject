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

package org.tweetyproject.graphs;

import java.util.Collection;
/**
 * The `GeneralGraph` interface represents a general structure for graphs where each graph
 * consists of nodes and edges. This interface provides methods for retrieving nodes,
 * edges, and subgraphs (restrictions of the graph).
 *
 * <p>
 * The `GeneralGraph` interface is parameterized with the type `T`, which extends `Node`.
 * This allows for flexibility in defining different types of graphs with specific
 * types of nodes.
 * </p>
 *
 * @param <T> the type of nodes in the graph, which extends the `Node` class.
 *
 * @author Sebastian Franke
 */
public interface GeneralGraph<T extends Node> extends Iterable<T> {

    /**
     * Returns a copy of this graph that contains only the specified nodes
     * and all corresponding edges between them.
     *
     * <p>
     * This method generates a subgraph (or restricted graph) from the current graph
     * by including only the given nodes and the edges that connect them. The returned
     * graph is a new instance and does not modify the original graph.
     * </p>
     *
     * @param nodes a collection of nodes to be included in the restricted graph.
     * @return a `GeneralGraph` object representing the restricted graph.
     */
    public GeneralGraph<T> getRestriction(Collection<T> nodes);

    /**
     * Returns the nodes of this graph.
     *
     * <p>
     * This method retrieves all nodes that are part of the current graph.
     * </p>
     *
     * @return a collection of nodes in the graph.
     */
    public Collection<T> getNodes();

    /**
     * Returns the edges of this graph.
     *
     * <p>
     * This method retrieves all edges that are part of the current graph.
     * The edges returned are of the type `GeneralEdge`, which is parameterized
     * by the type of nodes in the graph.
     * </p>
     *
     * @return a collection of edges in the graph.
     */
    public Collection<? extends GeneralEdge<? extends T>> getEdges();
}

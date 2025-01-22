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

/**
 * The `WeightedDirectedEdge` class represents a directed edge between two nodes in a graph,
 * with an associated weight.
 *
 * <p>
 * This class extends the `DirectedEdge` class and implements the `WeightedEdge` interface,
 * providing support for weighted directed edges. The weight of the edge is of type `T`,
 * which is constrained to be a subtype of `Number`, allowing for numeric weights such as
 * integers, floats, or doubles.
 * </p>
 *
 * @param <S> the type of nodes connected by this edge, which must extend `Node`.
 * @param <T> the type of the weight, which must extend `Number`.
 *
 * @author Lars Bengel, Sebastian Franke
 */
public class WeightedDirectedEdge<S extends Node, T extends Number> extends DirectedEdge<S> implements WeightedEdge<S, T> {

    /** The weight of this edge. */
    private T weight;

    /**
     * Creates a new weighted directed edge connecting two nodes with a given weight.
     *
     * @param nodeA the starting node of the edge.
     * @param nodeB the ending node of the edge.
     * @param weight the weight of the edge.
     */
    public WeightedDirectedEdge(S nodeA, S nodeB, T weight) {
        super(nodeA, nodeB);
        this.weight = weight;
    }

    /**
     * Returns the weight of this edge.
     *
     * @return the weight of this edge.
     */
    @Override
    public T getWeight() {
        return this.weight;
    }

    /**
     * Sets the weight of this edge to the specified value.
     *
     * @param number the new weight of the edge.
     */
    public void setWeight(T number) {
        this.weight = number;
    }
}


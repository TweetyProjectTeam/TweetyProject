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

 /**
  * The `GeneralEdge` class represents a general structure for edges in a graph.
  *
  * <p>
  * This abstract class is parameterized with the type `T`, which represents the type of nodes
  * connected by this edge. Subclasses of `GeneralEdge` should provide implementations
  * that define specific types of edges in a graph, such as directed or undirected edges.
  * </p>
  *
  * <p>
  * Since this class is abstract, it cannot be instantiated directly. It is intended to be
  * extended by more specific edge types, which will define additional properties and
  * behaviors of edges in a graph.
  * </p>
  *
  * @param <T> the type of nodes connected by this edge.
  *
  * @author Sebastian Franke
  */
 public abstract class GeneralEdge<T> {
    /** Default Constructor */
    public GeneralEdge(){
        //default
    }

 }


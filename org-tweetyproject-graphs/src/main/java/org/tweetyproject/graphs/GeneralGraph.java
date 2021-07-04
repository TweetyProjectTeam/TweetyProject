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
 * @author Sebastian Franke
 */

public interface GeneralGraph<T extends Node> extends Iterable<T> {
	
	/**
	 * Returns copy of this graph consisting only of the given 
	 * nodes and all corresponding edges. 
	 * @param nodes a set of nodes
	 * @return a graph.
	 */
	public GeneralGraph<T> getRestriction(Collection<T> nodes);
	
	/**
	 * Returns the nodes of this graph.
	 * @return the nodes of this graph.
	 */
	public Collection<T> getNodes();
	
	
	/**
	 * Returns the edges of this graph.
	 * @return the edges of this graph.
	 */
	public Collection<? extends GeneralEdge<? extends T>> getEdges();
	

}

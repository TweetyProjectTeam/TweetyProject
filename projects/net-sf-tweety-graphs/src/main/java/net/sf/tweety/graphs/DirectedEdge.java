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
package net.sf.tweety.graphs;

/**
 * Instances of this class represent directed edges.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of the nodes this edge connects
 */
public class DirectedEdge<T extends Node> extends Edge<T> {

	/** Creates a new directed edge for the given nodes.
	 * @param nodeA some node.
	 * @param nodeB some node.
	 */
	public DirectedEdge(T nodeA, T nodeB) {
		super(nodeA, nodeB);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "(" + this.getNodeA() + "," + this.getNodeB() + ")"; 
	}
}

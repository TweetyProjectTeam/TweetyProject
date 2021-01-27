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
package org.tweetyproject.logics.bpm.syntax;

import org.tweetyproject.graphs.Edge;

/**
 * A class to represent Edges in a BPMN Model
 * @author Benedikt Knopp
 */
public class BpmnEdge extends Edge<BpmnNode> {

	/**
	 * (Optional) the label of the edge
	 */
	private String label;
	
	/**
	 * the unique identifier of the edge
	 */
	private String id;
	
	/**
	 * Create a new Edge
	 * @param nodeA a node
	 * @param nodeB a node
	 */
	public BpmnEdge(BpmnNode nodeA, BpmnNode nodeB) {
		super(nodeA, nodeB);
	}
	
	/**
	 * Create a new labelled adge
	 * @param nodeA a node
	 * @param nodeB a node
	 * @param label the label
	 */
	public BpmnEdge(BpmnNode nodeA, BpmnNode nodeB, String label) {
		super(nodeA, nodeB, label);
	}
	
	/**
	 * retrieve the ID of this edge
	 * @return the unique element identifier
	 */
	public String getId() {
		return id;
	};
	
	/**
	 * set the ID of this edge
	 * @param id a unique element identifier
	 */
	public void setId(String id) {
		this.id = id;
	}

}

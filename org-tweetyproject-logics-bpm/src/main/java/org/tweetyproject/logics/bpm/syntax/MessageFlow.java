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

import org.tweetyproject.graphs.DirectedEdge;

/**
 * A class to represent edges of the message flow in a BPMN Model
 * @author Benedikt Knopp
 */
public class MessageFlow extends DirectedEdge<BpmnNode>{

	/**
	 * Create a new edge
	 * @param nodeA a node
	 * @param nodeB a node
	 */
	public MessageFlow(BpmnNode nodeA, BpmnNode nodeB) {
		super(nodeA, nodeB);
	}
	
	/**
	 * Create a new labeled edge
	 * @param nodeA a node
	 * @param nodeB a node
	 * @param label the label
	 */
	public MessageFlow(BpmnNode nodeA, BpmnNode nodeB, String label) {
		super(nodeA, nodeB, label);
	}

}

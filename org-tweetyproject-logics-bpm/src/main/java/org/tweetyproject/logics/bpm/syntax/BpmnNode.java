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

import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.Node;

/**
 * A class to represent all kinds of nodes in a BPMN Model
 * @author Benedikt Knopp
 */
public class BpmnNode extends BpmnElement implements Node{

	/**
	 * all directed edges that lead to this node
	 */
	private Map<String, Edge<BpmnNode>> incomingEdges = new HashMap<>();

	/**
	 * all directed edges that lead away from this node
	 */
	private Map<String, Edge<BpmnNode>> outgoingEdges = new HashMap<>();

	/**
	 * Create a new instance
	 */
	public BpmnNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Add a new incoming edge
	 * @param edgeId the id of the incoming edge
	 * @param edge the edge
	 */
	public void putIncomingEdge(String edgeId, Edge<BpmnNode> edge) {
		this.incomingEdges.put(edgeId, edge);
	}

	/**
	 * Add a new outgoing edge
	 * @param edgeId the id of the outgoing edge
	 * @param edge the edge
	 */
	public void putOutgoingEdge(String edgeId, Edge<BpmnNode> edge) {
		this.outgoingEdges.put(edgeId, edge);
	}

	/**
	 * Return all directed edges that lead to this node
	 * @return all directed edges that lead to this node
	 */
	public Map<String, Edge<BpmnNode>> getIncomingEdges(){
		return incomingEdges;
	}

	/**
	 * Return all directed edges leading away from this node
	 * @return all directed edges leading away from this node
	 */
	public Map<String, Edge<BpmnNode>> getOutgoingEdges(){
		return outgoingEdges;
	}

}

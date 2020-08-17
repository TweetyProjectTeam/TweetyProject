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
package net.sf.tweety.logics.bpm.syntax;

import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.graphs.Edge;
import net.sf.tweety.graphs.Node;

/**
 * @author Benedikt Knopp
 */
public class BpmnNode extends BpmnElement implements Node{

	private Map<String, Edge<BpmnNode>> incomingEdges = new HashMap<>();
	private Map<String, Edge<BpmnNode>> outgoingEdges = new HashMap<>();
	
	public BpmnNode() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void putIncomingEdge(String edgeId, Edge<BpmnNode> edge) {
		this.incomingEdges.put(edgeId, edge);
	}
	
	public void putOutgoingEdge(String edgeId, Edge<BpmnNode> edge) {
		this.outgoingEdges.put(edgeId, edge);
	}
	
	public Map<String, Edge<BpmnNode>> getIncomingEdges(){
		return incomingEdges;
	}
	
	public Map<String, Edge<BpmnNode>> getOutgoingEdges(){
		return outgoingEdges;
	}

}

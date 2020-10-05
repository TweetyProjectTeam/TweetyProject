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
package net.sf.tweety.logics.bpm.parser;

/**
 * This is a helper class for parsing edges in a BPMN Model
 * @author Benedikt Knopp
 */
public class BufferedBpmnEdge {
	
	/**
	 * unique element identifier
	 */
	
	private String id;
	/**
	 * (optional) label of the edge 
	 */
	
	private String name;
	
	/**
	 * the id of the source node
	 */
	private String sourceRef;
	
	/**
	 * the id of the target node
	 */
	private String targetRef;
	
	/**
	 * one of "sequence", "message"
	 */
	private String flowType;
	
	
	/**
	 * create a new instance
	 */
	public BufferedBpmnEdge() {
	}

	/**
	 * @return the unique identifier
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the (optional) edge label
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the id of the source node
	 */
	public String getSourceRef() {
		return sourceRef;
	}
	
	/**
	 * @return the id of the target node
	 */
	public String getTargetRef() {
		return targetRef;
	}
	
	/**
	 * @return the flow type
	 */
	public String getFlowType() {
		return flowType;
	}
	
	/**
	 * @param id the unique identifier
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @param name the (optional) edge label
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param sourceRef the id of the source node
	 */
	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}
	
	/**
	 * @param targetRef the id of the target node
	 */
	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}

	/**
	 * @param flowType one of "sequence", "message"
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}
	
}

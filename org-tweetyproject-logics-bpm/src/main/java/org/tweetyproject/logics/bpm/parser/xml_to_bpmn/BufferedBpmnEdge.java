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
package org.tweetyproject.logics.bpm.parser.xml_to_bpmn;

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
	 * Return the unique identifier
	 * @return the unique identifier
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return the (optional) edge label
	 * @return the (optional) edge label
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the id of the source node
	 * @return the id of the source node
	 */
	public String getSourceRef() {
		return sourceRef;
	}

	/**
	 * Return the id of the target node
	 * @return the id of the target node
	 */
	public String getTargetRef() {
		return targetRef;
	}

	/**
	 * Return the flow type
	 * @return the flow type
	 */
	public String getFlowType() {
		return flowType;
	}

	/**
	 * Setter for id
	 * @param id the unique identifier
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Setter for name
	 * @param name the (optional) edge label
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Setter for SourceRef
	 * @param sourceRef the id of the source node
	 */
	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}

	/**
	 * Setter for Target
	 * @param targetRef the id of the target node
	 */
	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}

	/**
	 * Setter for FlowType
	 * @param flowType one of "sequence", "message"
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

}

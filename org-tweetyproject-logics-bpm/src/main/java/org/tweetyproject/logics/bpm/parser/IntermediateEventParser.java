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
package org.tweetyproject.logics.bpm.parser;

import org.w3c.dom.Node;

import org.tweetyproject.logics.bpm.syntax.IntermediateEvent;

/**
 * Parse intermediate events in a BPMN model
 * @author Benedikt Knopp
 */
public class IntermediateEventParser extends AbstractElementParser<IntermediateEvent>{

	/**
	 * Create a new instance
	 * @param rootParser the root parser of the BPMN model
	 */
	public IntermediateEventParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new IntermediateEvent();
	}

	@Override
	protected void handleAttribute(Node attribute) {
		String attributeName = attribute.getNodeName();
		String attributeValue = attribute.getTextContent();
		switch(attributeName) {
		// generic attributes
		case "id":
			this.parsedElement.setId(attributeValue);
			break;
		case "name":
			this.parsedElement.setName(attributeValue);
			break;
		default:
			return;
		}
	}

	@Override
	protected void handleChildNode(Node childNode) {
		String tagName = rootParser.getNormalizedTagName(childNode);
		switch(tagName) {
		case "incoming":
			String incomingEdgeId = childNode.getTextContent();
			this.parsedElement.putIncomingEdge(incomingEdgeId, null);
			break;
		case "outgoing":
			String outgoingEdgeId = childNode.getTextContent();
			this.parsedElement.putIncomingEdge(outgoingEdgeId, null);
			break;
		}
	}

}

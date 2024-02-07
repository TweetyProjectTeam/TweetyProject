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

import org.w3c.dom.Node;

/**
 * Parse an edge of the sequence flow in a BPMN model
 * @author Benedikt Knopp
 */
public class SequenceFlowParser extends AbstractElementParser<BufferedBpmnEdge> {

	/**
	 * Create a new instance
	 * @param rootParser the root parser of the BPMN model
	 */
	public SequenceFlowParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new BufferedBpmnEdge();
		this.parsedElement.setFlowType("sequence");
	}

	@Override
	protected void handleAttribute(Node attribute) {
		String attributeName = attribute.getNodeName();
		String attributeValue = attribute.getTextContent();
		switch(attributeName) {
		case "id":
			this.parsedElement.setId(attributeValue);
			break;
		case "name":
			this.parsedElement.setName(attributeValue);
			break;
		case "sourceRef":
			this.parsedElement.setSourceRef(attributeValue);
			break;
		case "targetRef":
			this.parsedElement.setTargetRef(attributeValue);
			break;
		default:
			return;
		}
	}

	@Override
	protected void handleChildNode(Node childNode) {
		String tagName = rootParser.getNormalizedTagName(childNode);
		switch(tagName) {
			case "sourceRef":
				String sourceRef = childNode.getTextContent();
				this.parsedElement.setSourceRef(sourceRef);
				break;
			case "targetRef":
				String targetRef = childNode.getTextContent();
				this.parsedElement.setTargetRef(targetRef);
				break;
		}
	}
}

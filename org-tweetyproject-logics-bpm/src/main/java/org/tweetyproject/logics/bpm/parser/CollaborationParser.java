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
import org.w3c.dom.NodeList;

/**
 * A parser for the "collaboration" element in a XML tree of a BPMN model
 * @author Benedikt Knopp
 *
 */
public class CollaborationParser {
	
	/**
	 * the root parser of the BPMN model
	 */
	private RootParser rootParser;

	/**
	 * Create a new instance
	 * @param rootParser the root parser of the BPMN model
	 */
	public CollaborationParser(RootParser rootParser) {
		this.rootParser = rootParser;
	}

	/**
	 * @param node the XML representation of the element to parse
	 */
	public void parse(Node node) throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		int numberOfChildren = children.getLength();
		for(int i = 0; i < numberOfChildren; i++) {
			Node child = children.item(i);
			handleChildNode(child);
		}
		return;
	};

	/**
	 * handle child nodes of the XML element
	 * @param childNode the XML child element
	 */
	private void handleChildNode(Node childNode) {
		String tagName = rootParser.getNormalizedTagName(childNode);
		switch(tagName) {
			case "messageFlow":
				MessageFlowParser messageFlowParser = new MessageFlowParser(rootParser);
				BufferedBpmnEdge bufferedMessageFlow = messageFlowParser.parse(childNode);
				this.rootParser.putBufferedEdge(bufferedMessageFlow);
			default:
				return;
		}
	}
}

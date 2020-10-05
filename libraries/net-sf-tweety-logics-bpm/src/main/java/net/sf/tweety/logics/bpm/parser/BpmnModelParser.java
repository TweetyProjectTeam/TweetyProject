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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.tweety.logics.bpm.syntax.Process;
import net.sf.tweety.logics.bpm.syntax.BpmnModel;

/**
 * Parse a BPMN Model from the 'definitions' node of a XML representation
 * @author Benedikt Knopp
 */
public class BpmnModelParser {

	/**
	 * the root parser of the BPMN model
	 */
	private RootParser rootParser;
	/**
	 * the class instance where the parsed content goes
	 */
	private BpmnModel parsedElement;
	
	/**
	 * Creates a new parser for the given BPMN model
	 * @param rootParser the root parser of the BPMN model
	 */
	public BpmnModelParser(RootParser rootParser) {
		this.rootParser = rootParser;
		this.parsedElement = new BpmnModel();
	}
	
	/**
	 * @param rootNode the root of the XML tree representing the BPMN model
	 * @throws IllegalArgumentException if the name of the root node is not 'definitions'
	 */
	public void parse(Node rootNode) throws IllegalArgumentException {
		String tagName = rootParser.getNormalizedTagName(rootNode);
		if(!tagName.equals("definitions")) {
			throw new IllegalArgumentException("Unexpected root element name '" + tagName + "', 'definitions' expected");
		}
		NodeList children = rootNode.getChildNodes();
		int numberOfChildren = children.getLength();
		for(int i = 0; i < numberOfChildren; i++) {
			Node child = children.item(i);
			handleChildNode(child);
		}
		return;
	};
	
	/**
	 * handle tag attributes and assign to the parsed element
	 * @param attribute the attribute
	 */
	private void handleChildNode(Node node) {
		String tagName = rootParser.getNormalizedTagName(node);
		switch(tagName) {
		case "collaboration":
			CollaborationParser collaborationParser = new CollaborationParser(rootParser);
			collaborationParser.parse(node);
			break;
		case "process":
			ProcessParser processParser = new ProcessParser(rootParser);
			Process process = processParser.parse(node);
			this.parsedElement.add(process);
			break;
		default:
			return;
		}
	}
	

	/**
	 * retrieve the parsed instance of the BpmnModel class 
	 * @return the parsed BPMN Model 
	 */
	public BpmnModel get() {
		return this.parsedElement;
	}
	
}

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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The AbstractElementParser class
 *  @author Benedikt Knopp
 *
 * @param <T> the class to instantiate with the parsed content
 */
public abstract class AbstractElementParser<T> {

	/**
	 * the class instance where the parsed content goes
	 */
	protected T parsedElement;
	/**
	 * the root parser of the BPMN model
	 */
	protected RootParser rootParser;

	/**
	 * Creates a new parser for the given BPMN model
	 * @param rootParser the root parser of the BPMN model
	 */
	public AbstractElementParser (RootParser rootParser) {
		this.rootParser = rootParser;
	}

	/**
	 * Return the parsed element
	 * @param documentNode the XML representation of the element to parse
	 * @return the parsed element
	 */
	public T parse(Node documentNode) {
		if(documentNode.hasAttributes()) {
			NamedNodeMap attributes = documentNode.getAttributes();
			int numberOfAttributes = attributes.getLength();
			for(int i = 0; i < numberOfAttributes; i++) {
				Node attribute = attributes.item(i);
				handleAttribute(attribute);
			}
		}
		if(documentNode.hasChildNodes()) {
			NodeList children = documentNode.getChildNodes();
			int numberOfChildren = children.getLength();
			for(int i = 0; i < numberOfChildren; i++) {
				Node child = children.item(i);
				handleChildNode(child);
			}
		}
		return this.parsedElement;
	};


	/**
	 * handle tag attributes and assign to the parsed element
	 * @param attribute the attribute
	 */
	protected abstract void handleAttribute(Node attribute);

	/**
	 * handle child nodes of the XML element and assign to the parsed element
	 * @param childNode the XML child element
	 */
	protected abstract void handleChildNode(Node childNode);


}

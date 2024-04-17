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

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.bpm.syntax.Lane;
import org.w3c.dom.Node;

/**
 * Parse a set of lanes in a BPMN model
 * @author Benedikt Knopp
 */
public class LaneSetParser extends AbstractElementParser<Set<Lane>> {

	/**
	 * Create a new instance
	 * @param rootParser the root parser of the BPMN model
	 */
	public LaneSetParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new HashSet<>();
	}

	@Override
	protected void handleAttribute(Node attribute) {
		return;
	}

	@Override
	protected void handleChildNode(Node childNode) {
		String tagName = rootParser.getNormalizedTagName(childNode);
		switch(tagName) {
			case "lane":
				LaneParser laneParser = new LaneParser(rootParser);
				Lane lane = laneParser.parse(childNode);
				this.parsedElement.add(lane);
			default:
				return;
		}
	}

}

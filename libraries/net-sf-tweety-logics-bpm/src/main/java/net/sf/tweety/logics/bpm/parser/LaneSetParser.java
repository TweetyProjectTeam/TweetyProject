package net.sf.tweety.logics.bpm.parser;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

import net.sf.tweety.logics.bpm.syntax.Lane;
import net.sf.tweety.logics.bpm.syntax.Task;

public class LaneSetParser extends AbstractElementParser<Set<Lane>> {

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
		String tagName = childNode.getNodeName();
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

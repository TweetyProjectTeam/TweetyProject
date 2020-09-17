package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.Node;

import net.sf.tweety.logics.bpm.syntax.Lane;

public class LaneParser extends AbstractElementParser<Lane>{

	

	public LaneParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new Lane();
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
		case "flowNodeRef":
			// TODO
			String laneId = childNode.getTextContent();
			break;
		default:
			return;
		}
	}
	
}

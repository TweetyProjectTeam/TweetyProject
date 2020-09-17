package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.Node;

import net.sf.tweety.logics.bpm.syntax.IntermediateEvent;

public class IntermediateEventParser extends AbstractElementParser<IntermediateEvent>{

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

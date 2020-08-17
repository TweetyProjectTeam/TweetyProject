package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.Node;

import net.sf.tweety.logics.bpm.syntax.ExclusiveGateway;

public class ExclusiveGatewayParser extends AbstractElementParser<ExclusiveGateway>{

	public ExclusiveGatewayParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new ExclusiveGateway();
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
		String tagName = childNode.getNodeName();
		switch(tagName) {
		case "incoming":
			String incomingEdgeId = childNode.getTextContent();
			this.parsedElement.putIncomingEdge(incomingEdgeId, null);
			break;
		case "outgoing":
			String outgoingEdgeId = childNode.getTextContent();
			this.parsedElement.putOutgoingEdge(outgoingEdgeId, null);
			break;
		}
	}

}

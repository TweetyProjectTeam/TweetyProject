package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.Node;

import net.sf.tweety.logics.bpm.syntax.ExclusiveGateway;
import net.sf.tweety.logics.bpm.syntax.InclusiveGateway;

public class InclusiveGatewayParser extends AbstractElementParser<InclusiveGateway>{

	public InclusiveGatewayParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new InclusiveGateway();
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
			this.parsedElement.putOutgoingEdge(outgoingEdgeId, null);
			break;
		}
	}

}
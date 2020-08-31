package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.Node;

public class MessageFlowParser extends AbstractElementParser<BufferedBpmnEdge> {

	public MessageFlowParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new BufferedBpmnEdge();
		this.parsedElement.setFlowType("message");
	}

	@Override
	protected void handleAttribute(Node attribute) {
		String attributeName = attribute.getNodeName();
		String attributeValue = attribute.getTextContent();
		switch(attributeName) {
		case "id":
			this.parsedElement.setId(attributeValue);
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
		String tagName = childNode.getNodeName();
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

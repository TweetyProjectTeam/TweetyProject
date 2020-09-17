package net.sf.tweety.logics.bpm.parser;

import java.util.Set;

import org.w3c.dom.Node;

import net.sf.tweety.logics.bpm.syntax.Lane;
import net.sf.tweety.logics.bpm.syntax.SequenceFlow;
import net.sf.tweety.logics.bpm.syntax.Task;

public class SequenceFlowParser extends AbstractElementParser<BufferedBpmnEdge> {

	public SequenceFlowParser(RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new BufferedBpmnEdge();
		this.parsedElement.setFlowType("sequence");
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
		String tagName = rootParser.getNormalizedTagName(childNode);
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

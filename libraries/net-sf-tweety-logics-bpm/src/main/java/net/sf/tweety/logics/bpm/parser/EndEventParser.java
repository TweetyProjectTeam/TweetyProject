package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.Node;

import net.sf.tweety.logics.bpm.syntax.EndEvent;
import net.sf.tweety.logics.bpm.syntax.StartEvent;

public class EndEventParser extends AbstractElementParser<EndEvent> {
		
		public EndEventParser (RootParser rootParser) {
			super(rootParser);
			this.parsedElement = new EndEvent();
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
			}
		}
		

	}

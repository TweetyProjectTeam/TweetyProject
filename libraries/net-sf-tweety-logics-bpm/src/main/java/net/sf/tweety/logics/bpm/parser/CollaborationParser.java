package net.sf.tweety.logics.bpm.parser;

import java.util.HashSet;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.tweety.logics.bpm.syntax.Lane;

public class CollaborationParser {
	
	private RootParser rootParser;

	public CollaborationParser(RootParser rootParser) {
		this.rootParser = rootParser;
	}

	public void parse(Node node) throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		int numberOfChildren = children.getLength();
		for(int i = 0; i < numberOfChildren; i++) {
			Node child = children.item(i);
			handleChildNode(child);
		}
		return;
	};

	private void handleChildNode(Node childNode) {
		String tagName = rootParser.getNormalizedTagName(childNode);
		switch(tagName) {
			case "messageFlow":
				MessageFlowParser messageFlowParser = new MessageFlowParser(rootParser);
				BufferedBpmnEdge bufferedMessageFlow = messageFlowParser.parse(childNode);
				this.rootParser.putBufferedEdge(bufferedMessageFlow);
			default:
				return;
		}
	}
}

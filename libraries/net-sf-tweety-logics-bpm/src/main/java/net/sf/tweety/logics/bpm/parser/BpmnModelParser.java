package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.tweety.logics.bpm.syntax.Process;
import net.sf.tweety.logics.bpm.syntax.BpmnModel;

public class BpmnModelParser {

	private NodeList nodes;
	private RootParser rootParser;
	private BpmnModel parsedElement;
	
	public BpmnModelParser(RootParser rootParser) {
		this.rootParser = rootParser;
		this.parsedElement = new BpmnModel();
	}
	
	public void parse(Node rootNode) throws IllegalArgumentException {
		String tagName = rootNode.getNodeName();
		if(!tagName.equals("definitions")) {
			throw new IllegalArgumentException("Unexpected root element name '" + tagName + "', 'definitions' expected");
		}
		NodeList children = rootNode.getChildNodes();
		int numberOfChildren = children.getLength();
		for(int i = 0; i < numberOfChildren; i++) {
			Node child = children.item(i);
			handleChildNode(child);
		}
		return;
	};
	
	private void handleChildNode(Node node) {
		String tagName = node.getNodeName();
		switch(tagName) {
		case "collaboration":
			CollaborationParser collaborationParser = new CollaborationParser(rootParser);
			collaborationParser.parse(node);
			break;
		case "process":
			ProcessParser processParser = new ProcessParser(rootParser);
			Process process = processParser.parse(node);
			this.parsedElement.add(process);
			break;
		default:
			return;
		}
	}
	
	public BpmnModel get() {
		return this.parsedElement;
	}
	
}

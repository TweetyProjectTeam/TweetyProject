package net.sf.tweety.logics.bpm.parser;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.tweety.logics.bpm.syntax.BpmnElement;
import net.sf.tweety.logics.bpm.syntax.BpmnModel;

public abstract class AbstractElementParser<T> {

	protected T parsedElement;
	protected RootParser rootParser;

	public AbstractElementParser (RootParser rootParser) {
		this.rootParser = rootParser;
	}
	
	public T parse(Node documentNode) {
		if(documentNode.hasAttributes()) {
			NamedNodeMap attributes = documentNode.getAttributes();
			int numberOfAttributes = attributes.getLength();
			for(int i = 0; i < numberOfAttributes; i++) {
				Node attribute = attributes.item(i);
				handleAttribute(attribute);
			}
		}
		if(documentNode.hasChildNodes()) {
			NodeList children = documentNode.getChildNodes();
			int numberOfChildren = children.getLength();
			for(int i = 0; i < numberOfChildren; i++) {
				Node child = children.item(i);
				handleChildNode(child);
			}
		}
		return this.parsedElement;
	};
	
		
	protected abstract void handleAttribute(Node attribute);
	protected abstract void handleChildNode(Node childNode);

	
}

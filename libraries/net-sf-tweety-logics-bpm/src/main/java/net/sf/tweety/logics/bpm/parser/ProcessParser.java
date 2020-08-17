package net.sf.tweety.logics.bpm.parser;

import net.sf.tweety.logics.bpm.syntax.*;
import net.sf.tweety.logics.bpm.syntax.Process;

import java.util.Set;

import org.w3c.dom.Node;


public class ProcessParser extends AbstractElementParser<Process> {
	
	public ProcessParser (RootParser rootParser) {
		super(rootParser);
		this.parsedElement = new Process();
	}

	@Override
	protected void handleAttribute(Node attribute) {
		String attributeName = attribute.getNodeName();
		String attributeValue = attribute.getTextContent();
		switch(attributeName) {
		// generic attributes
		case "id":
			this.parsedElement.setId(attributeValue);
		case "name":
			this.parsedElement.setName(attributeValue);
		default:
			return;
		}
	}

	@Override
	protected void handleChildNode(Node childNode) {
		String tagName = childNode.getNodeName();
		switch(tagName) {
		case "subProcess":
			ProcessParser processParser = new ProcessParser(this.rootParser);
			Process subprocess = processParser.parse(childNode);
			this.parsedElement.addSubProcess(subprocess);
			break;
		case "task":
			TaskParser taskParser = new TaskParser(this.rootParser);
			Task task = taskParser.parse(childNode);
			this.parsedElement.addNode(task);
			this.rootParser.putNode(task);
			break;
		case "startEvent":
			StartEventParser startEventParser = new StartEventParser(this.rootParser);
			StartEvent startEvent = startEventParser.parse(childNode);
			this.parsedElement.addNode(startEvent);
			this.rootParser.putNode(startEvent);
			break;
		case "endEvent":
			EndEventParser endEventParser = new EndEventParser(this.rootParser);
			EndEvent endEvent = endEventParser.parse(childNode);
			this.parsedElement.addNode(endEvent);
			this.rootParser.putNode(endEvent);
			break;
		case "intermediateCatchEvent":
			IntermediateEventParser intermediateEventParser = new IntermediateEventParser(this.rootParser);
			IntermediateEvent intermediateEvent = intermediateEventParser.parse(childNode);
			this.parsedElement.addNode(intermediateEvent);
			this.rootParser.putNode(intermediateEvent);
			break;
		case "exclusiveGateway":
			ExclusiveGatewayParser exclusiveGatewayParser = new ExclusiveGatewayParser(this.rootParser);
			ExclusiveGateway exclusiveGateway = exclusiveGatewayParser.parse(childNode);
			this.rootParser.putNode(exclusiveGateway);
			break;
		case "inclusiveGateway":
			InclusiveGatewayParser inclusiveGatewayParser = new InclusiveGatewayParser(this.rootParser);
			InclusiveGateway inclusiveGateway = inclusiveGatewayParser.parse(childNode);
			this.rootParser.putNode(inclusiveGateway);
			break;
		case "laneSet":
			LaneSetParser laneSetParser = new LaneSetParser(this.rootParser);
			laneSetParser.parse(childNode);
			Set<Lane> laneSet = laneSetParser.parse(childNode);
			this.parsedElement.addLanes(laneSet);
			break;
		case "sequenceFlow":
			SequenceFlowParser sequenceFlowParser = new SequenceFlowParser(this.rootParser);
			BufferedBpmnEdge bufferedSequenceFlow = sequenceFlowParser.parse(childNode);
			this.rootParser.putBufferedEdge(bufferedSequenceFlow);
			break;
		case "messageFlow":
			MessageFlowParser messageFlowParser = new MessageFlowParser(this.rootParser);
			BufferedBpmnEdge bufferedMessageFlow = messageFlowParser.parse(childNode);
			this.rootParser.putBufferedEdge(bufferedMessageFlow);
			break;
		default:
			return;
		}	
	}
	
	

}

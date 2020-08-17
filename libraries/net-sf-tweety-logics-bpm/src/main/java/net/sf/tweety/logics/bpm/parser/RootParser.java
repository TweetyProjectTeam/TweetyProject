package net.sf.tweety.logics.bpm.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.tweety.graphs.Edge;
import net.sf.tweety.logics.bpm.syntax.*;

public class RootParser {

	private File xmlFile;
	private BpmnModel bpmnModel;
	private Map<String, BpmnNode> nodeMap = new HashMap<>();
	private Map<String, Edge<BpmnNode>> edgeMap = new HashMap<>();
	private Map<String, BufferedBpmnEdge> edgeBuffer = new HashMap<>();
	
	public RootParser(File xmlFile) {
		this.xmlFile = xmlFile;
	}
	
	public void parse() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder(); 
		Document xmlDocument = db.parse(xmlFile);
		Node rootNode = xmlDocument.getDocumentElement();
		BpmnModelParser bpmnModelParser = new BpmnModelParser(this);
		bpmnModelParser.parse(rootNode);
		this.bpmnModel = bpmnModelParser.get();
		// defer edge instantiation until node objects available after parsing
		handleEdgeBuffer();
		handleNodeBuffer();
	}
	
	public BpmnModel getBpmnModel() {
		return this.bpmnModel;
	}
	
	private void handleEdgeBuffer() {
		// create actual edge objects (need node objects for edge constructor)
		for(String edgeId : edgeBuffer.keySet()) {
			BufferedBpmnEdge edge = edgeBuffer.get(edgeId);
			BpmnNode source = nodeMap.get(edge.getSourceRef());
			BpmnNode target = nodeMap.get(edge.getTargetRef());
			String flowType = edge.getFlowType();
			Edge<BpmnNode> parsedEdge = null;
			switch(flowType) {
			case "sequence":
				parsedEdge = new SequenceFlow(source, target);
				break;
			case "message":
				parsedEdge = new MessageFlow(source, target);
				break;
			default:
				if(parsedEdge == null)
					continue;
			}
			this.bpmnModel.add(parsedEdge);
			this.edgeMap.put(edgeId, parsedEdge);
		}
	}
	
	private void handleNodeBuffer() {
		// update node member variables with parsed edge elements
		for(String nodeId : nodeMap.keySet()) {
			BpmnNode node = nodeMap.get(nodeId);
			Set<String> incomingEdgeIds = node.getIncomingEdges().keySet();
			for(String edgeId : incomingEdgeIds) {
				Edge<BpmnNode> incomingEdge = this.edgeMap.get(edgeId);
				node.putIncomingEdge(edgeId, incomingEdge);
				this.bpmnModel.add(node);
			}
			Set<String> outgoingEdgeIds = node.getOutgoingEdges().keySet();
			for(String edgeId : outgoingEdgeIds) {
				Edge<BpmnNode> outgoingEdge = this.edgeMap.get(edgeId);
				node.putOutgoingEdge(edgeId, outgoingEdge);
				this.bpmnModel.add(node);
			}
		}
	}
	
	public void putNode(BpmnNode node) {
		this.nodeMap.put(node.getId(), node);
	}
	
	public void putBufferedEdge(BufferedBpmnEdge edge) {
		this.edgeBuffer.put(edge.getId(), edge);
	}
	
}

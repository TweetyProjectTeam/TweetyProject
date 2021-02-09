/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.parser.xml_to_bpmn;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.logics.bpm.syntax.*;

/**
 * Instances of this class serve as the root of a parsing process for a BPMN XML file.
 * @author Benedikt
 */
public class RootParser extends Parser {

	/**
	 * The XML file to parse
	 */
	private File xmlFile;
	/**
	 * The parsed element
	 */
	private BpmnModel bpmnModel;
	/**
	 * The parsed nodes of the BPMN model by their id
	 */
	private Map<String, BpmnNode> nodeMap = new HashMap<>();
	/**
	 * The parsed edges of the BPMN model by their id
	 */
	private Map<String, Edge<BpmnNode>> edgeMap = new HashMap<>();
	/**
	 * The edges buffered in the parsing process by their id
	 */
	private Map<String, BufferedBpmnEdge> edgeBuffer = new HashMap<>();
	
	/**
	 * Create a new instance
	 */
	public RootParser() {
	}
	
	
	/**
	 * @param node the node to retrieve the name for
	 * @return the node name free of possible namespace prefixes
	 */
	public String getNormalizedTagName(Node node) {
		String tagName = node.getNodeName();
		tagName = tagName.replace("bpmn:", "");
		return tagName;
	}
	
	/**
	 * @return the parsed BpmnModel
	 */
	public BpmnModel getBpmnModel() {
		return this.bpmnModel;
	}
	
	/**
	 * create the actual edge objects from the buffered edges, 
	 * after the parsed nodes are available
	 */
	private void handleEdgeBuffer() {
		for(String edgeId : edgeBuffer.keySet()) {
			BufferedBpmnEdge edge = edgeBuffer.get(edgeId);
			BpmnNode source = nodeMap.get(edge.getSourceRef());
			BpmnNode target = nodeMap.get(edge.getTargetRef());
			String label = edge.getName();
			String flowType = edge.getFlowType();
			Edge<BpmnNode> parsedEdge = null;
			switch(flowType) {
			case "sequence":
				parsedEdge = new SequenceFlow(source, target, label);
				break;
			case "message":
				parsedEdge = new MessageFlow(source, target, label);
				break;
			default:
				if(parsedEdge == null)
					continue;
			}
			this.bpmnModel.add(parsedEdge);
			this.edgeMap.put(edgeId, parsedEdge);
		}
	}
	
	/**
	 * put a reference to incoming and outgoing edges into the node objects,
	 * after the parsed edges are available
	 */
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
	
	/**
	 * @param node a parsed node
	 */
	public void putNode(BpmnNode node) {
		this.nodeMap.put(node.getId(), node);
	}
	
	/**
	 * @param edge a prepared buffered edge
	 */
	public void putBufferedEdge(BufferedBpmnEdge edge) {
		this.edgeBuffer.put(edge.getId(), edge);
	}

	/**
	 * Parse the XML file to an instance of the BpmnModel class
	 * @param xmlFile the XML file to parse to a BPMN model
	 * @throws ParserConfigurationException  if some error occurs
	 * @throws SAXException if some error occurs
	 * @throws IOException if some error occurs
	 * @return the parsed belief base
	 */
	public BeliefBase parseFile(File xmlFile) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder(); 
		Document xmlDocument = db.parse(xmlFile);
		this.parse(xmlDocument);
		return this.bpmnModel;
	}
	
	  /*
	   * (non-Javadoc)
	   * @see org.tweetyproject.Parser#parseBeliefBase(java.io.Reader)
	   */
	@Override
	public BeliefBase parseBeliefBase(Reader reader) throws IOException, ParserException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			String xmlString = "";
			int c = reader.read();
			while( c != -1) {
				xmlString += (char) c;
				c = reader.read();
			}
			Document xmlDocument = builder.parse(new InputSource(new StringReader(xmlString)));
			this.parse(xmlDocument);
			return this.bpmnModel;
		} catch (Exception e) {
			throw new ParserException( e );
		}
	}
	
	/**
	 * Parse the XML Document object to an instance of the BpmnModel class
	 * @param xmlDocument the document object to parse to a BPMN model
	 */
	private void parse(Document xmlDocument) {
		Node rootNode = xmlDocument.getDocumentElement();
		BpmnModelParser bpmnModelParser = new BpmnModelParser(this);
		bpmnModelParser.parse(rootNode);
		this.bpmnModel = bpmnModelParser.get();
		// defer edge instantiation until node objects available after parsing
		handleEdgeBuffer();
		handleNodeBuffer();		
	}
	
	  /*
	   * (non-Javadoc)
	   * @see org.tweetyproject.Parser#parseFormula(java.io.Reader)
	   */
	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		throw new UnsupportedOperationException();
	}
	
}

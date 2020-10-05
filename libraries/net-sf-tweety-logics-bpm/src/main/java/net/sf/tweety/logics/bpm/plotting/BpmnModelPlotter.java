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
package net.sf.tweety.logics.bpm.plotting;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import net.sf.tweety.graphs.Edge;
import net.sf.tweety.logics.bpm.syntax.*;

/**
 * This class is for displaying instances of the BpmnModel class graphically
 * @author Benedikt Knopp
 */
public class BpmnModelPlotter {

	/**
	 * The BpmnModel class instance to display
	 */
	private BpmnModel bpmnModel;
	
	/**
	 * The nodes in the BpmnModel of interest
	 */
	private Set<BpmnNode> nodes;
	
	/**
	 * The edges in the BpmnModel of interest
	 */
	private Set<Edge<BpmnNode>> edges;
	
	/**
	 * The frame where the model is drawn
	 */
	private JFrame frame;
	
	/**
	 * The panel in the frame where the model is drawn
	 */
    private JPanel panel;
	
	/**
	 * Create a new instance for plotting the BpmnModel
	 * @param bpmnModel the model of interest
	 */
	public BpmnModelPlotter(BpmnModel bpmnModel) {
		this.bpmnModel = bpmnModel;
		this.nodes = bpmnModel.getNodes();
		this.edges = bpmnModel.getEdges();
		this.frame = new JFrame();
		this.panel = new JPanel();
	}
	
	/**
	 * prepare the graphical representation of the BPMN model
	 * @param frameWidth the width of the frame
	 * @param frameHeight the height of the frame
	 * @param nodeWidth the width of nodes in the BPMN model
	 * @param nodeHeight the height of nodes in the BPMN model
	 */
	public void createGraph(int frameWidth, int frameHeight, double nodeWidth, double nodeHeight, int fontSize) {
        this.frame.setSize(frameWidth, frameHeight);
        panel.setSize(frame.getMaximumSize().width,
                frame.getMaximumSize().height);
	    
        final mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        Map<String, Object> vertexObjects = new HashMap<>();
        this.nodes.forEach(node -> {
        	String vertexName = getPrettyName(node);
        	String style = getStyle(vertexName, fontSize);
            Object vertexObject = graph.insertVertex(parent, null, vertexName, 0, 0, nodeWidth, nodeHeight, style);
            vertexObjects.put(vertexName, vertexObject);
        });
        edges.forEach(edge -> {
        	BpmnNode nodeA = edge.getNodeA();
        	BpmnNode nodeB = edge.getNodeB();
        	String vertexNameA = getPrettyName(nodeA);
        	String vertexNameB = getPrettyName(nodeB);
        	Object vertexObjectA = vertexObjects.get(vertexNameA);
        	Object vertexObjectB = vertexObjects.get(vertexNameB);
        	String edgeLabel = getPrettyName(edge);
            graph.insertEdge(parent, null, edgeLabel, vertexObjectA, vertexObjectB);
        });
        
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(graph.getDefaultParent());
        graph.getModel().endUpdate();
        
        final mxGraphComponent graphComponent = new mxGraphComponent(graph);
        panel.setLayout( new FlowLayout(FlowLayout.LEFT, 20, 20) );
        panel.add(graphComponent);
	}
	
	/**
	 * Add a label to the frame
	 * Please note that the labels will be displayed from top to bottom in the order they are added
	 * You can add labels before and after executing the createGraph method,
	 * so that these labels will be displayed before and after the graph, respectively.
	 * @param label the textual content that will be displayed in the frame
	 */
	public void addLabel(String label) {
		this.panel.add( new JLabel(label) );
	}
	
	/**
	 * Display the created graph and all possibly added labels
	 */
	public void show() {
        this.frame.add(panel);
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * @param node the node to retrieve the pretty name for
	 * @return the pretty name of the node that will be displayed in the graph
	 */
	private String getPrettyName(BpmnNode node) {
		String nodeTypePrefix;
		if(Activity.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "Activity\n";
		}
		else if(ExclusiveGateway.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "XOR\n";
		}
		else if(InclusiveGateway.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "OR\n";
		}
		else if(StartEvent.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "Start-Event\n";
		}
		else if(EndEvent.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "End-Event\n";
		}
		else if(IntermediateEvent.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "Intermediate-Event\n";
		}
		else if(Event.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "Event\n";
		}
		else {
			throw new IllegalArgumentException("Could not find pretty name for argument '"
					+ node.getId() + "'");
		}
		String name;
		if(node.getName() != null) {
			name = node.getName();
		} else {
			name = node.getId();
		}
		return nodeTypePrefix + name;
	}
	
	/**
	 * @param edge the edge to retrieve the pretty name for
	 * @return the pretty name of the edge that will be displayed in the graph
	 */
	private String getPrettyName(Edge<BpmnNode> edge) {
		String label = edge.getLabel();
		if(!(label == null || label.isEmpty())) {
			return label;
		}
		return "";
	}
	
	/**
	 * @param prettyName the pretty name of the element by which the element class is derived
	 * @return a string formatted to carry style information for mxGraph cells
	 */
	private String getStyle(String prettyName, int fontSize) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + fontSize + ";"; 
		if(prettyName.startsWith("Activity")) {
			return  styleString
					+ mxConstants.STYLE_FILLCOLOR + "=lightblue;"
					+ mxConstants.STYLE_ROUNDED + "=true;";
		}
		if(prettyName.startsWith("XOR")
				|| prettyName.startsWith("OR")) {
			return  styleString 
					+ mxConstants.STYLE_FILLCOLOR + "=lightgreen;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_RHOMBUS;
		}
		if(prettyName.startsWith("Start-Event")
				|| prettyName.startsWith("End-Event")) {
			return styleString 
					+ mxConstants.STYLE_FILLCOLOR + "=lightpink;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_ELLIPSE;
		}
		if(prettyName.startsWith("Event")
				|| prettyName.startsWith("Intermediate-Event")) {
			return styleString
					+ mxConstants.STYLE_FILLCOLOR + "=lightyellow;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_DOUBLE_ELLIPSE;
		}
		return styleString
				+ mxConstants.STYLE_FILLCOLOR + "=white;";
	}
	
}

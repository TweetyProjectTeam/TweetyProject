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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.plotting;

import com.mxgraph.util.mxConstants;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.util.GraphPlotter;
import org.tweetyproject.logics.bpm.syntax.*;

/**
 * This class is for displaying instances of the BpmnModel class graphically
 * @author Benedikt Knopp
 */
public class BpmnModelPlotter extends GraphPlotter<BpmnNode, Edge<BpmnNode>>{

	/**
	 * Create a new instance for plotting the BpmnModel
	 * @param plotter the parent plotter
	 * @param bpmnModel the model of interest
	 */
	public BpmnModelPlotter(Plotter plotter, BpmnModel bpmnModel) {
		super(plotter, bpmnModel);
	}
	
	/**
	 * @param node the node to retrieve the pretty name for
	 * @return the pretty name of the node that will be displayed in the graph
	 */
	@Override
	protected String getPrettyName(BpmnNode node) {
		String nodeTypePrefix = getNodeTypePrefix(node);
		String name = node.getName();
		if(node.getName() == null) {
			name = node.getId();
		}
		return nodeTypePrefix + name;
	}
	
	/**
	 * @param edge the edge to retrieve the pretty name for
	 * @return the pretty name of the edge that will be displayed in the graph
	 */
	@Override
	protected String getPrettyName(Edge<BpmnNode> edge) {
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
	@Override
	protected String getStyle(BpmnNode node) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + fontSize + ";"; 
		String nodeTypePrefix = this.getNodeTypePrefix(node);
		if(nodeTypePrefix.startsWith("Activity")) {
			return  styleString
					+ mxConstants.STYLE_FILLCOLOR + "=lightblue;"
					+ mxConstants.STYLE_ROUNDED + "=true;";
		}
		if(nodeTypePrefix.startsWith("XOR")
				|| nodeTypePrefix.startsWith("AND")) {
			return  styleString 
					+ mxConstants.STYLE_FILLCOLOR + "=lightgreen;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_RHOMBUS;
		}
		if(nodeTypePrefix.startsWith("Start-Event")
				|| nodeTypePrefix.startsWith("End-Event")) {
			return styleString 
					+ mxConstants.STYLE_FILLCOLOR + "=lightpink;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_ELLIPSE;
		}
		if(nodeTypePrefix.startsWith("Event")
				|| nodeTypePrefix.startsWith("Intermediate-Event")) {
			return styleString
					+ mxConstants.STYLE_FILLCOLOR + "=lightyellow;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_DOUBLE_ELLIPSE;
		}
		return styleString
				+ mxConstants.STYLE_FILLCOLOR + "=white;";
	}
	
	/**
	 * Get a String prefix for the node in order to manage behavior for different types
	 * @param node the node
	 * @return the type prefix
	 */
	private String getNodeTypePrefix(BpmnNode node) {
		String nodeTypePrefix;
		if(Activity.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "Activity\n";
		}
		else if(ExclusiveGateway.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "XOR\n";
		}
		else if(InclusiveGateway.class.isAssignableFrom(node.getClass())) {
			nodeTypePrefix = "AND\n";
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
			throw new IllegalArgumentException("Could not find node type for argument '"
					+ node.getId() + "'");
		}
		return nodeTypePrefix;
	}

	@Override
	protected double getVertexWidth() {
		return 30;
	}

	@Override
	protected double getVertexHeight() {
		return 30;
	}

	@Override
	protected int getFontSize() {
		return 14;
	}

	@Override
	protected int getVertexSpacing() {
		return 50;
	}
	
}

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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.util;

import java.util.ArrayList;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationNode;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.util.GraphPlotter;

import com.mxgraph.util.mxConstants;

/**
 * This class represents a specialization of {@link GraphPlotter GraphPlotters} used
 * to visualize the process of generating extensions by serialising sets of arguments,
 * as realised in {@link SerialisableExtensionReasoner}.
 * <br>
 * The class defines the layout specifications of the visualization:
 * <ul>
 * 		<li> label of a node
 * 		<li> label of an edge
 * 		<li> size of a node
 * 		<li> style and font-size of a node
 * 		<li> spacing between nodes
 * </ul>
 * <br>
 * @see org.tweetyproject.arg.dung.util.DungTheoryPlotter
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class SerialisationGraphPlotter extends GraphPlotter<SerialisationNode, Edge<SerialisationNode>> {

	/**
	 * Plots the specified graph in a new created frame.
	 *
	 * @param graph Graph of a generation process of serialisable extensions
	 * @param width Width of the new frame created.
	 * @param height Height of the new frame created.
	 * @param title Title of the Graph to plot.
	 */
	public static void plotGraph(SerialisationGraph graph, int width, int height, String title) {

		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);

		SerialisationGraphPlotter.plotGraph(graph, groundPlotter, title);

		groundPlotter.show();
	}

	/**
	 * Plots the specified graph in the frame of the specified plotter
	 *
	 * @param graph Graph of a generation process of serialisable extensions
	 * @param groundPlotter Plotter, which creates the frame
	 * @param title Title of the Graph to plot.
	 */
	public static void plotGraph(SerialisationGraph graph, Plotter groundPlotter, String title) {
		SerialisationGraphPlotter sePlotter = new SerialisationGraphPlotter(groundPlotter, graph);
		sePlotter.createGraph(false);
		var lstLabels = new ArrayList<String>();
		lstLabels.add(title);
		lstLabels.add(graph.getSemantics().description());
		lstLabels.add("Green: found Extensions");
		//lstLabels.add("Found Extensions (shown in green):");
		//lstLabels.add(graph.getExtensions().toString());
		sePlotter.addLabels(lstLabels);
	}

	private final String LABEL_DEFAULT_NODE = "N.A.";
	private final String LABEL_DEFAULT_EDGE = "";
	private final String STYLE_NODE_FILLCOLOR = "=lightblue;";
	private final String STYLE_NODE_ROUNDED = "=true;";
	private final String STYLE_NODE_HIGHLIGHTED_FILLCOLOR = "=lightgreen;";
	private final String STYLE_NODE_HIGHLIGHTED_ROUNDED = "=true;";
	private final int VERTEX_WIDTH = 90;
	private final int VERTEX_HEIGHT = 30;
	private final int VERTEX_SPACING = 90;
	private final int FONTSIZE = 10;

	private SerialisationGraph graph;

/**
 * Constructs a SerialisationGraphPlotter with the specified plotter and serialisation graph.
 * This constructor initializes the plotter and associates it with a specific serialisation graph,
 * which represents the graph of a generation process of serialisable extensions.
 * 
 *
 * @param plotter The plotter to be used for visualizing the graph. This component handles the graphical rendering.
 * @param graph The serialisation graph that represents the structure and data of a serialisable extension generation process.
 */
public SerialisationGraphPlotter(Plotter plotter, SerialisationGraph graph) {
    super(plotter, graph); // Call to the superclass constructor to initialize the plotter and graph

    this.graph = graph; // Storing the graph in an instance variable for further use
}

	@Override
	public void createGraph(boolean isVertical) {
		super.createGraph(isVertical);
	}

	@Override
	public String getStyle(SerialisationNode node) {
		boolean isHighlighted = node.isTerminal();
		String style;

		if(isHighlighted) {
			style = this.getStyleNodeHighlighted(node);
		}else {
			style = this.getStyleNode(node);
		}

		return style;
	}


	@Override
	protected int getFontSize() {
		return this.FONTSIZE;
	}

	@Override
	protected String getPrettyName(Edge<SerialisationNode> edge) {
		String label = edge.getLabel();
		if((label == null) || label.isEmpty()) {
			label = this.LABEL_DEFAULT_EDGE;
		}
		return label;
	}

	@Override
	protected String getPrettyName(SerialisationNode node) {
		String name = node.toString();
		if((name == null)|| name.isEmpty()) {
			name = this.LABEL_DEFAULT_NODE;
		}
		return name;
	}

	/**
	 * Defines the layout style of a regular node.
	 * @param node Node, that shall be represented.
	 * @return String describing the layout of the regular node.
	 */
	protected String getStyleNode(SerialisationNode node) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + this.FONTSIZE + ";";
		return  styleString
				+ mxConstants.STYLE_FILLCOLOR + this.STYLE_NODE_FILLCOLOR
				+ mxConstants.STYLE_ROUNDED + this.STYLE_NODE_ROUNDED;
	}


	/**
	 * Defines the layout style of a node to highlight.
	 * @param node Node, that shall be represented as highlighted.
	 * @return String describing the layout of the highlighted node.
	 */
	protected String getStyleNodeHighlighted(SerialisationNode node) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + this.FONTSIZE + ";";
		return  styleString
				+ mxConstants.STYLE_FILLCOLOR + this.STYLE_NODE_HIGHLIGHTED_FILLCOLOR
				+ mxConstants.STYLE_ROUNDED + this.STYLE_NODE_HIGHLIGHTED_ROUNDED;
	}

	@Override
	protected double getVertexHeight() {
		return this.VERTEX_HEIGHT;
	}

	@Override
	protected int getVertexSpacing() {
		return this.VERTEX_SPACING;
	}

	@Override
	protected double getVertexWidth() {
		return this.VERTEX_WIDTH;
	}

}

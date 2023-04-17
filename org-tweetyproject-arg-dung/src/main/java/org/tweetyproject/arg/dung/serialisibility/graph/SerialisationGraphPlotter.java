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
package org.tweetyproject.arg.dung.serialisibility.graph;

import java.util.ArrayList;

import org.tweetyproject.arg.dung.serialisibility.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.util.GraphPlotter;

import com.mxgraph.util.mxConstants;

/**
 * This class represents a specialization of {@link GraphPlotter GraphPlotters} used
 * to visualize the process of generating extensions by serialising sets of arguments,
 * as realised in {@link SerialisableExtensionReasonerWithAnalysis}.
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
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation.
 *      Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract
 *      Argumentation. Computational Models of Argument (2022) DOI:
 *      10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class SerialisationGraphPlotter extends GraphPlotter<TransitionStateNode, Edge<TransitionStateNode>> {

	/**
	 * Plots the specified analysis in a new created frame.
	 *
	 * @param analysis Analysis of a generation process of serialisable extensions
	 * @param width Width of the new frame created.
	 * @param height Height of the new frame created.
	 */
	public static void plotAnalysis(SerialisationGraph analysis, int width, int height) {

		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);

		SerialisationGraphPlotter.plotAnalysis(analysis, groundPlotter);

		groundPlotter.show();
	}

	/**
	 * Plots the specified analysis in the frame of the specified plotter
	 *
	 * @param analysis Analysis of a generation process of serialisable extensions
	 * @param groundPlotter Plotter, which creates the frame
	 */
	public static void plotAnalysis(SerialisationGraph analysis, Plotter groundPlotter) {
		SerialisationGraphPlotter sePlotter = new SerialisationGraphPlotter(groundPlotter, analysis);
		sePlotter.createGraph(false);
		var lstLabels = new ArrayList<String>();
		lstLabels.add(analysis.getTitle());
		lstLabels.add(analysis.getSemanticsUsed().description());
		lstLabels.add("Green: found Extensions");
		//lstLabels.add("Found Extensions (shown in green):");
		//lstLabels.add(analysis.getExtensionsFound().toString());
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

	private SerialisationGraph analysis;

	public SerialisationGraphPlotter(Plotter plotter, SerialisationGraph analysis) {
		super(plotter, analysis.getGraphResulting());

		this.analysis = analysis;
	}

	@Override
	public void createGraph(boolean isVertical) {
		super.createGraph(isVertical);
	}

	@Override
	public String getStyle(TransitionStateNode node) {
		boolean isHighlighted = this.analysis.getExtensionsFound().contains(node.getState().getExtension());
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
	protected String getPrettyName(Edge<TransitionStateNode> edge) {
		String label = edge.getLabel();
		if((label == null) || label.isEmpty()) {
			label = this.LABEL_DEFAULT_EDGE;
		}
		return label;
	}

	@Override
	protected String getPrettyName(TransitionStateNode node) {
		String name = node.getName();
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
	protected String getStyleNode(TransitionStateNode node) {
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
	protected String getStyleNodeHighlighted(TransitionStateNode node) {
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

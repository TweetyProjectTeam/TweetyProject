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
package org.tweetyproject.arg.dung.serialisibility.plotter;

import com.mxgraph.util.mxConstants;

import java.util.ArrayList;
import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.ContainerTransitionStateAnalysis;
import org.tweetyproject.arg.dung.serialisibility.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.util.GraphPlotter;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.Graph;

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
public class SerialisableExtensionPlotter extends GraphPlotter<TransitionStateNode, Edge<TransitionStateNode>> {

	private final String LABEL_DEFAULT_NODE = "N.A.";
	private final String LABEL_DEFAULT_EDGE = "";
	private final String STYLE_NODE_FILLCOLOR = "=lightblue;";
	private final String STYLE_NODE_ROUNDED = "=true;";
	private final int VERTEX_WIDTH = 30;
	private final int VERTEX_HEIGHT = 30;
	private final int VERTEX_SPACING = 50;
	private final int FONTSIZE = 14;
	
	public SerialisableExtensionPlotter(Plotter plotter, Graph<TransitionStateNode> graph) {
		super(plotter, graph);
	}

	@Override
	protected String getPrettyName(TransitionStateNode node) {
		String name = node.getName();
		if(name == null|| name.isEmpty()) {
			name = LABEL_DEFAULT_NODE;
		}
		return name;
	}

	@Override
	protected String getPrettyName(Edge<TransitionStateNode> edge) {
		String label = edge.getLabel();
		if(label == null || label.isEmpty()) {
			label = LABEL_DEFAULT_EDGE;
		}
		return label;
	}

	@Override
	protected String getStyle(TransitionStateNode node) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + FONTSIZE + ";"; 
		return  styleString
				+ mxConstants.STYLE_FILLCOLOR + STYLE_NODE_FILLCOLOR
				+ mxConstants.STYLE_ROUNDED + STYLE_NODE_ROUNDED;
	}

	@Override
	protected double getVertexWidth() {
		return VERTEX_WIDTH;
	}

	@Override
	protected double getVertexHeight() {
		return VERTEX_HEIGHT;
	}

	@Override
	protected int getFontSize() {
		return FONTSIZE;
	}

	@Override
	protected int getVertexSpacing() {
		return VERTEX_SPACING;
	}
	
	/**
	 * Plots the specified graphs in a new created frame.
	 * 
	 * @param graphs Graphs of generation processes of serialisable extensions
	 * @param width Width of the new frame created.
	 * @param height Height of the new frame created.
	 * @param semanticsUsed Semantics of the extensions created in the graph to plot.
	 */
	public static void plotGraph(Collection<ContainerTransitionStateAnalysis> analyses, int width, int height) {
		int index = 0;
		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);
		
		for (ContainerTransitionStateAnalysis analysis : analyses) {
			analysis.setTitle(analysis.getTitle() + " Example " + index);
			plotGraph(analysis, groundPlotter);
			index++;
		}
		
		groundPlotter.show();
	}
	
	/**
	 * Plots the specified graph in a new created frame.
	 * 
	 * @param analysis Analysis of a generation process of serialisable extensions
	 * @param width Width of the new frame created.
	 * @param height Height of the new frame created.
	 */
	public static void plotGraph(ContainerTransitionStateAnalysis analysis, int width, int height) {
		
		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);
		
		plotGraph(analysis, groundPlotter);
		
		groundPlotter.show();
	}

	/**
	 * Plots the specified graph in the frame of the specified plotter 
	 * 
	 * @param analysis Analysis of a generation process of serialisable extensions
	 * @param groundPlotter Plotter, which creates the frame
	 */
	public static void plotGraph(ContainerTransitionStateAnalysis analysis, Plotter groundPlotter) {
		SerialisableExtensionPlotter sePlotter = new SerialisableExtensionPlotter(groundPlotter, analysis.getGraphResulting());
		sePlotter.createGraph();
		var lstLabels = new ArrayList<String>();
		lstLabels.add(analysis.getTitle());
		lstLabels.add(analysis.getSemanticsUsed().description());
		lstLabels.add("Found Extensions:");
		lstLabels.add(analysis.getExtensionsFound().toString());
		sePlotter.addLabels(lstLabels);
	}

}

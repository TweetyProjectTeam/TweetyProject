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
package org.tweetyproject.arg.dung.util;

import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.util.GraphPlotter;

import com.mxgraph.util.mxConstants;

import org.tweetyproject.graphs.Edge;

import java.util.ArrayList;
import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.*;

/**
 * This class represents a specialization of graph plotters used to visualize argumentation frameworks, 
 * also called @see org.tweetyproject.arg.dung.syntax.DungTheory.
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
 * This work was inspired by "org.tweetyproject.logics.bpm.plotting.BpmnModelPlotter"
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class DungTheoryPlotter extends GraphPlotter<Argument, Edge<Argument>> {

	private final String LABEL_DEFAULT_NODE = "N.A.";
	private final String LABEL_DEFAULT_EDGE = "";
	private final String STYLE_NODE_FILLCOLOR = "=lightblue;";
	private final String STYLE_NODE_ROUNDED = "=true;";
	private final int VERTEX_WIDTH = 30;
	private final int VERTEX_HEIGHT = 30;
	private final int VERTEX_SPACING = 100;
	private final int RANK_SPACING = 150;
	private final int FONTSIZE = 14;
	
	/**
	 * Creates a new instance for plotting an abstract argumentation framework, also called {@link DungTheory}
	 * 
	 * @param plotter Plotter, used to create a frame, which is then used by this plotter to visualize the argumentation framework
	 * @param argFramework abstract argumentation framework which is to be visualized
	 */
	public DungTheoryPlotter(Plotter plotter, DungTheory argFramework) {
		super(plotter, argFramework);
	}

	@Override
	protected String getPrettyName(Argument node) {
		String name = node.getName();
		if(name == null|| name.isEmpty()) {
			name = LABEL_DEFAULT_NODE;
		}
		return name;
	}

	@Override
	protected String getPrettyName(Edge<Argument> edge) {
		String label = edge.getLabel();
		if(label == null || label.isEmpty()) {
			label = LABEL_DEFAULT_EDGE;
		}
		return label;
	}

	@Override
	protected String getStyle(Argument node) {
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
	
	@Override
	protected void setVertexSpacing() {
		this.layout.setIntraCellSpacing(VERTEX_SPACING);
		this.layout.setInterRankCellSpacing(RANK_SPACING);
	}
	
	/**
	 * Plots the specified frameworks in a new created frame.
	 * 
	 * @param frameworks Argumentation framework to plot
	 * @param width Width of the new frame created.
	 * @param height Height of the new frame created.
	 */
	public static void plotFramework(Collection<DungTheory> frameworks, int width, int height) {
		int index = 0;
		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);
		
		for (DungTheory af : frameworks) {
			plotFramework(af, groundPlotter, "Graph " + index);
			index++;
		}
		
		groundPlotter.show();
	}
	
	/**
	 * Plots the specified framework in a new created frame.
	 * 
	 * @param framework Argumentation framework to plot
	 * @param width Width of the new frame created.
	 * @param height Height of the new frame created.
	 * @param titleGraph Title of the graph to be shown.
	 */
	public static void plotFramework(DungTheory framework, int width, int height, String titleGraph) {
		
		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);
		
		plotFramework(framework, groundPlotter, titleGraph);
		
		groundPlotter.show();
	}

	/**
	 * Plots the specified framework in the frame of the specified plotter.
	 * 
	 * @param framework Argumentation framework to plot
	 * @param groundPlotter Plotter, that creates the frame
	 * @param titleGraph Title of the graph to be shown.
	 */
	public static void plotFramework(DungTheory framework, Plotter groundPlotter, String titleGraph) {
		DungTheoryPlotter afPlotter = new DungTheoryPlotter(groundPlotter, framework);
		afPlotter.createGraph();
		var lstLabels = new ArrayList<String>();
		lstLabels.add(titleGraph);
		lstLabels.add("Argumentation Framework");
		//lstLabels.add(framework.toString());
		afPlotter.addLabels(lstLabels);
	}

}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.ContainerTransitionStateAnalysis;
import org.tweetyproject.arg.dung.serialisibility.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.SimpleGraph;
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
public class SerialisableExtensionPlotter extends GraphPlotter<TransitionStateNode, Edge<TransitionStateNode>> {

	/**
	 * Plots specified analyses of the serializing generation of extensions, each in a separate frame.
	 * @param analyses Analyses, which framework and graph should be plotted
	 * @param title Title, common to all specified analyses to plot
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static void plotAnalyses(ContainerTransitionStateAnalysis[] analyses, String title, int width, int height) {
		HashMap<DungTheory, ContainerTransitionStateAnalysis[]> convExamples = new HashMap<>();

		for (ContainerTransitionStateAnalysis analysis : analyses) {
			convExamples.put(analysis.getStateExamined().getTheory(), new ContainerTransitionStateAnalysis[] {analysis});
		}

		SerialisableExtensionPlotter.plotAnalyses(convExamples, title, width, height);
	}
	
	/**
	 * Plots specified frameworks and their associated analyses of the serializing generation of extensions
	 * for the specified semantics in one frame per framework.
	 * @param mapAFtoAnalyses Frameworks mapped to the associated analyses using different semantics
	 * @param title Title, common to all specified analyses to plot
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static void plotAnalyses(HashMap<DungTheory, ContainerTransitionStateAnalysis[]> mapAFtoAnalyses, String title, int width, int height) {
		int index = 0;
		for (DungTheory exampleFramework : mapAFtoAnalyses.keySet()) {
			Plotter groundPlotter = new Plotter();
			groundPlotter.createFrame(width, height);
			DungTheoryPlotter.plotFramework(exampleFramework, groundPlotter, "Example " + index);
			for (ContainerTransitionStateAnalysis analysis : mapAFtoAnalyses.get(exampleFramework)) {
				analysis.setTitle(analysis.getTitle() + " " + title + " " + index);
				SimpleGraph<TransitionStateNode> graph = analysis.getGraphResulting();
				SerialisableExtensionPlotter.plotAnalysis(analysis, groundPlotter);
			}
			groundPlotter.show();
			index++;
		}
	}
	
	/**
	 * Plots specified frameworks and their associated analyses of the serializing generation of extensions
	 * for the specified semantics in one frame per framework.
	 * @param semantics Semantics of the extension created
	 * @param frameworks Frameworks, for which the extensions should be found.
	 * @param title Title, common to all specified analyses to plot
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static void plotAnalyses(Semantics[] semantics, DungTheory[] frameworks, String title, int width, int height) {
		HashMap<DungTheory, ContainerTransitionStateAnalysis[]> convExamples = new HashMap<>();

		for (DungTheory example : frameworks) {
			ContainerTransitionStateAnalysis[] analyses = new ContainerTransitionStateAnalysis[semantics.length];
			for (int i = 0; i < analyses.length; i++) {
				analyses[i] = SerialisableExtensionReasonerWithAnalysis
						.getSerialisableReasonerForSemantics(semantics[i])
						.getModelsWithAnalysis(example);
			}
			convExamples.put(example, analyses);
		}

		SerialisableExtensionPlotter.plotAnalyses(convExamples, title, width, height);
	}

	/**
	 * Plots the specified analysis in a new created frame.
	 *
	 * @param analysis Analysis of a generation process of serialisable extensions
	 * @param width Width of the new frame created.
	 * @param height Height of the new frame created.
	 */
	public static void plotAnalysis(ContainerTransitionStateAnalysis analysis, int width, int height) {

		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);

		SerialisableExtensionPlotter.plotAnalysis(analysis, groundPlotter);

		groundPlotter.show();
	}
	
	/**
	 * Plots the specified analysis in the frame of the specified plotter
	 *
	 * @param analysis Analysis of a generation process of serialisable extensions
	 * @param groundPlotter Plotter, which creates the frame
	 */
	public static void plotAnalysis(ContainerTransitionStateAnalysis analysis, Plotter groundPlotter) {
		SerialisableExtensionPlotter sePlotter = new SerialisableExtensionPlotter(groundPlotter, analysis.getGraphResulting());
		sePlotter.createGraph();
		var lstLabels = new ArrayList<String>();
		lstLabels.add(analysis.getTitle());
		lstLabels.add(analysis.getSemanticsUsed().description());
		lstLabels.add("Found Extensions:");
		lstLabels.add(analysis.getExtensionsFound().toString());
		sePlotter.addLabels(lstLabels);
	}
	
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

	@Override
	protected String getStyle(TransitionStateNode node) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + this.FONTSIZE + ";";
		return  styleString
				+ mxConstants.STYLE_FILLCOLOR + this.STYLE_NODE_FILLCOLOR
				+ mxConstants.STYLE_ROUNDED + this.STYLE_NODE_ROUNDED;
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

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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.serialisability.util;

import java.util.List;

import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.util.GraphPlotter;
import org.tweetyproject.serialisability.SerialisationGraph;
import org.tweetyproject.serialisability.SerialisationState;
import org.tweetyproject.serialisability.SerialisationStep;

import com.mxgraph.util.mxConstants;

/**
 * Plots a {@link SerialisationGraph}. States are labeled by their model and
 * steps by their initial model; terminal states are highlighted.
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class SerialisationGraphPlotter<T, S> extends GraphPlotter<SerialisationState<T, S>, SerialisationStep<T, S>> {
	/** fill color of states */
	private static final String FILLCOLOR = "lightblue";
	/** fill color of terminal states */
	private static final String FILLCOLOR_TERMINAL = "lightgreen";
	/** width of a vertex */
	private static final int VERTEX_WIDTH = 90;
	/** height of a vertex */
	private static final int VERTEX_HEIGHT = 30;
	/** spacing between vertices */
	private static final int VERTEX_SPACING = 90;
	/** font size of labels */
	private static final int FONTSIZE = 10;

	/** the plotted graph */
	private final SerialisationGraph<T, S> graph;

	/**
	 * Creates a plotter for the given graph
	 * @param plotter the ground plotter
	 * @param graph   some serialisation graph
	 */
	public SerialisationGraphPlotter(Plotter plotter, SerialisationGraph<T, S> graph) {
		super(plotter, graph);
		this.graph = graph;
	}

	/**
	 * Plots the graph in a new frame
	 * @param <T>    the type of frameworks
	 * @param <S>    the type of models
	 * @param graph  some serialisation graph
	 * @param width  width of the frame
	 * @param height height of the frame
	 * @param title  title of the plot
	 */
	public static <T, S> void plotGraph(SerialisationGraph<T, S> graph, int width, int height, String title) {
		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(width, height);
		plotGraph(graph, groundPlotter, title);
		groundPlotter.show();
	}

	/**
	 * Plots the graph in the frame of the given plotter
	 * @param <T>           the type of frameworks
	 * @param <S>           the type of models
	 * @param graph         some serialisation graph
	 * @param groundPlotter the ground plotter
	 * @param title         title of the plot
	 */
	public static <T, S> void plotGraph(SerialisationGraph<T, S> graph, Plotter groundPlotter, String title) {
		SerialisationGraphPlotter<T, S> plotter = new SerialisationGraphPlotter<>(groundPlotter, graph);
		plotter.createGraph(false);
		plotter.addLabels(List.of(title, "Green: terminal states"));
	}

	@Override
	protected String getStyle(SerialisationState<T, S> state) {
		String fillColor = this.graph.isTerminal(state) ? FILLCOLOR_TERMINAL : FILLCOLOR;
		return mxConstants.STYLE_FONTSIZE + "=" + FONTSIZE + ";"
				+ mxConstants.STYLE_FILLCOLOR + "=" + fillColor + ";"
				+ mxConstants.STYLE_ROUNDED + "=true;";
	}

	@Override
	protected String getPrettyName(SerialisationStep<T, S> step) {
		return step.getLabel();
	}

	@Override
	protected String getPrettyName(SerialisationState<T, S> state) {
		return state.toString();
	}

	@Override
	protected int getFontSize() {
		return FONTSIZE;
	}

	@Override
	protected double getVertexHeight() {
		return VERTEX_HEIGHT;
	}

	@Override
	protected int getVertexSpacing() {
		return VERTEX_SPACING;
	}

	@Override
	protected double getVertexWidth() {
		return VERTEX_WIDTH;
	}
}

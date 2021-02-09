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
package org.tweetyproject.logics.petri.plotting;

import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.util.GraphPlotter;
import org.tweetyproject.logics.petri.syntax.Transition;
import org.tweetyproject.logics.petri.syntax.reachability_graph.Marking;
import org.tweetyproject.logics.petri.syntax.reachability_graph.MarkingEdge;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.math.probability.ProbabilityFunction;

import com.mxgraph.util.mxConstants;

/**
 * This class is for displaying instances of the ReachabilityGraph class graphically
 * @author Benedikt Knopp
 */
public class ReachabilityGraphPlotter extends GraphPlotter<Marking, MarkingEdge> {
	
	/**
	 * the probabilities of firing the transitions that correspond to the edges
	 */
	private ProbabilityFunction<MarkingEdge> edgeLabels;
	
	/**
	 * Create a new instance
	 * @param plotter the ground plotter
	 * @param graph the graph to plot
	 */
    public ReachabilityGraphPlotter(Plotter plotter, ReachabilityGraph graph) {
    	super(plotter, graph);
    	edgeLabels = graph.getProbabilityFunction();
    }

	@Override
	protected String getPrettyName(Marking marking) {
		return marking.getId();
	}

	@Override
	protected String getPrettyName(MarkingEdge edge) {
		Transition transition = edge.getTransition();
		double roundedProbability = Math.round(edgeLabels.get(edge).doubleValue() * 100) / 100d;
		return (transition != null ? transition.getName() : "{}")
			+ ", p(t)= " + roundedProbability;
	}

	@Override
	protected String getStyle(Marking marking) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + fontSize + ";"; 
		return styleString
			+ mxConstants.STYLE_FILLCOLOR + "=aquamarine;"
			+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_ELLIPSE;
	}


	@Override
	protected double getVertexWidth() {
		return 40;
	}

	@Override
	protected double getVertexHeight() {
		return 40;
	}

	@Override
	protected int getFontSize() {
		return 14;
	}

	@Override
	protected int getVertexSpacing() {
		return 300;
	}

}
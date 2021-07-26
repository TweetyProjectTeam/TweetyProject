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

import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.util.GraphPlotter;
import org.tweetyproject.logics.petri.syntax.Ark;
import org.tweetyproject.logics.petri.syntax.PetriNet;
import org.tweetyproject.logics.petri.syntax.PetriNetNode;
import org.tweetyproject.logics.petri.syntax.Place;
import org.tweetyproject.logics.petri.syntax.Transition;

import com.mxgraph.util.mxConstants;

/**
 * This class is for displaying instances of the PetriNet class graphically
 * @author Benedikt Knopp
 */
public class PetriNetPlotter extends GraphPlotter<PetriNetNode, Ark> {

    /**
     * Remember which labels are used to avoid naming conflicts
     */
    private Map<PetriNetNode, String> labels = new HashMap<>();
	
	/**
	 * Create a new instance for plotting the BpmnModel
     * 
     * @param plotter PetriNetPlotter
     * @param petriNet PetriNetPlotter 
     */
	public PetriNetPlotter(Plotter plotter, PetriNet petriNet) {
		super(plotter, petriNet);
	}
	
	/**
	 * @param node the node to retrieve the pretty name for
	 * @return the label of the node that will be displayed in the graph
	 */
	@Override
	protected String getPrettyName(PetriNetNode node) {
		final String nodeName = node.getName();
		long count = labels.values().stream().filter(label -> label.equals(nodeName)).count();
		String label = count == 0 ? nodeName : nodeName + String.valueOf(count+1);
		labels.put(node, label);
		return label;	
	}

	/**
	 * @param edge the ark to retrieve the pretty name for
	 * @return the label of the ark that will be displayed in the graph
	 */
	@Override
	protected String getPrettyName(Ark edge) {
		return "" + edge.getWeight();
	}

	/**
	 * @param node the node to be drawn
	 * @return a string formatted to carry style information for mxGraph cells
	 */
	@Override
	protected String getStyle(PetriNetNode node) {
		String styleString = "";
		styleString += mxConstants.STYLE_FONTSIZE + "=" + fontSize + ";"; 
		if(Place.class.isAssignableFrom(node.getClass())) {
			return  styleString
					+ mxConstants.STYLE_FILLCOLOR + "=white;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_ELLIPSE;
		}
		if(Transition.class.isAssignableFrom(node.getClass())) {
			return  styleString 
					+ mxConstants.STYLE_FILLCOLOR + "=white;"
					+ mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_RECTANGLE;
		}
		return styleString
				+ mxConstants.STYLE_FILLCOLOR + "=white;";
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
		return 12;
	}

	@Override
	protected int getVertexSpacing() {
		// TODO Auto-generated method stub
		return 80;
	}
	
}
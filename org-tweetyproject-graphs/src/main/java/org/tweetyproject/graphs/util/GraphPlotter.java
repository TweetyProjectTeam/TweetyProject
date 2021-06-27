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
package org.tweetyproject.graphs.util;

import java.awt.FlowLayout;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * A generic class for plotting graphs
 * @author Benedikt Knopp
 * @param <T> the node class of the graph which is to plot
 * @param <S> the edge class of the grpah which is to plot
 */
public abstract class GraphPlotter<T extends Node, S extends GeneralEdge<T>>  {
	
	/**
	 * the ground plotter which may accommodate multiple plots
	 */
	private Plotter plotter;
	/**
	 * the layout of the plot
	 */
	private mxHierarchicalLayout layout;
	/**
	 * a container to contain the plot
	 */
	private JPanel panel;
	/**
	 * an object to manage insertion of vertices and edges
	 */
	private final mxGraph graphPlot = new mxGraph();
	/**
	 * the nodes to be drawn
	 */
	private Collection<T> nodes;
	/**
	 * the edges to be drawn between the nodes
	 */
	private Collection<S> edges;

	/**
	 * the width of the nodes in the plot
	 */
	protected double vertexWidth;
	/**
	 * the height of the nodes in the plot
	 */
	protected double vertexHeight;
	/**
	 * horizontal spacing between vertices
	 * this property can be customized for readability
	 * due to varying length of edge labels
	 */
	protected int vertexSpacing = 50;
	/**
	 * the font size of labels in the plot
	 */
	protected int fontSize;
	
	/**
	 * Create a new instance
	 * @param plotter the ground plotter
	 * @param graph the graph to plot
	 */
	@SuppressWarnings("unchecked")
	public GraphPlotter(Plotter plotter, Graph<T> graph) {
		this.plotter = plotter;
		nodes = graph.getNodes();
		edges = (Collection<S>) graph.getEdges();
		vertexWidth = getVertexWidth();
		vertexHeight = getVertexHeight();
		fontSize = getFontSize();
		vertexSpacing = getVertexSpacing();
	}


	/**
	 * Parse the elements of the graph to visual elements and align them
	 * in a hierarchical (top-to-bottom) layout
	 */
	public void createGraph() {
		
		this.panel = new JPanel();
		int width = plotter.getFrame().getMaximumSize().width;
		int height = plotter.getFrame().getMaximumSize().height;
        panel.setSize(width, height);
	    
        Object parent = graphPlot.getDefaultParent();
        graphPlot.getModel().beginUpdate();

        Map<String, Object> vertexObjects = new HashMap<>();
        Map<Node, String> objectLabels = new HashMap<>();
        this.nodes.forEach(node -> {
        	String vertexName = getPrettyName(node);
        	String style = getStyle(node);
            Object vertexObject = graphPlot.insertVertex(parent, null, vertexName, 0, 0, vertexWidth, vertexHeight, style);
            vertexObjects.put(vertexName, vertexObject);
            objectLabels.put(node, vertexName);
        });
        edges.forEach(edge -> {
        	Node nodeA = ((Edge) edge).getNodeA();
        	Node nodeB = ((Edge) edge).getNodeB();
        	String vertexNameA = objectLabels.get(nodeA);
        	String vertexNameB = objectLabels.get(nodeB);
        	Object vertexObjectA = vertexObjects.get(vertexNameA);
        	Object vertexObjectB = vertexObjects.get(vertexNameB);
        	String edgeLabel = getPrettyName(edge);
        	graphPlot.insertEdge(parent, null, edgeLabel, vertexObjectA, vertexObjectB);
        });
        layout = new mxHierarchicalLayout(graphPlot);
        layout.setIntraCellSpacing(vertexSpacing);
        layout.execute(graphPlot.getDefaultParent());
        graphPlot.getModel().endUpdate();
        
        final mxGraphComponent graphComponent = new mxGraphComponent(graphPlot);
        int hgap = plotter.getHGap();
        int vgap = plotter.getVGap();
        panel.setLayout( new FlowLayout(FlowLayout.LEFT, hgap, vgap) );
        panel.add(graphComponent);
        plotter.add(panel);
	}
	
	/**
	 * Add some description to the panel
	 * @param labels some labels that will be aligned vertically
	 */
	public void addLabels(List<String> labels) {
		String labelHTML = "<html>";
		for(String label : labels ) {
			labelHTML += label + "<br>";
		}
		labelHTML += "</html>";
		this.panel.add( new JLabel(labelHTML), FlowLayout.CENTER );
	}
	
	/**
	 * Retrieve a pretty name for a node that will be displayed at the vertex
	 * @param node the node
	 * @return a pretty name
	 */
	protected abstract String getPrettyName(T node);
	/**
	 * Retrieve a pretty name for an edge that will be displayed at the vertex
	 * @param edge the edge
	 * @return a pretty name
	 */
	protected abstract String getPrettyName(S edge);
	/**
	 * Retrieve a string that describes the style of the vertex corresponding to the node
	 * according to the syntax of MX
	 * @param node the node
	 * @return a style string
	 */
	protected abstract String getStyle(T node);

	/**
	 * Get the width of a vertex
	 * @return the width
	 */
	protected abstract double getVertexWidth();
	/**
	 * Get the height of a vertex
	 * @return the height
	 */
	protected abstract double getVertexHeight();
	/**
	 * Get the font size of labels in the plot
	 * @return the font size
	 */
	protected abstract int getFontSize(); 
	/**
	 * Get the horizontal spacing between vertices
	 * @return the horizontal spacing
	 */
	protected abstract int getVertexSpacing();
	
}

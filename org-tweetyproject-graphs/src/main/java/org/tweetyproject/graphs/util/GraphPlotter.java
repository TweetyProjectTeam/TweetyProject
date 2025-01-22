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
import javax.swing.SwingConstants;

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
	protected mxHierarchicalLayout layout;
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
	protected Collection<T> nodes;
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
		this.nodes = graph.getNodes();
		this.edges = (Collection<S>) graph.getEdges();
		this.vertexWidth = this.getVertexWidth();
		this.vertexHeight = this.getVertexHeight();
		this.fontSize = this.getFontSize();
		this.vertexSpacing = this.getVertexSpacing();
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
	 * Parse the elements of the graph to visual elements and align them
	 * in a hierarchical (top-to-bottom) layout
	 */
	public void createGraph() {
		this.createGraph(true);
	}

	/**
	 * Parse the elements of the graph to visual elements and align them
	 * in a hierarchical layout in a specified orientation
	 * @param isVertical If TRUE layout of graph is "top-to-bottom", if FALSE, layout is "left-to-right"
	 */
	public void createGraph(boolean isVertical) {
		this.buildPanel();
		Object parent = this.getParentOfGraph();
		Map<String, Object> vertexObjects = new HashMap<>();
		Map<Node, String> objectLabels = new HashMap<>();
		this.insertNodesInGraph(parent, vertexObjects, objectLabels);
		this.insertEdgesInGraph(parent, vertexObjects, objectLabels);
		this.buildLayout(isVertical);
		this.buildGraph();
	}

	/**
	 * Get the font size of labels in the plot
	 * @return the font size
	 */
	protected abstract int getFontSize();


	/**
	 * Retrieve a pretty name for an edge that will be displayed at the vertex
	 * @param edge the edge
	 * @return a pretty name
	 */
	protected abstract String getPrettyName(S edge);


	/**
	 * Retrieve a pretty name for a node that will be displayed at the vertex
	 * @param node the node
	 * @return a pretty name
	 */
	protected abstract String getPrettyName(T node);


	/**
	 * Retrieve a string that describes the style of the vertex corresponding to the node
	 * according to the syntax of MX
	 * @param node the node
	 * @return a style string
	 */
	protected abstract String getStyle(T node);

	/**
	 * Get the height of a vertex
	 * @return the height
	 */
	protected abstract double getVertexHeight();

	/**
	 * Get the horizontal spacing between vertices
	 * @return the horizontal spacing
	 */
	protected abstract int getVertexSpacing();

	/**
	 * Get the width of a vertex
	 * @return the width
	 */
	protected abstract double getVertexWidth();

	/**
 * Builds the graph component by setting up the layout and adding it to the panel.
 * The method configures the panel layout with the horizontal and vertical gaps
 * provided by the plotter and adds the graph component to it.
 */
private void buildGraph() {
    final mxGraphComponent graphComponent = new mxGraphComponent(this.graphPlot);
    int hgap = this.plotter.getHGap();
    int vgap = this.plotter.getVGap();
    this.panel.setLayout(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
    this.panel.add(graphComponent);
    this.plotter.add(this.panel);
}

/**
 * Builds the layout of the graph depending on whether the layout should be vertical or horizontal.
 * If vertical, a NORTH-oriented hierarchical layout is used; otherwise, a WEST-oriented layout is applied.
 * The method also executes the layout and finalizes updates to the graph model.
 *
 * @param isVertical Determines whether the layout should be vertical (true) or horizontal (false).
 */
private void buildLayout(boolean isVertical) {
    if (isVertical) {
        this.layout = new mxHierarchicalLayout(this.graphPlot, SwingConstants.NORTH);
    } else {
        this.layout = new mxHierarchicalLayout(this.graphPlot, SwingConstants.WEST);
    }

    setVertexSpacing();
    this.layout.execute(this.graphPlot.getDefaultParent());
    this.graphPlot.getModel().endUpdate();
}

/**
 * Sets the spacing between vertices (nodes) in the graph layout.
 * This method configures the intra-cell spacing in the layout.
 */
protected void setVertexSpacing() {
    this.layout.setIntraCellSpacing(this.vertexSpacing);
}

/**
 * Builds the panel that contains the graph by initializing it and setting its size
 * based on the maximum size of the plotter's frame.
 */
private void buildPanel() {
    this.panel = new JPanel();
    int width = this.plotter.getFrame().getMaximumSize().width;
    int height = this.plotter.getFrame().getMaximumSize().height;
    this.panel.setSize(width, height);
}

/**
 * Retrieves the parent object of the graph, and begins an update process in the graph model.
 *
 * @return The parent object of the graph's default parent.
 */
private Object getParentOfGraph() {
    Object parent = this.graphPlot.getDefaultParent();
    this.graphPlot.getModel().beginUpdate();
    return parent;
}

/**
 * Inserts edges into the graph based on the provided parent object, vertex mappings,
 * and node labels. Each edge connects two nodes (vertices) in the graph,
 * and the edges are labeled accordingly.
 *
 * @param parent         The parent object of the graph to which the edges are added.
 * @param vertexObjects  A mapping of vertex names to their corresponding graph objects.
 * @param objectLabels   A mapping of nodes to their respective vertex labels.
 */
private void insertEdgesInGraph(Object parent, Map<String, Object> vertexObjects,
        Map<Node, String> objectLabels) {
    this.edges.forEach(edge -> {
        Node nodeA = ((Edge) edge).getNodeA();
        Node nodeB = ((Edge) edge).getNodeB();
        String vertexNameA = objectLabels.get(nodeA);
        String vertexNameB = objectLabels.get(nodeB);
        Object vertexObjectA = vertexObjects.get(vertexNameA);
        Object vertexObjectB = vertexObjects.get(vertexNameB);
        String edgeLabel = this.getPrettyName(edge);
        this.graphPlot.insertEdge(parent, null, edgeLabel, vertexObjectA, vertexObjectB);
    });
}

/**
 * Inserts a single node into the graph by adding it to the vertex and label mappings.
 *
 * @param vertexObjects  A mapping of vertex names to their corresponding graph objects.
 * @param objectLabels   A mapping of nodes to their respective vertex labels.
 * @param node           The node to be inserted into the graph.
 * @param vertexName     The label or name of the vertex.
 * @param vertexObject   The graphical object representing the vertex in the graph.
 */
private void insertNode(Map<String, Object> vertexObjects, Map<Node, String> objectLabels, T node,
        String vertexName, Object vertexObject) {
    vertexObjects.put(vertexName, vertexObject);
    objectLabels.put(node, vertexName);
}

/**
 * Inserts nodes into the graph by iterating over the list of nodes and adding
 * each one as a vertex in the graph. The nodes are styled and labeled accordingly.
 *
 * @param parent         The parent object of the graph to which the nodes are added.
 * @param vertexObjects  A mapping of vertex names to their corresponding graph objects.
 * @param objectLabels   A mapping of nodes to their respective vertex labels.
 */
private void insertNodesInGraph(Object parent, Map<String, Object> vertexObjects,
        Map<Node, String> objectLabels) {
    this.nodes.forEach(node -> {
        String vertexName = this.getPrettyName(node);
        String style = this.getStyle(node);
        Object vertexObject = this.graphPlot.insertVertex(parent, null, vertexName, 0, 0, this.vertexWidth, this.vertexHeight, style);
        this.insertNode(vertexObjects, objectLabels, node, vertexName, vertexObject);
    });
}
}

/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.graphs.util;

import org.tweetyproject.graphs.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements methods for displaying graphs via the aig-graph-component
 *
 * @see <a href="https://github.com/aig-hagen/aig_graph_component">AIG Graph
 *      Component</a>
 *
 * @author Lars Bengel
 */
public class AigGraphPlotter<G extends Graph<N>, N extends Node> {
    // General options
    /**
     * Enables LaTeX rendering for labels or formulas.
     * When true, LaTeX expressions will be rendered using a compatible renderer
     * (e.g., MathJax).
     */
    protected boolean enableLatex = true;

    /**
     * Enables zoom functionality in the graphical interface.
     * When true, users can zoom in and out of the graph view.
     */
    protected boolean toggleZoom = true;

    /**
     * Enables physics-based layout for graph nodes.
     * When true, nodes will move according to simulated physical forces (e.g.,
     * repulsion).
     */
    protected boolean toggleNodePhysics = true;

    /**
     * Fixes the distance between linked nodes if enabled.
     * When false, link lengths can vary; when true, all links maintain a fixed
     * distance.
     */
    protected boolean toggleFixedLinkDistance = false;

    /**
     * Enables interactive graph editing in the GUI.
     * When true, users can add, remove, or modify nodes and edges through the
     * interface.
     */
    protected boolean toggleGraphEditingInGUI = true;

    /**
     * Toggles the display of node labels.
     * When true, labels for each node will be shown.
     */
    protected boolean toggleNodeLabels = true;

    /**
     * Toggles the display of edge (link) labels.
     * When true, labels for each link/edge will be shown.
     */
    protected boolean toggleLinkLabels = false;

    /** internal storage of nodes */
    private Map<N, AigNode> nodes;
    /** internal storage of links */
    private Map<GeneralEdge<? extends N>, AigLink> links;

    /**
     * Initializes a new instance of the graph plotter for the given graph
     *
     * @param graph some graph
     */
    public AigGraphPlotter(G graph) {
        int id = 1;
        this.nodes = new HashMap<>();
        this.links = new HashMap<>();
        for (N node : graph.getNodes()) {
            if (enableLatex) {
                this.nodes.put(node, new AigNode(id++, convert(node.toString())));
            } else {
                this.nodes.put(node, new AigNode(id++, node));
            }
        }
        for (GeneralEdge<? extends N> edge : graph.getEdges()) {
            if (edge instanceof DirectedEdge<?>) {
                DirectedEdge<N> dirEdge = (DirectedEdge<N>) edge;
                links.put(edge, (new AigLink(nodes.get(dirEdge.getNodeA()), nodes.get(dirEdge.getNodeB()),
                        convert(dirEdge.getLabel()))));
            } else if (edge instanceof UndirectedEdge<?>) {
                throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
            } else
                throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
        }

    }

    /**
     * Creates a JSON-String for the given graph in the aig-graph format
     *
     * @param graph some graph
     * @return JSON-String representation of the given graph
     */
    public static String write(Graph<? extends Node> graph) {
        AigGraphPlotter<?, ?> plotter = new AigGraphPlotter<>(graph);
        return plotter.write();
    }

    /**
     * Creates a JSON-String for the graph in the aig-graph format
     *
     * @return JSON-String representation of the graph
     */
    public String write() {
        StringBuilder s = new StringBuilder();

        // Write Nodes
        s.append("{\nnodes: [\n");
        for (AigNode node : this.nodes.values()) {
            s.append(node.toJson()).append(",\n");
        }

        // Write Links
        s.append("],\nlinks: [\n");
        for (AigLink edge : links.values()) {
            s.append(edge.toJson()).append(",\n");
        }
        s.append("]\n}\n");
        return s.toString();
    }

    /**
     * ensures that the graph nodes are positioned in levels depending on their
     * distance to the given root node
     *
     * @param root origin node
     */
    public void makeLeveled(N root) {
        Graph<AigNode> graph = new DefaultGraph<>();
        for (AigNode node : this.nodes.values()) {
            graph.add(node);
        }
        for (AigLink link : this.links.values()) {
            graph.add(link);
        }
        Stack<AigNode> frontier = new Stack<>();
        frontier.push(this.nodes.get(root));
        int level = 0;
        int levelDistance = 300;
        while (!frontier.isEmpty()) {
            Collection<AigNode> states = new HashSet<>(frontier);
            frontier.clear();
            for (AigNode node : states) {
                frontier.addAll(graph.getChildren(node));
                node.setX(levelDistance + (level * levelDistance));
            }
            level++;
        }
        this.setNodeFixedPositionX(true);
    }

    /**
     * Renders the given graph in the web browser via the aig-graph-component
     *
     * @param graph some graph
     */
    public static void show(Graph<? extends Node> graph) {
        AigGraphPlotter<?, ?> plotter = new AigGraphPlotter<>(graph);
        plotter.show();
    }

    /**
     * Renders the graph in the web browser via the aig-graph-component
     */
    public void show() {
        Path outputPath = Paths.get("index.html");

        try {
            String template = Files.readString(Paths.get(getResource("aiggraph/graph.template")));
            String output = String.format(template,
                    toggleZoom, toggleNodePhysics, toggleFixedLinkDistance, toggleGraphEditingInGUI,
                    toggleNodeLabels, toggleLinkLabels,
                    write(),
                    getResource("aiggraph/favicon.ico"), getResource("aiggraph/style.css"),
                    getResource("aiggraph/load-mathjax.js"), getResource("aiggraph/graph-component.js"));

            Files.writeString(outputPath, output);

            // show graph in web browser
            File htmlFile = new File("index.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Allow incomming links */
    protected boolean nodeAllowIncomingLinks = true;
    /** Allow outgoing links */
    protected boolean nodeAllowOutgoingLinks = true;

    /**
     * Utility method that converts a String into a LaTeX-math string
     *
     * @param input some string containing LaTeX math-code
     * @return the converted string
     */
    public static String convert(String input) {
        if (input == null)
            return "";

        String latexMath = input.replace("{", "\\\\{").replace("}", "\\\\}");

        Pattern pattern = Pattern.compile("([a-zA-Z])(\\d+)"); // Match letter followed by number(s)
        Matcher matcher = pattern.matcher(latexMath);

        // Replace each match with the LaTeX subscript format
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String replacement = matcher.group(1) + "_{" + matcher.group(2) + "}";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        // Wrap the output in math mode delimiters
        return "$" + result + "$";
    }

    /**
     * Utility method to extract relative path to resource file
     *
     * @param name name of resource file
     * @return relative path to resource file
     */
    protected static String getResource(String name) {
        Path workingDir = Paths.get(System.getProperty("user.dir"));
        try {
            Path resourcePath = Paths
                    .get(Objects.requireNonNull(AigGraphPlotter.class.getClassLoader().getResource(name)).toURI());
            return workingDir.relativize(resourcePath).toString().replace("\\", "/");
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aktiviert oder deaktiviert die Verwendung von LaTeX zur Darstellung von
     * Formeln oder Text.
     *
     * @param enableLatex {@code true}, um LaTeX-Darstellung zu aktivieren;
     *                    {@code false}, um sie zu deaktivieren.
     */
    public void setEnableLatex(boolean enableLatex) {
        this.enableLatex = enableLatex;
    }

    /**
     * Aktiviert oder deaktiviert die Zoom-Funktionalität im Graph.
     *
     * @param toggleZoom {@code true}, um Zoom zu aktivieren; {@code false}, um Zoom
     *                   zu deaktivieren.
     */
    public void setToggleZoom(boolean toggleZoom) {
        this.toggleZoom = toggleZoom;
    }

    /**
     * Aktiviert oder deaktiviert die Physiksimulation für Knoten im Graphen.
     *
     * @param toggleNodePhysics {@code true}, um Physiksimulation zu aktivieren;
     *                          {@code false}, um sie zu deaktivieren.
     */
    public void setToggleNodePhysics(boolean toggleNodePhysics) {
        this.toggleNodePhysics = toggleNodePhysics;
    }

    /**
     * Aktiviert oder deaktiviert eine feste Kantenlänge im Graphenlayout.
     *
     * @param toggleFixedLinkDistance {@code true}, um eine feste Kantenlänge zu
     *                                verwenden; {@code false}, um sie dynamisch zu
     *                                lassen.
     */
    public void setToggleFixedLinkDistance(boolean toggleFixedLinkDistance) {
        this.toggleFixedLinkDistance = toggleFixedLinkDistance;
    }

    /**
     * Aktiviert oder deaktiviert die Möglichkeit zur Bearbeitung des Graphen über
     * die Benutzeroberfläche.
     *
     * @param toggleGraphEditingInGUI {@code true}, um die Bearbeitung im GUI zu
     *                                erlauben; {@code false}, um sie zu
     *                                deaktivieren.
     */
    public void setToggleGraphEditingInGUI(boolean toggleGraphEditingInGUI) {
        this.toggleGraphEditingInGUI = toggleGraphEditingInGUI;
    }

    /**
     * Aktiviert oder deaktiviert die Anzeige von Knotentexten im Graphen.
     *
     * @param toggleNodeLabels {@code true}, um Knotentexte anzuzeigen;
     *                         {@code false}, um sie auszublenden.
     */
    public void setToggleNodeLabels(boolean toggleNodeLabels) {
        this.toggleNodeLabels = toggleNodeLabels;
    }

    /**
     * Aktiviert oder deaktiviert die Anzeige von Kantentexten im Graphen.
     *
     * @param toggleLinkLabels {@code true}, um Kantentexte anzuzeigen;
     *                         {@code false}, um sie auszublenden.
     */
    public void setToggleLinkLabels(boolean toggleLinkLabels) {
        this.toggleLinkLabels = toggleLinkLabels;
    }

    /**
     * Sets whether the specified node is deletable.
     *
     * @param node the node whose deletable status is to be set
     * @param nodeDeletable {@code true} if the node should be deletable; {@code false} otherwise
     */
    public void setDeletable(N node, boolean nodeDeletable) {
        this.nodes.get(node).setDeletable(nodeDeletable);
    }

    /**
     * Sets the deletable status for all nodes in the graph.
     * <p>
     * This method iterates over all nodes and updates their deletable property
     * according to the specified value.
     * </p>
     *
     * @param nodeDeletable {@code true} if nodes should be deletable; {@code false} otherwise
     */
    public void setNodeDeletable(boolean nodeDeletable) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setDeletable(nodeDeletable);
        }
    }

    /**
     * Determines whether the specified node is deletable.
     *
     * @param node the node to check for deletability
     * @return {@code true} if the node is deletable; {@code false} otherwise
     */
    public boolean isDeletable(N node) {
        return this.nodes.get(node).isDeletable();
    }

    /**
     * Sets whether the label of the specified node is editable.
     *
     * @param node the node whose label editability is to be set
     * @param labelEditable {@code true} if the label should be editable; {@code false} otherwise
     */
    public void setLabelEditable(N node, boolean labelEditable) {
        this.nodes.get(node).setLabelEditable(labelEditable);
    }

    /**
     * Sets whether the labels of all nodes in the graph are editable.
     *
     * @param labelEditable {@code true} if node labels should be editable; {@code false} otherwise.
     */
    public void setNodeLabelEditable(boolean labelEditable) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setLabelEditable(labelEditable);
        }
    }

    /**
     * Determines whether the label of the specified node is editable.
     *
     * @param node the node whose label editability is to be checked
     * @return {@code true} if the label of the node is editable; {@code false} otherwise
     */
    public boolean isLabelEditable(N node) {
        return this.nodes.get(node).isLabelEditable();
    }

    /**
     * Sets whether the X position of the specified node should be fixed.
     *
     * @param node the node whose X position fixed state is to be set
     * @param fixedPositionX {@code true} to fix the X position of the node, {@code false} to allow it to move
     */
    public void setNodeFixedPositionX(N node, boolean fixedPositionX) {
        this.nodes.get(node).setFixedPositionX(fixedPositionX);
    }

    /**
     * Sets whether the X position of all nodes in the graph should be fixed.
     *
     * @param fixedPositionX {@code true} to fix the X position of all nodes, {@code false} to allow movement.
     */
    public void setNodeFixedPositionX(boolean fixedPositionX) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setFixedPositionX(fixedPositionX);
        }
    }

    /**
     * Checks whether the specified node has a fixed X position.
     *
     * @param node the node to check
     * @return {@code true} if the node has a fixed X position; {@code false} otherwise
     */
    public boolean isNodeFixedPositionX(N node) {
        return this.nodes.get(node).isFixedPositionX();
    }

    /**
     * Sets whether the Y position of the specified node should be fixed.
     *
     * @param node the node whose Y position fixed status is to be set
     * @param fixedPositionY {@code true} to fix the Y position of the node, {@code false} to allow it to move
     */
    public void setNodeFixedPositionY(N node, boolean fixedPositionY) {
        this.nodes.get(node).setFixedPositionY(fixedPositionY);
    }

    /**
     * Sets whether the Y position of all nodes in the graph should be fixed.
     *
     * @param fixedPositionY {@code true} to fix the Y position of all nodes, {@code false} to allow movement.
     */
    public void setNodeFixedPositionY(boolean fixedPositionY) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setFixedPositionY(fixedPositionY);
        }
    }

    /**
     * Checks whether the Y position of the specified node is fixed.
     *
     * @param node the node to check
     * @return {@code true} if the Y position of the node is fixed; {@code false} otherwise
     */
    public boolean isNodeFixedPositionY(N node) {
        return this.nodes.get(node).isFixedPositionY();
    }

    /**
     * Sets the X-coordinate position for the specified node in the graph.
     *
     * @param node the node whose X position is to be set
     * @param positionX the X-coordinate value to assign to the node
     */
    public void setNodePositionX(N node, int positionX) {
        this.nodes.get(node).setX(positionX);
    }

    /**
     * Sets the Y-coordinate position for the specified node in the graph.
     *
     * @param node the node whose Y position is to be set
     * @param positionY the Y-coordinate value to assign to the node
     */
    public void setNodePositionY(N node, int positionY) {
        this.nodes.get(node).setY(positionY);
    }

    /**
     * Sets whether the specified link (edge) in the graph is deletable.
     *
     * @param link the edge whose deletable status is to be set
     * @param linkDeletable {@code true} if the link should be deletable; {@code false} otherwise
     */
    public void setDeletable(GeneralEdge<? extends N> link, boolean linkDeletable) {
        this.links.get(link).setDeletable(linkDeletable);
    }

    /**
     * Sets the deletable status for all links in the graph.
     * <p>
     * This method iterates over all links and updates their deletable property
     * according to the specified value.
     * </p>
     *
     * @param linkDeletable {@code true} if the links should be deletable; {@code false} otherwise.
     */
    public void setLinkDeletable(boolean linkDeletable) {
        for (GeneralEdge<? extends N> node : links.keySet()) {
            this.links.get(node).setDeletable(linkDeletable);
        }
    }

    /**
     * Determines whether the specified edge can be deleted from the graph.
     *
     * @param link the edge to check for deletability
     * @return {@code true} if the edge is deletable; {@code false} otherwise
     */
    public boolean isDeletable(GeneralEdge<? extends N> link) {
        return this.links.get(link).isDeletable();
    }

    /**
     * Sets whether the label of the specified edge is editable.
     *
     * @param link the edge whose label editability is to be set
     * @param labelEditable {@code true} if the label should be editable; {@code false} otherwise
     */
    public void setLabelEditable(GeneralEdge<? extends N> link, boolean labelEditable) {
        this.links.get(link).setLabelEditable(labelEditable);
    }

    /**
     * Sets the editability of the labels for all links in the graph.
     *
     * @param labelEditable {@code true} if the link labels should be editable; {@code false} otherwise.
     */
    public void setLinkLabelEditable(boolean labelEditable) {
        for (GeneralEdge<? extends N> link : links.keySet()) {
            this.links.get(link).setLabelEditable(labelEditable);
        }
    }

    /**
     * Determines whether the label of the specified edge is editable.
     *
     * @param link the edge whose label editability is to be checked
     * @return {@code true} if the label of the given edge is editable; {@code false} otherwise
     */
    public boolean isLabelEditable(GeneralEdge<? extends N> link) {
        return this.links.get(link).isLabelEditable();
    }

    /**
     * Sets the color of the specified edge in the graph.
     *
     * @param link the edge whose color is to be set
     * @param color the color to assign to the edge
     */
    public void setColor(GeneralEdge<? extends N> link, String color) {
        this.links.get(link).setColor(color);
    }

    /**
     * Sets the color of the specified node in the graph.
     *
     * @param node the node whose color is to be set
     * @param color the color to assign to the node, represented as a string (e.g., "red", "#FF0000")
     */
    public void setColor(N node, String color) {
        this.nodes.get(node).setColor(color);
    }

    /**
     * Sets the color of all nodes in the graph to the specified color.
     *
     * @param color the color to set for all nodes
     */
    public void setNodeColor(String color) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setColor(color);
        }
    }

    /**
     * Sets the color of all links in the graph to the specified color.
     *
     * @param color the color to set for all links, represented as a string (e.g., a hex color code or color name)
     */
    public void setLinkColor(String color) {
        for (GeneralEdge<? extends N> link : links.keySet()) {
            this.links.get(link).setColor(color);
        }
    }

    /**
     * Returns the color associated with the specified edge in the graph.
     *
     * @param link the edge whose color is to be retrieved
     * @return the color of the given edge as a String
     */
    public String getColor(GeneralEdge<? extends N> link) {
        return this.links.get(link).getColor();
    }

    /**
     * Returns the color associated with the specified node.
     *
     * @param node the node whose color is to be retrieved
     * @return the color of the given node as a String
     */
    public String getColor(N node) {
        return this.nodes.get(node).getColor();
    }
}

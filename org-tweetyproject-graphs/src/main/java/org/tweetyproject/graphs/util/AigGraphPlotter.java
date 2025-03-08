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
 * @see <a href="https://github.com/aig-hagen/aig_graph_component">AIG Graph Component</a>
 *
 * @author Lars Bengel
 */
public class AigGraphPlotter<G extends Graph<N>, N extends Node> {
    // General options
    protected boolean enableLatex = true;
    protected boolean toggleZoom = true;
    protected boolean toggleNodePhysics = true;
    protected boolean toggleFixedLinkDistance = false;
    protected boolean toggleGraphEditingInGUI = true;
    protected boolean toggleNodeLabels = true;
    protected boolean toggleLinkLabels = false;

    /** internal storage of nodes */
    private Map<N,AigNode> nodes;
    /** internal storage of links */
    private Map<GeneralEdge<? extends N>,AigLink> links;

    /**
     * Initializes a new instance of the graph plotter for the given graph
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
                links.put(edge, (new AigLink(nodes.get(dirEdge.getNodeA()), nodes.get(dirEdge.getNodeB()), convert(dirEdge.getLabel()))));
            } else if (edge instanceof UndirectedEdge<?>) {
                throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
            } else throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
        }

    }

    /**
     * Creates a JSON-String for the given graph in the aig-graph format
     * @param graph some graph
     * @return JSON-String representation of the given graph
     */
    public static String write(Graph<? extends Node> graph) {
        AigGraphPlotter<?,?> plotter = new AigGraphPlotter<>(graph);
        return plotter.write();
    }

    /**
     * Creates a JSON-String for the graph in the aig-graph format
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
     * ensures that the graph nodes are positioned in levels depending on their distance to the given root node
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
     * @param graph some graph
     */
    public static void show(Graph<? extends Node> graph) {
        AigGraphPlotter<?,?> plotter = new AigGraphPlotter<>(graph);
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
                    getResource("aiggraph/load-mathjax.js"), getResource("aiggraph/graph-component.js")
            );

            Files.writeString(outputPath, output);

            // show graph in web browser
            File htmlFile = new File("index.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Node options
    protected boolean nodeAllowIncomingLinks = true;
    protected boolean nodeAllowOutgoingLinks = true;


    /**
     * Utility method that converts a String into a LaTeX-math string
     * @param input some string containing LaTeX math-code
     * @return the converted string
     */
    public static String convert(String input) {
        if (input == null) return "";

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
     * @param name name of resource file
     * @return relative path to resource file
     */
    protected static String getResource(String name) {
        Path workingDir = Paths.get(System.getProperty("user.dir"));
        try {
            Path resourcePath = Paths.get(Objects.requireNonNull(AigGraphPlotter.class.getClassLoader().getResource(name)).toURI());
            return workingDir.relativize(resourcePath).toString().replace("\\", "/");
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public void setEnableLatex(boolean enableLatex) {
        this.enableLatex = enableLatex;
    }

    public void setToggleZoom(boolean toggleZoom) {
        this.toggleZoom = toggleZoom;
    }

    public void setToggleNodePhysics(boolean toggleNodePhysics) {
        this.toggleNodePhysics = toggleNodePhysics;
    }

    public void setToggleFixedLinkDistance(boolean toggleFixedLinkDistance) {
        this.toggleFixedLinkDistance = toggleFixedLinkDistance;
    }

    public void setToggleGraphEditingInGUI(boolean toggleGraphEditingInGUI) {
        this.toggleGraphEditingInGUI = toggleGraphEditingInGUI;
    }

    public void setToggleNodeLabels(boolean toggleNodeLabels) {
        this.toggleNodeLabels = toggleNodeLabels;
    }

    public void setToggleLinkLabels(boolean toggleLinkLabels) {
        this.toggleLinkLabels = toggleLinkLabels;
    }

    public void setDeletable(N node, boolean nodeDeletable) {
        this.nodes.get(node).setDeletable(nodeDeletable);
    }

    public void setNodeDeletable(boolean nodeDeletable) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setDeletable(nodeDeletable);
        }
    }

    public boolean isDeletable(N node) {
        return this.nodes.get(node).isDeletable();
    }

    public void setLabelEditable(N node, boolean labelEditable) {
        this.nodes.get(node).setLabelEditable(labelEditable);
    }

    public void setNodeLabelEditable(boolean labelEditable) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setLabelEditable(labelEditable);
        }
    }

    public boolean isLabelEditable(N node) {
        return this.nodes.get(node).isLabelEditable();
    }

    public void setNodeFixedPositionX(N node, boolean fixedPositionX) {
        this.nodes.get(node).setFixedPositionX(fixedPositionX);
    }

    public void setNodeFixedPositionX(boolean fixedPositionX) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setFixedPositionX(fixedPositionX);
        }
    }

    public boolean isNodeFixedPositionX(N node) {
        return this.nodes.get(node).isFixedPositionX();
    }

    public void setNodeFixedPositionY(N node, boolean fixedPositionY) {
        this.nodes.get(node).setFixedPositionY(fixedPositionY);
    }

    public void setNodeFixedPositionY(boolean fixedPositionY) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setFixedPositionY(fixedPositionY);
        }
    }

    public boolean isNodeFixedPositionY(N node) {
        return this.nodes.get(node).isFixedPositionY();
    }

    public void setNodePositionX(N node, int positionX) {
        this.nodes.get(node).setX(positionX);
    }

    public void setNodePositionY(N node, int positionY) {
        this.nodes.get(node).setY(positionY);
    }

    public void setDeletable(GeneralEdge<? extends N> link, boolean linkDeletable) {
        this.links.get(link).setDeletable(linkDeletable);
    }

    public void setLinkDeletable(boolean linkDeletable) {
        for (GeneralEdge<? extends N> node : links.keySet()) {
            this.links.get(node).setDeletable(linkDeletable);
        }
    }

    public boolean isDeletable(GeneralEdge<? extends N> link) {
        return this.links.get(link).isDeletable();
    }

    public void setLabelEditable(GeneralEdge<? extends N> link, boolean labelEditable) {
        this.links.get(link).setLabelEditable(labelEditable);
    }

    public void setLinkLabelEditable(boolean labelEditable) {
        for (GeneralEdge<? extends N> link : links.keySet()) {
            this.links.get(link).setLabelEditable(labelEditable);
        }
    }

    public boolean isLabelEditable(GeneralEdge<? extends N> link) {
        return this.links.get(link).isLabelEditable();
    }

    public void setColor(GeneralEdge<? extends N> link, String color) {
        this.links.get(link).setColor(color);
    }

    public void setColor(N node, String color) {
        this.nodes.get(node).setColor(color);
    }

    public void setNodeColor(String color) {
        for (N node : nodes.keySet()) {
            this.nodes.get(node).setColor(color);
        }
    }

    public void setLinkColor(String color) {
        for (GeneralEdge<? extends N> link : links.keySet()) {
            this.links.get(link).setColor(color);
        }
    }

    public String getColor(GeneralEdge<? extends N> link) {
        return this.links.get(link).getColor();
    }

    public String getColor(N node) {
        return this.nodes.get(node).getColor();
    }
}

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
package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationState;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Writes a given graph into the AIG-Graph JSON-Format
 *
 * @author Lars Bengel
 */
public class AigGraphWriter {
    // General options
    protected boolean enableLatex = true;
    protected boolean toggleZoom = true;
    protected boolean toggleNodePhysics = true;
    protected boolean toggleFixedLinkDistance = false;
    protected boolean toggleGraphEditingInGUI = true;
    protected boolean toggleNodeLabels = true;
    protected boolean toggleLinkLabels = true;


    // Node options
    protected boolean nodeDeletable = true;
    protected boolean nodeLabelEditable = true;
    protected boolean nodeFixedPositionX = false;
    protected boolean nodeFixedPositionY = false;
    protected boolean nodeAllowIncomingLinks = true;
    protected boolean nodeAllowOutgoingLinks = true;

    // Link options
    protected boolean linkDeletable = true;
    protected boolean linkLabelEditable = true;

    // Style options
    protected String nodeColor;
    protected String linkColor;

    public void showSerialisation(DungTheory theory, Semantics semantics) {
        SerialisedExtensionReasoner reasoner = new SerialisedExtensionReasoner(semantics);

        SerialisationGraph graph = reasoner.getSerialisationGraph(theory);
        SerialisationState root = new SerialisationState(theory, new Extension<>(), reasoner.isTerminal(theory, new Extension<>()));

        showDoubleGraph(writeGraph(theory), writeLeveledGraph(graph, root));
    }

    public void showDoubleGraph(Graph<? extends Node> graph1, Graph<? extends Node> graph2) {
        showDoubleGraph(writeGraph(graph1), writeGraph(graph2));
    }

    public void showDoubleGraph(String graph1, String graph2) {
        Path outputPath = Paths.get("index.html");

        try {
            String template = Files.readString(Paths.get(getResource("aiggraph/serialisation.template")));
            String output = String.format(template,
                    graph1, graph2,
                    getResource("aiggraph/favicon.ico"), getResource("aiggraph/style.css"),
                    getResource("aiggraph/load-mathjax.js"), getResource("aiggraph/graph-component.js")
            );

            Files.writeString(outputPath, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeLeveledGraph(Graph<? extends Node> graph, Node root) {
        Graph<AigNode> aigGraph = convertGraph(graph);
        AigNode aigRoot = null;
        for (AigNode node : aigGraph) {
            if (node.equals(new AigNode(0, convert(root.toString())))) {
                aigRoot = node;
                break;
            }
        }
        return writeLeveledGraphInternal(aigGraph, aigRoot);
    }
    private String writeLeveledGraphInternal(Graph<AigNode> graph, AigNode root) {
        StringBuilder s = new StringBuilder();

        // Write Nodes
        s.append("{\nnodes: [\n");
        Stack<AigNode> frontier = new Stack<>();
        frontier.push(root);
        int level = 0;
        int levelDistance = 300;
        while (!frontier.isEmpty()) {
            Collection<AigNode> states = new HashSet<>(frontier);
            frontier.clear();
            for (AigNode node : states) {
                frontier.addAll(graph.getChildren(node));
                node.setX(levelDistance + (level * levelDistance));
                node.setColor(nodeColor);
                s.append(node.toJson(nodeDeletable, nodeLabelEditable, true, nodeFixedPositionY)).append(",\n");
            }
            level++;
        }

        // Write Links
        s.append("],\nlinks: [\n");
        for (GeneralEdge<? extends AigNode> edge : graph.getEdges()) {
            if (edge instanceof AigLink) {
                ((AigLink) edge).setColor(linkColor);
                s.append(((AigLink) edge).toJson(linkDeletable, linkLabelEditable)).append(",\n");
            }
        }

        s.append("]\n}\n");
        return s.toString();
    }

    /**
     * Show graph in graph tool in the web-browser
     * @param graph JSON-String of a graph
     */
    public void showGraph(String graph) {
        showGraphInternal(graph);
    }

    /**
     * Show graph in graph tool in the web-browser
     * @param graph some graph
     */
    public void showGraph(Graph<? extends Node> graph) {
        showGraph(writeGraphInternal(convertGraph(graph)));
    }

    /**
     * Show graph in graph tool in the web-browser
     * @param graph an aig graph
     */
    private void showGraphInternal(String graph) {
        Path outputPath = Paths.get("index.html");

        try {
            String template = Files.readString(Paths.get(getResource("aiggraph/graph.template")));
            String output = String.format(template,
                    toggleZoom, toggleNodePhysics, toggleFixedLinkDistance, toggleGraphEditingInGUI,
                    toggleNodeLabels, toggleLinkLabels,
                    graph,
                    getResource("aiggraph/favicon.ico"), getResource("aiggraph/style.css"),
                    getResource("aiggraph/load-mathjax.js"), getResource("aiggraph/graph-component.js")
            );

            Files.writeString(outputPath, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extract relative path to resource file
     * @param name name of resource file
     * @return relative path to resource file
     */
    private static String getResource(String name) {
        Path workingDir = Paths.get(System.getProperty("user.dir"));
        try {
            Path resourcePath = Paths.get(Objects.requireNonNull(AigGraphWriter.class.getClassLoader().getResource(name)).toURI());
            return workingDir.relativize(resourcePath).toString().replace("\\", "/");
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a graph into a JSON-string for the AIG graph tool
     * @param graph an aig graph
     * @return JSON-String of the given graph
     */
    private String writeGraphInternal(Graph<AigNode> graph) {
        StringBuilder s = new StringBuilder();

        // Write Nodes
        s.append("{\nnodes: [\n");
        for (AigNode node : graph.getNodes()) {
            node.setColor(nodeColor);
            s.append(node.toJson(nodeDeletable, nodeLabelEditable, nodeFixedPositionX, nodeFixedPositionY)).append(",\n");
        }

        // Write Links
        s.append("],\nlinks: [\n");
        for (GeneralEdge<? extends AigNode> edge : graph.getEdges()) {
            if (edge instanceof AigLink) {
                ((AigLink) edge).setColor(linkColor);
                s.append(((AigLink) edge).toJson(linkDeletable, linkLabelEditable)).append(",\n");
            }
        }

        s.append("]\n}\n");
        return s.toString();
    }

    /**
     * Writes a graph into a JSON-string for the AIG graph tool
     * @param graph some graph
     * @return JSON-String of the given graph
     */
    public String writeGraph(Graph<? extends Node> graph) {
        return writeGraphInternal(convertGraph(graph));
    }

    /**
     * Converts any graph into an aig graph
     * @param graph some graph
     * @return the corresponding aig graph
     */
    private Graph<AigNode> convertGraph(Graph<? extends Node> graph) {
        Map<Node,AigNode> nodeToInt = new HashMap<>();
        Graph<AigNode> aigGraph = new DefaultGraph<>();
        int id = 0;
        for (Node node : graph.getNodes()) {
            if (enableLatex) {
                nodeToInt.put(node, new AigNode(id++, convert(node.toString())));
            } else {
                nodeToInt.put(node, new AigNode(id++, node));
            }
            aigGraph.add(nodeToInt.get(node));
        }
        for (GeneralEdge<? extends Node> edge : graph.getEdges()) {
            if (edge instanceof DirectedEdge<?>) {
                DirectedEdge<?> dirEdge = (DirectedEdge<?>) edge;
                aigGraph.add(new AigLink(nodeToInt.get(dirEdge.getNodeA()), nodeToInt.get(dirEdge.getNodeB()), convert(dirEdge.getLabel())));
            } else if (edge instanceof UndirectedEdge<?>) {
                throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
            } else throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
        }
        return aigGraph;
    }

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
}

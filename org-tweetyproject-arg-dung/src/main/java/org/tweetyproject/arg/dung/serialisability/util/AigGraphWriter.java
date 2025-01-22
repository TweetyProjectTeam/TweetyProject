package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.graphs.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AigGraphWriter<G extends Graph<N>, N extends Node> {
    // General options
    protected boolean enableLatex = true;

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
                // TODO implement link color
                s.append(((AigLink) edge).toJson(linkDeletable, linkLabelEditable)).append(",\n");
            }
        }

        s.append("]\n}\n");
        return s.toString();
    }

    public String writeGraph(Graph<? extends Node> graph) {
        return writeGraphInternal(convertGraph(graph));
    }

    private Graph<AigNode> convertGraph(Graph<? extends Node> graph) {
        Map<Node,AigNode> nodeToInt = new HashMap<>();
        Graph<AigNode> aigGraph = new DefaultGraph<>();
        int id = 0;
        for (Node node : graph.getNodes()) {
            if (enableLatex) {
                nodeToInt.put(node, new AigNode(id++, ArgumentationLatexWriter.writeArgument(node.toString())));
            } else {
                nodeToInt.put(node, new AigNode(id++, node));
            }
            aigGraph.add(nodeToInt.get(node));
        }
        for (GeneralEdge<? extends Node> edge : graph.getEdges()) {
            if (edge instanceof DirectedEdge<?>) {
                DirectedEdge<?> dirEdge = (DirectedEdge<?>) edge;
                aigGraph.add(new AigLink(nodeToInt.get(dirEdge.getNodeA()), nodeToInt.get(dirEdge.getNodeB()), dirEdge.getLabel())); // TODO edge label latex
            } else if (edge instanceof UndirectedEdge<?>) {
                throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
            } else throw new IllegalArgumentException("AigGraphWriter currently only supports directed graphs.");
        }
        return aigGraph;
    }
}

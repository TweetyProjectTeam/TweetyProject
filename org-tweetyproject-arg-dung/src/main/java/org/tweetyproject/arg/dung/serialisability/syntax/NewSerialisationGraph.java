package org.tweetyproject.arg.dung.serialisability.syntax;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.DirectedEdge;
import org.tweetyproject.graphs.Edge;

import java.util.Collection;
import java.util.HashSet;

public class NewSerialisationGraph extends DefaultGraph<SerialisationNode> {
    public NewSerialisationGraph(DungTheory theory, Collection<SerialisationSequence> sequences) {
        SerialisationNode root = new SerialisationNode(theory, new Extension<>());
        this.add(root);
        SerialisationNode predecessor = root;
        for (SerialisationSequence sequence: sequences) {
            Collection<Argument> ext = new HashSet<>();
            System.out.println(sequence);
            for (Collection<? extends Argument> set: sequence) {
                ext.addAll(set);
                SerialisationNode node = new SerialisationNode(theory.getReduct(ext), new Extension<>(ext));
                this.add(node);
                System.out.print(predecessor);
                System.out.print(" -> ");
                System.out.println(node);

                this.add(new DirectedEdge<>(predecessor, node));
                predecessor = node;
            }
            predecessor = root;
        }
    }

    public NewSerialisationGraph(DungTheory theory, Semantics semantics) {

    }

    /** Pretty print of the graph.
     * @return the pretty print of the graph.
     */
    public String prettyPrint(){
        StringBuilder output = new StringBuilder();
        for (SerialisationNode serialisationNode : this)
            output.append("node(").append(serialisationNode.toString()).append(").\n");
        output.append("\n");
        for (Edge<SerialisationNode> serialisationNodeEdge : this.getEdges())
            output.append("edge").append(serialisationNodeEdge.toString()).append(".\n");
        return output.toString();
    }
}

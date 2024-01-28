package org.tweetyproject.arg.dung.serialisability.semantics;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.DirectedEdge;
import org.tweetyproject.graphs.Edge;

import java.util.Collection;
import java.util.HashSet;

public class SerialisationGraph extends DefaultGraph<SerialisationNode> {

    /** semantics for this serialisation graph */
    private final Semantics semantics;

    /**
     * Construct the serialisation graph for the given argumentation framework and set of serialisation sequences
     * @param theory some argumentation framework
     * @param sequences the set of serialisation sequences
     */
    public SerialisationGraph(DungTheory theory, Collection<SerialisationSequence> sequences, Semantics semantics) {
        this.semantics = semantics;
        SerialisableExtensionReasoner reasoner = SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics);
        SerialisationNode root = new SerialisationNode(theory, new Extension<>(), reasoner.terminationFunction(theory, new Extension<>()));
        this.add(root);
        SerialisationNode predecessor = root;
        for (SerialisationSequence sequence: sequences) {
            Extension<DungTheory> ext = new Extension<>();
            for (Collection<? extends Argument> set: sequence) {
                ext.addAll(set);
                DungTheory reduct = theory.getReduct(ext);
                SerialisationNode node = new SerialisationNode(reduct, new Extension<>(ext), reasoner.terminationFunction(reduct, ext));
                this.add(node);
                this.add(new DirectedEdge<>(predecessor, node));
                predecessor = node;
            }
            predecessor = root;
        }
    }

    public SerialisationGraph(DungTheory theory, Semantics semantics) {
        this(theory, SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics).getSequences(theory), semantics);
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

    public Semantics getSemantics() {
        return this.semantics;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SerialisationGraph)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        SerialisationGraph other = (SerialisationGraph) obj;
        return super.equals(other);
    }

    /**
     * @return The set of extensions that have been found during the process shown by this graph.
     */
    public Collection<Extension<DungTheory>> getExtensions(){
        Collection<Extension<DungTheory>> result = new HashSet<>();
        for (SerialisationNode node: this) {
            if (node.isTerminal()) {
                result.add(node.getExtension());
            }
        }
        return result;
    }

    /**
     * @return All leaf-nodes of this graph.
     */
    public Collection<Extension<DungTheory>> getLeaves() {
        return null;
    }

    /**
     * Returns all sequences of sets of arguments from the root to the specified node
     * @param node Destination of the path
     * @return Sequences of sets, used to construct the extension, represented by the node
     */
    public Collection<SerialisationSequence> getSerialisationSequences(Extension<DungTheory> node) {
        // TODO implement
        return null;
    }

    private HashSet<Extension<DungTheory>> getLeavesRecursive(Extension<DungTheory> node){
        /*TODO fix
         var output = new HashSet<Extension<DungTheory>>();
        Collection<Extension<DungTheory>> children = this.getChildren(node);

        if(children.isEmpty()) {
            output.add(node); // leaf found
        }
        else {
            for (Extension<DungTheory> child : children) {
                output.addAll(this.getLeavesRecursive(child));
            }
        }

        return output;*/
        return null;
    }

    private Collection<SerialisationSequence> getSequencesBFSRecursive(
            Extension<DungTheory> targetNode,
            SerialisationSequence currentSequence) {
        /* TODO fix

        var output = new HashSet<SerialisationSequence>();
        var currentNode = this.getNode(currentSequence.getExtension());

        //[TERMINATION CONDITION]
        if(currentNode.equals(targetNode)) {
            output.add(currentSequence);
            return output;
        }

        for (SerialisationNode child : this.getChildren(currentNode)) {

            var childSequence = new SerialisationSequence(currentSequence);
            childSequence.add(this.subtract(child, currentNode));

            //[RECURSIVE CALL]
            output.addAll(this.getSequencesBFSRecursive(targetNode, childSequence));
        }
        return output;

         */
        return null;
    }

    /**
     * @param superSet Node of the graph.
     * @param subSet Node of the graph.
     * @return All elements of nodeA, which are not contained in nodeB.
     */
    private Extension<DungTheory> subtract(SerialisationNode superSet, Extension<DungTheory> subSet){
        var output = new Extension<DungTheory>();
        output.addAll(superSet.getExtension());
        output.removeAll(subSet);
        return output;
    }
}

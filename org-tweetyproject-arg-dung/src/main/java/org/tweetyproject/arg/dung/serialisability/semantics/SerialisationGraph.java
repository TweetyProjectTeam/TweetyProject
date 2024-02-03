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
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.semantics;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;
import org.tweetyproject.math.matrix.Matrix;

import java.util.*;

/**
 * A Graph-based representation of the {@link SerialisationSequence Serialisation Sequences}
 * of some {@link DungTheory Argumentation Framework} wrt. some {@link Semantics}.
 *
 * @author Lars Bengel
 * @author Julian Sander
 */
public class SerialisationGraph implements Graph<SerialisationNode> {

    /** semantics for this serialisation graph */
    private final Semantics semantics;
    /** explicit storage of parents for each node */
    private Map<SerialisationNode, Set<SerialisationNode>> parents = new HashMap<>();
    /** explicit storage of children for each node */
    private Map<SerialisationNode, Set<SerialisationNode>> children = new HashMap<>();


    /**
     * Construct a serialisation graph for the given argumentation framework and set of serialisation sequences
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

    /**
     * Construct a serialisation graph for the given argumentation framework and semantics
     * @param theory some argumentation framework
     * @param semantics some semantics
     */
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
        for (GeneralEdge<? extends SerialisationNode> serialisationNodeEdge : this.getEdges())
            output.append("edge").append(serialisationNodeEdge.toString()).append(".\n");
        return output.toString();
    }

    /**
     * Return the semantics of this serialisation graph
     * @return the semantics for which the serialisation graph has been constructed
     */
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
     * Return the extension that the represented serialisation sequences correspond to
     * @return The set of extensions
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
     * Return the node of this graph that corresponds to the given extension
     * @param extension some extension
     * @return the node corresponding to the extension
     */
    public SerialisationNode getNodeForExtension(Extension<DungTheory> extension) {
        for (SerialisationNode node: this) {
            if (node.getExtension().equals(extension))
                return node;
        }
        return null;
    }

    @Override
    public GeneralGraph<SerialisationNode> getRestriction(Collection<SerialisationNode> nodes) {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public boolean add(SerialisationNode node) {
        if (this.parents.containsKey(node)) return false;
        this.parents.put(node, new HashSet<>());
        this.children.put(node, new HashSet<>());
        return true;
    }

    @Override
    public boolean add(GeneralEdge<SerialisationNode> edge) {
        boolean result = false;
        DirectedEdge<SerialisationNode> e = (DirectedEdge<SerialisationNode>) edge;
        result |= this.add(e.getNodeA());
        result |= this.add(e.getNodeB());
        result |= children.get(e.getNodeA()).add(e.getNodeB());
        result |= parents.get(e.getNodeB()).add(e.getNodeA());
        return result;
    }

    @Override
    public Collection<SerialisationNode> getNodes() {
        return this.parents.keySet();
    }

    @Override
    public int getNumberOfNodes() {
        return this.parents.keySet().size();
    }

    @Override
    public boolean areAdjacent(SerialisationNode a, SerialisationNode b) {
        return this.parents.get(a).contains(b) || this.parents.get(b).contains(a);
    }

    @Override
    public GeneralEdge<SerialisationNode> getEdge(SerialisationNode a, SerialisationNode b) {
        return new DirectedEdge<>(a, b);
    }

    @Override
    public Collection<? extends GeneralEdge<? extends SerialisationNode>> getEdges() {
        Collection<DirectedEdge<SerialisationNode>> edges = new HashSet<>();
        for (SerialisationNode node: this) {
            for (SerialisationNode succ: this.children.get(node)) {
                DirectedEdge<SerialisationNode> edge = new DirectedEdge<>(node, succ);
                edges.add(edge);
            }
        }
        return edges;
    }

    @Override
    public Iterator<SerialisationNode> iterator() {
        return this.parents.keySet().iterator();
    }

    @Override
    public boolean contains(Object obj) {
        if (obj instanceof SerialisationNode) {
            return this.parents.containsKey(obj);
        }
        return false;
    }

    @Override
    public Collection<SerialisationNode> getChildren(Node node) {
        return this.children.get((SerialisationNode) node);
    }

    @Override
    public Collection<SerialisationNode> getParents(Node node) {
        return this.parents.get((SerialisationNode) node);
    }

    @Override
    public boolean existsDirectedPath(SerialisationNode node1, SerialisationNode node2) {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public Collection<SerialisationNode> getNeighbors(SerialisationNode node) {
        Collection<SerialisationNode> neighbors = new HashSet<>(this.getChildren(node));
        neighbors.addAll(this.getParents(node));
        return neighbors;
    }

    @Override
    public Matrix getAdjacencyMatrix() {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public Graph<SerialisationNode> getComplementGraph(int selfloops) {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public Collection<Collection<SerialisationNode>> getStronglyConnectedComponents() {
        return new HashSet<>();
    }

    @Override
    public Collection<Graph<SerialisationNode>> getSubgraphs() {
        Collection<Graph<SerialisationNode>> subgraphs = new HashSet<>();
        subgraphs.add(this);
        return subgraphs;
    }

    @Override
    public boolean hasSelfLoops() {
        return false;
    }

    @Override
    public boolean isWeightedGraph() {
        return false;
    }
}

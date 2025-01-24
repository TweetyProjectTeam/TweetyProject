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

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
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
 * @author Matthias Thimm
 */
public class SerialisationGraph implements Graph<SerialisationState> {

    /** semantics for this serialisation graph */
    private final Semantics semantics;
    /** explicit storage of parents for each node */
    private Map<SerialisationState, Set<SerialisationState>> parents = new HashMap<>();
    /** explicit storage of children for each node */
    private Map<SerialisationState, Set<SerialisationState>> children = new HashMap<>();

    private Collection<GeneralEdge<SerialisationState>> edges = new HashSet<>();


    /**
     * Construct a serialisation graph for the given argumentation framework and serialisation reasoner
     * @param theory some argumentation framework
     * @param reasoner some serialised reasoner
     */
    public SerialisationGraph(DungTheory theory, SerialisedExtensionReasoner reasoner) {
        Collection<SerialisationSequence> sequences = reasoner.getSequences(theory);
        this.semantics = reasoner.getSemantics();
        SerialisationState root = new SerialisationState(theory, new Extension<>(), reasoner.isTerminal(theory, new Extension<>()));
        this.add(root);
        SerialisationState predecessor = root;
        for (SerialisationSequence sequence: sequences) {
            Extension<DungTheory> ext = new Extension<>();
            for (Collection<? extends Argument> set: sequence) {
                ext.addAll(set);
                DungTheory reduct = theory.getReduct(ext);
                SerialisationState node = new SerialisationState(reduct, new Extension<>(ext), reasoner.isTerminal(reduct, ext));
                this.add(node);
                this.add(new DirectedEdge<>(predecessor, node, set.toString()));
                predecessor = node;
            }
            predecessor = root;
        }
    }

    /**
     * Construct a serialisation graph for the given argumentation framework and semantics
     * @param theory some argumentation framework
     * @param semantics some serialisable semantics
     */
    public SerialisationGraph(DungTheory theory, Semantics semantics) {
        this(theory, new SerialisedExtensionReasoner(semantics));
    }

    /** Pretty print of the graph.
     * @return the pretty print of the graph.
     */
    public String prettyPrint() {
        StringBuilder output = new StringBuilder();
        for (SerialisationState serialisationNode : this)
            output.append("node(").append(serialisationNode.toString()).append(").\n");
        output.append("\n");
        for (GeneralEdge<? extends SerialisationState> serialisationNodeEdge : this.getEdges())
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
        for (SerialisationState node: this) {
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
    public SerialisationState getNodeForExtension(Extension<DungTheory> extension) {
        for (SerialisationState node: this) {
            if (node.getExtension().equals(extension))
                return node;
        }
        return null;
    }

    @Override
    public GeneralGraph<SerialisationState> getRestriction(Collection<SerialisationState> nodes) {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public boolean add(SerialisationState node) {
        if (this.parents.containsKey(node)) return false;
        this.parents.put(node, new HashSet<>());
        this.children.put(node, new HashSet<>());
        return true;
    }

    @Override
    public boolean add(GeneralEdge<SerialisationState> edge) {
        boolean result = false;
        DirectedEdge<SerialisationState> e = (DirectedEdge<SerialisationState>) edge;
        result |= this.add(e.getNodeA());
        result |= this.add(e.getNodeB());
        result |= children.get(e.getNodeA()).add(e.getNodeB());
        result |= parents.get(e.getNodeB()).add(e.getNodeA());
        result |= edges.add(e);
        return result;
    }

    @Override
    public Collection<SerialisationState> getNodes() {
        return this.parents.keySet();
    }

    @Override
    public int getNumberOfNodes() {
        return this.parents.keySet().size();
    }

    @Override
	public int getNumberOfEdges() {
    	int num = 0;
		for(SerialisationState a: this.parents.keySet())
			num += this.parents.get(a).size();
		return num;
	}

    @Override
    public boolean areAdjacent(SerialisationState a, SerialisationState b) {
        return this.parents.get(a).contains(b) || this.parents.get(b).contains(a);
    }

    @Override
    public GeneralEdge<SerialisationState> getEdge(SerialisationState a, SerialisationState b) {
        for (GeneralEdge<SerialisationState> edge : edges) {
            DirectedEdge<SerialisationState> e = (DirectedEdge<SerialisationState>) edge;
            if (e.getNodeA().equals(a) && e.getNodeB().equals(b)) {
                return new DirectedEdge<>(e.getNodeA(), e.getNodeB(), e.getLabel());
            }
        }
        return null;
    }

    @Override
    public Collection<? extends GeneralEdge<? extends SerialisationState>> getEdges() {
        return new HashSet<>(this.edges);
    }

    @Override
    public Iterator<SerialisationState> iterator() {
        return this.parents.keySet().iterator();
    }

    @Override
    public boolean contains(Object obj) {
        if (obj instanceof SerialisationState) {
            return this.parents.containsKey(obj);
        }
        return false;
    }

    @Override
    public Collection<SerialisationState> getChildren(Node node) {
        return this.children.get((SerialisationState) node);
    }

    @Override
    public Collection<SerialisationState> getParents(Node node) {
        return this.parents.get((SerialisationState) node);
    }

    @Override
    public boolean existsDirectedPath(SerialisationState node1, SerialisationState node2) {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public Collection<SerialisationState> getNeighbors(SerialisationState node) {
        Collection<SerialisationState> neighbors = new HashSet<>(this.getChildren(node));
        neighbors.addAll(this.getParents(node));
        return neighbors;
    }

    @Override
    public Matrix getAdjacencyMatrix() {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public Graph<SerialisationState> getComplementGraph(int selfloops) {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public Collection<Collection<SerialisationState>> getConnectedComponents() {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }
    
    @Override
    public Collection<Collection<SerialisationState>> getStronglyConnectedComponents() {
        throw new UnsupportedOperationException("Operation not supported for serialisation graphs");
    }

    @Override
    public Collection<Graph<SerialisationState>> getSubgraphs() {
        Collection<Graph<SerialisationState>> subgraphs = new HashSet<>();
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

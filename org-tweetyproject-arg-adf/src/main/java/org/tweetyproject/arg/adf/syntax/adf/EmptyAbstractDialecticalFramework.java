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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.syntax.adf;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.transform.Transformer;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.GeneralGraph;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.math.matrix.Matrix;

/**
 * An ADF without arguments, acceptance conditions or links.
 *
 * @author Mathias Hofer
 * @author Lars Bengel
 */
enum EmptyAbstractDialecticalFramework implements AbstractDialecticalFramework {
    INSTANCE;

    @Override
    public Set<Argument> getArguments() {
        return Set.of();
    }

    @Override
    public Set<Link> links() {
        return Set.of();
    }

    @Override
    public Link link(Argument parent, Argument child) {
        throw new IllegalArgumentException();
    }

    @Override
    public Set<Link> linksTo(Argument child) {
        return Set.of();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Set<Link> linksFrom(Argument parent) {
        return Set.of();
    }

    @Override
    public Set<Argument> parents(Argument child) {
        return Set.of();
    }

    @Override
    public Set<Argument> children(Argument parent) {
        return Set.of();
    }

    @Override
    public int kBipolar() {
        return 0;
    }

    @Override
    public AcceptanceCondition getAcceptanceCondition(Argument argument) {
        throw new IllegalArgumentException();
    }

    @Override
    public int incomingDegree(Argument arg) {
        return 0;
    }

    @Override
    public int outgoingDegree(Argument arg) {
        return 0;
    }

    @Override
    public boolean contains(Argument arg) {
        return false;
    }
    @Override
    public AbstractDialecticalFramework transform(Function<AcceptanceCondition, AcceptanceCondition> transformer) {
        return this;
    }

    @Override
    public AbstractDialecticalFramework transform(BiFunction<Argument, AcceptanceCondition, AcceptanceCondition> transformer) {
        return this;
    }

    @Override
    public AbstractDialecticalFramework transform(Transformer<AcceptanceCondition> transformer) {
        return this;
    }

    @Override
    public boolean add(Argument node) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean isAttacked(Argument a, Extension<? extends ArgumentationFramework<?>> ext) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Argument> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean add(GeneralEdge<Argument> edge) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public GeneralGraph<Argument> getRestriction(Collection<Argument> nodes) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Collection<Argument> getNodes() {
        return List.of();
    }

    @Override
    public int getNumberOfNodes() {
        return 0;
    }

    @Override
    public int getNumberOfEdges() {
        return 0;
    }

    @Override
    public boolean areAdjacent(Argument a, Argument b) {
        return false;
    }

    @Override
    public GeneralEdge<Argument> getEdge(Argument a, Argument b) {
        return null;
    }

    @Override
    public Collection<? extends GeneralEdge<? extends Argument>> getEdges() {
        return List.of();
    }

    @Override
    public Iterator<Argument> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean contains(Object obj) {
        return false;
    }

    @Override
    public Collection<Argument> getChildren(Node node) {
        return List.of();
    }

    @Override
    public Collection<Argument> getParents(Node node) {
        return List.of();
    }

    @Override
    public boolean existsDirectedPath(Argument node1, Argument node2) {
        return false;
    }

    @Override
    public Collection<Argument> getNeighbors(Argument node) {
        return List.of();
    }

    @Override
    public Matrix getAdjacencyMatrix() {
        return null;
    }

    @Override
    public Graph<Argument> getComplementGraph(int selfloops) {
        return null;
    }

    
    @Override
    public Collection<Collection<Argument>> getConnectedComponents() {
        return List.of();
    }
    
    @Override
    public Collection<Collection<Argument>> getStronglyConnectedComponents() {
        return List.of();
    }

    @Override
    public Collection<Graph<Argument>> getSubgraphs() {
        return List.of();
    }

    @Override
    public boolean hasSelfLoops() {
        return false;
    }

    @Override
    public boolean isWeightedGraph() {
        return false;
    }

    @Override
    public Signature getMinimalSignature() {
        throw new UnsupportedOperationException("Unsupported");
    }
}

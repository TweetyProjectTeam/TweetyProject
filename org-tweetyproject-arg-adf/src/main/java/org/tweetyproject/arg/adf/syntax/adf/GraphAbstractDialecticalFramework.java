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

import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.semantics.link.SimpleLink;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.transform.Transformer;
import org.tweetyproject.arg.adf.util.AbstractUnmodifiableCollection;
import org.tweetyproject.arg.adf.util.LazyMap;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.GeneralGraph;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.math.matrix.Matrix;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Internally represented as a graph-like structure. This allows for efficient
 * traversal at the cost of a bit more memory overhead, since every argument
 * maintains references to its parents and children.
 *
 * @author Mathias Hofer
 * @author Lars Bengel
 */
final class GraphAbstractDialecticalFramework implements AbstractDialecticalFramework {

    private final Map<Argument, Node> index;

    private transient int k = -1;

    private final LinkStrategy linkStrategy;

    private GraphAbstractDialecticalFramework(Builder builder) {
        this.linkStrategy = builder.linkStrategy;
        this.index = new HashMap<>(builder.arguments.size());

        // create nodes
        for (Entry<Argument, AcceptanceCondition> entry : builder.arguments.entrySet()) {
            index.put(entry.getKey(), new Node(entry.getKey(), entry.getValue()));
        }

        // create edges
        for (Entry<Argument, Node> entry : index.entrySet()) {
            Node child = entry.getValue();

            Collection<Link> links = builder.linksTo(child.arg);
            for (Link link : links) {
                Argument parent = link.getFrom();
                child.parents.put(parent, link);
                node(parent).children.put(child.arg, link);
            }

            // add the missing keys for the lazy map
            child.acc.arguments().forEach(parent -> {
                child.parents.putIfAbsent(parent, null);
                node(parent).children.putIfAbsent(child.arg, null);
            });
        }
    }

    @Override
    public AbstractDialecticalFramework transform(Function<AcceptanceCondition, AcceptanceCondition> transformer) {
        Builder builder = new Builder();
        for (Argument arg : index.keySet()) {
            AcceptanceCondition acc = getAcceptanceCondition(arg);
            builder.add(arg, transformer.apply(acc));
        }
        return builder.lazy(linkStrategy).build();
    }

    @Override
    public AbstractDialecticalFramework transform(Transformer<AcceptanceCondition> transformer) {
        Builder builder = new Builder();
        for (Argument arg : index.keySet()) {
            AcceptanceCondition acc = getAcceptanceCondition(arg);
            builder.add(arg, transformer.transform(acc));
        }
        return builder.lazy(linkStrategy).build();
    }

    @Override
    public AbstractDialecticalFramework transform(BiFunction<Argument, AcceptanceCondition, AcceptanceCondition> transformer) {
        Builder builder = new Builder();
        for (Argument arg : index.keySet()) {
            AcceptanceCondition acc = getAcceptanceCondition(arg);
            builder.add(arg, transformer.apply(arg, acc));
        }
        return builder.lazy(linkStrategy).build();
    }

    @Override
    public Set<Argument> getArguments() {
        return Collections.unmodifiableSet(index.keySet());
    }

    @Override
    public int size() {
        return index.size();
    }

    @Override
    public boolean isEmpty() {
        return index.isEmpty();
    }

    @Override
    public Set<Link> links() {
        return linksStream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Stream<Link> linksStream() {
        return index.values()
                .stream()
                .flatMap(n -> n.parents.values().stream());
    }

    @Override
    public Link link(Argument parent, Argument child) {
        return node(child).parents.get(parent);
    }

    @Override
    public boolean contains(Argument arg) {
        return index.get(arg) != null;
    }

    @Override
    public Set<Link> linksTo(Argument child) {
        return new ParentSet(child, node(child).parents);
    }

    @Override
    public Set<Link> linksFrom(Argument parent) {
        return new ChildrenSet(parent, node(parent).children);
    }

    @Override
    public Set<Argument> parents(Argument child) {
        return Collections.unmodifiableSet(node(child).parents.keySet());
    }

    @Override
    public Set<Argument> children(Argument parent) {
        return Collections.unmodifiableSet(node(parent).children.keySet());
    }

    @Override
    public AcceptanceCondition getAcceptanceCondition(Argument argument) {
        return node(argument).acc;
    }

    @Override
    public int incomingDegree(Argument arg) {
        return node(arg).parents.size();
    }

    @Override
    public int outgoingDegree(Argument arg) {
        return node(arg).children.size();
    }

    /**
     * We do not want to return null values, therefore fail if we do not find the given argument.
     *
     * @param arg
     * @return
     */
    private Node node(Argument arg) {
        Node node = index.get(arg);
        if (node == null) {
            throw new IllegalArgumentException("Could not find Argument " + arg);
        }
        return node;
    }

    @Override
    public int kBipolar() {
        if (this.k < 0) {
            long count = linksStream()
                    .map(Link::getType)
                    .filter(LinkType::isNonBipolar)
                    .count();
            this.k = Math.toIntExact(count);
        }
        return k;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((index == null) ? 0 : index.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GraphAbstractDialecticalFramework)) {
            return false;
        }
        GraphAbstractDialecticalFramework other = (GraphAbstractDialecticalFramework) obj;
        return Objects.equals(index, other.index);
    }

    @Override
    public boolean add(Argument node) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.index.keySet().containsAll(c);
    }


    @Override
    public boolean addAll(Collection<? extends Argument> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported");
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
        return this;
    }

    @Override
    public int getNumberOfNodes() {
        return this.index.keySet().size();
    }

    @Override
    public int getNumberOfEdges() {
        return this.getEdges().size();
    }

    @Override
    public boolean areAdjacent(Argument a, Argument b) {
        return index.get(a).parents.containsKey(b) || index.get(a).children.containsKey(b);
    }

    @Override
    public GeneralEdge<Argument> getEdge(Argument a, Argument b) {
        if (index.get(a).children.containsKey(b)) {
            return new SimpleLink(a, b);
        }
        return null;
    }

    @Override
    public Collection<? extends GeneralEdge<? extends Argument>> getEdges() {
        Collection<SimpleLink> edges = new HashSet<>();
        for (Argument arg: this) {
            for (Argument parent: index.get(arg).parents.keySet()) {
                edges.add(new SimpleLink(parent, arg));
            }
        }
        return edges;
    }

    @Override
    public Iterator<Argument> iterator() {
        List<String> names = new ArrayList<>();
        Map<String, Argument> index = new HashMap<>();
        for (Argument arg: this.getArguments()) {
            names.add(arg.getName());
            index.put(arg.getName(), arg);
        }
        Collections.sort(names);
        Collection<Argument> sorted = new ArrayList<>();
        for (String name: names) {
            sorted.add(index.get(name));
        }
        return sorted.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.index.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.index.keySet().toArray(a);
    }

    @Override
    public boolean contains(Object obj) {
        return false;
    }

    @Override
    public Collection<Argument> getChildren(org.tweetyproject.graphs.Node node) {
        return Collections.unmodifiableSet(node((Argument) node).children.keySet());
    }

    @Override
    public Collection<Argument> getParents(org.tweetyproject.graphs.Node node) {
        return Collections.unmodifiableSet(node((Argument) node).parents.keySet());
    }

    @Override
    public boolean existsDirectedPath(Argument node1, Argument node2) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Collection<Argument> getNeighbors(Argument node) {
        Collection<Argument> neighbors = new HashSet<>(getChildren(node));
        neighbors.addAll(getParents(node));
        return neighbors;
    }

    @Override
    public Matrix getAdjacencyMatrix() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Graph<Argument> getComplementGraph(int selfloops) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Collection<Collection<Argument>> getConnectedComponents() {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public Collection<Collection<Argument>> getStronglyConnectedComponents() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Collection<Graph<Argument>> getSubgraphs() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean hasSelfLoops() {
        for (Argument arg: this) {
            if (index.get(arg).parents.containsKey(arg)) return true;
        }
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

    private final class Node {

        private final Argument arg;

        private final AcceptanceCondition acc;

        private final Map<Argument, Link> parents;

        private final Map<Argument, Link> children;

        Node(Argument arg, AcceptanceCondition acc) {
            this.arg = arg;
            this.acc = acc;
            this.parents = new LazyMap<>(this::computeIncoming);
            this.children = new LazyMap<>(this::computeOutgoing);
        }

        private Link computeOutgoing(Argument to) {
            LinkType type = linkStrategy.compute(arg, node(to).acc);
            return Link.of(arg, to, type);
        }

        private Link computeIncoming(Argument from) {
            LinkType type = linkStrategy.compute(from, acc);
            return Link.of(from, arg, type);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + acc.hashCode();
            result = prime * result + arg.hashCode();
            result = prime * result + children.keySet().hashCode();
            result = prime * result + parents.keySet().hashCode();
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Node)) {
                return false;
            }
            Node other = (Node) obj;
            return Objects.equals(acc, other.acc)
                    && Objects.equals(arg, other.arg)
                    && Objects.equals(children.keySet(), other.children.keySet())
                    && Objects.equals(parents.keySet(), other.parents.keySet());
        }
    }

    private static abstract class LinkSet extends AbstractUnmodifiableCollection<Link> implements Set<Link> {

        private final Collection<Link> edges;

        /**
         * By construction we expect no duplicates in <code>edges</code>, therefore the set wrapper should be safe.
         *
         * @param edges
         */
        public LinkSet(Collection<Link> edges) {
            this.edges = Collections.unmodifiableCollection(edges);
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<Link> iterator() {
            return edges.iterator();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return edges.size();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#isEmpty()
         */
        @Override
        public boolean isEmpty() {
            return edges.isEmpty();
        }

    }

    private static final class ChildrenSet extends LinkSet {

        private final Argument parent;

        private final Map<Argument, Link> children;

        /**
         * @param parent
         * @param children
         */
        public ChildrenSet(Argument parent, Map<Argument, Link> children) {
            super(children.values());
            this.parent = parent;
            this.children = children;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(Object o) {
            if (o instanceof Link) {
                Link link = (Link) o;
                if (parent.equals(link.getFrom())) {
                    Link child = children.get(link.getTo());
                    return child != null && child.equals(link);
                }
            }
            return false;
        }

    }

    private static final class ParentSet extends LinkSet {

        private final Argument child;

        private final Map<Argument, Link> parents;

        /**
         * @param child
         * @param parents
         */
        public ParentSet(Argument child, Map<Argument, Link> parents) {
            super(parents.values());
            this.child = child;
            this.parents = parents;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(Object o) {
            if (o instanceof Link) {
                Link link = (Link) o;
                if (child.equals(link.getTo())) {
                    Link child = parents.get(link.getFrom());
                    return child != null && child.equals(link);
                }
            }
            return false;
        }

    }

    static final class Builder extends AbstractBuilder {

        @Override
        public AbstractDialecticalFramework build() {
            return new GraphAbstractDialecticalFramework(this);
        }
    }

	@Override
	public boolean isAttacked(Argument a, Extension<? extends ArgumentationFramework<?>> ext) {
		throw new UnsupportedOperationException("Not supported");
	}

}

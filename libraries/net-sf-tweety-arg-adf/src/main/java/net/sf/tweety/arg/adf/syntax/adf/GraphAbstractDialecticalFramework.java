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
package net.sf.tweety.arg.adf.syntax.adf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.LinkType;
import net.sf.tweety.arg.adf.semantics.SimpleLink;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.util.AbstractUnmodifiableSet;
import net.sf.tweety.arg.adf.util.LazyMap;

/**
 * Internally represented as a graph-like structure. This allows for efficient queries most of the time.
 * 
 * @author Mathias Hofer
 *
 */
public final class GraphAbstractDialecticalFramework implements AbstractDialecticalFramework {

	private final Map<Argument, Node> index;

	private transient int k = -1;

	private final LinkStrategy linkStrategy;

	private GraphAbstractDialecticalFramework(GraphBuilder builder) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#
	 * getArguments()
	 */
	@Override
	public Set<Argument> getArguments() {
		return Collections.unmodifiableSet(index.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#getLinks()
	 */
	@Override
	public Set<Link> links() {
		return linksStream().collect(Collectors.toSet());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#linksStream()
	 */
	@Override
	public Stream<Link> linksStream() {
		return index.values()
				.stream()
				.flatMap(n -> n.parents.values().stream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#link(net.sf
	 * .tweety.arg.adf.syntax.Argument, net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Link link(Argument parent, Argument child) {
		return node(child).parents.get(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#contains(
	 * net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public boolean contains(Argument arg) {
		return index.get(arg) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#
	 * linksFromParents(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksTo(Argument child) {
		return new ParentSet(child, node(child).parents);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#
	 * linksToChildren(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksFrom(Argument parent) {
		return new ChildrenSet(parent, node(parent).children);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#parents(net
	 * .sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Argument> parents(Argument child) {
		return Collections.unmodifiableSet(node(child).parents.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#children(
	 * net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Argument> children(Argument parent) {
		return Collections.unmodifiableSet(node(parent).children.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#
	 * getAcceptanceCondition(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public AcceptanceCondition getAcceptanceCondition(Argument argument) {
		return node(argument).acc;
	}

	private Node node(Argument arg) {
		Node node = index.get(arg);
		if (node == null) {
			throw new IllegalArgumentException("Could not find Argument " + arg);
		}
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#kBipolar(
	 * int)
	 */
	@Override
	public int kBipolar() {
		if (this.k < 0) {
			long count = linksStream()
					.map(Link::getLinkType)
					.filter(LinkType::isNonBipolar)
					.count();
			this.k = Math.toIntExact(count);
		}
		return k;
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
			LinkType type = linkStrategy.compute(arg, to, node(to).acc);
			return new SimpleLink(arg, to, type);
		}

		private Link computeIncoming(Argument from) {
			LinkType type = linkStrategy.compute(from, arg, acc);
			return new SimpleLink(from, arg, type);
		}		

	}
	
	private static abstract class LinkSet extends AbstractUnmodifiableSet<Link> {
		
		private final Collection<Link> edges;
			
		/**
		 * By construction we expect no duplicates in <code>edges</code>, therefore the set wrapper should be safe.
		 * 
		 * @param edges
		 */
		public LinkSet(Collection<Link> edges) {
			this.edges = edges;
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

	static final class GraphBuilder extends AbstractBuilder {

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder#build()
		 */
		@Override
		public AbstractDialecticalFramework build() {
			return new GraphAbstractDialecticalFramework(this);
		}
	}

}

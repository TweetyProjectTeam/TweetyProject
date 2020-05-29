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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.semantics.link.Link;
import net.sf.tweety.arg.adf.semantics.link.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.link.LinkType;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.util.LazyMap;
import net.sf.tweety.arg.adf.util.UnionSetView;

/**
 * Extended in the sense of the addition of arguments and links to an existing
 * ADF. Since the {@link AbstractDialecticalFramework} contract ensures
 * immutability, this implementation basically only stores the delta of the
 * extended ADF. This keeps the memory and computation footprint as low as
 * possible, since we reuse as much as possible and only introduce new
 * references to the delta.
 * 
 * @author Mathias Hofer
 *
 */
public final class ExtendedAbstractDialecticalFramework implements AbstractDialecticalFramework {

	private final AbstractDialecticalFramework extended;

	private final Map<Argument, Node> index;
		
	private final LinkStrategy linkStrategy;

	/**
	 * @param extended
	 */
	private ExtendedAbstractDialecticalFramework(ExtendedBuilder builder) {
		this.extended = builder.adf;
		this.linkStrategy = builder.linkStrategy;
		this.index = new HashMap<>(builder.arguments.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#
	 * getArguments()
	 */
	@Override
	public Set<Argument> getArguments() {
		return new UnionSetView<Argument>(extended.getArguments(), index.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#links()
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
		Stream<Link> deltaStream = index.values()
				.stream()
				.flatMap(n -> n.parents.values().stream());
		return Stream.concat(deltaStream, extended.linksStream());
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
		if (index.containsKey(child)) {
			return node(child).parents.get(parent);
		}
		return extended.link(parent, child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#linksTo(net
	 * .sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksTo(Argument child) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#linksFrom(
	 * net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksFrom(Argument parent) {
		Set<Link> linksFrom = extended.linksFrom(parent);
		return null;
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
		if (index.containsKey(child)) {
			return node(child).parents.keySet();
		}
		return extended.parents(child);
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#
	 * getAcceptanceCondition(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public AcceptanceCondition getAcceptanceCondition(Argument argument) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#kBipolar()
	 */
	@Override
	public int kBipolar() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#incomingDegree(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public int incomingDegree(Argument arg) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#outgoingDegree(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public int outgoingDegree(Argument arg) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private Node node(Argument arg) {
		Node node = index.get(arg);
		if (node == null) {
			throw new IllegalArgumentException("Could not find Argument " + arg);
		}
		return node;
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

	static final class ExtendedBuilder extends AbstractBuilder {

		private final AbstractDialecticalFramework adf;

		/**
		 * @param adf the ADF to extend
		 */
		public ExtendedBuilder(AbstractDialecticalFramework adf) {
			this.adf = adf;
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractBuilder#add(net.sf.tweety.arg.adf.syntax.Argument, net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition)
		 */
		@Override
		public Builder add(Argument arg, AcceptanceCondition acc) {
			if (adf.contains(arg)) {
				throw new IllegalArgumentException("Argument already exists in the ADF!");
			}
			return super.add(arg, acc);
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractBuilder#remove(net.sf.tweety.arg.adf.syntax.Argument)
		 */
		@Override
		public Builder remove(Argument arg) {
			throw new UnsupportedOperationException();
		}
	
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder#build()
		 */
		@Override
		public AbstractDialecticalFramework build() {
			return new ExtendedAbstractDialecticalFramework(this);
		}

	}
}

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
package org.tweetyproject.arg.adf.util;

import java.util.Iterator;
import java.util.List;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation.Builder;
import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * Iterates through all two-valued(!) interpretations except the ones equal to
 * or more specific than the provided ones.
 *
 * @author Mathias Hofer
 *
 */
public class ExcludeTwoValuedSubinterpretationsIterator implements Iterator<Interpretation> {

	/** Builder used for generated interpretations. */
	private final Interpretation.Builder builder;

	/** Root of the exclusion tree. */
	private final Node root;

	/** Argument iteration order. */
	private final Argument[] order;

	/**
	 * Constructs a new {@code ExcludeTwoValuedSubinterpretationsIterator} that
	 * iterates over a list of interpretations,
	 * excluding two-valued subinterpretations according to a specific order and
	 * structure.
	 *
	 *
	 * @param interpretations A list of {@code Interpretation} objects to be
	 *                        iterated over. The list must not be null or empty.
	 * @throws IllegalArgumentException if the list of interpretations is null or
	 *                                  empty.
	 */
	public ExcludeTwoValuedSubinterpretationsIterator(List<Interpretation> interpretations) {
		if (interpretations == null || interpretations.isEmpty()) {
			throw new IllegalArgumentException("interpretations must not be null!");
		}
		Iterator<Interpretation> iterator = interpretations.iterator();
		Interpretation first = iterator.next();
		this.order = arguments(first);
		this.builder = Interpretation.builder(List.of(order));
		this.root = new InnerNode(0);
		root.add(0, first.numDecided(), first);
		while (iterator.hasNext()) {
			Interpretation next = iterator.next();
			root.add(0, next.numDecided(), next);
		}
		root.addLeafs();
		root.propagateSink();
	}

	/**
	 * Creates an array of the arguments in <code>interpretation</code> but
	 * orders them s.t. the undecided ones are at the end.
	 *
	 * @param interpretation the interpretation whose arguments are ordered
	 * @return the arguments as an array and in a more efficient order
	 */
	private static Argument[] arguments(Interpretation interpretation) {
		Argument[] arguments = new Argument[interpretation.size()];
		int index = 0;
		for (Argument a : interpretation.satisfied()) {
			arguments[index++] = a;
		}
		for (Argument a : interpretation.unsatisfied()) {
			arguments[index++] = a;
		}
		for (Argument a : interpretation.undecided()) {
			arguments[index++] = a;
		}
		return arguments;
	}

	@Override
	public boolean hasNext() {
		return !root.done();
	}

	@Override
	public Interpretation next() {
		root.buildNext(builder);
		return builder.build();
	}

	/** Internal node of the exclusion tree. */
	private interface Node {

		/**
		 * Returns whether this node has been exhausted.
		 *
		 * @return {@code true} if this node has no more interpretations
		 */
		boolean done();

		/**
		 * Builds the next interpretation fragment.
		 *
		 * @param builder builder receiving the next fragment
		 */
		void buildNext(Builder builder);

		/**
		 * Adds the given interpretation to the tree.
		 *
		 * @param offset current argument offset
		 * @param remaining remaining decided arguments
		 * @param interpretation interpretation to add
		 */
		void add(int offset, int remaining, Interpretation interpretation);

		/**
		 * Adds leaf nodes below this node.
		 */
		void addLeafs();

		/**
		 * Propagates sink nodes where possible.
		 *
		 * @return this node or a replacement sink node
		 */
		default Node propagateSink() {
			return this;
		}

	}

	/** Internal branching node. */
	private final class InnerNode implements Node {

		/** Current argument index. */
		private final int index;

		/** Current branch value. */
		private boolean value;

		/** Child node for false assignments. */
		private Node fNode;

		/** Child node for true assignments. */
		private Node tNode;

		/**
		 * Creates an inner node.
		 *
		 * @param index the current argument index
		 */
		public InnerNode(int index) {
			this.index = index;
		}

		@Override
		public boolean done() {
			return fNode.done() && tNode.done();
		}

		/** Returns the active child node. */
		private Node current() {
			if (value) {
				return tNode;
			} else {
				return fNode;
			}
		}

		@Override
		public void buildNext(Builder builder) {
			Node current = current();
			if (current.done()) {
				value = true;
				current = tNode;
			}
			builder.put(order[index], value);
			current.buildNext(builder);
			if (current.done()) {
				value = true;
			}
		}

		@Override
		public void add(int offset, int remaining, Interpretation interpretation) {
			Argument arg = order[offset];
			int newOffset = offset + 1;
			if (interpretation.satisfied(arg)) {
				tNode = createIfNecessary(newOffset, remaining, tNode, interpretation);
				tNode.add(newOffset, remaining - 1, interpretation);
			} else if (interpretation.unsatisfied(arg)) {
				fNode = createIfNecessary(newOffset, remaining, fNode, interpretation);
				fNode.add(newOffset, remaining - 1, interpretation);
			} else {
				// TODO can we do better, without "wildcard branching"?
				fNode = createInnerIfNecessary(newOffset, fNode);
				tNode = createInnerIfNecessary(newOffset, tNode);

				fNode.add(newOffset, remaining, interpretation);
				tNode.add(newOffset, remaining, interpretation);
			}
		}

		/**
		 * Creates an inner node if no node exists yet.
		 *
		 * @param offset the next argument offset
		 * @param node the existing child node
		 * @return the existing or created node
		 */
		private Node createInnerIfNecessary(int offset, Node node) {
			if (node == null) {
				return new InnerNode(offset);
			}
			return node;
		}

		/**
		 * Creates a branch node or sink node if needed.
		 *
		 * @param offset the next argument offset
		 * @param remaining the number of remaining decided arguments
		 * @param node the existing child node
		 * @param interpretation the interpretation being inserted
		 * @return the existing or created node
		 */
		private Node createIfNecessary(int offset, int remaining, Node node, Interpretation interpretation) {
			if (remaining <= 1) {
				// we should only be here if the current argument is decided
				return SinkNode.INSTANCE;
			} else if (node == null) {
				return new InnerNode(offset);
			}
			return node;
		}

		@Override
		public void addLeafs() {
			fNode = createLeaf(fNode, false);
			tNode = createLeaf(tNode, true);
		}

		@Override
		public Node propagateSink() {
			fNode = fNode.propagateSink();
			tNode = tNode.propagateSink();
			if (fNode == SinkNode.INSTANCE && tNode == SinkNode.INSTANCE) {
				return SinkNode.INSTANCE;
			}
			return this;
		}

		/**
		 * Creates a leaf node if no node exists yet.
		 *
		 * @param node the existing child node
		 * @param value the leaf value
		 * @return the existing or created node
		 */
		private Node createLeaf(Node node, Boolean value) {
			Node leaf = node;
			if (leaf == null) {
				if (index + 1 < order.length) {
					leaf = new TailNode(index + 1);
				} else {
					return new LeafNode(value);
				}
			}
			leaf.addLeafs();
			return leaf;
		}
	}

		/** Tail node covering remaining arguments. */
		private final class TailNode implements Node {

			/** First argument offset covered by this tail. */
			private final int offset;

			/** Maximum encoded value for this tail. */
			private final int max;

			/** Whether this tail has produced its first value. */
			private boolean first = true; // do not swallow the all undecided

			/** Current encoded tail value. */
			private int value;

			/**
			 * Creates a tail node.
			 *
			 * @param offset the first argument index covered by the tail
			 */
			public TailNode(int offset) {
			this.offset = offset;
			int diff = order.length - offset - 1;
			int max = 1;
			for (int i = 0; i < diff; i++) {
				max |= max << 1;
			}
			this.max = max;
		}

		@Override
		public boolean done() {
			return value >= max;
		}

			/**
			 * Returns whether the given bit is set.
			 *
			 * @param n encoded value
			 * @param k bit index
			 * @return {@code true} if the bit is set
			 */
			boolean getBit(int n, int k) {
			return ((n >> k) & 1) == 1;
		}

		@Override
		public void buildNext(Builder builder) {
			if (!first) {
				value++;
			}
			first = false;
			for (int i = 0; i + offset < order.length; i++) {
				builder.put(order[i + offset], getBit(value, i));
			}
		}

		@Override
		public void add(int offset, int remaining, Interpretation interpretation) {
			// acts as a leaf, therefore do nothing
		}

		@Override
		public void addLeafs() {
		}

	}

		/** Leaf node for a fixed argument value. */
		private final class LeafNode implements Node {

			/** Fixed value represented by this leaf. */
			private final boolean value;

		/** Whether this node has been exhausted. */
		private boolean done = false;

			/**
			 * Creates a leaf node.
			 *
			 * @param value the fixed value for this leaf
			 */
			public LeafNode(boolean value) {
			this.value = value;
		}

		@Override
		public boolean done() {
			return done;
		}

		@Override
		public void buildNext(Builder builder) {
			builder.put(order[order.length - 1], value);
			done = true;
		}

		@Override
		public void add(int offset, int remaining, Interpretation interpretation) {
		}

		@Override
		public void addLeafs() {
		}

	}

	/** Sink node that terminates a branch. */
	private enum SinkNode implements Node {
		/** Singleton sink node instance. */
		INSTANCE;

		@Override
		public boolean done() {
			return true;
		}

		@Override
		public void buildNext(Builder builder) {
			throw new AssertionError();
		}

		@Override
		public void add(int offset, int remaining, Interpretation interpretation) {
			// a sinkhole for more specific interpretations
		}

		@Override
		public void addLeafs() {
		}

	}

}

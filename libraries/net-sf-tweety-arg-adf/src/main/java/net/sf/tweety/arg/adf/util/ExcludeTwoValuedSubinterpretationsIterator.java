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
package net.sf.tweety.arg.adf.util;

import java.util.Iterator;
import java.util.List;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation.Builder;
import net.sf.tweety.arg.adf.syntax.Argument;

/**
 * Iterates through all two-valued(!) interpretations except the ones equal to
 * or more specific than the provided ones.
 * 
 * @author Mathias Hofer
 *
 */
public class ExcludeTwoValuedSubinterpretationsIterator implements Iterator<Interpretation> {

	private final Interpretation.Builder builder;

	private final Node root;

	private final Argument[] order;
	
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
	 * @param interpretation
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

	private interface Node {

		boolean done();

		void buildNext(Builder builder);

		void add(int offset, int remaining, Interpretation interpretation);
		
		void addLeafs();
		
		default Node propagateSink() {
			return this;
		}

	}

	private final class InnerNode implements Node {

		private final int index;
		
		private boolean value;

		private Node fNode;

		private Node tNode;

		public InnerNode(int index) {
			this.index = index;
		}

		@Override
		public boolean done() {
			return fNode.done() && tNode.done();
		}

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

		private Node createInnerIfNecessary(int offset, Node node) {
			if (node == null) {
				return new InnerNode(offset);
			}
			return node;
		}

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

	private final class TailNode implements Node {

		private final int offset;
		
		private final int max;

		private boolean first = true; // do not swallow the all undecided
		
		private int value;

		/**
		 * @param bitSet
		 * @param offset
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
		public void addLeafs() {}

	}

	private final class LeafNode implements Node {

		private final boolean value;

		private boolean done = false;

		/**
		 * @param bitSet
		 * @param value
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
		public void add(int offset, int remaining, Interpretation interpretation) {}

		@Override
		public void addLeafs() {}

	}

	private enum SinkNode implements Node {
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

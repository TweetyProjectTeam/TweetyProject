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
 * Iterates through all interpretations except the ones equal to or more specific than the provided ones.
 * 
 * @author Mathias Hofer
 *
 */
public class ExcludeSubinterpretationsIterator implements Iterator<Interpretation> {
	
	private final Interpretation.Builder builder;

	private final Node root;
	
	private final Argument[] order;
	
	public ExcludeSubinterpretationsIterator(List<Interpretation> interpretations) {
		Iterator<Interpretation> iterator = interpretations.iterator();
		Interpretation first = iterator.next();
		this.order = arguments(first);
		this.builder = Interpretation.builder(List.of(order));
		this.root = new InnerNode(new ThreeValuedBitSet(order.length), 0);
		root.add(0, first.numDecided(), first);
		while (iterator.hasNext()) {
			Interpretation next = iterator.next();
			root.add(0, next.numDecided(), next);
		}
		root.addLeafs();
	}
	
	/**
	 * Creates an array of the arguments in <code>interpretation</code> but orders them s.t. the undecided ones are at the end.
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
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !root.done();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Interpretation next() {
		root.buildNext(builder);
		return builder.build();
	}
	
	private interface Node {
		
		boolean done();
				
		void buildNext(Builder builder);
		
		void add(int offset, int remaining, Interpretation interpretation);
		
		void reset();
		
		void addLeafs();
		
		void addReference(int index, boolean value, Node reference);
		
	}
	
	private final class InnerNode implements Node {
		
		private final ThreeValuedBitSet bitSet;
		
		private final int index;
		
		private Node uNode;
		
		private Node fNode;
		
		private Node tNode;

		/**
		 * @param bitSet
		 * @param index
		 */
		public InnerNode(ThreeValuedBitSet bitSet, int index) {
			this.bitSet = bitSet;
			this.index = index;
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.util.ExcludeSubinterpretationsIterator2.Node#done()
		 */
		@Override
		public boolean done() {
			return uNode.done() && fNode.done() && tNode.done();
		}
		
		private Node current() {
			Boolean value = bitSet.get(index);
			if (value == null) {
				return uNode;
			} else if (value) {
				return tNode;
			} else {
				return fNode;
			}
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.util.ExcludeSubinterpretationsIterator.Node#reset()
		 */
		@Override
		public void reset() {
			bitSet.clear(index);
			uNode.reset();
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.util.ExcludeSubinterpretationsIterator2.Node#build(net.sf.tweety.arg.adf.semantics.interpretation.Interpretation.Builder)
		 */
		@Override
		public void buildNext(Builder builder) {
			Node current = current();
			builder.put(order[index], bitSet.get(index));
			current.buildNext(builder);
			if (current.done()) {
				incrementIfPossible();
			}
		}
		
		private void incrementIfPossible() {
			Boolean value = bitSet.get(index);
			if (value == null || !value) {
				bitSet.increment(index);
				Node current = current();
				current.reset();
				if (current.done()) {
					// sink
					incrementIfPossible();
				}
			}
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.util.ExcludeSubinterpretationsIterator2.Node#add(java.util.List, net.sf.tweety.arg.adf.semantics.interpretation.Interpretation)
		 */
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
				uNode = createInnerIfNecessary(newOffset, uNode);
				fNode = createInnerIfNecessary(newOffset, fNode);
				tNode = createInnerIfNecessary(newOffset, tNode);
				
				uNode.add(newOffset, remaining, interpretation);
				fNode.add(newOffset, remaining, interpretation);
				tNode.add(newOffset, remaining, interpretation);
			}
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.util.ExcludeSubinterpretationsIterator2.Node#addReference(int, boolean)
		 */
		@Override
		public void addReference(int index, boolean value, Node reference) {
			// TODO currently not used, check if we need it or if the current undecided handling makes manual references obsolete
			if (index > 0) {
				addReferenceIfPossible(uNode, index, value, reference);
				addReferenceIfPossible(fNode, index, value, reference);
				addReferenceIfPossible(tNode, index, value, reference);
			} else {
				if (value && tNode == null) {
					tNode = reference;
				} else if (!value && fNode == null) {
					fNode = reference;
				}
			}
		}
		
		private void addReferenceIfPossible(Node node, int index, boolean value, Node reference) {
			if (node != null) {
				node.addReference(index - 1, value, reference);
			}
		}
		
		private Node createInnerIfNecessary(int offset, Node node) {
			if (node == null) {
				return new InnerNode(bitSet, offset);
			}
			return node;
		}

		private Node createIfNecessary(int offset, int remaining, Node node, Interpretation interpretation) {
			if (remaining <= 1) {
				// we should only be here if the current argument is decided
				return SinkNode.INSTANCE;
			} else if (node == null) {
				return new InnerNode(bitSet, offset);
			}
			return node;
		}
		
		@Override
		public void addLeafs() {
			uNode = createLeaf(uNode, null);
			fNode = createLeaf(fNode, false);
			tNode = createLeaf(tNode, true);
		}
		
		private Node createLeaf(Node node, Boolean value) {
			Node leaf = node;
			if (leaf == null) {
				if (index + 1 < order.length) {
					leaf =  new TailNode(bitSet, index + 1);
				} else {
					return new LeafNode(bitSet, value);
				}
			}
			leaf.addLeafs();
			return leaf;
		}
	}
	
	private final class TailNode implements Node {
		
		private final ThreeValuedBitSet bitSet;
		
		private final int offset;
		
		private boolean first = true; // do not swallow the all undecided
		
		private boolean done = false;

		/**
		 * @param bitSet
		 * @param offset
		 */
		public TailNode(ThreeValuedBitSet bitSet, int offset) {
			this.bitSet = bitSet;
			this.offset = offset;
		}

		@Override
		public boolean done() {
			if (!done) {
				// this works because we increment monotonically
				Boolean msb = bitSet.get(offset);
				Boolean lsb = bitSet.get(order.length - 1);
				if (msb != null && lsb != null) {
					done = msb && lsb;
				}
			}
			return done;
		}
		
		@Override
		public void buildNext(Builder builder) {
			if (!first) {
				bitSet.increment(offset);
			}
			first = false;
			for (int i = offset; i < order.length; i++) {
				builder.put(order[i], bitSet.get(i));
			}
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.arg.adf.util.ExcludeSubinterpretationsIterator.Node#reset()
		 */
		@Override
		public void reset() {
			for (int i = offset; i < order.length; i++) {
				bitSet.clear(i);
			}
			done = false;
		}
		
		@Override
		public void add(int offset, int remaining, Interpretation interpretation) {
			// acts as a leaf, therefore do nothing
		}
		
		@Override
		public void addLeafs() {}
		
		@Override
		public void addReference(int index, boolean value, Node reference) {}
		
	}
	
	private final class LeafNode implements Node {

		private final ThreeValuedBitSet bitSet;
		
		private final Boolean value;
		
		private boolean done = false;

		/**
		 * @param bitSet
		 * @param value
		 */
		public LeafNode(ThreeValuedBitSet bitSet, Boolean value) {
			this.bitSet = bitSet;
			this.value = value;
		}

		@Override
		public boolean done() {
			return done;
		}

		@Override
		public void buildNext(Builder builder) {
			bitSet.set(order.length - 1, value);
			builder.put(order[order.length - 1], value);
			done = true;
		}

		@Override
		public void add(int offset, int remaining, Interpretation interpretation) {}

		@Override
		public void reset() {}

		@Override
		public void addLeafs() {}

		@Override
		public void addReference(int index, boolean value, Node reference) {}
		
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
		public void reset() {}
		
		@Override
		public void add(int offset, int remaining, Interpretation interpretation) {
			// a sinkhole for more specific interpretations
		}
		
		@Override
		public void addLeafs() {}
		
		@Override
		public void addReference(int index, boolean value, Node reference) {}
	}
	
}

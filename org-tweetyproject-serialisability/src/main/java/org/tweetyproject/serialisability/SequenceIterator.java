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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.serialisability;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Depth-first enumeration of the paths of an (acyclic) serialisation graph
 * from the root to a terminal state or, if a target is given, to the target.
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
final class SequenceIterator<T, S> implements Iterator<SerialisationSequence<S>> {
	/** the graph */
	private final SerialisationGraph<T, S> graph;
	/** the target state, or null for all terminal states */
	private final SerialisationState<T, S> target;
	/** the states from which the target is reachable, or null for all states */
	private final Collection<SerialisationState<T, S>> relevant;
	/** the remaining outgoing steps of each state on the current path */
	private final Deque<Iterator<SerialisationStep<T, S>>> steps = new ArrayDeque<>();
	/** the sequence leading to each state on the current path */
	private final Deque<SerialisationSequence<S>> sequences = new ArrayDeque<>();
	/** the next sequence, or null if not yet computed */
	private SerialisationSequence<S> next;

	/**
	 * @param graph  some serialisation graph
	 * @param target the target state, or null for all terminal states
	 */
	SequenceIterator(SerialisationGraph<T, S> graph, SerialisationState<T, S> target) {
		this.graph = graph;
		this.target = target;
		if (target == null) {
			this.relevant = null;
		} else {
			this.relevant = new HashSet<>(graph.getAncestors(target));
			this.relevant.add(target);
		}
		SerialisationState<T, S> root = graph.getRoot();
		if (this.relevant != null && !this.relevant.contains(root))
			return;
		SerialisationSequence<S> empty = new SerialisationSequence<>(List.of());
		this.push(root, empty);
		if (this.isEnd(root))
			this.next = empty;
	}

	/**
	 * Appends a state to the current path
	 * @param state    some state
	 * @param sequence the sequence leading to the state
	 */
	private void push(SerialisationState<T, S> state, SerialisationSequence<S> sequence) {
		this.steps.push(this.graph.getOutgoingSteps(state).iterator());
		this.sequences.push(sequence);
	}

	/**
	 * @param state some state
	 * @return true iff a sequence ends in the state
	 */
	private boolean isEnd(SerialisationState<T, S> state) {
		if (this.target == null)
			return this.graph.isTerminal(state);
		return this.target.equals(state);
	}

	/**
	 * @return the next sequence, or null if there is none
	 */
	private SerialisationSequence<S> advance() {
		while (!this.steps.isEmpty()) {
			Iterator<SerialisationStep<T, S>> it = this.steps.peek();
			if (!it.hasNext()) {
				this.steps.pop();
				this.sequences.pop();
				continue;
			}
			SerialisationStep<T, S> step = it.next();
			SerialisationState<T, S> state = step.getNodeB();
			if (this.relevant != null && !this.relevant.contains(state))
				continue;
			SerialisationSequence<S> sequence = this.sequences.peek().extend(step.getInitialModel());
			this.push(state, sequence);
			if (this.isEnd(state))
				return sequence;
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		if (this.next == null)
			this.next = this.advance();
		return this.next != null;
	}

	@Override
	public SerialisationSequence<S> next() {
		if (!this.hasNext())
			throw new NoSuchElementException();
		SerialisationSequence<S> result = this.next;
		this.next = null;
		return result;
	}
}

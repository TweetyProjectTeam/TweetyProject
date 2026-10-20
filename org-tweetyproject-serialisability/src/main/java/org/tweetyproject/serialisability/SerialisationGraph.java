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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.term.IntegerConstant;

/**
 * The reachable part of a serialisation system for some framework: the states,
 * the steps between them, the terminal states and the initial models of each
 * state. The graph is acyclic and has at most one step between two states.
 * Sequences are the paths from the root to a terminal state.
 * <p>
 * The graph is computed by {@link SerialisationSystem#getGraph(Object)} and
 * cannot be modified: {@link #add(SerialisationState)} and
 * {@link #add(GeneralEdge)} throw an {@link UnsupportedOperationException}.
 * Restrictions, complements and subgraphs are returned as ordinary
 * {@link DefaultGraph}s.
 *
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
public final class SerialisationGraph<T, S> implements Graph<SerialisationState<T, S>> {
	/** the root state */
	private final SerialisationState<T, S> root;
	/** the entries of all states, indexed by their model, in insertion order */
	private final Map<S, Entry> entries = new LinkedHashMap<>();

	/**
	 * A state of this graph together with its data.
	 */
	private final class Entry {
		/** the state */
		private final SerialisationState<T, S> state;
		/** the classified initial models of the framework of the state */
		private final InitialModels<S> initialModels;
		/** the outgoing steps of the state */
		private final List<SerialisationStep<T, S>> outgoing = new ArrayList<>();
		/** the incoming steps of the state */
		private final List<SerialisationStep<T, S>> incoming = new ArrayList<>();
		/** whether the state is terminal */
		private boolean terminal;

		/**
		 * @param state         the state
		 * @param initialModels the classified initial models of the framework of the state
		 */
		Entry(SerialisationState<T, S> state, InitialModels<S> initialModels) {
			this.state = state;
			this.initialModels = initialModels;
		}
	}

	/**
	 * Creates a graph with the given root; the root is added with {@link #addState}.
	 * @param root the root state
	 */
	SerialisationGraph(SerialisationState<T, S> root) {
		this.root = root;
	}

	/**
	 * Adds a state to this graph
	 * @param state         some state
	 * @param initialModels the classified initial models of the framework of the state
	 */
	void addState(SerialisationState<T, S> state, InitialModels<S> initialModels) {
		this.entries.put(state.getModel(), new Entry(state, initialModels));
	}

	/**
	 * Adds a step between two states of this graph
	 * @param step some step
	 */
	void addStep(SerialisationStep<T, S> step) {
		this.entries.get(step.getNodeA().getModel()).outgoing.add(step);
		this.entries.get(step.getNodeB().getModel()).incoming.add(step);
	}

	/**
	 * Marks a state of this graph as terminal
	 * @param state some state
	 */
	void setTerminal(SerialisationState<T, S> state) {
		this.entries.get(state.getModel()).terminal = true;
	}

	/**
	 * @return the root state
	 */
	public SerialisationState<T, S> getRoot() {
		return this.root;
	}

	/**
	 * @param state some state
	 * @return true iff the state is a terminal state of this graph
	 */
	public boolean isTerminal(SerialisationState<T, S> state) {
		Entry entry = this.find(state);
		return entry != null && entry.terminal;
	}

	/**
	 * @return the terminal states
	 */
	public Set<SerialisationState<T, S>> getTerminalStates() {
		Set<SerialisationState<T, S>> result = new LinkedHashSet<>();
		for (Entry entry : this.entries.values())
			if (entry.terminal)
				result.add(entry.state);
		return Collections.unmodifiableSet(result);
	}

	/**
	 * @return the models of the terminal states
	 */
	public Set<S> getModels() {
		Set<S> result = new HashSet<>();
		for (Entry entry : this.entries.values())
			if (entry.terminal)
				result.add(entry.state.getModel());
		return result;
	}

	/**
	 * @param state some state of this graph
	 * @return the classified initial models of the framework of the state
	 */
	public InitialModels<S> getInitialModels(SerialisationState<T, S> state) {
		return this.require(state).initialModels;
	}

	/**
	 * @param model some model
	 * @return the state with the given model, or null if there is none
	 */
	public SerialisationState<T, S> getState(S model) {
		Entry entry = this.entries.get(model);
		return entry == null ? null : entry.state;
	}

	/**
	 * @param state some state of this graph
	 * @return the outgoing steps of the state
	 */
	public Collection<SerialisationStep<T, S>> getOutgoingSteps(SerialisationState<T, S> state) {
		return Collections.unmodifiableList(this.require(state).outgoing);
	}

	/**
	 * Returns all sequences, i.e. the paths from the root to some terminal state.
	 * This includes the empty sequence if the root is terminal and paths passing
	 * through other terminal states. The number of sequences may be exponential
	 * in the number of states.
	 *
	 * @return the serialisation sequences
	 */
	public Set<SerialisationSequence<S>> getSequences() {
		Set<SerialisationSequence<S>> result = new HashSet<>();
		for (SerialisationSequence<S> sequence : this.iterateSequences())
			result.add(sequence);
		return result;
	}

	/**
	 * Returns all paths from the root to the given state, i.e. the sequences
	 * inducing the model of the state.
	 *
	 * @param state some state of this graph
	 * @return the sequences leading to the state
	 */
	public Set<SerialisationSequence<S>> getSequences(SerialisationState<T, S> state) {
		Set<SerialisationSequence<S>> result = new HashSet<>();
		Iterator<SerialisationSequence<S>> it = new SequenceIterator<>(this, this.require(state).state);
		while (it.hasNext())
			result.add(it.next());
		return result;
	}

	/**
	 * @return the sequences of {@link #getSequences()}, enumerated lazily
	 */
	public Iterable<SerialisationSequence<S>> iterateSequences() {
		return () -> new SequenceIterator<>(this, null);
	}

	/**
	 * @param node some object
	 * @return the entry of the node, or null if the node is not in this graph
	 */
	private Entry find(Object node) {
		if (!(node instanceof SerialisationState))
			return null;
		return this.entries.get(((SerialisationState<?, ?>) node).getModel());
	}

	/**
	 * @param node some object
	 * @return the entry of the node
	 * @throws IllegalArgumentException if the node is not in this graph
	 */
	private Entry require(Object node) {
		Entry entry = this.find(node);
		if (entry == null)
			throw new IllegalArgumentException("The node is not in this graph.");
		return entry;
	}

	@Override
	public boolean add(SerialisationState<T, S> node) {
		throw new UnsupportedOperationException("Serialisation graphs cannot be modified");
	}

	@Override
	public boolean add(GeneralEdge<SerialisationState<T, S>> edge) {
		throw new UnsupportedOperationException("Serialisation graphs cannot be modified");
	}

	@Override
	public Collection<SerialisationState<T, S>> getNodes() {
		List<SerialisationState<T, S>> result = new ArrayList<>();
		for (Entry entry : this.entries.values())
			result.add(entry.state);
		return Collections.unmodifiableList(result);
	}

	@Override
	public int getNumberOfNodes() {
		return this.entries.size();
	}

	@Override
	public int getNumberOfEdges() {
		int result = 0;
		for (Entry entry : this.entries.values())
			result += entry.outgoing.size();
		return result;
	}

	@Override
	public boolean areAdjacent(SerialisationState<T, S> a, SerialisationState<T, S> b) {
		return this.getEdge(a, b) != null;
	}

	@Override
	public SerialisationStep<T, S> getEdge(SerialisationState<T, S> a, SerialisationState<T, S> b) {
		Entry entry = this.find(a);
		if (entry == null)
			return null;
		for (SerialisationStep<T, S> step : entry.outgoing)
			if (step.getNodeB().equals(b))
				return step;
		return null;
	}

	@Override
	public Collection<SerialisationStep<T, S>> getEdges() {
		List<SerialisationStep<T, S>> result = new ArrayList<>();
		for (Entry entry : this.entries.values())
			result.addAll(entry.outgoing);
		return Collections.unmodifiableList(result);
	}

	@Override
	public Iterator<SerialisationState<T, S>> iterator() {
		return this.getNodes().iterator();
	}

	@Override
	public boolean contains(Object obj) {
		return this.find(obj) != null;
	}

	@Override
	public Collection<SerialisationState<T, S>> getChildren(Node node) {
		Set<SerialisationState<T, S>> result = new LinkedHashSet<>();
		for (SerialisationStep<T, S> step : this.require(node).outgoing)
			result.add(step.getNodeB());
		return Collections.unmodifiableSet(result);
	}

	@Override
	public Collection<SerialisationState<T, S>> getParents(Node node) {
		Set<SerialisationState<T, S>> result = new LinkedHashSet<>();
		for (SerialisationStep<T, S> step : this.require(node).incoming)
			result.add(step.getNodeA());
		return Collections.unmodifiableSet(result);
	}

	@Override
	public boolean existsDirectedPath(SerialisationState<T, S> node1, SerialisationState<T, S> node2) {
		Entry goal = this.require(node2);
		Set<Entry> visited = new HashSet<>();
		Deque<Entry> open = new ArrayDeque<>();
		visited.add(this.require(node1));
		open.push(this.require(node1));
		while (!open.isEmpty()) {
			Entry current = open.pop();
			if (current == goal)
				return true;
			for (SerialisationStep<T, S> step : current.outgoing) {
				Entry next = this.entries.get(step.getNodeB().getModel());
				if (visited.add(next))
					open.push(next);
			}
		}
		return false;
	}

	@Override
	public Collection<SerialisationState<T, S>> getNeighbors(SerialisationState<T, S> node) {
		Set<SerialisationState<T, S>> result = new LinkedHashSet<>(this.getChildren(node));
		result.addAll(this.getParents(node));
		return Collections.unmodifiableSet(result);
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		Matrix m = new Matrix(this.getNumberOfNodes(), this.getNumberOfNodes());
		int i = 0, j;
		for (Entry a : this.entries.values()) {
			j = 0;
			for (Entry b : this.entries.values()) {
				m.setEntry(i, j, new IntegerConstant(this.areAdjacent(a.state, b.state) ? 1 : 0));
				j++;
			}
			i++;
		}
		return m;
	}

	@Override
	public Graph<SerialisationState<T, S>> getComplementGraph(int selfloops) {
		return this.getRestriction(this.getNodes()).getComplementGraph(selfloops);
	}

	@Override
	public Collection<Collection<SerialisationState<T, S>>> getConnectedComponents() {
		return DefaultGraph.getConnectedComponents(this);
	}

	@Override
	public Collection<Collection<SerialisationState<T, S>>> getStronglyConnectedComponents() {
		return DefaultGraph.getStronglyConnectedComponents(this);
	}

	/**
	 * Returns all subgraphs; their number is exponential in the number of states and steps.
	 */
	@Override
	public Collection<Graph<SerialisationState<T, S>>> getSubgraphs() {
		return DefaultGraph.getSubgraphs(this);
	}

	/**
	 * Returns the restriction to the given states as an ordinary graph; states
	 * not in this graph are ignored.
	 */
	@Override
	public DefaultGraph<SerialisationState<T, S>> getRestriction(Collection<SerialisationState<T, S>> nodes) {
		DefaultGraph<SerialisationState<T, S>> graph = new DefaultGraph<>();
		Set<Entry> retained = new HashSet<>();
		for (SerialisationState<T, S> node : nodes) {
			Entry entry = this.find(node);
			if (entry != null && retained.add(entry))
				graph.add(entry.state);
		}
		for (Entry entry : retained)
			for (SerialisationStep<T, S> step : entry.outgoing)
				if (retained.contains(this.entries.get(step.getNodeB().getModel())))
					graph.add(step);
		return graph;
	}

	@Override
	public boolean hasSelfLoops() {
		for (Entry entry : this.entries.values())
			if (this.areAdjacent(entry.state, entry.state))
				return true;
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		return false;
	}

	@Override
	public String toString() {
		return "<" + this.getNodes() + "," + this.getEdges() + ">";
	}
}

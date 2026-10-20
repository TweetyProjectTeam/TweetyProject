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
import java.util.Set;

import org.tweetyproject.graphs.DefaultGraph;

/**
 * An αβ-serialisation system: starting from (F, ∅), a state (F', E) can
 * transition to (F'<sup>S</sup>, E ⊔ S) for every initial model S of F'
 * selected by α, and E is a model of the induced semantics iff β accepts the
 * state.
 * <p>
 * The transition system is explored once per call. States with the same model
 * are merged (contract C4 of {@link SerialisationScheme}) and the initial models
 * of each state are computed once. The exploration checks that α only selects
 * initial models, that every step changes the model, that there is at most one
 * step between two states (C3), and that the result is acyclic.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument &amp; Computation (2022)"
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
public final class SerialisationSystem<T, S> {
	/** the serialisation scheme */
	private final SerialisationScheme<T, S> scheme;
	/** the selection function */
	private final SelectionFunction<T, S> alpha;
	/** the termination function */
	private final TerminationFunction<T, S> beta;

	/**
	 * Creates a serialisation system
	 * @param scheme the serialisation scheme
	 * @param alpha  the selection function
	 * @param beta   the termination function
	 */
	public SerialisationSystem(SerialisationScheme<T, S> scheme, SelectionFunction<T, S> alpha,
			TerminationFunction<T, S> beta) {
		this.scheme = scheme;
		this.alpha = alpha;
		this.beta = beta;
	}

	/**
	 * @return the serialisation scheme
	 */
	public SerialisationScheme<T, S> getScheme() {
		return this.scheme;
	}

	/**
	 * @return the selection function
	 */
	public SelectionFunction<T, S> getSelectionFunction() {
		return this.alpha;
	}

	/**
	 * @return the termination function
	 */
	public TerminationFunction<T, S> getTerminationFunction() {
		return this.beta;
	}

	/**
	 * Computes the states reachable from (F, ∅) and the steps between them.
	 *
	 * @param framework some framework
	 * @return the serialisation graph of the framework
	 * @throws IllegalStateException if the scheme or α violate the checked conditions
	 */
	public SerialisationGraph<T, S> getGraph(T framework) {
		SerialisationState<T, S> root = new SerialisationState<>(framework, this.scheme.empty(framework));
		SerialisationGraph<T, S> graph = new SerialisationGraph<>(root);
		graph.addState(root, this.scheme.partition(framework));
		Deque<SerialisationState<T, S>> open = new ArrayDeque<>();
		open.push(root);
		while (!open.isEmpty()) {
			SerialisationState<T, S> state = open.pop();
			InitialModels<S> initialModels = graph.getInitialModels(state);
			if (this.beta.isTerminal(state.getFramework(), state.getModel(), initialModels))
				graph.setTerminal(state);
			Collection<S> selection = this.alpha.select(state.getFramework(), state.getModel(), initialModels);
			if (!initialModels.getAll().containsAll(selection))
				throw new IllegalStateException("Selection " + selection + " contains models that are not initial in state " + state);
			for (S initialModel : selection) {
				S model = this.scheme.union(state.getModel(), initialModel);
				if (model.equals(state.getModel()))
					throw new IllegalStateException("Selecting " + initialModel + " does not change the model of state " + state);
				SerialisationState<T, S> next = graph.getState(model);
				if (next == null) {
					T reduct = this.scheme.getReduct(state.getFramework(), initialModel);
					next = new SerialisationState<>(reduct, model);
					graph.addState(next, this.scheme.partition(reduct));
					open.push(next);
				} else if (graph.areAdjacent(state, next)) {
					throw new IllegalStateException("Selecting " + initialModel + " leads to the already connected state " + next);
				}
				graph.addStep(new SerialisationStep<>(state, next, initialModel));
			}
		}
		if (DefaultGraph.containsCycle(graph))
			throw new IllegalStateException("The serialisation graph contains a cycle");
		return graph;
	}

	/**
	 * @param framework some framework
	 * @return the models of the semantics induced by α and β
	 */
	public Set<S> getModels(T framework) {
		return this.getGraph(framework).getModels();
	}

	/**
	 * Returns all serialisation sequences, see {@link SerialisationGraph#getSequences()}.
	 * @param framework some framework
	 * @return the sequences of the framework
	 */
	public Set<SerialisationSequence<S>> getSequences(T framework) {
		return this.getGraph(framework).getSequences();
	}
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The formalism-specific part of a serialisation system: initial models, their
 * classification, the reduct and the union of models. The initial models are
 * the ⊑-minimal non-empty models of a base notion τ (e.g. admissibility), where
 * ⊑ is an information order documented by each scheme (e.g. set inclusion for
 * AF extensions). For AFs, initial models are the initial sets.
 * <p>
 * By default, the initial models are computed from the models of τ, so the
 * first part of C5 holds by construction. Enumerating τ(F) is exponential in
 * general; implementations may override {@link #getInitialModels(Object)} with
 * an equivalent, more efficient computation.
 * <p>
 * Contract:
 * <ul>
 * <li>C1 Scheme operations are deterministic and do not modify their
 * arguments. Frameworks and models have consistent {@code equals} and
 * {@code hashCode} and must not be modified while they are retained by a graph
 * or sequence. Reducts and unions are fresh objects.
 * <li>C2 On compatible models, {@link #union(Object, Object)} is the least upper
 * bound wrt. ⊑; incompatible operands are rejected with an
 * {@link IllegalArgumentException}. {@link #empty(Object)} is neutral.
 * <li>C3 An initial model of the reduct F<sup>E</sup> only decides what E leaves
 * undecided. Hence it is compatible with E, strictly extends E, distinct
 * initial models lead to distinct unions, and every transition sequence is
 * finite.
 * <li>C4 {@code getReduct(F, empty(F))} equals F, and
 * {@code getReduct(getReduct(F, e), s)} equals {@code getReduct(F, union(e, s))}.
 * Hence, within the serialisation of F, a state is determined by its model.
 * </ul>
 * For the base notion τ a scheme may additionally satisfy:
 * <ul>
 * <li>C5 {@code empty(F)} ∈ τ(F), the initial models are the ⊑-minimal
 * non-empty members of τ(F), and the union of e ∈ τ(F) and some
 * r ∈ τ(F<sup>e</sup>) is a member of τ(F).
 * <li>C6 Every e ∈ τ(F) other than {@code empty(F)} is the union of an initial
 * model s of F and some r ∈ τ(F<sup>s</sup>).
 * </ul>
 * Under C1–C6, {@link SelectionFunction#all()} and
 * {@link TerminationFunction#always()} yield exactly τ(F). Implementations
 * state whether C5/C6 are proven or conjectured.
 * <p>
 * C1–C4 are preconditions of {@link SerialisationSystem}; in particular, merging
 * states by model is only sound under C4. C5/C6 only concern the correspondence
 * with τ. Whether a combination of τ, reduct and classification is well-behaved
 * is the responsibility of the implementation and is not enforced.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument &amp; Computation (2022)"
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
public interface SerialisationScheme<T, S> {

	/**
	 * @param framework some framework
	 * @return the least model of the framework, i.e. the model of the root state
	 */
	S empty(T framework);

	/**
	 * @param framework some framework
	 * @return the models of the base notion τ for the framework
	 */
	Collection<S> getBaseModels(T framework);

	/**
	 * Returns the initial models of the framework, i.e. the ⊑-minimal models of
	 * τ(F) other than {@code empty(F)}. Overrides must return the same models.
	 *
	 * @param framework some framework
	 * @return the initial models of the framework
	 */
	default Collection<S> getInitialModels(T framework) {
		S empty = this.empty(framework);
		List<S> candidates = new ArrayList<>();
		for (S model : this.getBaseModels(framework))
			if (!this.isLessOrEqual(model, empty))
				candidates.add(model);
		List<S> result = new ArrayList<>();
		for (S model : candidates) {
			boolean minimal = true;
			for (S other : candidates) {
				if (!other.equals(model) && this.isLessOrEqual(other, model)) {
					minimal = false;
					break;
				}
			}
			if (minimal)
				result.add(model);
		}
		return result;
	}

	/**
	 * @param framework    some framework
	 * @param initialModel an initial model of the framework
	 * @return true iff the initial model is unattacked in the framework
	 */
	boolean isUnattacked(T framework, S initialModel);

	/**
	 * Checks whether two initial models are conflicting; this relation is symmetric.
	 * For AFs, some argument of one attacks the other.
	 *
	 * @param framework    some framework
	 * @param initialModel an initial model of the framework
	 * @param other        another initial model of the framework
	 * @return true iff the initial models are conflicting
	 */
	boolean isConflicting(T framework, S initialModel, S other);

	/**
	 * @param framework some framework
	 * @param model     some model of the framework
	 * @return the reduct of the framework wrt. the model
	 */
	T getReduct(T framework, S model);

	/**
	 * @param model some model
	 * @param other a model compatible with the first one
	 * @return the union (least upper bound) of both models
	 * @throws IllegalArgumentException if the models are incompatible
	 */
	S union(S model, S other);

	/**
	 * @param model some model
	 * @param other some model
	 * @return true iff model ⊑ other in the information order of this scheme
	 */
	boolean isLessOrEqual(S model, S other);

	/**
	 * Partitions the initial models of the framework into unattacked, unchallenged
	 * and challenged ones. Overrides must return the same groups.
	 *
	 * @param framework some framework
	 * @return the classified initial models of the framework
	 */
	default InitialModels<S> partition(T framework) {
		Collection<S> initialModels = this.getInitialModels(framework);
		List<S> unattacked = new ArrayList<>();
		List<S> unchallenged = new ArrayList<>();
		List<S> challenged = new ArrayList<>();
		for (S initialModel : initialModels) {
			if (this.isUnattacked(framework, initialModel)) {
				unattacked.add(initialModel);
				continue;
			}
			boolean isChallenged = false;
			for (S other : initialModels) {
				if (this.isConflicting(framework, initialModel, other)) {
					isChallenged = true;
					break;
				}
			}
			if (isChallenged) {
				challenged.add(initialModel);
			} else {
				unchallenged.add(initialModel);
			}
		}
		return new InitialModels<>(unattacked, unchallenged, challenged);
	}
}

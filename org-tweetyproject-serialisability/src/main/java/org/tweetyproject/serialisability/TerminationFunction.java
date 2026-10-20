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

import java.util.Collection;

/**
 * A termination function β decides whether a state (F, E) is terminal, i.e.
 * whether E is a model of the semantics. Besides F and E, β is given the
 * classified initial models of F. β is deterministic and does not modify its
 * arguments. A terminal state may still have successors.
 *
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
@FunctionalInterface
public interface TerminationFunction<T, S> {

	/**
	 * Decides whether the state (F, E) is terminal
	 * @param framework     the residual framework F
	 * @param model         the model E constructed so far
	 * @param initialModels the classified initial models of F
	 * @return true iff the state is terminal
	 */
	boolean isTerminal(T framework, S model, InitialModels<S> initialModels);

	/**
	 * Every state is terminal (β_ad, used for admissible and strongly admissible semantics).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the termination function
	 */
	static <T, S> TerminationFunction<T, S> always() {
		return (framework, model, initialModels) -> true;
	}

	/**
	 * Terminal iff there is no unattacked initial model (β_co, used for complete and grounded semantics).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the termination function
	 */
	static <T, S> TerminationFunction<T, S> noUnattackedInitialModels() {
		return (framework, model, initialModels) -> initialModels.getUnattacked().isEmpty();
	}

	/**
	 * Terminal iff there is no initial model (β_pr, used for preferred semantics).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the termination function
	 */
	static <T, S> TerminationFunction<T, S> noInitialModels() {
		return (framework, model, initialModels) -> initialModels.isEmpty();
	}

	/**
	 * Terminal iff the framework is empty (β_st, used for stable semantics of AFs;
	 * for ADFs this yields two-valued models, not stable models).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the termination function
	 */
	static <T extends Collection<?>, S> TerminationFunction<T, S> emptyFramework() {
		return (framework, model, initialModels) -> framework.isEmpty();
	}

	/**
	 * Terminal iff there is no unattacked or unchallenged initial model (β_uc, used for unchallenged semantics).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the termination function
	 */
	static <T, S> TerminationFunction<T, S> noUnattackedOrUnchallengedInitialModels() {
		return (framework, model, initialModels) -> initialModels.getUnattacked().isEmpty()
				&& initialModels.getUnchallenged().isEmpty();
	}
}

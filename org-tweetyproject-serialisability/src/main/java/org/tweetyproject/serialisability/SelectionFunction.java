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
import java.util.HashSet;
import java.util.Set;

/**
 * A selection function α selects, from the classified initial models of the
 * framework of a state (F, E), those that may be added in the next step. The
 * result must be a subset of {@link InitialModels#getAll()}; selection is
 * deterministic and does not modify its arguments.
 * <p>
 * As in the literature, the provided selection functions only depend on the
 * classified initial models; F and E are available for domain-specific
 * selection functions.
 *
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
@FunctionalInterface
public interface SelectionFunction<T, S> {

	/**
	 * Selects a subset of the initial models of the state (F, E)
	 * @param framework     the residual framework F
	 * @param model         the model E constructed so far
	 * @param initialModels the classified initial models of F
	 * @return the selectable initial models
	 */
	Collection<S> select(T framework, S model, InitialModels<S> initialModels);

	/**
	 * Selects all initial models (α_ad, used for admissible, complete, preferred and stable semantics).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the selection function
	 */
	static <T, S> SelectionFunction<T, S> all() {
		return (framework, model, initialModels) -> initialModels.getAll();
	}

	/**
	 * Selects the unattacked initial models (α_gr, used for grounded and strongly admissible semantics).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the selection function
	 */
	static <T, S> SelectionFunction<T, S> unattacked() {
		return (framework, model, initialModels) -> initialModels.getUnattacked();
	}

	/**
	 * Selects the unattacked and unchallenged initial models (α_uc, used for unchallenged semantics).
	 * @param <T> the type of frameworks
	 * @param <S> the type of models
	 * @return the selection function
	 */
	static <T, S> SelectionFunction<T, S> unattackedOrUnchallenged() {
		return (framework, model, initialModels) -> {
			Set<S> result = new HashSet<>(initialModels.getUnattacked());
			result.addAll(initialModels.getUnchallenged());
			return result;
		};
	}
}

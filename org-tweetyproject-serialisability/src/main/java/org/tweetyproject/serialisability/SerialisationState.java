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

import org.tweetyproject.graphs.Node;

/**
 * A state (F, E) of a serialisation system, consisting of the residual
 * framework F and the model E constructed so far. Within the serialisation of
 * one framework, a state is determined by its model, so states are identified
 * by their model; comparing states of different serialisations is not
 * meaningful.
 *
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
public final class SerialisationState<T, S> implements Node {
	/** the residual framework */
	private final T framework;
	/** the model constructed so far */
	private final S model;

	/**
	 * Creates a new state
	 * @param framework the residual framework
	 * @param model     the model constructed so far
	 */
	SerialisationState(T framework, S model) {
		this.framework = framework;
		this.model = model;
	}

	/**
	 * @return the residual framework of this state
	 */
	public T getFramework() {
		return this.framework;
	}

	/**
	 * @return the model constructed so far
	 */
	public S getModel() {
		return this.model;
	}

	@Override
	public int hashCode() {
		return this.model.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SerialisationState))
			return false;
		return this.model.equals(((SerialisationState<?, ?>) obj).model);
	}

	@Override
	public String toString() {
		return this.model.toString();
	}
}

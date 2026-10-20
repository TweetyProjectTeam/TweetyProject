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

import org.tweetyproject.graphs.DirectedEdge;

/**
 * A transition between two states of a serialisation system, labelled with the
 * selected initial model.
 *
 * @param <T> the type of frameworks
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
public final class SerialisationStep<T, S> extends DirectedEdge<SerialisationState<T, S>> {
	/** the selected initial model */
	private final S initialModel;

	/**
	 * Creates a step
	 * @param from         the source state
	 * @param to           the target state
	 * @param initialModel the initial model of the framework of the source state selected by this step
	 */
	SerialisationStep(SerialisationState<T, S> from, SerialisationState<T, S> to, S initialModel) {
		super(from, to, initialModel.toString());
		this.initialModel = initialModel;
	}

	/**
	 * @return the initial model selected by this step
	 */
	public S getInitialModel() {
		return this.initialModel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getNodeA().hashCode();
		result = prime * result + this.getNodeB().hashCode();
		result = prime * result + this.initialModel.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SerialisationStep))
			return false;
		SerialisationStep<?, ?> other = (SerialisationStep<?, ?>) obj;
		return this.initialModel.equals(other.initialModel) && this.getNodeA().equals(other.getNodeA())
				&& this.getNodeB().equals(other.getNodeB());
	}
}

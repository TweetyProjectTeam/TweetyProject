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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The initial models of a framework, partitioned into unattacked, unchallenged
 * and challenged ones. The groups are unmodifiable; their elements are not copied.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument &amp; Computation (2022)"
 * @see "Lars Bengel, Matthias Thimm. 'Serialisability in Formal Argumentation' (2026)"
 *
 * @param <S> the type of models
 *
 * @author Lars Bengel
 */
public final class InitialModels<S> {
	/** the unattacked initial models */
	private final Set<S> unattacked;
	/** the unchallenged initial models */
	private final Set<S> unchallenged;
	/** the challenged initial models */
	private final Set<S> challenged;

	/**
	 * Creates the partition from the three groups of initial models
	 * @param unattacked   the unattacked initial models
	 * @param unchallenged the unchallenged initial models
	 * @param challenged   the challenged initial models
	 */
	public InitialModels(Collection<S> unattacked, Collection<S> unchallenged, Collection<S> challenged) {
		this.unattacked = Collections.unmodifiableSet(new HashSet<>(unattacked));
		this.unchallenged = Collections.unmodifiableSet(new HashSet<>(unchallenged));
		this.challenged = Collections.unmodifiableSet(new HashSet<>(challenged));
	}

	/**
	 * @return the unattacked initial models
	 */
	public Set<S> getUnattacked() {
		return this.unattacked;
	}

	/**
	 * @return the unchallenged initial models
	 */
	public Set<S> getUnchallenged() {
		return this.unchallenged;
	}

	/**
	 * @return the challenged initial models
	 */
	public Set<S> getChallenged() {
		return this.challenged;
	}

	/**
	 * @return all initial models
	 */
	public Set<S> getAll() {
		Set<S> result = new HashSet<>(this.unattacked);
		result.addAll(this.unchallenged);
		result.addAll(this.challenged);
		return result;
	}

	/**
	 * @return true iff there are no initial models
	 */
	public boolean isEmpty() {
		return this.unattacked.isEmpty() && this.unchallenged.isEmpty() && this.challenged.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.unattacked.hashCode();
		result = prime * result + this.unchallenged.hashCode();
		result = prime * result + this.challenged.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof InitialModels))
			return false;
		InitialModels<?> other = (InitialModels<?>) obj;
		return this.unattacked.equals(other.unattacked) && this.unchallenged.equals(other.unchallenged)
				&& this.challenged.equals(other.challenged);
	}

	@Override
	public String toString() {
		return "{ua=" + this.unattacked + ", uc=" + this.unchallenged + ", c=" + this.challenged + "}";
	}
}

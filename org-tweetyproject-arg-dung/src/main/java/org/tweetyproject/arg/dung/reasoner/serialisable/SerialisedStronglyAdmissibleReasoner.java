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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner.serialisable;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents a reasoner to compute the extensions of the strongly admissible {@link Semantics} by serialising unattacked initial sets.
 * A set of arguments E is strongly admissible iff every argument A in E is defended by some argument B in E \ {A}, which itself is strongly defended by E \ {A}, 
 * in other words, no argument in E is defended only by itself.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisedStronglyAdmissibleReasoner extends SerialisableExtensionReasoner {

	/**
	 * Initializes a {@link SerialisableExtensionReasoner} for the strongly admissible semantics
	 */
	public SerialisedStronglyAdmissibleReasoner() {
		super(Semantics.SAD);
	}

	/**
	 * Select a subset of the initial sets of the AF, i.e. the possible successor states
	 * @param unattacked the set of unattacked initial sets
	 * @param unchallenged the set of unchallenged initial sets
	 * @param challenged the set of challenged initial sets
	 * @return the set of unattacked initial sets
	 */
	@Override
	public Collection<Extension<DungTheory>> selectionFunction(Collection<Extension<DungTheory>> unattacked,
			Collection<Extension<DungTheory>> unchallenged, Collection<Extension<DungTheory>> challenged) {
		return new HashSet<>(unattacked);
	}

	@Override
	public boolean terminationFunction(DungTheory theory, Extension<DungTheory> extension) {
		return true;
	}
}

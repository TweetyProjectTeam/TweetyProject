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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This reasoner for Dung theories performs inference on the conflict-free extensions.
 * @author Matthias Thimm
 * @author Lars Bengel
 */
public class SimpleConflictFreeReasoner extends AbstractExtensionReasoner {

    /**
     * Creates a new SimpleConflictFreeReasoner instance.
     */
    public SimpleConflictFreeReasoner() {
        // default constructor
    }

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
		Collection<Extension<DungTheory>> extensions = new HashSet<>();
		// self-attacking arguments are never part of a conflict-free set
		Set<Argument> candidates = new HashSet<>();
		for (Argument argument: bbase) {
			if (!bbase.isAttackedBy(argument, argument))
				candidates.add(argument);
		}
		this.getModels(bbase, new HashSet<>(), candidates, extensions);
		return extensions;
	}

	/**
	 * collects the given conflict-free set and all its conflict-free extensions by arguments
	 * from candidates with a simple backtracking algorithm
	 * @param bbase an argumentation framework
	 * @param current the conflict-free set built so far
	 * @param candidates arguments that can still be added to current
	 * @param extensions the collection of conflict-free sets
	 */
	private void getModels(DungTheory bbase, Set<Argument> current, Set<Argument> candidates, Collection<Extension<DungTheory>> extensions) {
		extensions.add(new Extension<>(current));
		for (Argument argument: new ArrayList<>(candidates)) {
			// later branches must not pick argument again, so each set is built only once
			candidates.remove(argument);
			Set<Argument> remainingCandidates = new HashSet<>(candidates);
			remainingCandidates.removeAll(bbase.getAttacked(argument));
			remainingCandidates.removeAll(bbase.getAttackers(argument));

			current.add(argument);
			this.getModels(bbase, current, remainingCandidates, extensions);
			current.remove(argument);
		}
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension<DungTheory> getModel(DungTheory bbase) {
		// as the empty set is always conflict-free we return that one.
		return new Extension<DungTheory>();
	}
}

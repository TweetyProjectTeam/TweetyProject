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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner.serialisable;

import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * Serialised version of the preferred semantics
 *
 * @author Lars Bengel
 */
public class SerialisedPreferredReasoner extends SerialisedAdmissibleReasoner {
    
	/**
	 * Initializes a {@link SerialisableExtensionReasoner} for the preferred semantics
	 */
	public SerialisedPreferredReasoner() {
		super();
		this.semantics = Semantics.PR;
	}

	/**
     * Determines whether the current state represents an extension wrt. the semantics of the reasoner or not.
     * @param reducedFramework The current framework of the transition system
	 * @param constructedExtension The extension constructed so far
     * @return true, iff the AF has no initial sets
     */
    @Override
    public boolean terminationFunction(DungTheory reducedFramework, Extension<DungTheory> constructedExtension) {
        return (new SimpleInitialReasoner().getModels(reducedFramework).isEmpty());
    }
}

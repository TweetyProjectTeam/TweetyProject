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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * Serialised version of the complete semantics
 */
public class SerialisedCompleteReasoner extends SerialisedAdmissibleReasoner {
    
	public SerialisedCompleteReasoner() {
		super();
		setSemantic(Semantics.CO);
	}

	/**
     * a set is accepted iff the specified framework has no unattacked arguments
     * @param reducedFramework The current framework of the transition system
	 * @param constructedExtension The extension constructed so far.
     * @return true, if no unattacked argument exists in AF
     */
    @Override
    public boolean terminationFunction(DungTheory reducedFramework, Extension<DungTheory> constructedExtension) {
        DungTheory theory = reducedFramework;
        for (Argument arg: theory) {
            if (theory.getAttackers(arg).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

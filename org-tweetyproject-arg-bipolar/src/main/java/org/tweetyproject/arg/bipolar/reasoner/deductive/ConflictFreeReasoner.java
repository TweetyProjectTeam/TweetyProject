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


package org.tweetyproject.arg.bipolar.reasoner.deductive;

import org.tweetyproject.arg.bipolar.syntax.*;
import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * reasoner for conflict-freeness in bipolar argumentation frameworks with a deductive support interpretation
 * a set of arguments is conflict-free iff it is conflict-free in regards to the complex attacks in the framework
 */
public class ConflictFreeReasoner {
	/**
	 * 
	 * @param bbase argumentation framework
	 * @return models
	 */
    public Collection<ArgumentSet> getModels(DeductiveArgumentationFramework bbase) {
        // get a dung theory containing all direct and complex attacks in the given bipolar argumentation framework
        DungTheory theory = bbase.getCompleteAssociatedDungTheory();

        Collection<ArgumentSet> result = new HashSet<>();
        for (Extension<DungTheory> ext: new SimpleConflictFreeReasoner().getModels(theory)) {
            result.add(new ArgumentSet(ext));
        }
        return result;
    }

    /** Default Constructor */
    public ConflictFreeReasoner(){}
}

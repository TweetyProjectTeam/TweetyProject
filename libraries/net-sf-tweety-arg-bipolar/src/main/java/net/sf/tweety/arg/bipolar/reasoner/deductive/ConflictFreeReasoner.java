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


package net.sf.tweety.arg.bipolar.reasoner.deductive;

import net.sf.tweety.arg.bipolar.syntax.*;
import net.sf.tweety.arg.dung.reasoner.SimpleConflictFreeReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * reasoner for conflict-freeness in bipolar argumentation frameworks with a deductive support interpretation
 * a set of arguments is conflict-free iff it is conflict-free in regards to the complex attacks in the framework
 */
public class ConflictFreeReasoner {
    public Collection<ArgumentSet> getModels(DeductiveArgumentationFramework bbase) {
        // get a dung theory containing all direct and complex attacks in the given bipolar argumentation framework
        DungTheory theory = bbase.getCompleteAssociatedDungTheory();

        Collection<ArgumentSet> result = new HashSet<>();
        for (Extension ext: new SimpleConflictFreeReasoner().getModels(theory)) {
            result.add(new ArgumentSet(ext));
        }
        return result;
    }
}

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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for strong admissibility
 * A set of arguments E is strongly admissible iff all every argument A in E is defended by some argument B in E \ {A}, which itself is strongly defended by E \ {A}, 
 * i.e. no argument in E is defended only by itself
 *
 * @author Lars Bengel
 */
public class StronglyAdmissibleReasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        // check all admissible extensions of bbase
        Collection<Extension<DungTheory>> admExts = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ADM).getModels(bbase);
        Collection<Extension<DungTheory>> exts = new HashSet<>();
        for (Extension<DungTheory> ext: admExts) {
            if (bbase.isStronglyAdmissable(ext)) {
            	exts.add(ext);
            } 
        }
        return exts;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        return this.getModels(bbase).iterator().next();
    }

}

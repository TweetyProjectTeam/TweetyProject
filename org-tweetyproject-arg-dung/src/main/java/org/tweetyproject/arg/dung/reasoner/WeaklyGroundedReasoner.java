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
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Reasoner for weakly grounded semantics
 * a set of arguments E is w-grounded iff it is w-complete and minimal
 *
 * Note: unlike the grounded extension which is unique, there can be more than one w-grounded extension
 *
 * see: Baumann, Brewka, Ulbricht:  Revisiting  the  foundations  of  abstract argumentation-semantics based on weak admissibility and weak defense.
 *
 * @author Lars Bengel
 */
public class WeaklyGroundedReasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Collection<Extension<DungTheory>> w_complete = new WeaklyCompleteReasoner().getModels(bbase);

        Set<Extension<DungTheory>> result = new HashSet<>();
        boolean minimal;
        for(Extension<DungTheory> e1: w_complete){
            minimal = true;
            for(Extension<DungTheory> e2: w_complete)
                if(e1 != e2 && e1.containsAll(e2)){
                    minimal = false;
                    break;
                }
            if(minimal)
                result.add(e1);
        }
        return result;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        return this.getModels(bbase).iterator().next();
    }
}

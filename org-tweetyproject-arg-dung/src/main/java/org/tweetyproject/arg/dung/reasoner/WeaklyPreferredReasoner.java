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
 * Reasoner for weakly preferred semantics
 *
 * see: Baumann, Brewka, Ulbricht:  Revisiting  the  foundations  of  abstract argumentation-semantics based on weak admissibility and weak defense.
 *
 * @author Lars Bengel
 */
public class WeaklyPreferredReasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Collection<Extension<DungTheory>> w_adm_exts = new WeaklyAdmissibleReasoner().getModels(bbase);

        Set<Extension<DungTheory>> result = new HashSet<>();
        boolean maximal;
        for(Extension<DungTheory> e1: w_adm_exts){
            maximal = true;
            for(Extension<DungTheory> e2: w_adm_exts)
                if(e1 != e2 && e2.containsAll(e1)){
                    maximal = false;
                    break;
                }
            if(maximal)
                result.add(e1);
        }
        return result;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        return this.getModels(bbase).iterator().next();
    }
}

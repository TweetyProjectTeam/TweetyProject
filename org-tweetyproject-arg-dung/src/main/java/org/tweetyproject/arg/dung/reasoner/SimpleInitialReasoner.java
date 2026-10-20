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
package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Basic Implementation of a reasoner for initial sets
 * A set of arguments S is considered initial iff it is non-empty and minimal among the non-empty admissible sets
 *
 * @see "Yuming Xu and Claudette Cayrol. 'Initial sets in abstract argumentation frameworks' (2016)"
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument & Computation (2022)"
 *
 * @author Lars Bengel
 */
public class SimpleInitialReasoner extends AbstractExtensionReasoner {

    /**
     * Creates a new initial reasoner.
     */
    public SimpleInitialReasoner() {
        // default constructor
    }

    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Collection<Extension<DungTheory>> admExtensions = new SimpleAdmissibleReasoner().getModels(bbase);
        admExtensions.remove(new Extension<DungTheory>());

        Collection<Extension<DungTheory>> initExtensions = new HashSet<>();

        boolean minimal;
        for(Extension<DungTheory> e1: admExtensions){
            minimal = true;
            for(Extension<DungTheory> e2: admExtensions)
                if(e1 != e2 && e1.containsAll(e2)){
                    minimal = false;
                    break;
                }
            if(minimal)
                initExtensions.add(e1);
        }

        return initExtensions;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        Iterator<Extension<DungTheory>> models = this.getModels(bbase).iterator();
        return models.hasNext() ? models.next() : null;
    }
}

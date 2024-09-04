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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.extended.reasoner;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.extended.syntax.ExtendedTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * General reasoner for extended theories that computes extended by flattening and then computing the AF extensions
 *
 * @author Lars Bengel
 */
public class FlattenBasedExtendedExtensionReasoner extends AbstractExtendedExtensionReasoner {

    private final AbstractExtensionReasoner reasoner;

    /**
     * Initialize new reasoner for the given semantics
     * @param semantics some semantics
     */
    public FlattenBasedExtendedExtensionReasoner(Semantics semantics) {
        this.reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
    }

    /**
     * Initialize new reasoner for the given AF reasoner
     * @param reasoner some AF reasoner
     */
    public FlattenBasedExtendedExtensionReasoner(AbstractExtensionReasoner reasoner) {
        this.reasoner = reasoner;
    }

    @Override
    public Collection<Extension<ExtendedTheory>> getModels(ExtendedTheory bbase) {
        DungTheory flattenedTheory = bbase.flatten();
        System.out.println(flattenedTheory.prettyPrint());
        Collection<Extension<DungTheory>> extensions = reasoner.getModels(flattenedTheory);
        System.out.println("Got flattened models");
        Collection<Extension<ExtendedTheory>> result = new HashSet<>();
        for (Extension<DungTheory> ext: extensions) {
            Collection<Argument> restriction = new HashSet<>(ext);
            restriction.retainAll(bbase);
            result.add(new Extension<>(restriction));
        }
        return result;
    }

    @Override
    public Extension<ExtendedTheory> getModel(ExtendedTheory bbase) {
        return this.getModels(bbase).iterator().next();
    }
}

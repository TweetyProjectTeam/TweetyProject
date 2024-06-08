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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.extended.syntax.ExtendedTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Simple Reasoner for complete extensions of extended theories
 *
 * @author Lars Bengel
 */
public class SimpleExtendedCompleteReasoner extends AbstractExtendedExtensionReasoner {
    @Override
    public Collection<Extension<ExtendedTheory>> getModels(ExtendedTheory bbase) {
        Collection<Extension<ExtendedTheory>> result = new HashSet<>();
        for (Extension<ExtendedTheory> ext: new SimpleExtendedAdmissibleReasoner().getModels(bbase)) {
            if (bbase.isComplete(ext)) {
                result.add(ext);
            }
        }
        return result;
    }

    @Override
    public Extension<ExtendedTheory> getModel(ExtendedTheory bbase) {
        return getModels(bbase).iterator().next();
    }
}

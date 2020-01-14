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
package net.sf.tweety.arg.bipolar.reasoner.evidential;

import net.sf.tweety.arg.dung.reasoner.SimpleConflictFreeReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.bipolar.syntax.EvidentialArgSystem;

import java.util.*;

public class AdmissibleReasoner {
    public Collection<Extension> getModels(EvidentialArgSystem bbase) {
        Set<Extension> extensions = new HashSet<Extension>();
        // Check all conflict-free subsets
        SimpleConflictFreeReasoner cfReasoner = new SimpleConflictFreeReasoner();
        for(Extension ext: cfReasoner.getModels(bbase)){
            boolean admissible = true;
            for (Argument argument : ext) {
                admissible &= bbase.isAcceptable(argument, ext);
            }
            if (admissible)
                extensions.add(new Extension(ext));
        }
        return extensions;
    }

    public Extension getModel(EvidentialArgSystem bbase) {
        // as the empty set is always self-supporting we return that one.
        return new Extension();
    }
}

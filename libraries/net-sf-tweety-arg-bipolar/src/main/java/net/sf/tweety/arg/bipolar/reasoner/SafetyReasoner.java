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
package net.sf.tweety.arg.bipolar.reasoner;

import net.sf.tweety.arg.dung.reasoner.SimpleConflictFreeReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.bipolar.syntax.BipolarArgFramework;

import java.util.*;


public class SafetyReasoner  {

    public Collection<Extension> getModels(BipolarArgFramework bbase) {
        Set<Extension> extensions = new HashSet<Extension>();
        // Check only conflict-free subsets
        SimpleConflictFreeReasoner cfReasoner = new SimpleConflictFreeReasoner();
        for(Extension ext: cfReasoner.getModels(bbase.getCompleteAssociatedDungTheory())){
            Set<Argument> supported = bbase.getSupported(ext);
            supported.removeAll(ext);
            boolean isSafe = true;
            for (Argument a: ext) {
                for (Argument c: supported) {
                    if (bbase.isAttackedBy(c, a) || bbase.isSupportedAttack(a, c) ||
                            bbase.isMediatedAttack(a, c) || bbase.isSuperMediatedAttack(a, c)) {
                        isSafe = false;
                    }
                }
            }
            if (isSafe)
                extensions.add(ext);
        }

        return extensions;
    }

    public Extension getModel(DungTheory bbase) {
        // as the empty set is always safe we return that one.
        return new Extension();
    }
}

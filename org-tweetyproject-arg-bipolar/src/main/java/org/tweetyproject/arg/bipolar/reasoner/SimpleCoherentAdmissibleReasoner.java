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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.reasoner;

import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for coherent admissible sets (also called s-admissible sets) in bipolar argumentation.
 *
 * @see "Cayrol, C. and M.-C. Lagasquie-Schiex. 'On the acceptability of arguments in bipolar argumentation frameworks'. ECSQARU’05, pp. 378–389, 2005"
 *
 * @author Lars Bengel
 */
public class SimpleCoherentAdmissibleReasoner extends AbstractBipolarExtensionReasoner {
    @Override
    public Collection<Extension<BipolarArgumentationFramework>> getModels(BipolarArgumentationFramework bbase) {
        Collection<Extension<BipolarArgumentationFramework>> result = new HashSet<>();

        for (Extension<BipolarArgumentationFramework> ext: new SimpleCoherentReasoner().getModels(bbase)) {
            boolean defended = true;
            for (Argument arg: ext) {
                DungTheory theory = bbase.getAssociatedTheory();
                Collection<Argument> attackers = theory.getAttackers(arg);
                attackers.removeAll(theory.getAttacked(ext));
                if (!attackers.isEmpty()) {
                    defended = false;
                    break;
                }
            }
            if (defended)
                result.add(ext);
        }
        return result;
    }

    @Override
    public Extension<BipolarArgumentationFramework> getModel(BipolarArgumentationFramework bbase) {
        return getModels(bbase).iterator().next();
    }
}
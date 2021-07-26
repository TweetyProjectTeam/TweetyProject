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
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for solid admissibility
 * a set of arguments E solid-defends an argument a, iff for all attackers b of a it holds that
 * all arguments c which attack b are in E
 *
 * see: Liu, X., and Chen, W. Solid semantics and extension aggregation using quota rules under integrity constraints. (2021)
 *
 * @author Lars Bengel
 */
public class SolidAdmissibleReasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension> getModels(ArgumentationFramework bbase) {
        Collection<Extension> result = new HashSet<>();

        Collection<Extension> conflictFreeSets = new SimpleConflictFreeReasoner().getModels(bbase);
        for (Extension ext: conflictFreeSets) {
            Collection<Argument> solidlyDefended = this.getSolidlyDefended(ext, (DungTheory) bbase);
            if (solidlyDefended.containsAll(ext)) {
                result.add(ext);
            }
        }
        return null;
    }

    @Override
    public Extension getModel(ArgumentationFramework bbase) {
        return this.getModels(bbase).iterator().next();
    }

    /**
     * 
     * @param arg arg
     * @param ext ext
     * @param theory theory
     * @return isSolidlyDefendedBy
     */
    public boolean isSolidlyDefendedBy(Argument arg, Extension ext, DungTheory theory) {
        Collection<Argument> defenders = new HashSet<>();
        for (Argument attacker: theory.getAttackers(arg)) {
            defenders.addAll(theory.getAttackers(attacker));
        }
        return ext.containsAll(defenders);
    }

    /**
     * 
     * @param ext ext
     * @param theory theory
     * @return DungTheory
     */
    public Collection<Argument> getSolidlyDefended(Extension ext, DungTheory theory) {
        Collection<Argument> defended = new HashSet<>();
        for (Argument arg: theory) {
            if (this.isSolidlyDefendedBy(arg, ext, theory)) {
                defended.add(arg);
            }
        }
        return defended;
    }
}

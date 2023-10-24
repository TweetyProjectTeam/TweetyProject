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

package org.tweetyproject.arg.dung.principles;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleSccRecursiveReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * SCC Decomposability Principle
 * also: SCC-Recursiveness
 *
 * A semantics satisfies SCC decomposability iff for all AFs we have:
 * The extensions of F are the same as computing the extensions of each SCC individually and combining the result
 * 
 * Can return false even if principle is not violated, in case that the semantics is different to the base function. 
 * E.g. SCF2-semantics or CF2-semantics
 *
 * see: Pietro Baroni et al. “On the input/output behavior of argumentation frameworks” 2014
 *
 * @author Lars Bengel
 */
public class SccDecomposabilityPrinciple extends Principle {
    @Override
    public String getName() {
        return "SCC Decomposability";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return (kb instanceof DungTheory);
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
        DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);

        AbstractExtensionReasoner scc_reasoner = new SimpleSccRecursiveReasoner(ev);
        Collection<Extension<DungTheory>> exts_sccs = scc_reasoner.getModels(theory);

        return exts.equals(exts_sccs);
    }
}

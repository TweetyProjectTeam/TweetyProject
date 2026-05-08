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
import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for conflict-freeness in bipolar argumentation under a default support interpretation.
 * A set of arguments is strongly conflict-free iff it contains no conflict wrt. the classical attacks as well as supported and indirect attacks
 *
 * @author Lars Bengel
 */
public class SimpleStronglyConflictFreeReasoner extends AbstractBipolarExtensionReasoner {

    @Override
    public Collection<Extension<BipolarArgumentationFramework>> getModels(BipolarArgumentationFramework bbase) {
        DungTheory theory = new DungTheory();
        theory.addAll(bbase.getNodes());
        theory.addAllAttacks(bbase.getSupportedAttacks());
        theory.addAllAttacks(bbase.getIndirectAttacks());
        Collection<Extension<BipolarArgumentationFramework>> result = new HashSet<>();
        for (Extension<DungTheory> ext: new SimpleConflictFreeReasoner().getModels(theory)) {
            result.add(new Extension<>(ext));
        }
        return result;
    }

    @Override
    public Extension<BipolarArgumentationFramework> getModel(BipolarArgumentationFramework bbase) {
        return new Extension<>();
    }
}
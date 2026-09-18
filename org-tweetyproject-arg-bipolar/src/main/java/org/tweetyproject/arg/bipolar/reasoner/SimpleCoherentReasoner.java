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
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * A reasoner for coherent sets in bipolar argumentation under a default support interpretation.
 * A Set of arguments is coherent iff it is strongly conflict-free and does not support and attack some argument at the same time
 *
 * @author Lars Bengel
 */
public class SimpleCoherentReasoner extends AbstractBipolarExtensionReasoner {

    @Override
    public Collection<Extension<BipolarArgumentationFramework>> getModels(BipolarArgumentationFramework bbase) {
        Collection<Extension<BipolarArgumentationFramework>> result = new HashSet<>();

        DungTheory theory = new DungTheory();
        theory.addAll(bbase.getNodes());
        theory.addAllAttacks(bbase.getSupportedAttacks());
        theory.addAllAttacks(bbase.getIndirectAttacks());

        for (Extension<DungTheory> ext: new SimpleConflictFreeReasoner().getModels(theory)) {
            Collection<Argument> supported = new HashSet<>(ext);
            supported.addAll(bbase.getSupported(ext));
            Collection<Argument> intersect = new HashSet<>(theory.getAttacked(ext));
            intersect.retainAll(supported);
            if (intersect.isEmpty()) result.add(new Extension<>(ext));
        }
        return result;
    }

    @Override
    public Extension<BipolarArgumentationFramework> getModel(BipolarArgumentationFramework bbase) {
        return getModels(bbase).iterator().next();
    }
}
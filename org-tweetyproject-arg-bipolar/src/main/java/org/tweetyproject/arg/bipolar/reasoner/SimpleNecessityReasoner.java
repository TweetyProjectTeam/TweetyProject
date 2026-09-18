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
import org.tweetyproject.arg.bipolar.syntax.Support;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;

import java.util.Collection;

/**
 * Reasoner for bipolar argumentation under the deductive support interpretation.
 *
 * @see "F. Nouioua and V. Risch. 'Argumentation frameworks with necessities'. SUM’11, pp. 163–176, 2011"
 *
 * @author Lars Bengel
 */
public class SimpleNecessityReasoner extends AbstractBipolarExtensionReasoner {
    /** the underlying semantics */
    private final Semantics semantics;

    /**
     * Initializes a new reasoner with the given semantics
     * @param semantics some semantics
     */
    public SimpleNecessityReasoner(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public Collection<Extension<BipolarArgumentationFramework>> getModels(BipolarArgumentationFramework bbase) {
        BipolarArgumentationFramework theory = new BipolarArgumentationFramework();
        theory.addAll(bbase.getNodes());
        theory.addAllAttacks(bbase.getAttacks());
        for (Support supp: bbase.getSupports()) {
            theory.addSupport(supp.getSupported(), supp.getSupporter());
        }
        return new SimpleDeductiveReasoner(semantics).getModels(theory);
    }

    @Override
    public Extension<BipolarArgumentationFramework> getModel(BipolarArgumentationFramework bbase) {
        return getModels(bbase).iterator().next();
    }
}

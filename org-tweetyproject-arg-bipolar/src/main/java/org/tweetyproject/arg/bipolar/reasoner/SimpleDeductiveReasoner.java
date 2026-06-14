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
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for bipolar argumentation under the deductive support interpretation.
 * Under this interpretation we account for mediated attacks.
 *
 * @see "Boella, G., Dov Gabbay, L. van der Torre, and S. Villata. 'Support in abstract argumentation'. COMMA’10, 2010."
 *
 * @author Lars Bengel
 */
public class SimpleDeductiveReasoner extends AbstractBipolarExtensionReasoner {

    /** the underlying reasoner */
    private final AbstractExtensionReasoner reasoner;

    /**
     * Initializes a new reasoner based on the given semantics
     * @param semantics some semantics
     */
    public SimpleDeductiveReasoner(Semantics semantics) {
        this.reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
    }

    @Override
    public Collection<Extension<BipolarArgumentationFramework>> getModels(BipolarArgumentationFramework bbase) {
        DungTheory theory = new DungTheory();
        theory.addAll(bbase.getNodes());
        theory.addAllAttacks(bbase.getMediatedAttacks());
        Collection<Extension<BipolarArgumentationFramework>> result = new HashSet<>();
        for (Extension<DungTheory> ext: reasoner.getModels(theory)) {
            result.add(new Extension<>(ext));
        }
        return result;
    }

    @Override
    public Extension<BipolarArgumentationFramework> getModel(BipolarArgumentationFramework bbase) {
        return getModels(bbase).iterator().next();
    }
}

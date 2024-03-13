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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class defines 'standard' equivalence for {@link DungTheory Argumentation Frameworks} wrt. some {@link Semantics},
 * i.e., two AFs are standard equivalent if they possess the same set of
 * {@link org.tweetyproject.arg.dung.semantics.Extension Extensions} wrt. some {@link Semantics}.
 *
 * @author Julian Sander
 */
public class StandardEquivalence implements Equivalence<DungTheory> {

	/** the semantics for this equivalence instance */
    private final Semantics semantics;

    /**
	 * Initializes a new instance of this equivalence notion for the given semantics
     * @param semantics some semantics
     */
    public StandardEquivalence(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public boolean isEquivalent(Collection<DungTheory> objects) {
        DungTheory first = objects.iterator().next();
        for (DungTheory theory : objects) {
            if (theory == first) {
                continue;
            }
            if (!this.isEquivalent(theory, first)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEquivalent(DungTheory obj1, DungTheory obj2) {
        AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.semantics);
        return reasoner.getModels(obj1).equals(reasoner.getModels(obj2));
    }

	@Override
	public String getName() {
		return "Standard Equivalence";
	}
}

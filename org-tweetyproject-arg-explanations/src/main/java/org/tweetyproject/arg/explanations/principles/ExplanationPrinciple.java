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
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.explanations.principles;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.reasoner.acceptance.AbstractAcceptanceExplanationReasoner;
import org.tweetyproject.commons.postulates.Postulate;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

import java.util.Collection;

/**
 * Abstract class for modeling principles/postulates for explanations in abstract argumentation
 *
 * @author Lars Bengel
 */
public abstract class ExplanationPrinciple implements Postulate<Argument> {
    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return kb instanceof DungTheory;
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, PostulateEvaluatable<Argument> ev) {
        if(ev instanceof AbstractAcceptanceExplanationReasoner)
            return this.isSatisfied(kb, (AbstractAcceptanceExplanationReasoner) ev);
        throw new RuntimeException("PostulateEvaluatable of type AbstractAcceptanceExplanationReasoner expected.");
    }

    /**
     * Determines whether this principle is satisfied by the given reasoner for the given instance
     * @param kb some knowledge base
     * @param ev some reasoner
     * @return TRUE, iff this principle is satisfied by the given reasoner for the given instance
     */
    public abstract boolean isSatisfied(Collection<Argument> kb, AbstractAcceptanceExplanationReasoner ev);

}

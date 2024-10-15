/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.causal.reasoner;

import org.tweetyproject.arg.deductive.syntax.DeductiveKnowledgeBase;
import org.tweetyproject.causal.semantics.CausalStatement;
import org.tweetyproject.causal.syntax.InducedTheory;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.Collection;

public class ArgumentationBasedCausalReasoner {

    public InducedTheory getInducedTheory(Collection<PlFormula> observations) {

        DeductiveKnowledgeBase knowledgeBase = new DeductiveKnowledgeBase();
        // TODO incorporate observations into argument construction
        // Utilise DeductiveArgumentation package ??
        return null;
    }
    public boolean query(CausalStatement statement) {
        return true;
    }

    public boolean query(Collection<PlFormula> observations, PlFormula effect) {
        return true;
    }

    public Collection<PlFormula> getConclusions(PlFormula observation) {
        return null;
    }

    public Collection<PlFormula> getConclusions(Collection<PlFormula> observations) {
        return null;
    }
}

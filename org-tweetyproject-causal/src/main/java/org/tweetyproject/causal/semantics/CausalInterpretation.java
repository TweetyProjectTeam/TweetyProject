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
package org.tweetyproject.causal.semantics;

import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;

/**
 * Representation of a propositional interpretation of a causal knowledge base
 *
 * @author Lars Bengel
 * TODO add explicit encoding of rejected atoms
 */
public class CausalInterpretation extends InterpretationSet<Proposition, CausalKnowledgeBase, PlFormula> {

    /**
     * Initializes a new empty causal interpretation
     */
    public CausalInterpretation() {
        super();
    }

    /**
     * Initializes a new causal interpretation
     * @param formulas the set of proposition that are true in the interpretation
     */
    public CausalInterpretation(Collection<Proposition> formulas) {
        super(formulas);
    }

    @Override
    public boolean satisfies(PlFormula formula) throws IllegalArgumentException {
        return this.contains(formula);
    }

    @Override
    public boolean satisfies(CausalKnowledgeBase beliefBase) throws IllegalArgumentException {
        throw new IllegalArgumentException("Satisfaction of belief bases by causal interpretations is undefined.");
    }
}

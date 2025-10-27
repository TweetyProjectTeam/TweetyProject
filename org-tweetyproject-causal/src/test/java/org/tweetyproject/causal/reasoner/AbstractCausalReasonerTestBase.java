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
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.causal.reasoner;

import org.junit.jupiter.api.Test;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractCausalReasonerTestBase<Reasoner extends  AbstractCausalReasoner> {
    protected abstract Reasoner createReasoner();

    @Test
    public void example1() throws Throwable {
        Proposition corona = new Proposition("corona");
        Proposition influenza = new Proposition("influenza");
        Proposition atRisk = new Proposition("at-risk");
        Proposition covid = new Proposition("covid");
        Proposition flu = new Proposition("flu");
        Proposition shortOfBreath = new Proposition("short-of-breath");
        Proposition fever = new Proposition("fever");
        Proposition chills = new Proposition("chills");
        Collection<PlFormula> modelEquations = List.of(
                new Equivalence(covid, corona),
                new Equivalence(flu, influenza),
                new Equivalence(fever, covid.combineWithOr(flu)),
                new Equivalence(chills, fever),
                new Equivalence(shortOfBreath, covid.combineWithAnd(atRisk))
        );
        StructuralCausalModel model = new StructuralCausalModel(modelEquations);
        List<PlFormula> assumptions = List.of(atRisk, corona, new Negation(influenza));
        CausalKnowledgeBase knowledgeBase = new CausalKnowledgeBase(model, assumptions);
        Collection<PlFormula> observations = List.of(covid);
        AbstractCausalReasoner reasoner = createReasoner();

        Collection<PlFormula> conclusions = reasoner.getConclusions(knowledgeBase, observations);

        Collection<PlFormula> expectedConclusions = Set.of(
                atRisk,
                corona,
                new Negation(influenza),
                covid,
                shortOfBreath,
                fever,
                chills,
                new Negation(flu)
        );
        assertEquals(expectedConclusions, conclusions);
    }
}

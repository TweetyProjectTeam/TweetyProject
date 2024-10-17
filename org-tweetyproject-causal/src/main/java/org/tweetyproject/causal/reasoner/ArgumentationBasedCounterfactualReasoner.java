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

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.semantics.CausalInterpretation;
import org.tweetyproject.causal.semantics.CausalStatement;
import org.tweetyproject.causal.semantics.CounterfactualStatement;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ArgumentationBasedCounterfactualReasoner extends AbstractArgumentationBasedCausalReasoner {

    public DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions) {
        StructuralCausalModel twinModel = cbase.getCausalModel();
        for (Proposition atom : interventions.keySet()) {
            twinModel = twinModel.intervene(atom, interventions.get(atom));
        }
        return new ArgumentationBasedCausalReasoner().getInducedTheory(new CausalKnowledgeBase(twinModel, cbase.getAssumptions()), observations);
    }

    @Override
    public DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations) {
        return getInducedTheory(cbase, observations, new HashMap<>());
    }


    @Override
    public Collection<CausalInterpretation> getModels(CausalKnowledgeBase cbase, Collection<PlFormula> observations) {
        return null;
    }

    public boolean query(CausalKnowledgeBase cbase, CounterfactualStatement statement) {
        return query(cbase, statement.getObservations(), statement.getInterventions(), statement.getConclusion());
    }

    /**
     * Determines whether the given causal statements holds under the causal knowledge base
     *
     * @param cbase      some causal knowledge base
     * @param statement  some causal statement
     * @return TRUE iff the causal statement holds under the causal knowledge base
     */
    public boolean query(CausalKnowledgeBase cbase, CausalStatement statement) {
        return query(cbase, statement.getObservations(), statement.getConclusion());
    }
}

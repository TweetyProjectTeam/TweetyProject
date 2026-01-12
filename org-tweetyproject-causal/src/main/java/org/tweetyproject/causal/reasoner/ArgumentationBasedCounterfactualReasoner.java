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
import org.tweetyproject.causal.semantics.CounterfactualStatement;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Implements the approach to argumentation-based counterfactual reasoning as described in<br>
 * <br>
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm,
 * 'Argumentation-based Causal and Counterfactual Reasoning',
 * 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), (2022)
 *
 * @author Lars Bengel
 */
public class ArgumentationBasedCounterfactualReasoner extends AbstractArgumentationBasedCausalReasoner {

    @Override
    public DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions) {
        return new ArgumentationBasedCausalReasoner().getInducedTheory(cbase, observations, interventions);
    }

    /**
     * Evaluates the given counterfactual statement against the causal knowledge base.
     *
     * @param cbase the causal knowledge base
     * @param statement the counterfactual statement
     * @return {@code true} if the statement holds, {@code false} otherwise
     */
    public boolean query(CausalKnowledgeBase cbase, CounterfactualStatement statement) {
        return query(cbase, statement.getObservations(), statement.getInterventions(), statement.getConclusion());
    }

    @Override
    public Collection<CausalInterpretation> getModels(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions) {
        return super.getModels(cbase.getTwinVersion(), observations, interventions);
    }

    @Override
    public Collection<PlFormula> getConclusions(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions) {
        Collection<PlFormula> result = new HashSet<>();
        Collection<CausalInterpretation> models = getModels(cbase, observations, interventions);

        cbase = cbase.getTwinVersion();

        for (Proposition prop : cbase.getSignature()) {
            result.add(prop);
            result.add(new Negation(prop));
        }
        for (CausalInterpretation model : models) {
            for (Proposition prop : cbase.getSignature()) {
                if (model.contains(prop)) {
                    if (!result.contains(prop) && result.contains(new Negation(prop))) {
                        result.remove(prop);
                    }
                    result.remove(new Negation(prop));
                } else {
                    if (result.contains(prop) && !result.contains(new Negation(prop))) {
                        result.remove(new Negation(prop));
                    }
                    result.remove(prop);
                }
            }
        }
        return result;
    }
}

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
import org.tweetyproject.causal.semantics.CounterfactualStatement;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
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
        StructuralCausalModel twinModel = cbase.getCausalModel().getTwinModel();
        return new ArgumentationBasedCausalReasoner().getInducedTheory(new CausalKnowledgeBase(twinModel, cbase.getAssumptions()), observations, interventions);
    }

    public boolean query(CausalKnowledgeBase cbase, CounterfactualStatement statement) {
        return query(cbase, statement.getObservations(), statement.getInterventions(), statement.getConclusion());
    }
}

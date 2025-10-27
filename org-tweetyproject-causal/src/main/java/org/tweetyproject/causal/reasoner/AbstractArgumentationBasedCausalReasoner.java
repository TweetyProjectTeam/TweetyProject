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

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.semantics.CausalInterpretation;
import org.tweetyproject.causal.syntax.CausalArgument;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.logics.pl.reasoner.AbstractPlReasoner;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Base class for argumentation-based causal reasoners as described in<br>
 * <br>
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm,
 * 'Argumentation-based Causal and Counterfactual Reasoning',
 * 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), (2022)
 *
 * @author Lars Bengel
 */
public abstract class AbstractArgumentationBasedCausalReasoner extends AbstractCausalReasoner {
    /** Internal reasoner */
    protected final AbstractPlReasoner reasoner = new SimplePlReasoner();
    /** Internal reasoner for stable semantics */
    protected final AbstractExtensionReasoner extensionReasoner = new SimpleStableReasoner();

    /**
     * Constructs a logical argumentation framework from a given causal knowledge base and some observations
     *
     * @param cbase         some causal knowledge base
     * @param observations  some logical formulae representing the observations of causal atoms
     * @param interventions a set of interventions on causal atoms
     * @return the argumentation framework induced from the causal knowledge base and the observations
     */
    public abstract DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions);

    /**
     * Constructs a logical argumentation framework from a given causal knowledge base and some observations
     *
     * @param cbase         some causal knowledge base
     * @param observations  some logical formulae representing the observations of causal atoms
     * @return the argumentation framework induced from the causal knowledge base and the observations
     */
    public DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations) {
        return getInducedTheory(cbase, observations, new HashMap<>());
    }

    @Override
    public Collection<CausalInterpretation> getModels(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions) {
        Collection<CausalInterpretation> result = new HashSet<>();
        DungTheory theory = this.getInducedTheory(cbase, observations, interventions);
        Collection<Extension<DungTheory>> extensions = extensionReasoner.getModels(theory);
        for (Extension<DungTheory> extension : extensions) {
            CausalInterpretation interpretation = new CausalInterpretation();
            for (Argument argument : extension) {
                PlFormula conclusion = ((CausalArgument) argument).getConclusion();
                if (conclusion instanceof Proposition) {
                    interpretation.add((Proposition) conclusion);
                }
            }
            result.add(interpretation);
        }
        return result;
    }
}

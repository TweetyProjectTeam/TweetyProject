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

import org.tweetyproject.causal.semantics.CausalStatement;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.Collection;
import java.util.HashSet;

/**
 * General abstract reasoner for basic causal reasoning with {@link CausalKnowledgeBase}
 *
 * @author Lars Bengel
 */
public abstract class AbstractCausalReasoner implements QualitativeReasoner<CausalKnowledgeBase,PlFormula> {

    /**
     * Determines whether the given effect is entailed by the causal knowledge base together with the observations
     *
     * @param cbase         some causal knowledge base
     * @param observations  some logical formulae over atoms of the causal knowledge base
     * @param effect        some logical formula over atoms of the causal knowledge base
     * @return TRUE iff the causal knowledge base together with the observations logically entails the effect
     */
    public abstract boolean query(CausalKnowledgeBase cbase, Collection<PlFormula> observations, PlFormula effect);

    /**
     * Determines whether the given effect is entailed by the causal knowledge base together with the observation
     *
     * @param cbase         some causal knowledge base
     * @param observation  some logical formula over atoms of the causal knowledge base
     * @param effect        some logical formula over atoms of the causal knowledge base
     * @return TRUE iff the causal knowledge base together with the observation logically entails the effect
     */
    public boolean query(CausalKnowledgeBase cbase, PlFormula observation, PlFormula effect) {
        Collection<PlFormula> observations = new HashSet<>();
        observations.add(observation);
        return query(cbase, observations, effect);
    }

    @Override
    public Boolean query(CausalKnowledgeBase cbase, PlFormula effect) {
        return query(cbase, new HashSet<>(), effect);
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

    /**
     *
     * @param cbase
     * @param observations
     * @return
     */
    public abstract Collection<PlFormula> getConclusions(CausalKnowledgeBase cbase, Collection<PlFormula> observations);

    /**
     *
     * @param cbase
     * @param observation
     * @return
     */
    public Collection<PlFormula> getConclusions(CausalKnowledgeBase cbase, PlFormula observation) {
        Collection<PlFormula> observations = new HashSet<>();
        observations.add(observation);
        return getConclusions(cbase, observations);
    }

    /**
     *
     * @param cbase
     * @return
     */
    public Collection<PlFormula> getConclusions(CausalKnowledgeBase cbase) {
        return getConclusions(cbase, new HashSet<>());
    }

    @Override
    public boolean isInstalled() {
        return true;
    }
}

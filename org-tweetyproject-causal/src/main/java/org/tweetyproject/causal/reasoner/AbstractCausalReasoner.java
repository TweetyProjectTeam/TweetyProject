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

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.causal.semantics.CausalStatement;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * General abstract reasoner for basic causal reasoning with {@link CausalKnowledgeBase}
 *
 * @author Lars Bengel
 */
public abstract class AbstractCausalReasoner implements QualitativeReasoner<CausalKnowledgeBase,PlFormula>, ModelProvider<PlFormula,CausalKnowledgeBase, Interpretation<CausalKnowledgeBase,PlFormula>> {
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
     * @param observation   some logical formula over atoms of the causal knowledge base
     * @param effect        some logical formula over atoms of the causal knowledge base
     * @return TRUE iff the causal knowledge base together with the observation logically entails the effect
     */
    public boolean query(CausalKnowledgeBase cbase, PlFormula observation, PlFormula effect) {
        Collection<PlFormula> observations = new HashSet<>();
        observations.add(observation);
        return query(cbase, observations, effect);
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

    @Override
    public Boolean query(CausalKnowledgeBase cbase, PlFormula effect) {
        return query(cbase, new HashSet<>(), effect);
    }

    /**
     * Computes the set of all literal expressions that can be concluded from the causal knowledge base and observations
     *
     * @param cbase         some causal knowledge base
     * @param observations  some logical formulae over atoms of the causal knowledge base
     * @return the set of expressions that can be concluded from the given knowledge
     */
    public abstract Collection<PlFormula> getConclusions(CausalKnowledgeBase cbase, Collection<PlFormula> observations);

    /**
     * Computes the set of all literal expressions that can be concluded from the causal knowledge base and observation
     *
     * @param cbase         some causal knowledge base
     * @param observation   some logical formula over atoms of the causal knowledge base
     * @return the set of expressions that can be concluded from the given knowledge
     */
    public Collection<PlFormula> getConclusions(CausalKnowledgeBase cbase, PlFormula observation) {
        Collection<PlFormula> observations = new HashSet<>();
        observations.add(observation);
        return getConclusions(cbase, observations);
    }

    /**
     * Computes the set of all literal expressions that can be concluded from the causal knowledge base
     *
     * @param cbase some causal knowledge base
     * @return the set of expressions that can be concluded from the given knowledge
     */
    public Collection<PlFormula> getConclusions(CausalKnowledgeBase cbase) {
        return getConclusions(cbase, new HashSet<>());
    }

    @Override
    public Collection<Interpretation<CausalKnowledgeBase, PlFormula>> getModels(CausalKnowledgeBase bbase) {
        // TODO implement
        return List.of();
    }

    @Override
    public Interpretation<CausalKnowledgeBase, PlFormula> getModel(CausalKnowledgeBase bbase) {
        // TODO implement
        return null;
    }

    @Override
    public boolean isInstalled() {
        return true;
    }
}

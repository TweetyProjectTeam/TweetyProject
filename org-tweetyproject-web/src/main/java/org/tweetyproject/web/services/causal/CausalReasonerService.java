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
package org.tweetyproject.web.services.causal;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.reasoner.acceptance.DialecticalSequenceExplanationReasoner;
import org.tweetyproject.arg.explanations.semantics.DialectialSequenceExplanation;
import org.tweetyproject.causal.reasoner.ArgumentationBasedCausalReasoner;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for querying causal conclusions and sequence explanations.
 *
 * @author Oleksandr Dzhychko
 */
@Service
public final class CausalReasonerService {
    /**
     * Default constructor.
     */
    public CausalReasonerService() {
    }

    /** the underlying causal reasoner */
    private final ArgumentationBasedCausalReasoner causalReasoner = new ArgumentationBasedCausalReasoner();
    /** the provider of explanations */
    private final DialecticalSequenceExplanationReasoner explanationReasoner = new DialecticalSequenceExplanationReasoner();

    /**
     * Computes the causal conclusions of the causal knowledge based given the observations
     * @param causalKnowledgeBase the causal knowledge base
     * @param observations        a set of observations about causal atoms
     * @param conclusionFilter    the set of propositions to consider
     * @return the causal conclusions of the causal knowledge based given the observations
     */
    public Collection<PlFormula> queryConclusions(CausalKnowledgeBase causalKnowledgeBase, Collection<PlFormula> observations, Set<Proposition> conclusionFilter) {
        Collection<PlFormula> conclusions = causalReasoner.getConclusions(causalKnowledgeBase, observations);
        conclusions = filterConclusions(conclusions, conclusionFilter);
        return conclusions;
    }

    /**
     * Filter out the relevant conclusions
     * @param conclusions       all conclusions
     * @param conclusionFilter  the propositions to consider
     * @return the filtered conclusions
     */
    private static Collection<PlFormula> filterConclusions(Collection<PlFormula> conclusions, @Nullable Set<Proposition> conclusionFilter) {
        if (conclusionFilter == null) {
            return conclusions;
        }
        return conclusions.stream()
                .filter(formula -> isConclusionInFilter(formula, conclusionFilter))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Determine whether the conclusion is relevant
     * @param conclusion        all conclusions
     * @param conclusionFilter  the relevant conclusions
     * @return "true" if the conclusion is relevant
     */
    public static boolean isConclusionInFilter(PlFormula conclusion, @NonNull Set<Proposition> conclusionFilter) {
        return conclusionFilter.stream()
                .anyMatch(proposition -> conclusion.getAtoms().contains(proposition));
    }

    /**
     * Determine mapping of significant atoms per atom
     * @param causalKnowledgeBase   some causal knowledge base
     * @param observations          a collection of observations
     * @param conclusionFilter      the relevant propositions
     * @return mapping of significant atoms per atom
     */
    public Map<Proposition, Collection<Proposition>> queryPerAtomSignificantAtoms(CausalKnowledgeBase causalKnowledgeBase, Collection<PlFormula> observations, Set<Proposition> conclusionFilter) {
        return causalReasoner.getSignificantAtoms(causalKnowledgeBase, observations, Map.of(), conclusionFilter);
    }

    /**
     * Determine the collection of sequence explanations
     * @param causalKnowledgeBase some causal knowledge base
     * @param observations        some observations
     * @param conclusionFilter    the relevant atoms
     * @return the collection of sequence explanations
     */
    public SequenceExplanations querySequenceExplanations(CausalKnowledgeBase causalKnowledgeBase, Collection<PlFormula> observations, Set<Proposition> conclusionFilter) {
        var theory = causalReasoner.getInducedTheory(causalKnowledgeBase, observations, Map.of());
        var perAtomArgumentsWithAtomInConclusion = causalReasoner.getPerAtomArgumentsWithAtomInConclusion(theory, conclusionFilter);

        var perAtomPerSequenceExplanations = new LinkedHashMap<Proposition, List<DialectialSequenceExplanation>>();
        for (var atomWithArgumentsAtomInConclusion : perAtomArgumentsWithAtomInConclusion.entrySet()) {
            var atom = atomWithArgumentsAtomInConclusion.getKey();
            var allSequenceExplanations = new ArrayList<DialectialSequenceExplanation>();
            perAtomPerSequenceExplanations.put(atom, allSequenceExplanations);
            for (var argument : atomWithArgumentsAtomInConclusion.getValue().stream().findFirst().stream().collect(Collectors.toUnmodifiableList())) {
                var explanations = explanationReasoner.getExplanations(theory, argument);
                var sequenceExplanations = explanations.stream()
                        .map(explanation -> (DialectialSequenceExplanation) explanation)
                        .collect(Collectors.toUnmodifiableList());
                allSequenceExplanations.addAll(sequenceExplanations);
            }
        }
        return new SequenceExplanations(theory.getAttacks(), perAtomPerSequenceExplanations);
    }

    /**
     * Class bundling sequence explanations and additional information
     */
    public static final class SequenceExplanations {
        /** set of associated attacks */
        private final Set<Attack> attacks;
        /** map of atoms to their sequence explanations */
        private final Map<Proposition, List<DialectialSequenceExplanation>> perAtomSequenceExplanations;

        /**
         * Initialize new instance
         * @param attacks                       a set of attacks
         * @param perAtomSequenceExplanations   Map of atoms to sequence explanations
         */
        public SequenceExplanations(Set<Attack> attacks,
                                    Map<Proposition, List<DialectialSequenceExplanation>> perAtomSequenceExplanations) {
            this.attacks = attacks;
            this.perAtomSequenceExplanations = perAtomSequenceExplanations;
        }

        /**
         * Returns the attacks that were used to build the sequence explanations.
         *
         * @return set of attacks
         */
        public Set<Attack> getAttacks() {
            return attacks;
        }

        /**
         * Returns the sequence explanations grouped by conclusion atom.
         *
         * @return map from proposition to sequence explanations
         */
        public Map<Proposition, List<DialectialSequenceExplanation>> getPerAtomSequenceExplanations() {
            return perAtomSequenceExplanations;
        }
    }

    /**
     * Compute the induced dung theory
     * @param causalKnowledgeBase   some causal knowledge base
     * @param observations          some observations
     * @return the induced dung theory
     */
    public DungTheory queryArgumentationFramework(CausalKnowledgeBase causalKnowledgeBase, Collection<PlFormula> observations) {
        return causalReasoner.getInducedTheory(causalKnowledgeBase, observations, Map.of());
    }
}

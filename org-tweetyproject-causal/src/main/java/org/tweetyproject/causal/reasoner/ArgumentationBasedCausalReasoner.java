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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.semantics.CausalStatement;
import org.tweetyproject.causal.semantics.InterventionalStatement;
import org.tweetyproject.causal.syntax.CausalArgument;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Implements the approach to argumentation-based causal reasoning as described in<br>
 * <br>
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm,
 * 'Argumentation-based Causal and Counterfactual Reasoning',
 * 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), (2022)
 *
 * @author Lars Bengel
 */
public class ArgumentationBasedCausalReasoner extends AbstractArgumentationBasedCausalReasoner {
    @Override
    public DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions) {
        StructuralCausalModel model = cbase.getCausalModel();
        for (Proposition atom : interventions.keySet()) {
            model = model.intervene(atom, interventions.get(atom));
        }
        PlBeliefSet base = new PlBeliefSet(model.getStructuralEquations());
        base.addAll(observations);

        Collection<PlFormula> literals = new HashSet<>();
        for (Proposition atom : base.getSignature()) {
            literals.add(atom);
            literals.add(new Negation(atom));
        }
        // Construct all possible arguments
        // TODO enhance arguments by adding all applied rules (see AbstractPlReasoner.getKernels())
        DungTheory theory = new DungTheory();
        for (PlFormula literal : literals) {
            if (reasoner.query(base, literal)) {
                // follows from observations directly
                CausalArgument argument = new CausalArgument(new HashSet<>(), literal);
                //System.out.printf("%s: %s%n",literal,reasoner.getKernels(base, literal));
                theory.add(argument);
                continue;
            }
            // Compute all sets of assumptions that entail the atom
            // TODO optimise using {@link SubsetIterator}
            Collection<Collection<PlFormula>> candidates = new HashSet<>();
            for (Collection<PlFormula> assumptions : new SetTools<PlFormula>().subsets(cbase.getAssumptions())) {
                if (reasoner.query(new PlBeliefSet(assumptions), new Contradiction())) continue;
                PlBeliefSet knowledge = new PlBeliefSet(base);
                knowledge.addAll(assumptions);
                if (reasoner.query(knowledge, new Contradiction())) continue;
                if (reasoner.query(knowledge, literal)) {
                    candidates.add(assumptions);
                }
            }

            // Find the minimal sets
            for (Collection<PlFormula> a1 : candidates) {
                boolean isMinimal = true;
                for (Collection<PlFormula> a2: candidates) {
                    if (a1.equals(a2)) continue;
                    if (a1.containsAll(a2)) {
                        isMinimal = false;
                        break;
                    }
                }
                if (isMinimal) {
                    CausalArgument argument = new CausalArgument(a1, literal);
                    theory.add(argument);
                }
            }
        }

        // Construct undercut attacks
        for (Argument arg1 : theory) {
            for (Argument arg2 : theory) {
                for(PlFormula premise : ((CausalArgument) arg2).getPremises()) {
                    if(((CausalArgument) arg1).getConclusion().complement().equals(premise)) {
                        theory.addAttack(arg1, arg2);
                        break;
                    }
                }
            }
        }
        return theory;
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
     * Determines whether the given causal statements holds under the causal knowledge base
     *
     * @param cbase      some causal knowledge base
     * @param statement  some causal statement with interventions
     * @return TRUE iff the causal statement holds under the causal knowledge base
     */
    public boolean query(CausalKnowledgeBase cbase, InterventionalStatement statement) {
        return query(cbase, statement.getObservations(), statement.getInterventions(), statement.getConclusion());
    }
}

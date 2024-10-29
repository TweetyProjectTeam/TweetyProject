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
package org.tweetyproject.causal.examples;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.reasoner.AbstractArgumentationBasedCausalReasoner;
import org.tweetyproject.causal.reasoner.ArgumentationBasedCausalReasoner;
import org.tweetyproject.causal.reasoner.ArgumentationBasedCounterfactualReasoner;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Example usage of the {@link ArgumentationBasedCounterfactualReasoner} based on the example from <br>
 * <br>
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm,
 * 'Argumentation-based Causal and Counterfactual Reasoning',
 * 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), (2022)
 *
 * @author Lars Bengel
 */
public class CounterfactualReasoningExample {
    /**
     *
     * @param args cmdline arguments (unused)
     */
    public static void main(String[] args) throws StructuralCausalModel.CyclicDependencyException {
        // Background atoms
        Proposition corona = new Proposition("corona");
        Proposition influenza = new Proposition("influenza");
        Proposition atRisk = new Proposition("at-risk");

        // Explainable atoms
        Proposition covid = new Proposition("covid");
        Proposition flu = new Proposition("flu");
        Proposition shortOfBreath = new Proposition("short-of-breath");
        Proposition fever = new Proposition("fever");
        Proposition chills = new Proposition("chills");

        // Construct Causal Model
        StructuralCausalModel model = new StructuralCausalModel();
        // Add background atoms
        model.addBackgroundAtom(corona);
        model.addBackgroundAtom(influenza);
        model.addBackgroundAtom(atRisk);
        // Add structural equations
        model.addExplainableAtom(covid, corona);
        model.addExplainableAtom(flu, influenza);
        model.addExplainableAtom(fever, new Disjunction(covid, flu));
        model.addExplainableAtom(chills, fever);
        model.addExplainableAtom(shortOfBreath, new Conjunction(covid, atRisk));

        // Initialize causal knowledge base
        CausalKnowledgeBase cbase = new CausalKnowledgeBase(model);
        // Add assumptions
        cbase.addAssumption(new Negation(corona));
        cbase.addAssumption(new Negation(influenza));
        cbase.addAssumption(new Negation(atRisk));
        cbase.addAssumption(atRisk);

        System.out.println("Causal Knowledge Base: " + cbase);

        // Initialize Causal Reasoner and induce an argumentation framework
        AbstractArgumentationBasedCausalReasoner reasoner = new ArgumentationBasedCounterfactualReasoner();

        // Define variables for the example
        Collection<PlFormula> observations = new HashSet<>();
        observations.add(fever);

        PlFormula conclusion1 = model.getCounterfactualCopy(fever);
        PlFormula conclusion2 = new Negation(model.getCounterfactualCopy(fever));

        Map<Proposition,Boolean> interventions = new HashMap<>();
        interventions.put(model.getCounterfactualCopy(covid), false);


        // Example usage
        System.out.printf("Observing '%1$s' and intervening '%2$s' implies '%3$s': %4$s%n", observations, interventions, conclusion1, reasoner.query(cbase, observations, interventions, conclusion1));
        System.out.printf("Observing '%1$s' and intervening '%2$s' implies '%3$s': %4$s%n", observations, interventions, conclusion2, reasoner.query(cbase, observations, interventions, conclusion2));

        // modify observations
        observations.add(shortOfBreath);

        System.out.printf("Observing '%1$s' and intervening '%2$s' implies '%3$s': %4$s%n", observations, interventions, conclusion2, reasoner.query(cbase, observations, interventions, conclusion2));

        DungTheory theory = reasoner.getInducedTheory(cbase.getTwinVersion(), observations, interventions);
        System.out.println("Induced Argumentation Framework:\n" + theory.prettyPrint());
        System.out.println(reasoner.getModels(cbase, observations, interventions));
        System.out.println(reasoner.getConclusions(cbase, observations));
    }
}

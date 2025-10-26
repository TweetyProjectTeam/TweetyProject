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
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
import org.tweetyproject.causal.reasoner.ArgumentationBasedCausalReasoner;
import org.tweetyproject.causal.reasoner.SimpleCausalReasoner;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * Example usage of the {@link ArgumentationBasedCausalReasoner} based on the example from <br>
 * <br>
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm,
 * 'Argumentation-based Causal and Counterfactual Reasoning',
 * 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), (2022)
 *
 * @author Lars Bengel
 */
public class CausalReasoningExampleVirus {
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
        //cbase.addAssumption(corona);
        cbase.addAssumption(atRisk);
        //cbase.addAssumption(influenza);


        System.out.println("Causal Knowledge Base: " + cbase);


        // Define variables for the example
        Collection<PlFormula> observations = new HashSet<>();
        observations.add(fever);

        PlFormula conclusion1 = shortOfBreath;
        PlFormula conclusion2 = new Negation(shortOfBreath);

        // Initialize Causal Reasoner and induce an argumentation framework
        ArgumentationBasedCausalReasoner reasoner = new ArgumentationBasedCausalReasoner();
        DungTheory theory = reasoner.getInducedTheory(cbase,observations);

        System.out.println("Induced Argumentation Framework:");
        System.out.println(theory.prettyPrint());

        // Do some causal reasoning
        System.out.printf("Observing '%1$s' implies '%2$s': %3$s%n", observations, conclusion1, reasoner.query(cbase, observations, conclusion1));
        System.out.printf("Observing '%1$s' implies '%2$s': %3$s%n", observations, conclusion2, reasoner.query(cbase, observations, conclusion2));
        System.out.printf("Possible Conclusions of observing '%1$s': %2$s%n", observations, reasoner.getConclusions(cbase, observations));
        System.out.printf("Models: %s%n", reasoner.getModels(cbase, observations));

        // Visualisation of the induced argumentation framework
        //DungTheoryPlotter.plotFramework(theory, 3000, 2000, "Premises: " + observations + " \n Conclusion: " + shortOfBreath);
    }
}

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
import org.tweetyproject.causal.reasoner.AbstractCausalReasoner;
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
 * 'Argumentation-Based Probabilistic Causal Reasoning',
 * Conference on Advances in Robust Argumentation Machines, (2024)
 *
 * @author Lars Bengel
 */
public class CausalReasoningExampleSurfer {
    /**
     *
     * @param args cmdline arguments (unused)
     */
    public static void main(String[] args) throws StructuralCausalModel.CyclicDependencyException {
        // Background atoms
        Proposition giantWave = new Proposition("giant-wave");
        Proposition strongCurrent = new Proposition("strong-current");
        Proposition jellyfish = new Proposition("jellyfish");

        // Explainable atoms
        Proposition brokenBoard = new Proposition("broken-board");
        Proposition submersion = new Proposition("submersion");
        Proposition cramp = new Proposition("cramp");
        Proposition drowning = new Proposition("drowning");

        // Construct Causal Model
        StructuralCausalModel model = new StructuralCausalModel();
        // Add background atoms
        model.addBackgroundAtom(giantWave);
        model.addBackgroundAtom(strongCurrent);
        model.addBackgroundAtom(jellyfish);
        // Add structural equations
        model.addExplainableAtom(brokenBoard, giantWave);
        model.addExplainableAtom(submersion, new Conjunction(giantWave, strongCurrent));
        model.addExplainableAtom(cramp, new Disjunction(strongCurrent, jellyfish));
        model.addExplainableAtom(drowning, new Disjunction(cramp, submersion));

        // Initialize causal knowledge base
        CausalKnowledgeBase cbase = new CausalKnowledgeBase(model);
        // Add assumptions
        cbase.addAssumption(new Negation(strongCurrent));
        cbase.addAssumption(strongCurrent);
        cbase.addAssumption(giantWave);
        cbase.addAssumption(jellyfish);

        System.out.println("Causal Knowledge Base: " + cbase);

        // Define variables for the example
        Collection<PlFormula> observations = new HashSet<>();
        observations.add(drowning);

        PlFormula conclusion1 = submersion;
        PlFormula conclusion2 = new Negation(submersion);

        // Initialize Causal Reasoner and induce an argumentation framework
        ArgumentationBasedCausalReasoner reasoner = new ArgumentationBasedCausalReasoner();
        DungTheory theory = reasoner.getInducedTheory(cbase, observations);

        System.out.println("Induced Argumentation Framework:");
        System.out.println(theory.prettyPrint());

        // Do some causal reasoning
        System.out.printf("Observing '%s' implies '%s': %s%n", observations, conclusion1, reasoner.query(cbase, observations, conclusion1));
        System.out.printf("Observing '%s' implies '%s': %s%n", observations, conclusion2, reasoner.query(cbase, observations, conclusion2));
        System.out.printf("Possible Conclusions of observing '%1$s': %2$s%n", observations, reasoner.getConclusions(cbase, observations));
        System.out.printf("Models: %s%n", reasoner.getModels(cbase, observations));


    }
}

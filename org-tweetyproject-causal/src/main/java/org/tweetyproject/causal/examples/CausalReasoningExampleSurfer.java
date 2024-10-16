package org.tweetyproject.causal.examples;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.reasoner.ArgumentationBasedCausalReasoner;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashSet;

/**
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

        Collection<PlFormula> observations = new HashSet<>();
        observations.add(drowning);

        // Initialize Causal Reasoner and induce an argumentation framework
        ArgumentationBasedCausalReasoner reasoner = new ArgumentationBasedCausalReasoner();
        DungTheory theory = reasoner.getInducedTheory(cbase, observations);

        System.out.println("Induced Argumentation Framework:");
        System.out.println(theory.prettyPrint());

        // Do some causal reasoning
        System.out.println("Observing 'drowning' implies 'submersion': " + reasoner.query(cbase, observations, cramp));
        System.out.println("Observing 'drowning' implies 'not submersion': " + reasoner.query(cbase, observations, new Negation(submersion)));

        System.out.printf("Possible Conclusions of observing '%2$s': %1$s", reasoner.getConclusions(cbase, observations), observations);
    }
}

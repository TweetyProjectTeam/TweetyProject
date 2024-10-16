package org.tweetyproject.causal.examples;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.reasoner.ArgumentationBasedCausalReasoner;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashSet;

/**
 *
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
        cbase.addAssumption(atRisk);
        //cbase.addAssumption(influenza);

        System.out.println("Causal Knowledge Base: " + cbase);

        Collection<PlFormula> observations = new HashSet<>();
        observations.add(fever);

        // Initialize Causal Reasoner and induce an argumentation framework
        ArgumentationBasedCausalReasoner reasoner = new ArgumentationBasedCausalReasoner();
        DungTheory theory = reasoner.getInducedTheory(cbase, observations);

        System.out.println("Induced Argumentation Framework:");
        System.out.println(theory.prettyPrint());

        // Do some causal reasoning
        System.out.println("Observing 'fever' implies 'shortOfBreath': " + reasoner.query(cbase, observations, shortOfBreath));
        System.out.println("Observing 'fever' implies 'not shortOfBreath': " + reasoner.query(cbase, observations, new Negation(shortOfBreath)));

        System.out.printf("Possible Conclusions of observing '%2$s': %1$s", reasoner.getConclusions(cbase, observations), observations);
    }
}

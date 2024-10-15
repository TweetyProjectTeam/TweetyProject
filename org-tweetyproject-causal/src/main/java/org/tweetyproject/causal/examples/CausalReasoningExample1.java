package org.tweetyproject.causal.examples;

import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.Collection;
import java.util.HashSet;

public class CausalReasoningExample1 {
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

        // Define set of assumptions
        Collection<PlFormula> assumptions = new HashSet<>();
        assumptions.add(new Negation(corona));
        assumptions.add(new Negation(influenza));
        assumptions.add(new Negation(atRisk));
        assumptions.add(atRisk);

        // Initialize causal knowledge base
        CausalKnowledgeBase kbase = new CausalKnowledgeBase(model, assumptions);

        System.out.println(kbase);

        // TODO
    }
}

package org.tweetyproject.causal.reasoner;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.semantics.CounterfactualStatement;
import org.tweetyproject.causal.syntax.CausalArgument;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class ArgumentationBasedCounterfactualReasoner {
    /** Internal reasoner for stable semantics of the induced AF */
    protected AbstractExtensionReasoner stableReasoner = new SimpleStableReasoner();

    /**
     *
     * @param cbase
     * @param observations
     * @param interventions
     * @return
     */
    public DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations, Map<Proposition,Boolean> interventions) {
        StructuralCausalModel twinModel = cbase.getCausalModel();
        for (Proposition atom : interventions.keySet()) {
            twinModel = twinModel.intervene(atom, interventions.get(atom));
        }
        return new ArgumentationBasedCausalReasoner().getInducedTheory(new CausalKnowledgeBase(twinModel, cbase.getAssumptions()), observations);
    }

    /**
     *
     * @param cbase
     * @param statement
     * @return
     */
    public boolean query(CausalKnowledgeBase cbase, CounterfactualStatement statement) {
        DungTheory theory = getInducedTheory(cbase, statement.getObservations(), statement.getInterventions());
        Collection<Extension<DungTheory>> extensions = stableReasoner.getModels(theory);

        for (Extension<DungTheory> extension : extensions) {
            boolean concludesEffect = false;
            for (Argument argument : extension) {
                if (((CausalArgument) argument).getConclusion().equals(statement.getConclusion())) {
                    concludesEffect = true;
                    break;
                }
            }
            if (!concludesEffect) {
                return false;
            }
        }
        return true;
    }
}

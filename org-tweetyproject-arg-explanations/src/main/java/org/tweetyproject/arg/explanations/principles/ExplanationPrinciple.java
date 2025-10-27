package org.tweetyproject.arg.explanations.principles;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.explanations.reasoner.acceptance.AbstractAcceptanceExplanationReasoner;
import org.tweetyproject.commons.postulates.Postulate;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

import java.util.Collection;

public abstract class ExplanationPrinciple implements Postulate<Argument> {
    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return kb instanceof DungTheory;
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, PostulateEvaluatable<Argument> ev) {
        if(ev instanceof AbstractAcceptanceExplanationReasoner)
            return this.isSatisfied(kb, (AbstractAcceptanceExplanationReasoner) ev);
        throw new RuntimeException("PostulateEvaluatable of type AbstractAcceptanceExplanationReasoner expected.");
    }

    /**
     * Determines whether this principle is satisfied by the given reasoner for the given instance
     * @param kb some knowledge base
     * @param ev some reasoner
     * @return TRUE, iff this principle is satisfied by the given reasoner for the given instance
     */
    public abstract boolean isSatisfied(Collection<Argument> kb, AbstractAcceptanceExplanationReasoner ev);

}

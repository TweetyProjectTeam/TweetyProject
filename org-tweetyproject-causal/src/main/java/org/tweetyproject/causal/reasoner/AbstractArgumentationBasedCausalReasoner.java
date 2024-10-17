package org.tweetyproject.causal.reasoner;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.logics.pl.reasoner.AbstractPlReasoner;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.Collection;

/**
 *
 */
public abstract class AbstractArgumentationBasedCausalReasoner extends AbstractCausalReasoner {
    /** Internal reasoner */
    protected final AbstractPlReasoner reasoner = new SimplePlReasoner();
    /** Internal reasoner for stable semantics */
    protected final AbstractExtensionReasoner extensionReasoner = new SimpleStableReasoner();

    /**
     * Constructs a logical argumentation framework from a given causal knowledge base and some observations
     *
     * @param cbase        some causal knowledge base
     * @param observations some logical formulae representing the observations of causal atoms
     * @return the argumentation framework induced from the causal knowledge base and the observations
     */
    public abstract DungTheory getInducedTheory(CausalKnowledgeBase cbase, Collection<PlFormula> observations);
}

package org.tweetyproject.arg.dung.causal.reasoner;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.Collection;

public abstract class CausalReasoner {

    protected Collection<PlFormula> observations;

    /*
    remove observations from CKB
    CKB is a BeliefSet for the struct equations + a set of assumptions
    observations only in the causal reasoner
    causal reasoner has method to create AF, get conclusions, verify conclusions
     */
}

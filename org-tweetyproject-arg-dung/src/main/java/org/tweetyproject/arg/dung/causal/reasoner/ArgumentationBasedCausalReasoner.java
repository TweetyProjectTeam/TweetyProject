package org.tweetyproject.arg.dung.causal.reasoner;

import org.tweetyproject.arg.dung.causal.semantics.CausalStatement;
import org.tweetyproject.arg.dung.causal.syntax.InducedTheory;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.Collection;

public class ArgumentationBasedCausalReasoner {

    public InducedTheory getInducedTheory(Collection<PlFormula> observations) {
        // TODO incorporate observations into argument construction
        // Utilise DeductiveArgumentation package ??
        return null;
    }
    public boolean query(CausalStatement statement) {
        return true;
    }

    public boolean query(Collection<PlFormula> observations, PlFormula effect) {
        return true;
    }

    public Collection<PlFormula> getConclusions(PlFormula observation) {
        return null;
    }

    public Collection<PlFormula> getConclusions(Collection<PlFormula> observations) {
        return null;
    }
}

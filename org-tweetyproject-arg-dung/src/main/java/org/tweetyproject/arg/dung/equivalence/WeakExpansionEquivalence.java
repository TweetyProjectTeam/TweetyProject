package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

public class WeakExpansionEquivalence implements Equivalence<DungTheory> {

    private Semantics semantics;

    public WeakExpansionEquivalence(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public boolean isEquivalent(DungTheory theory1, DungTheory theory2) {
        switch (semantics) {
            case ADM,PR,CO -> {
                Collection<Extension<DungTheory>> exts1 = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics).getModels(theory1);
                Collection<Extension<DungTheory>> exts2 = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics).getModels(theory2);
                if (!new HashSet<>(theory1).equals(new HashSet<>(theory2))) return false;
                if (!exts1.equals(exts2)) return false;

                for (Extension<DungTheory> ext : exts1) {
                    Labeling lab1 = new Labeling(theory1, ext);
                    Labeling lab2 = new Labeling(theory2, ext);
                    if (!lab1.getArgumentsOfStatus(ArgumentStatus.UNDECIDED).equals(lab2.getArgumentsOfStatus(ArgumentStatus.UNDECIDED)))
                        return false;
                }
                return true;
            }
            case ST -> {
                Collection<Extension<DungTheory>> exts1 = new SimpleStableReasoner().getModels(theory1);
                Collection<Extension<DungTheory>> exts2 = new SimpleStableReasoner().getModels(theory2);
                if (exts1.isEmpty() && exts2.isEmpty()) return true;
                if (!new HashSet<>(theory1).equals(new HashSet<>(theory2))) return false;
                return exts1.equals(exts2);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported Semantics");
            }
        }
    }

    @Override
    public boolean isEquivalent(Collection<DungTheory> theories) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getName() {
        return "Weak Expansion Equivalence";
    }
}

package org.tweetyproject.arg.rankings.postulates;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.GeneralComparator;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents the Skeptical Sigma-Compatibility postulate for ranking-based
 * argumentation frameworks.
 * <p>
 * The class can be instantiated with either a specific
 * {@link AbstractExtensionReasoner} or a
 * predefined {@link Semantics} to automatically derive a suitable reasoner.
 * </p>
 *
 * @see RankingPostulate
 * @see AbstractExtensionReasoner
 * @see Semantics
 */
public class RaSkeptSigmaCompatibility extends RankingPostulate {

    /**
     * The extension-based reasoner used to derive extensions of the argumentation
     * framework.
     */
    AbstractExtensionReasoner reasoner;

    /**
     * Constructs a {@code RaSkeptSigmaCompatibility} postulate with the specified
     * extension reasoner.
     *
     * @param reasoner The {@link AbstractExtensionReasoner} used to compute
     *                 extensions of the argumentation framework.
     */
    public RaSkeptSigmaCompatibility(AbstractExtensionReasoner reasoner) {
        this.reasoner = reasoner;
    }

    /**
     * Constructs a {@code RaSkeptSigmaCompatibility} postulate using a specified
     * semantics.
     * @param semantics The {@link Semantics} defining the reasoning approach for
     *                  argument evaluation.
     */
    public RaSkeptSigmaCompatibility(Semantics semantics) {
        this(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics));
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return ((kb instanceof DungTheory) && (kb.size() >= 2));
    }

    @Override
    public String getName() {
        return "Skeptical Sigma-Compatibility";
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb,
            AbstractRankingReasoner<GeneralComparator<Argument, DungTheory>> ev) {
        if (!this.isApplicable(kb))
            return true;
        if (ev.getModel((DungTheory) kb) == null)
            return true;

        DungTheory dt = new DungTheory((DungTheory) kb);
        Collection<Argument> skeptArguments = new HashSet<>(dt);
        for (Extension<DungTheory> ext : reasoner.getModels(dt)) {
            skeptArguments.retainAll(ext);
        }
        Collection<Argument> otherArguments = new HashSet<>(dt);
        otherArguments.removeAll(skeptArguments);

        GeneralComparator<Argument, DungTheory> ranking = ev.getModel(dt);
        for (Argument a : skeptArguments) {
            for (Argument b : otherArguments) {
                if (!ranking.isStrictlyMoreAcceptableThan(a, b)) {
                    return false;
                }
            }
        }
        return true;
    }
}

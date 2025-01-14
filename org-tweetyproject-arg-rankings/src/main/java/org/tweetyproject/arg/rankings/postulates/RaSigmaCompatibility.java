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
import java.util.Iterator;


/**
 * Represents the Sigma-Compatibility postulate for ranking-based argumentation frameworks.
 *
 *
 * @see RankingPostulate
 * @see AbstractExtensionReasoner
 * @see Semantics
 */
public class RaSigmaCompatibility extends RankingPostulate {

        /**
     * The extension-based reasoner used to derive extensions of the argumentation framework.
     */
    AbstractExtensionReasoner reasoner;

    /**
     * Constructs a {@code RaSigmaCompatibility} postulate with the specified extension reasoner.
     *
     * @param reasoner The {@link AbstractExtensionReasoner} used to compute extensions of the argumentation framework.
     */
    public RaSigmaCompatibility(AbstractExtensionReasoner reasoner) {
        this.reasoner = reasoner;
    }

    /**
     * Constructs a {@code RaSigmaCompatibility} postulate using a specified semantics.
     *
     * @param semantics The {@link Semantics} defining the reasoning approach for argument evaluation.
     */
    public RaSigmaCompatibility(Semantics semantics) {
        this(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics));
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return ((kb instanceof DungTheory) && (kb.size() >= 2));
    }

    @Override
    public String getName() {
        return "Sigma-Compatibility";
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<GeneralComparator<Argument, DungTheory>> ev) {
        if (!this.isApplicable(kb))
            return true;
        if (ev.getModel((DungTheory) kb) == null)
            return true;

        DungTheory dt = new DungTheory((DungTheory) kb);
        Collection<Argument> credArguments = new HashSet<>();
        for (Extension<DungTheory> ext: reasoner.getModels(dt)) {
            credArguments.addAll(ext);
        }
        Collection<Argument> rejArguments = new HashSet<>(dt);
        rejArguments.removeAll(credArguments);

        GeneralComparator<Argument, DungTheory> ranking = ev.getModel(dt);
        for (Argument a : credArguments) {
            for (Argument b: rejArguments) {
                if (!ranking.isStrictlyMoreAcceptableThan(a,b)) {
                    return false;
                }
            }
        }
        return true;
    }
}

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

public class RaSkeptSigmaCompatibility extends RankingPostulate {

    AbstractExtensionReasoner reasoner;

    public RaSkeptSigmaCompatibility(AbstractExtensionReasoner reasoner) {
        this.reasoner = reasoner;
    }

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
    public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<GeneralComparator<Argument, DungTheory>> ev) {
        if (!this.isApplicable(kb))
            return true;
        if (ev.getModel((DungTheory) kb) == null)
            return true;

        DungTheory dt = new DungTheory((DungTheory) kb);
        Collection<Argument> skeptArguments = new HashSet<>(dt);
        for (Extension<DungTheory> ext: reasoner.getModels(dt)) {
            skeptArguments.retainAll(ext);
        }
        Collection<Argument> otherArguments = new HashSet<>(dt);
        otherArguments.removeAll(skeptArguments);

        GeneralComparator<Argument, DungTheory> ranking = ev.getModel(dt);
        for (Argument a : skeptArguments) {
            for (Argument b: otherArguments) {
                if (!ranking.isStrictlyMoreAcceptableThan(a,b)) {
                    return false;
                }
            }
        }
        return true;
    }
}

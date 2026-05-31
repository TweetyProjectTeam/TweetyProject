package org.tweetyproject.web.services.rankings;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.arg.rankings.util.RankingTools;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.comparator.NumericalPartialOrder;
import org.tweetyproject.web.services.Callee;

public class RankingReasonerGetModelCallee extends Callee {
    /** The AbstractRankingReasoner used for obtaining the model */
    private AbstractRankingReasoner<?> reasoner;

    /** The DungTheory on which the getModel operation is performed */
    private DungTheory bbase;

    /**
     * Constructs a new RankingReasonerGetRankingCallee with the specified reasoner and base DungTheory.
     *
     * @param reasoner The AbstractRankingReasoner to be used for obtaining the model
     * @param bbase    The base DungTheory on which the getModel operation is performed
     */
    public RankingReasonerGetModelCallee(AbstractRankingReasoner<?> reasoner, DungTheory bbase) {
        this.reasoner = reasoner;
        this.bbase = bbase;
    }

    /**
     * Executes the getModel operation using the specified reasoner and base DungTheory.
     *
     * @return A Ranking representing the obtained model
     * @throws Exception If an error occurs during the getModel operation
     */
    @Override
    public GeneralComparator<Argument,DungTheory> call() throws Exception {
        GeneralComparator<Argument, DungTheory> result = this.reasoner.getModel(bbase);
        return RankingTools.roundRanking((NumericalPartialOrder<Argument, DungTheory>) result, 2);
    }
}

package org.tweetyproject.web.services.rankings;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.ProbabilisticRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.SerialisableRankingReasoner;
import org.tweetyproject.math.probability.Probability;

import java.util.NoSuchElementException;

public class AbstractRankingReasonerFactory {
    public enum RankingSemantics {
        CAT("CAT", "categorizer"),
        SER("SER", "serialized"),
        PROB("PROB", "probabilistic");

        /** The description of the semantics. */
        public final String name;
        /** The abbreviation of the semantics. */
        public final String id;

        /**
         * Creates a new semantics.
         * @param id some identifier
         * @param name the name of the semantics
         */
        RankingSemantics(String id, String name){
            this.id = id;
            this.name = name;
        }

        /**
         * Returns the semantics whose abbreviation matched the given string.
         * @param abbreviation Abbreviation of the semantics to return.
         * @return Semantics with the specified abbreviation.
         */
        public static RankingSemantics getSemantics(String abbreviation) {
            for (RankingSemantics element : RankingSemantics.values()) {
                if (element.id.equals(abbreviation)) {
                    return element;
                }
            }
            throw new NoSuchElementException();
        }
    }

    public static RankingSemantics[] getSemantics() {
        return RankingSemantics.values();
    }

    public static AbstractRankingReasoner<?> getReasoner(RankingSemantics semantics) {
        switch (semantics) {
            case CAT -> {
                return new CategorizerRankingReasoner();
            } case SER -> {
                return new SerialisableRankingReasoner();
            } case PROB -> {
                return new ProbabilisticRankingReasoner(Semantics.GR, new Probability(0.5), false);
            } default -> throw new IllegalArgumentException("unsupported semantics!");
        }
    }
}

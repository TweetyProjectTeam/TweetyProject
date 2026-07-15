/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.rankings;

import java.util.Map;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CountingRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.ProbabilisticRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.PropagationRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.SAFRankingReasoner;
import org.tweetyproject.arg.rankings.semantics.RankingSemantics;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.math.probability.Probability;

/**
 * Factory for creating reasoner for ranking semantics
 *
 * @author Lars Bengel
 */
public class AbstractRankingReasonerFactory {
    /**
     * returns the list of all available semantics
     * @return the list o all ranking semantics
     */
    public static RankingSemantics[] getSemantics() {
        return new RankingSemantics[]{RankingSemantics.CAT,RankingSemantics.SER,RankingSemantics.BB,RankingSemantics.CO,RankingSemantics.DB,RankingSemantics.IGD,RankingSemantics.SAF,RankingSemantics.SB,RankingSemantics.TU,RankingSemantics.PR,RankingSemantics.PROB};
    }

    /**
     * returns a simple reasoner for the given semantics, using default parameters
     * @param semantics some ranking semantics
     * @return simple reasoner for the given semantics
     */
    public static AbstractRankingReasoner<?> getReasoner(RankingSemantics semantics) {
        return AbstractRankingReasoner.getRankingReasonerForSemantics(semantics);
    }

    /**
     * returns a reasoner for the given semantics, initialized with the given parameters.
     * Parameters that are missing from {@code args} fall back to the reasoner's own defaults.
     * @param semantics some ranking semantics
     * @param args map of constructor parameters for the reasoner, keyed by parameter name
     * @return reasoner for the given semantics, configured with the given parameters
     */
    public static AbstractRankingReasoner<?> getReasoner(RankingSemantics semantics, Map<String, Object> args) {
        if (args == null || args.isEmpty())
            return getReasoner(semantics);
        switch (semantics) {
            case CAT -> {
                return new CategorizerRankingReasoner(getDouble(args, "epsilon", 0.001));
            } case CO -> {
                return new CountingRankingReasoner(getDouble(args, "dampingFactor", 0.9), getDouble(args, "epsilon", 0.001));
            } case SAF -> {
                return new SAFRankingReasoner(getDouble(args, "epsilon", 0.1), getDouble(args, "precision", 0.0001), getDouble(args, "tolerance", 0.0001));
            } case PR -> {
                PropagationRankingReasoner.PropagationSemantics propagationSemantics = PropagationRankingReasoner.PropagationSemantics.valueOf(
                        getString(args, "propagationSemantics", "PROPAGATION1"));
                return new PropagationRankingReasoner(getDouble(args, "attackedArgumentsInfluence", 0.75), getBoolean(args, "useMultiset", true), propagationSemantics);
            } case PROB -> {
                Semantics classicalSemantics = Semantics.getSemantics(getString(args, "classicalSemantics", "GR"));
                Probability probability = new Probability(getDouble(args, "probability", 0.5));
                InferenceMode mode = InferenceMode.valueOf(getString(args, "inferenceMode", "SKEPTICAL"));
                return new ProbabilisticRankingReasoner(classicalSemantics, probability, getBoolean(args, "exactInference", true), mode);
            } default -> {
                return getReasoner(semantics);
            }
        }
    }

    private static double getDouble(Map<String, Object> args, String key, double defaultValue) {
        Object value = args.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : defaultValue;
    }

    private static boolean getBoolean(Map<String, Object> args, String key, boolean defaultValue) {
        Object value = args.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    private static String getString(Map<String, Object> args, String key, String defaultValue) {
        Object value = args.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    /**
     * Determine what type of ranking the semantics computes
     * @param semantics some ranking semantics as a string
     * @return the  ranking type
     */
    public static String getRankingType(String semantics) {
        RankingSemantics sem = RankingSemantics.getSemantics(semantics);
        switch (sem) {
            case CAT,CO,SAF,SB,PROB -> {
                return "numerical";
            } case BB, SER, DB, IGD, TU, PR -> {
                return "lattice";
            } default -> throw new IllegalArgumentException("unsupported semantics");
        }
    }
}

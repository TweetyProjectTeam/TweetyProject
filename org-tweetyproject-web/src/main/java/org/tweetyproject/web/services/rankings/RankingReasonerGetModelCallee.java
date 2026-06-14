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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.arg.rankings.util.RankingTools;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.comparator.LatticePartialOrder;
import org.tweetyproject.comparator.NumericalPartialOrder;
import org.tweetyproject.web.services.Callee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Callee for getting a ranking model
 *
 * @author Lars Bengel
 */
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
        result = toNumericalRanking(result);
        return RankingTools.roundRanking((NumericalPartialOrder<Argument, DungTheory>) result, 2);
    }

    /**
     * Convert relative (lattice-based) ranking to a numerical ranking
     * @param ranking some ranking
     * @return the numerical ranking
     */
    public static NumericalPartialOrder<Argument,DungTheory> toNumericalRanking(GeneralComparator<Argument,DungTheory> ranking) {
        if (ranking instanceof NumericalPartialOrder<?,?>) {
            return (NumericalPartialOrder<Argument, DungTheory>) ranking;
        } else if (ranking instanceof LatticePartialOrder<?,?>) {
            return toNumericalRanking((LatticePartialOrder<Argument, DungTheory>) ranking);
        } else throw new IllegalArgumentException("Unsupported order type.");
    }

    /**
     * Convert relative (lattice-based) ranking to a numerical ranking
     * @param ranking some ranking
     * @return the numerical ranking
     */
    public static NumericalPartialOrder<Argument,DungTheory> toNumericalRanking(LatticePartialOrder<Argument,DungTheory> ranking) {
        NumericalPartialOrder<Argument,DungTheory> numericalRanking = new NumericalPartialOrder<>();
        if (!ranking.containsIncomparableArguments()) {
            List<Argument> args = new ArrayList<>(ranking.getOrder().getElements());
            Collections.sort(args, new LatticeComparator(ranking));
            double rank = 1;
            numericalRanking.put(args.get(args.size()-1), rank);
            for (int i = args.size() - 2; i >= 0; i--) {
                Argument a = args.get(i);
                Argument b = args.get(i + 1);
                if (ranking.isEquallyAcceptableThan(a, b)) {
                    numericalRanking.put(a, rank);
                } else {
                    numericalRanking.put(a, ++rank);
                }
            }
        } else throw new IllegalArgumentException("ranking contains incomparable arguments.");
        return numericalRanking;
    }

    private static class LatticeComparator implements Comparator<Argument> {
        private final LatticePartialOrder<Argument,DungTheory> order;

        public LatticeComparator(LatticePartialOrder<Argument,DungTheory> order) {
            this.order = order;
        }

        @Override
        public int compare(Argument a, Argument b) {
            if (order.isIncomparable(a, b))
                throw new IllegalArgumentException("Incomparable arguments " + a + ", " + b);
            else if (order.isStrictlyLessAcceptableThan(a, b))
                return -1;
            else if (order.isStrictlyMoreAcceptableThan(a, b))
                return 1;
            else
                return 0;
        }
    }
}

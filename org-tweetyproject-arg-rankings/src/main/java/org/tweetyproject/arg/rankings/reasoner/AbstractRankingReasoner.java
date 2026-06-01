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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.reasoner;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.semantics.RankingSemantics;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.postulates.PostulateEvaluatable;

/**
 * Common abstract class for ranking reasoners for abstract argumentation. Currently,
 * the available ranking types are:
 * <br> - Numerical Ranking: Arguments are assigned numerical values. Acceptability follows from some order
 * that is opposed on those values.
 * <br> - Lattice Ranking: The acceptability of the arguments is represented by a graph-based structure.
 *
 * @author Anna Gessler
 *
 * @param <R> the type of ranking
 */
public abstract class AbstractRankingReasoner<R extends GeneralComparator<Argument, DungTheory>> implements ModelProvider<Argument,DungTheory,R>,  PostulateEvaluatable<Argument>  {

    /**
     * Is installed
     * @return is installed status
     */
    public abstract boolean isInstalled();

    /** Default Constructor */
    public AbstractRankingReasoner(){}

    /**
     * returns the corresponding reasoner for the given semantics
     * @param semantics some ranking semantics
     * @return the corresponding reasoner
     */
    public static AbstractRankingReasoner<?> getRankingReasonerForSemantics(RankingSemantics semantics) {
        switch (semantics) {
            case CAT -> {
                return new CategorizerRankingReasoner();
            } case BB -> {
                return new BurdenBasedRankingReasoner();
            } case CT -> {
                throw new UnsupportedOperationException("Can currently not be initialized without a given graph.");
            } case CO -> {
                return new CountingRankingReasoner();
            } case DB -> {
                return new DiscussionBasedRankingReasoner();
            } case IGD -> {
                return new IteratedGradedDefenseReasoner();
            } case SAF -> {
                return new SAFRankingReasoner();
            } case SER -> {
                return new SerialisableRankingReasoner();
            } case SB -> {
                return new StrategyBasedRankingReasoner();
            } case TU -> {
                return new TuplesRankingReasoner();
            } case PR -> {
                throw new UnsupportedOperationException("Semantics only supports acyclic graphs");
            } case PROB -> {
                throw new UnsupportedOperationException("Currently not supported");
            } default -> throw new IllegalArgumentException("Unsupported semantics.");
        }
    }
}

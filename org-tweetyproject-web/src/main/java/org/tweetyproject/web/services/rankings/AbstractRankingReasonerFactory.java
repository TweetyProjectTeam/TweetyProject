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

import org.tweetyproject.arg.rankings.semantics.RankingSemantics;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;

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
        return RankingSemantics.values();
    }

    /**
     * returns a simple reasoner for the given semantics
     * @param semantics some ranking semantics
     * @return simple reasoner for the given semantics
     */
    public static AbstractRankingReasoner<?> getReasoner(RankingSemantics semantics) {
        return AbstractRankingReasoner.getRankingReasonerForSemantics(semantics);
    }
}

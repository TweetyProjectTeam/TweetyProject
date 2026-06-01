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
package org.tweetyproject.arg.rankings.semantics;

import java.util.NoSuchElementException;

/**
 * Class representing ranking-based semantics
 *
 * @author Lars Bengel
 */
public enum RankingSemantics {
    CAT("CAT", "categorizer"),
    SER("SER", "serialized"),
    BB("BB", "burden-based"),
    CT("CT", "counter transitivity"),
    CO("CO", "counting"),
    DB("DB", "discussion-based"),
    IGD("IGD", "iterated graded defense"),
    PR("PR", "propagation"),
    SAF("SAF", "social argumentation"),
    SB("SB", "strategy-based"),
    TU("TU", "tuples"),
    PROB("PROB", "probabilistic");

    /** The description of the semantics. */
    private final String name;
    /** The abbreviation of the semantics. */
    private final String id;

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
     * returns the identifier of the semantics
     * @return the identifier of the semantics
     */
    public String getId() {
        return id;
    }

    /**
     * returns the name of the semantics
     * @return the name of the semantics
     */
    public String getName() {
        return name;
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
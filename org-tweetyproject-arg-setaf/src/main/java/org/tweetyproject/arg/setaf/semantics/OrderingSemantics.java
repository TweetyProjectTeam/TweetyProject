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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.setaf.semantics;

import org.tweetyproject.arg.setaf.semantics.OrderingSemantics;

/**
 * This enum lists ordering semantics.
 * @author  Sebastian Franke
 */
public enum OrderingSemantics {
    CF ("conflict-free semantics", "CF"),
    AD ("admissible semantics", "AD"),
    ST ("stable semantics", "ST"),
    DN ("defended not in semantics", "DN");

    public static final OrderingSemantics CONFLICTFREE_SEMANTICS = CF,
            ADMISSIBLE_SEMANTICS = AD,
            STABLE_SEMANTICS = ST,
            DEFENDED_NOT_IN_SEMANTICS = DN;

    /** The description of the ordering semantics. */
    private String description;
    /** The abbreviation of the ordering semantics. */
    private String abbreviation;

    /**
     * Creates a new ordering semantics.
     * @param description some description
     * @param abbreviation an abbreviation
     */
    private OrderingSemantics(String description, String abbreviation){
        this.description = description;
        this.abbreviation = abbreviation;
    }

    /**
     * Returns the description of the ordering semantics.
     * @return the description of the ordering semantics.
     */
    public String description(){
        return this.description;
    }

    /**
     * Returns the abbreviation of the ordering semantics.
     * @return the abbreviation of the ordering semantics.
     */
    public String abbreviation(){
        return this.abbreviation;
    }
}

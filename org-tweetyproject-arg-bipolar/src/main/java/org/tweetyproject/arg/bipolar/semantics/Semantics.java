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
package org.tweetyproject.arg.bipolar.semantics;

/**
 * Enum of semantics for bipolar argumentation
 *
 * @author Lars Bengel
 */
public enum Semantics {
    /**
     * CF
     */
    BCF("conflict-free semantics", "B-CF"),
    /**
     * COH
     */
    BCOH("coherent semantics", "B-COH"),
    /**
     * ADM
     */
    BAD("admissible semantics", "B-AD"),
    /**
     * diverse
     */
    diverse("diverse semantics", "div");
    /**
     * all semantics
     */
    @SuppressWarnings("javadoc")
    public static final Semantics
            /** CONFLICT_FREE SEMANTICS*/
            B_CONFLICT_FREE_SEMANTICS = BCF,
            /** COHERENT SEMANTICS*/
            B_COHERENT_SEMANTICS = BCOH,
            /** COHERENT_ADMISSIBLE SEMANTICS */
            B_ADMISSIBLE_SEMANTICS = BAD;

    /**
     * The description of the semantics.
     */
    private String description;
    /**
     * The abbreviation of the semantics.
     */
    private String abbreviation;

    /**
     * Creates a new semantics.
     *
     * @param description  some description
     * @param abbreviation an abbreviation
     */
    private Semantics(String description, String abbreviation) {
        this.description = description;
        this.abbreviation = abbreviation;
    }

    /**
     * Returns the description of the semantics.
     *
     * @return the description of the semantics.
     */
    public String description() {
        return this.description;
    }

    /**
     * Returns the abbreviation of the semantics.
     *
     * @return the abbreviation of the semantics.
     */
    public String abbreviation() {
        return this.abbreviation;
    }
}


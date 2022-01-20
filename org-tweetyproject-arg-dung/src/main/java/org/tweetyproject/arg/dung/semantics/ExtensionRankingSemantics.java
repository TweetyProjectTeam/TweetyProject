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

package org.tweetyproject.arg.dung.semantics;

/**
 * This enum lists ordering semantics.
 * @author Lars Bengel
 * @author Daniel Letkemann
 */
public enum ExtensionRankingSemantics {
    /**
     * R_AD
     */
    R_CF("conflict-free", "R_CF"),
    /**
     * R_AD
     */
    R_AD("admissible", "R_AD"),
    /**
     * R_CO
     */
    R_CO("complete", "R_CO"),
    /**
     * R_GR
     */
    R_GR("grounded", "R_GR"),
    /**
     * R_PR
     */
    R_PR("preferred", "R_PR"),
    /**
     * R_SST
     */
    R_SST("semi-stable", "R_SST");


    public static final ExtensionRankingSemantics
            /** ADMISSIBLE */
            CONFLICT_FREE_SEMANTICS = R_CF,
            /** ADMISSIBLE */
            ADMISSIBLE_SEMANTICS = R_AD,
    /** COMPLETE */
    COMPLETE_SEMANTICS = R_CO,

    /** GROUNDED */
    GROUNDED_SEMANTICS = R_GR,


    /** PREFERRED */
    PREFERRED_SEMANTICS = R_PR,

    /** SEMI_STABLE */
    SEMI_STABLE_SEMANTICS = R_SST;



    /** The description of the ordering semantics. */
    private String description;
    /** The abbreviation of the ordering semantics. */
    private String abbreviation;

    /**
     * Creates a new ordering semantics.
     * @param description some description
     * @param abbreviation an abbreviation
     */
    private ExtensionRankingSemantics(String description, String abbreviation){
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

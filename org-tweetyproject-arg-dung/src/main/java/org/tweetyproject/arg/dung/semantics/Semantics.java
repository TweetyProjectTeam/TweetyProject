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

import java.util.NoSuchElementException;

/**
 * This enum lists all semantics.
 * @author Matthias Thimm
 */
public enum Semantics {
    /** CF */
    CF ("conflict-free semantics", "CF"),
    /** ADM */
    ADM ("admissible semantics", "ADM"),
    /** CO */
    CO ("complete semantics", "CO"),
    /** GR */
    GR ("grounded semantics", "GR"),
    /** PR */
    PR ("preferred semantics", "PR"),
    /** ST */
    ST ("stable semantics", "ST"),
    /** STG */
    STG ("stage semantics", "STG"),
    /** STG2 */
    STG2 ("stage2 semantics", "STG2"),
    /** SST */
    SST ("semi-stable semantics", "SST"),
    /** ID */
    ID ("ideal semantics", "ID"),
    /** EA */
    EA ("eager semantics", "EA"),
    /** CF2 */
    CF2 ("CF2 semantics", "CF2"),
    /** SCF2 */
    SCF2 ("SCF2 semantics", "SCF2"),
    /** NA */
    NA ("naive semantics", "NA"),
    /** SAD */
    SAD ("strongly-admissible semantics", "SAD"),
    /** IS */
    IS ("initial semantics", "IS"),
    /** UC */
    UC ("unchallenged semantics", "UC"),
    /** UD */
    UD ("undisputed semantics", "UD"),
    /** SUD */
    SUD ("strongly-undisputed semantics", "SUD"),
    /** WAD */
    WAD ("weakly-admissible semantics", "WAD"),
    /** WCO */
    WCO ("weakly-complete semantics", "WCO"),
    /** WPR */
    WPR ("weakly-preferred semantics", "WPR"),
    /** WGR */
    WGR ("weakly-grounded semantics", "WGR"),
    /** diverse */
    diverse ("diverse semantics", "div");
    /**
     * all semantics
     */
    @SuppressWarnings("javadoc")
    public static final Semantics
        /** GROUNDED SEMANTICS*/
        GROUNDED_SEMANTICS = GR,
        /** STABLE_SEMANTICS */
        STABLE_SEMANTICS = ST,
        /** PREFERRED_SEMANTICS */
        PREFERRED_SEMANTICS = PR,
        /** COMPLETE_SEMANTICS */
        COMPLETE_SEMANTICS = CO,
        /** ADMISSIBLE_SEMANTICS */
        ADMISSIBLE_SEMANTICS = ADM,
        /** CONFLICTFREE_SEMANTICS */
        CONFLICTFREE_SEMANTICS = CF,
        /** SEMISTABLE_SEMANTICS */
        SEMISTABLE_SEMANTICS = SST,
        /** IDEAL_SEMANTICS */
        IDEAL_SEMANTICS = ID,
        /** EAGER_SEMANTICS */
        EAGER_SEMANTICS = EA,
        /** STAGE_SEMANTICS */
        STAGE_SEMANTICS = STG,
        /** STAGE2_SEMANTICS */
        STAGE2_SEMANTICS = STG2,
        /** CF2_SEMANTICS */
        CF2_SEMANTICS = CF2,
        /** SCF2_SEMANTICS */
        SCF2_SEMANTICS = SCF2,
        /** NAIVE_SEMANTICS */
        NAIVE_SEMANTICS = NA,
        /** STRONGLY_ADMISSIBLE_SEMANTICS */
        STRONGLY_ADMISSIBLE_SEMANTICS = SAD,
        /** INITIAL_SEMANTICS */
        INITIAL_SEMANTICS = IS,
        /** UNCHALLENGED_SEMANTICS */
        UNCHALLENGED_SEMANTICS = UC,
        /** UNDISPUTED_SEMANTICS */
        UNDISPUTED_SEMANTICS = UD,
        /** STRONGLY_UNDISPUTED_SEMANTICS */
        STRONGLY_UNDISPUTED_SEMANTICS = SUD,
        /** WEAKLY_ADMISSIBLE_SEMANTICS */
        WEAKLY_ADMISSIBLE_SEMANTICS = WAD,
        /** WEAKLY_COMPLETE_SEMANTICS */
        WEAKLY_COMPLETE_SEMANTICS = WCO,
        /** WEAKLY_PREFERRED_SEMANTICS */
        WEAKLY_PREFERRED_SEMANTICS = WPR,
        /** WEAKLY_GROUNDED_SEMANTICS */
        WEAKLY_GROUNDED_SEMANTICS = WGR;


    /** The description of the semantics. */
    private String description;
    /** The abbreviation of the semantics. */
    private String abbreviation;

    /**
     * Creates a new semantics.
     * @param description some description
     * @param abbreviation an abbreviation
     */
    private Semantics(String description, String abbreviation){
        this.description = description;
        this.abbreviation = abbreviation;
    }

    /**
     * Returns the description of the semantics.
     * @return the description of the semantics.
     */
    public String description(){
        return this.description;
    }

    /**
     * Returns the abbreviation of the semantics.
     * @return the abbreviation of the semantics.
     */
    public String abbreviation(){
        return this.abbreviation;
    }

    /**
     * Returns the semantics whose abbreviation matched the given string.
     * @param abbreviation Abbreviation of the semantics to return.
     * @return Semantics with the specified abbreviation.
     */
    public static Semantics getSemantics(String abbreviation) {
        for (Semantics element : Semantics.values()) {
            if (element.abbreviation().equals(abbreviation)) {
                return element;
            }
        }
        throw new NoSuchElementException();
    }
}

/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.semantics;

import java.util.NoSuchElementException;



/**
 * This enum lists all semantics for Epistemic Argumentation Frameworks.
 * 
 * @author Sandra Hoffmann
 *
 */
public enum EAFSemantics {
	 /** ADM */
    EAF_ADM("EAF-admissible semantics", "E-ADM"),
    /** GR */
    EAF_GR("EAF-grounded semantics", "E-GR"),
    /** CO */
    EAF_CO("EAF-complete semantics", "E-Co"),
    /** PR */
    EAF_PR("EAF-preferred semantics", "E-PR"),
    /** ST */
    EAF_ST("EAF-stable semantics", "E-ST");

    /**
     * The description of the semantics.
     */
    private final String description;

    /**
     * The abbreviation of the semantics.
     */
    private final String abbreviation;

    /** Static constants for grounded semantics */
    public static final EAFSemantics EAF_GROUNDED_SEMANTICS = EAF_GR;
    /** Static constants for complete semantics */
    public static final EAFSemantics EAF_COMPLETE_SEMANTICS = EAF_CO;
    /** Static constants for stable semantics */
    public static final EAFSemantics EAF_STABLE_SEMANTICS = EAF_ST;
        /** Static constants for preferred semantics */
    public static final EAFSemantics EAF_PREFERRED_SEMANTICS = EAF_PR;
    /** Static constants for admissible semantics */
    public static final EAFSemantics EAF_ADMISSIBLE_SEMANTICS = EAF_ADM;

    /**
     * Creates a new semantics.
     * @param description some description
     * @param abbreviation an abbreviation
     */
    private EAFSemantics(String description, String abbreviation) {
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

    /**
     * Returns the semantics whose abbreviation matches the given string.
     *
     * @param abbreviation Abbreviation of the semantics to return.
     * @return Semantics with the specified abbreviation.
     */
    public static EAFSemantics getSemantics(String abbreviation) {
        for (EAFSemantics element : EAFSemantics.values()) {
            if (element.abbreviation().equals(abbreviation)) {
                return element;
            }
        }
        throw new NoSuchElementException("No EAFSemantics found for abbreviation: " + abbreviation);
    }
}

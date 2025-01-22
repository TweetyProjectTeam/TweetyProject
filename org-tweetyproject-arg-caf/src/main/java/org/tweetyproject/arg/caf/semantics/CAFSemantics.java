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
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.caf.semantics;

import java.util.NoSuchElementException;

/**
 * This enum lists all semantics for Constrained Argumentation Frameworks.
 * 
 * @author Sandra Hoffmann
 *
 */
public enum CAFSemantics {
    /** ADM */
    CAF_ADM("C-admissible semantics", "C-ADM"),
    /** GR */
    CAF_GR("C-grounded semantics", "C-GR"),
    /** PR */
    CAF_PR("C-preferred semantics", "C-PR"),
    /** ST */
    CAF_ST("C-stable semantics", "C-ST");

    /**
     * The description of the semantics.
     */
    private final String description;

    /**
     * The abbreviation of the semantics.
     */
    private final String abbreviation;

    /**
     * Static constants for each of the semantics types
     */
    public static final CAFSemantics CAF_GROUNDED_SEMANTICS = CAF_GR;
    public static final CAFSemantics CAF_STABLE_SEMANTICS = CAF_ST;
    public static final CAFSemantics CAF_PREFERRED_SEMANTICS = CAF_PR;
    public static final CAFSemantics CAF_ADMISSIBLE_SEMANTICS = CAF_ADM;

    /**
     * Creates a new semantics.
     * @param description some description
     * @param abbreviation an abbreviation
     */
    private CAFSemantics(String description, String abbreviation) {
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
    public static CAFSemantics getSemantics(String abbreviation) {
        for (CAFSemantics element : CAFSemantics.values()) {
            if (element.abbreviation().equals(abbreviation)) {
                return element;
            }
        }
        throw new NoSuchElementException("No CAFSemantics found for abbreviation: " + abbreviation);
    }
}


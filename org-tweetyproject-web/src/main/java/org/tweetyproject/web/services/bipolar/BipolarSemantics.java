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
package org.tweetyproject.web.services.bipolar;

import org.tweetyproject.arg.bipolar.syntax.Support;

import java.util.NoSuchElementException;


/**
 * An enumeration of all available semantics through the web service.
 *
 * @author Oleksandr Dzhychko
 */
public enum BipolarSemantics {

    /**
     * General Semantics
     */
    BCF("b-cf", "Conflict-Free", Support.Type.DEFAULT),
    BCOH("b-coh", "Coherent", Support.Type.DEFAULT),
    BAD("b-ad", "Coherent Admissible", Support.Type.DEFAULT),

    /**
     * Coalition Semantics
     */
    CAD("b-coal-ad", "Coalition-Admissible", Support.Type.DEFAULT),
    CCO("b-coal-co", "Coalition-Complete", Support.Type.DEFAULT),
    CGR("b-coal-gr", "Coalition-Grounded", Support.Type.DEFAULT),
    CPR("b-coal-pr", "Coalition-Preferred", Support.Type.DEFAULT),
    CST("b-coal-st", "Coalition-Stable", Support.Type.DEFAULT),

    /**
     * Semantics for Deductive Interpretation
     */
    DAD("d-ad", "Admissible", Support.Type.DEDUCTIVE),
    DCO("d-co", "Complete", Support.Type.DEDUCTIVE),
    DGR("d-gr", "Grounded", Support.Type.DEDUCTIVE),
    DPR("d-pr", "Preferred", Support.Type.DEDUCTIVE),
    DST("d-st", "Stable", Support.Type.DEDUCTIVE),

    /**
     * Semantics for Necessity Interpretation
     */
    NAD("n-ad", "Admissible", Support.Type.NECESSITY),
    NCO("n-co", "Complete", Support.Type.NECESSITY),
    NGR("n-gr", "Grounded", Support.Type.NECESSITY),
    NPR("n-pr", "Preferred", Support.Type.NECESSITY),
    NST("n-st", "Stable", Support.Type.NECESSITY);

    /**
     * id
     */
    public String id;
    /**
     * label
     */
    public String label;
    /**
     * What type of input is expected for this semantic.
     */
    public Support.Type type;

    BipolarSemantics(String id, String label, Support.Type input) {
        this.id = id;
        this.label = label;
        this.type = input;
    }

    /**
     *
     * @param id ID
     * @return the semantics
     */
    public static BipolarSemantics getSemantics(String id) {
        for (BipolarSemantics m : BipolarSemantics.values())
            if (m.id.equals(id))
                return m;
        throw new NoSuchElementException("semantics does not exist");
    }
}
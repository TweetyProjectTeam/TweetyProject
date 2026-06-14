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

    /** bipolar conflict-free semantics */
    BCF("b-cf", "Conflict-Free", Support.Type.DEFAULT),
    /** bipolar coherent semantics */
    BCOH("b-coh", "Coherent", Support.Type.DEFAULT),
    /** bipolar coherent admissible semantics */
    BAD("b-ad", "Coherent Admissible", Support.Type.DEFAULT),
    /** coalition-admissible semantics */
    CAD("b-coal-ad", "Coalition-Admissible", Support.Type.DEFAULT),
    /** coalition-complete semantics */
    CCO("b-coal-co", "Coalition-Complete", Support.Type.DEFAULT),
    /** coalition-grounded semantics */
    CGR("b-coal-gr", "Coalition-Grounded", Support.Type.DEFAULT),
    /** coalition-preferred semantics */
    CPR("b-coal-pr", "Coalition-Preferred", Support.Type.DEFAULT),
    /** coalition-stable semantics */
    CST("b-coal-st", "Coalition-Stable", Support.Type.DEFAULT),
    /** deductive-admissible semantics */
    DAD("d-ad", "Admissible", Support.Type.DEDUCTIVE),
    /** deductive-complete semantics */
    DCO("d-co", "Complete", Support.Type.DEDUCTIVE),
    /** deductive-grounded semantics */
    DGR("d-gr", "Grounded", Support.Type.DEDUCTIVE),
    /** deductive-preferred semantics */
    DPR("d-pr", "Preferred", Support.Type.DEDUCTIVE),
    /** deductive-stable semantics */
    DST("d-st", "Stable", Support.Type.DEDUCTIVE),
    /** necessary-admissible semantics */
    NAD("n-ad", "Admissible", Support.Type.NECESSITY),
    /** necessary-complete semantics */
    NCO("n-co", "Complete", Support.Type.NECESSITY),
    /** necessary-grounded semantics */
    NGR("n-gr", "Grounded", Support.Type.NECESSITY),
    /** necessary-preferred semantics */
    NPR("n-pr", "Preferred", Support.Type.NECESSITY),
    /** necessary-stable semantics */
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
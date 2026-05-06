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

import static org.tweetyproject.web.services.bipolar.BipolarArgumentationFrameworkType.DeductiveArgumentationFramework;
import static org.tweetyproject.web.services.bipolar.BipolarArgumentationFrameworkType.NecessityArgumentationFramework;

/**
 * An enumeration of all available semantics through the web service.
 *
 * @author Oleksandr Dzhychko
 */
public enum BipolarSemantics {

    /**
     * General Semantics
     */
    CF("cf", "Conflict-Free", DeductiveArgumentationFramework),
    SA("sa", "Safe", DeductiveArgumentationFramework),
    CL("cl", "Closed", DeductiveArgumentationFramework),
    /**
     * Semantics for Deductive Interpretation
     */
    CAD("c-ad", "c-Admissible", DeductiveArgumentationFramework),
    DAD("d-ad", "d-Admissible", DeductiveArgumentationFramework),
    /**
     * Semantics for Necessary Interpretation
     */
    NAD("n-ad", "Admissible", NecessityArgumentationFramework),
    NCO("n-co", "Complete", NecessityArgumentationFramework),
    NGR("n-gr", "Grounded", NecessityArgumentationFramework),
    NPR("n-pr", "Preferred", NecessityArgumentationFramework),
    NST("n-st", "Stable", NecessityArgumentationFramework);

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
    public BipolarArgumentationFrameworkType input;

    BipolarSemantics(String id, String label, BipolarArgumentationFrameworkType input) {
        this.id = id;
        this.label = label;
        this.input = input;
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
        return null;
    }
}
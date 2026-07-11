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

import org.tweetyproject.arg.bipolar.reasoner.*;
import org.tweetyproject.arg.dung.semantics.Semantics;

/**
 * Abstract factory for retrieving bipolar extension reasoners.
 *
 * @author Lars Bengel
 */
public abstract class AbstractBipolarExtensionReasonerFactory {

	/**
	 * Prevents instantiation.
	 */
	protected AbstractBipolarExtensionReasonerFactory() {
	}

	/**
	 * Returns an array of all available semantics.
	 *
	 * @return An array of all available semantics.
	 */
	public static BipolarSemantics[] getSemantics() {
		return BipolarSemantics.values();
	}

	/**
	 * Creates a new reasoner measure of the given semantics with default
	 * settings.
	 * 
	 * @param sem some identifier of a semantics.
	 * @return the requested reasoner.
	 */
	public static AbstractBipolarExtensionReasoner getReasoner(BipolarSemantics sem) {
        return switch (sem) {
			case BCF -> new SimpleStronglyConflictFreeReasoner();
			case BCOH -> new SimpleCoherentReasoner();
			case BAD -> new SimpleCoherentAdmissibleReasoner();
			case CAD -> new SimpleCoalitionReasoner(Semantics.ADM);
			case CCO -> new SimpleCoalitionReasoner(Semantics.CO);
			case CGR -> new SimpleCoalitionReasoner(Semantics.GR);
			case CPR -> new SimpleCoalitionReasoner(Semantics.PR);
			case CST -> new SimpleCoalitionReasoner(Semantics.ST);
			case DAD -> new SimpleDeductiveReasoner(Semantics.ADM);
			case DCO -> new SimpleDeductiveReasoner(Semantics.CO);
			case DGR -> new SimpleDeductiveReasoner(Semantics.GR);
			case DPR -> new SimpleDeductiveReasoner(Semantics.PR);
			case DST -> new SimpleDeductiveReasoner(Semantics.ST);
			case NAD -> new SimpleNecessityReasoner(Semantics.ADM);
			case NCO -> new SimpleNecessityReasoner(Semantics.CO);
			case NGR -> new SimpleNecessityReasoner(Semantics.GR);
			case NPR -> new SimpleNecessityReasoner(Semantics.PR);
			case NST -> new SimpleNecessityReasoner(Semantics.ST);

            default -> throw new RuntimeException("No reasoner found for semantics " + sem.toString());
        };
	}
}

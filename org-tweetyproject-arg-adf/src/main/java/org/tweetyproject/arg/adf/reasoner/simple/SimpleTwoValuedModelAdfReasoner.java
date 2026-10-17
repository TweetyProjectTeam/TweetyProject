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
package org.tweetyproject.arg.adf.reasoner.simple;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * Enumerates the two-valued models (MOD) of an ADF: the two-valued
 * interpretations v with v(a) = v(phi_a) for every argument a. Checks all 2^n
 * two-valued interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleTwoValuedModelAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	protected boolean isTwoValued() {
		return true;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		if (!v.isTwoValued()) {
			return false;
		}
		for (Argument a : adf) {
			if (v.evaluate(adf.getAcceptanceCondition(a)) != v.get(a)) {
				return false;
			}
		}
		return true;
	}

}

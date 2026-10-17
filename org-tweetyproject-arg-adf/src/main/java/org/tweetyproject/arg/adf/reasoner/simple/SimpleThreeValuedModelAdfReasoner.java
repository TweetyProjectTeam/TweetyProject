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

/**
 * Enumerates the three-valued models (3MOD) of an ADF: the interpretations v
 * such that v(a) = u or v(a) = v(phi_a) under Kleene's strong three-valued
 * evaluation, for every argument a. Unlike conflict-freeness in the sense of
 * {@link SimpleConflictFreeAdfReasoner}, this notion does not contain all
 * admissible interpretations. Checks all 3^n interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleThreeValuedModelAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		return v.satisfies(adf);
	}

}

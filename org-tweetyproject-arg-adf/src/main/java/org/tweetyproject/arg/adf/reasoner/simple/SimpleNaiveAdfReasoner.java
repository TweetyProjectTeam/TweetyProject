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
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * Enumerates the naive interpretations (NA) of an ADF: the ≤_i-maximal
 * conflict-free interpretations in the sense of
 * {@link SimpleConflictFreeAdfReasoner}.
 * Since conflict-freeness is closed under ≤_i-smaller interpretations, a
 * conflict-free v is maximal iff no single undecided argument can be set to t
 * or f. Checks all 3^n interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleNaiveAdfReasoner extends AbstractSimpleAdfReasoner {

	private final SimpleConflictFreeAdfReasoner conflictFree = new SimpleConflictFreeAdfReasoner();

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		if (!conflictFree.isModel(adf, v)) {
			return false;
		}
		for (Argument a : v.undecided()) {
			if (conflictFree.isModel(adf, v.with(a, TruthValue.TRUE)) || conflictFree.isModel(adf, v.with(a, TruthValue.FALSE))) {
				return false;
			}
		}
		return true;
	}

}

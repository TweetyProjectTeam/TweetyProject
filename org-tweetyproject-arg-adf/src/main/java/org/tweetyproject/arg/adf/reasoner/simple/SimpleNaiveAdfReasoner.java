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

import org.tweetyproject.arg.adf.reasoner.ModelIterator;
import org.tweetyproject.arg.adf.semantics.CompiledAdf;

/**
 * Enumerates the naive interpretations (NA) of an ADF: the ≤_i-maximal
 * conflict-free interpretations in the sense of
 * {@link SimpleConflictFreeAdfReasoner}.
 * <p>
 * Conflict-freeness is closed under ≤_i-smaller interpretations (a smaller
 * interpretation has more completions), so a conflict-free υ is maximal iff no
 * single undecided argument can be set to t or f without losing
 * conflict-freeness. Checks all 3^n interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleNaiveAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	protected ModelIterator models(CompiledAdf adf) {
		return threeValued(adf, this::verify);
	}

	@Override
	protected boolean verify(CompiledAdf adf, byte[] v) {
		if (!SimpleConflictFreeAdfReasoner.isConflictFree(adf, v)) {
			return false;
		}
		for (int a = 0; a < v.length; a++) {
			if (v[a] == CompiledAdf.U) {
				try {
					v[a] = CompiledAdf.T;
					if (SimpleConflictFreeAdfReasoner.isConflictFree(adf, v)) {
						return false;
					}
					v[a] = CompiledAdf.F;
					if (SimpleConflictFreeAdfReasoner.isConflictFree(adf, v)) {
						return false;
					}
				} finally {
					v[a] = CompiledAdf.U;
				}
			}
		}
		return true;
	}

}

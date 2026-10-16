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
 * Enumerates the admissible interpretations (ADM) of an ADF: the υ with
 * υ ≤_i Γ(υ) (Brewka et al. 2013, Definition 2). Checks all 3^n
 * interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleAdmissibleAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	protected ModelIterator models(CompiledAdf adf) {
		return threeValued(adf, SimpleAdmissibleAdfReasoner::isAdmissible);
	}

	@Override
	protected boolean verify(CompiledAdf adf, byte[] v) {
		return isAdmissible(adf, v);
	}

	static boolean isAdmissible(CompiledAdf adf, byte[] v) {
		for (int a = 0; a < v.length; a++) {
			if (v[a] != CompiledAdf.U && adf.gamma(a, v) != v[a]) {
				return false;
			}
		}
		return true;
	}

}

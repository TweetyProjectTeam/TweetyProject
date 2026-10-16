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

import java.util.List;

import org.tweetyproject.arg.adf.semantics.CompiledAdf;

/**
 * Enumerates the complete interpretations (CO) of an ADF: the fixpoints
 * v = Γ(v) (Brewka et al. 2013, Definition 3). Checks all 3^n
 * interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleCompleteAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	protected List<byte[]> models(CompiledAdf adf) {
		return threeValued(adf, SimpleCompleteAdfReasoner::isComplete);
	}

	@Override
	protected boolean verify(CompiledAdf adf, byte[] v) {
		return isComplete(adf, v);
	}

	static boolean isComplete(CompiledAdf adf, byte[] v) {
		for (int a = 0; a < v.length; a++) {
			if (adf.gamma(a, v) != v[a]) {
				return false;
			}
		}
		return true;
	}

}

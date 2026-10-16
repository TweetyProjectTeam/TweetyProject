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
 * Enumerates the two-valued models (MOD) of an ADF: the two-valued
 * interpretations υ with υ(a) = υ(φ_a) for every argument a. Checks all 2^n
 * two-valued interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleTwoValuedModelAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	protected List<byte[]> models(CompiledAdf adf) {
		return twoValued(adf, SimpleTwoValuedModelAdfReasoner::isTwoValuedModel);
	}

	@Override
	protected boolean verify(CompiledAdf adf, byte[] v) {
		return isTwoValuedModel(adf, v);
	}

	static boolean isTwoValuedModel(CompiledAdf adf, byte[] v) {
		for (int a = 0; a < v.length; a++) {
			if (v[a] == CompiledAdf.U || adf.kleene(a, v) != v[a]) {
				return false;
			}
		}
		return true;
	}

}

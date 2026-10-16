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
 * Enumerates the stable models (ST) of an ADF (Brewka et al. 2013,
 * Definition 6): the two-valued models υ such that the arguments mapped to t
 * are exactly those mapped to t by the grounded interpretation of the reduct
 * D^υ.
 * <p>
 * The reduct keeps the true arguments and replaces every false argument by ⊥;
 * its grounded interpretation is computed by iterating Γ on the original
 * conditions with the false arguments fixed to f, which is the same operator.
 * Checks all 2^n two-valued interpretations.
 *
 * @author Lars Bengel
 */
public class SimpleStableAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	protected List<byte[]> models(CompiledAdf adf) {
		return twoValued(adf, this::verify);
	}

	@Override
	protected boolean verify(CompiledAdf adf, byte[] v) {
		return SimpleTwoValuedModelAdfReasoner.isTwoValuedModel(adf, v) && reductGroundedIsTotal(adf, v);
	}

	/** @return true if the grounded interpretation of the reduct D^v maps every true argument of v to t */
	private static boolean reductGroundedIsTotal(CompiledAdf adf, byte[] v) {
		byte[] w = new byte[v.length];
		for (int a = 0; a < v.length; a++) {
			w[a] = v[a] == CompiledAdf.F ? CompiledAdf.F : CompiledAdf.U;
		}
		boolean changed = true;
		while (changed) {
			changed = false;
			byte[] next = w.clone();
			for (int a = 0; a < v.length; a++) {
				if (v[a] == CompiledAdf.T) {
					next[a] = adf.gamma(a, w);
					changed |= next[a] != w[a];
				}
			}
			w = next;
		}
		for (int a = 0; a < v.length; a++) {
			if (v[a] == CompiledAdf.T && w[a] != CompiledAdf.T) {
				return false;
			}
		}
		return true;
	}

}

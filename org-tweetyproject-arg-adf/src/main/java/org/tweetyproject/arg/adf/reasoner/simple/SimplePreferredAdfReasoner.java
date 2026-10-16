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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.tweetyproject.arg.adf.semantics.CompiledAdf;

/**
 * Enumerates the preferred interpretations (PR) of an ADF: the ≤_i-maximal
 * admissible interpretations (Brewka et al. 2013, Definition 4).
 * <p>
 * Every admissible interpretation lies below a complete one, so these are
 * exactly the ≤_i-maximal complete interpretations. The reasoner collects all
 * complete interpretations (3^n candidates) and keeps the maximal ones.
 *
 * @author Lars Bengel
 */
public class SimplePreferredAdfReasoner extends AbstractSimpleAdfReasoner {

	@Override
	protected List<byte[]> models(CompiledAdf adf) {
		return preferred(adf);
	}

	@Override
	protected boolean verify(CompiledAdf adf, byte[] v) {
		if (!SimpleCompleteAdfReasoner.isComplete(adf, v)) {
			return false;
		}
		for (byte[] p : preferred(adf)) {
			if (isAtLeastAsInformativeAs(p, v)) {
				return java.util.Arrays.equals(v, p);
			}
		}
		throw new IllegalStateException("A complete interpretation must lie below a preferred one.");
	}

	private static List<byte[]> preferred(CompiledAdf adf) {
		List<byte[]> complete = threeValued(adf, SimpleCompleteAdfReasoner::isComplete);
		// candidates with more decided arguments first: anything strictly above a
		// candidate is visited before it, and so is a maximal element above that
		complete.sort(Comparator.comparingInt(SimplePreferredAdfReasoner::undecided));
		List<byte[]> maximal = new ArrayList<>();
		for (byte[] candidate : complete) {
			boolean dominated = false;
			for (byte[] m : maximal) {
				if (isAtLeastAsInformativeAs(m, candidate)) {
					dominated = true;
					break;
				}
			}
			if (!dominated) {
				maximal.add(candidate);
			}
		}
		return maximal;
	}

	private static int undecided(byte[] v) {
		int count = 0;
		for (byte b : v) {
			if (b == CompiledAdf.U) {
				count++;
			}
		}
		return count;
	}

}

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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat.query;

import java.util.Iterator;
import java.util.Objects;

import org.tweetyproject.arg.adf.reasoner.sat.execution.Execution;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * @author Mathias Hofer
 *
 */
final class CandidateIterator implements Iterator<Interpretation>{

	private final Execution execution;

	private Interpretation next;

	private boolean end;

	public CandidateIterator(Execution execution) {
		this.execution = Objects.requireNonNull(execution);
	}

	@Override
	public boolean hasNext() {
		if (!end && next == null) {
			// we do not know if we have already reached the end
			next = execution.computeCandidate();
		}
		return next != null;
	}

	@Override
	public Interpretation next() {
		Interpretation result = next;

		if (result != null) {
			next = null;
		} else if (!end) {
			result = execution.computeCandidate();
			end = result == null;
		}

		return result;
	}
	
}

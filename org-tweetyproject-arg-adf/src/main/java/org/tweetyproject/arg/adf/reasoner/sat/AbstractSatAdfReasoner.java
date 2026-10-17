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
package org.tweetyproject.arg.adf.reasoner.sat;

import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.sat.SatSolver;

/**
 * Base class of the SAT-based reasoners. Every call creates its own solvers
 * with the given supplier and closes them before it returns. Long computations
 * stop with a {@link CancellationException} if the current thread is
 * interrupted.
 *
 * @author Lars Bengel
 */
public abstract class AbstractSatAdfReasoner extends AbstractAdfReasoner {

	/** creates a fresh solver for every call */
	protected final Supplier<SatSolver> solvers;

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	protected AbstractSatAdfReasoner(Supplier<SatSolver> solvers) {
		this.solvers = solvers;
	}

	/**
	 * @throws CancellationException if the current thread is interrupted
	 */
	protected static void checkInterrupted() {
		if (Thread.currentThread().isInterrupted()) {
			throw new CancellationException("Computation interrupted.");
		}
	}

}

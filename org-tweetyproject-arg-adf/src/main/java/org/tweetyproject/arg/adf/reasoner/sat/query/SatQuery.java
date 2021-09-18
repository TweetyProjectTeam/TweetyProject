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

import java.util.Objects;

import org.tweetyproject.arg.adf.reasoner.query.Query;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Configuration;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Execution;
import org.tweetyproject.arg.adf.reasoner.sat.execution.ParallelExecution;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Semantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.SequentialExecution;

/**
 * @author Mathias Hofer
 *
 */
abstract class SatQuery<T> implements Query<T> {

	final Semantics semantics;

	final Configuration configuration;

	SatQuery(Semantics semantics, Configuration configuration) {
		this.semantics = Objects.requireNonNull(semantics);
		this.configuration = Objects.requireNonNull(configuration);
	}

	abstract T execute(Execution execution);
	
	@Override
	public abstract SatQuery<T> configure(Configuration configuration);

	@Override
	public T execute() {
		try (Execution execution = new SequentialExecution(semantics, configuration.getSatSolver())) {
			return execute(execution);
		}
	}

	@Override
	public T executeParallel() {
		try (Execution execution = new ParallelExecution(semantics, configuration.getSatSolver(), configuration.getParallelism())) {
			return execute(execution);
		}
	}

}

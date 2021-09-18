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
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * @author Mathias Hofer
 *
 */
final class ForAllSatQuery implements Query<Boolean> {
	
	private final Semantics semantics;

	private final Configuration configuration;
	
	private final Argument condition;

	public ForAllSatQuery(Semantics semantics, Configuration configuration, Argument condition) {
		this.semantics = Objects.requireNonNull(semantics);
		this.configuration = Objects.requireNonNull(configuration);
		this.condition = Objects.requireNonNull(condition);
	}
	
	@Override
	public Query<Boolean> configure(Configuration configuration) {
		return new ForAllSatQuery(semantics, configuration, condition);
	}

	@Override
	public Boolean execute() {
		try (Execution execution = new SequentialExecution(semantics.restrict(Interpretation.ofUnsatisfied(condition)), configuration.getSatSolver())) {
			if (execution.stream().findAny().isPresent()) {
				return false;
			}
		}
		try (Execution execution = new SequentialExecution(semantics.restrict(Interpretation.ofUndecided(condition)), configuration.getSatSolver())) {
			return execution.stream().findAny().isEmpty();
		}
	}

	@Override
	public Boolean executeParallel() {
		try (Execution execution = new ParallelExecution(semantics.restrict(Interpretation.ofUnsatisfied(condition)), configuration.getSatSolver(), configuration.getParallelism())) {
			if (execution.stream().findAny().isPresent()) {
				return false;
			}
		}
		try (Execution execution = new ParallelExecution(semantics.restrict(Interpretation.ofUndecided(condition)), configuration.getSatSolver(), configuration.getParallelism())) {
			return execution.stream().findAny().isEmpty();
		}
	}

}

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

import java.util.stream.Stream;

import org.tweetyproject.arg.adf.reasoner.sat.execution.Configuration;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Execution;
import org.tweetyproject.arg.adf.reasoner.sat.execution.ParallelExecution;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Semantics;
import org.tweetyproject.arg.adf.reasoner.sat.execution.SequentialExecution;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * @author Mathias Hofer
 *
 */
final class InterpretationsSatQuery extends SatQuery<Stream<Interpretation>>{

	public InterpretationsSatQuery(Semantics semantics, Configuration configuration) {
		super(semantics, configuration);
	}

	@Override
	Stream<Interpretation> execute(Execution execution) {
		return execution.stream();
	}
	
	@Override
	public SatQuery<Stream<Interpretation>> configure(Configuration configuration) {
		return new InterpretationsSatQuery(semantics, configuration);
	}
	
	@Override
	public Stream<Interpretation> execute() {
		return execute(new SequentialExecution(semantics, configuration.getSatSolver()));
	}
	
	@Override
	public Stream<Interpretation> executeParallel() {
		return execute(new ParallelExecution(semantics, configuration.getSatSolver(), configuration.getParallelism()));
	}

}

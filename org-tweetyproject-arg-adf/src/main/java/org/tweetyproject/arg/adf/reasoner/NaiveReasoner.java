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
package org.tweetyproject.arg.adf.reasoner;

import org.tweetyproject.arg.adf.reasoner.sat.Pipeline;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ConflictFreeGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.MaximizeInterpretationProcessor;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;

public class NaiveReasoner extends AbstractDialecticalFrameworkReasoner {

	/**
	 * 
	 * @param solver
	 *            the underlying sat solver
	 */
	public NaiveReasoner(IncrementalSatSolver solver) {
		super(satBased(solver));
	}

	private static Pipeline satBased(IncrementalSatSolver solver) {
		return Pipeline.builder(new ConflictFreeGenerator(), solver)
				.addModelProcessor(new MaximizeInterpretationProcessor())
				.build();
	}

}

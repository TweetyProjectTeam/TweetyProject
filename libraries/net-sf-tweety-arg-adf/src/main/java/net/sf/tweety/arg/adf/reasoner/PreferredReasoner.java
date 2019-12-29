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
package net.sf.tweety.arg.adf.reasoner;

import net.sf.tweety.arg.adf.reasoner.generator.SatConflictFreeGenerator;
import net.sf.tweety.arg.adf.reasoner.processor.SatKBipolarStateProcessor;
import net.sf.tweety.arg.adf.reasoner.processor.SatMaximizeInterpretationProcessor;
import net.sf.tweety.arg.adf.reasoner.verifier.SatAdmissibleVerifier;
import net.sf.tweety.arg.adf.reasoner.verifier.Verifier;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;

/**
 * @author Mathias Hofer
 *
 */
public class PreferredReasoner extends AbstractDialecticalFrameworkReasoner {

	/**
	 * @param solver
	 *            the underlying sat solver
	 */
	public PreferredReasoner(IncrementalSatSolver solver) {
		super(satBased(solver));
	}

	private static Pipeline<SatReasonerContext> satBased(IncrementalSatSolver solver) {
		Verifier<SatReasonerContext> admissibleVerifier = new SatAdmissibleVerifier();
		return Pipeline.builder(new SatConflictFreeGenerator(solver))
				.addStateProcessor(new SatKBipolarStateProcessor())
				.addVerifier(admissibleVerifier)
				.addModelProcessor(new SatMaximizeInterpretationProcessor(admissibleVerifier), true)
				.build();
	}

}

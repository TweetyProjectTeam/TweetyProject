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
package net.sf.tweety.arg.adf.reasoner.sat.processor;

import net.sf.tweety.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.RefineLargerSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.verifier.Verifier;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Maximizes the given interpretation and afterwards restricts the search space
 * from generating smaller ones.
 * 
 * @author Mathias Hofer
 *
 */
public class MaximizeInterpretationProcessor implements InterpretationProcessor {

	private final Verifier verifier;
	
	public MaximizeInterpretationProcessor() {
		this(null);
	}

	/**
	 * @param verifier verifies a property of the intermediate solutions
	 */
	public MaximizeInterpretationProcessor(Verifier verifier) {
		this.verifier = verifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.processor.InterpretationProcessor#process(
	 * java.lang. Object, net.sf.tweety.arg.adf.semantics.Interpretation)
	 */
	@Override
	public Interpretation process(SatSolverState processingState, SatSolverState verificationState, PropositionalMapping mapping, Interpretation interpretation,
			AbstractDialecticalFramework adf) {
		Interpretation maximal = interpretation;
		new LargerInterpretationSatEncoding(maximal).encode(processingState::add, mapping, adf);
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = null;
		while ((witness = processingState.witness()) != null) {
			Interpretation larger = Interpretation.fromWitness(witness, mapping, adf);
			SatEncoding restrict = null;
			if (verifier == null || verifier.verify(verificationState, mapping, larger, adf)) {
				maximal = larger;
				restrict = new LargerInterpretationSatEncoding(maximal);
			} else {
				// does not have some property established by the verifier, but
				// nonetheless we have to prevent the candidate from being
				// computed again
				restrict = new RefineUnequalSatEncoding(larger);
			}
			restrict.encode(processingState::add, mapping, adf);
		}

		return maximal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.processor.InterpretationProcessor#
	 * updateState(net.sf.tweety.arg.adf.reasoner.SatReasonerContext,
	 * net.sf.tweety.arg.adf.semantics.Interpretation,
	 * net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void updateState(SatSolverState state, PropositionalMapping mapping, Interpretation maximal, AbstractDialecticalFramework adf) {
		// we maximized the given interpretation, now prevent all smaller ones
		// from being computed in future by the given state
		new RefineLargerSatEncoding(maximal).encode(state::add, mapping, adf);
	}

}

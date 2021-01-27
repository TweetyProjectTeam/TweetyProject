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
package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.Set;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineLargerSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Atom;

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
	 * org.tweetyproject.arg.adf.reasoner.processor.InterpretationProcessor#process(
	 * java.lang. Object, org.tweetyproject.arg.adf.semantics.Interpretation)
	 */
	@Override
	public Interpretation process(SatSolverState processingState, SatSolverState verificationState, PropositionalMapping mapping, Interpretation interpretation,
			AbstractDialecticalFramework adf) {
		Interpretation maximal = interpretation;
		new LargerInterpretationSatEncoding(maximal).encode(processingState::add, mapping, adf);
		Set<Atom> witness = null;
		while ((witness = processingState.witness(mapping.getArguments())) != null) {
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
	 * @see org.tweetyproject.arg.adf.reasoner.processor.InterpretationProcessor#
	 * updateState(org.tweetyproject.arg.adf.reasoner.SatReasonerContext,
	 * org.tweetyproject.arg.adf.semantics.Interpretation,
	 * org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void updateState(SatSolverState state, PropositionalMapping mapping, Interpretation maximal, AbstractDialecticalFramework adf) {
		// we maximized the given interpretation, now prevent all smaller ones
		// from being computed in future by the given state
		new RefineLargerSatEncoding(maximal).encode(state::add, mapping, adf);
	}

}

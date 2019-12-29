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
package net.sf.tweety.arg.adf.reasoner.processor;

import net.sf.tweety.arg.adf.reasoner.SatReasonerContext;
import net.sf.tweety.arg.adf.reasoner.encodings.LargerInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.RefineLargerSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.RefineUnequalSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncodingContext;
import net.sf.tweety.arg.adf.reasoner.verifier.Verifier;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Maximizes the given interpretation and afterwards restricts the search space
 * from generating smaller ones.
 * 
 * @author Mathias Hofer
 *
 */
public class SatMaximizeInterpretationProcessor implements InterpretationProcessor<SatReasonerContext> {

	private static final SatEncoding LARGER_INTERPRETATION = new LargerInterpretationSatEncoding();

	private static final SatEncoding REFINE_LARGER = new RefineLargerSatEncoding();

	private static final SatEncoding REFINE_UNEQUAL = new RefineUnequalSatEncoding();

	private Verifier<SatReasonerContext> verifier;

	public SatMaximizeInterpretationProcessor() {
		this(null);
	}

	/**
	 * @param verifier
	 */
	public SatMaximizeInterpretationProcessor(Verifier<SatReasonerContext> verifier) {
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
	public Interpretation process(SatReasonerContext context, Interpretation interpretation,
			AbstractDialecticalFramework adf) {
		SatEncodingContext encodingContext = context.getEncodingContext();
		SatSolverState state = context.getSolverState();
		Interpretation maximal = interpretation;
		state.add(LARGER_INTERPRETATION.encode(encodingContext, maximal));
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = null;
		while ((witness = state.witness()) != null) {
			Interpretation larger = encodingContext.interpretationFromWitness(witness);
			if (verifier == null || verifier.verify(context, larger, adf)) {
				maximal = larger;
				state.add(LARGER_INTERPRETATION.encode(encodingContext, maximal));
			} else {
				// does not have some property established by the verifier, but
				// nonetheless we have to prevent the candidate from being
				// computed again
				state.add(REFINE_UNEQUAL.encode(encodingContext, larger));
			}
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
	public void updateState(SatReasonerContext context, Interpretation maximal, AbstractDialecticalFramework adf) {
		// we maximized the given interpretation, now prevent all smaller ones
		// from being computed in future by the given state
		SatEncodingContext encodingContext = context.getEncodingContext();
		SatSolverState state = context.getSolverState();
		state.add(REFINE_LARGER.encode(encodingContext, maximal));
	}

}

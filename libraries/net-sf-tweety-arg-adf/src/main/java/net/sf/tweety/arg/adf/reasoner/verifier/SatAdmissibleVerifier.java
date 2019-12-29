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
package net.sf.tweety.arg.adf.reasoner.verifier;

import net.sf.tweety.arg.adf.reasoner.SatReasonerContext;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncodingContext;
import net.sf.tweety.arg.adf.reasoner.encodings.VerifyAdmissibleSatEncoding;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class SatAdmissibleVerifier implements Verifier<SatReasonerContext> {

	private static final SatEncoding VERIFY_ADMISSIBLE_ENCODING = new VerifyAdmissibleSatEncoding();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.verifier.Verifier#verify(java.lang.Object,
	 * net.sf.tweety.arg.adf.semantics.Interpretation)
	 */
	@Override
	public boolean verify(SatReasonerContext state, Interpretation candidate, AbstractDialecticalFramework adf) {
		IncrementalSatSolver satSolver = state.getSolver();
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		try (SatSolverState verificationState = satSolver.createState()) {
			verificationState.add(VERIFY_ADMISSIBLE_ENCODING.encode(encodingContext, candidate));
			boolean notAdmissible = verificationState.satisfiable();
			return !notAdmissible;
		} catch (Exception e) {
			// TODO exception handling
			e.printStackTrace();
		}
		return false;
	}

}

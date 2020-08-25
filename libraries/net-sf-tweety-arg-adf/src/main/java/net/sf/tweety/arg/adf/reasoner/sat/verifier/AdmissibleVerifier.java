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
package net.sf.tweety.arg.adf.reasoner.sat.verifier;

import net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.VerifyAdmissibleSatEncoding;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class AdmissibleVerifier implements Verifier {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.sat.verifier.Verifier#prepareState(net.sf.
	 * tweety.arg.adf.sat.SatSolverState,
	 * net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping,
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework)
	 */
	@Override
	public void prepareState(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.verifier.Verifier#verify(java.lang.Object,
	 * net.sf.tweety.arg.adf.semantics.Interpretation)
	 */
	@Override
	public boolean verify(SatSolverState state, PropositionalMapping mapping, Interpretation candidate, AbstractDialecticalFramework adf) {
		new VerifyAdmissibleSatEncoding(candidate).encode(state::add, mapping, adf);
		boolean notAdmissible = state.satisfiable();
		return !notAdmissible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.sat.verifier.Verifier#postVerification(net
	 * .sf.tweety.arg.adf.sat.SatSolverState,
	 * net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping,
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation,
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework, boolean)
	 */
	@Override
	public boolean postVerification(SatSolverState state, PropositionalMapping mapping, Interpretation candidate,
			AbstractDialecticalFramework adf, boolean verificationResult) {
		return true;
	}
}

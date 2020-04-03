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
import net.sf.tweety.arg.adf.reasoner.encodings.BipolarSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.KBipolarSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncodingContext;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class SatKBipolarStateProcessor implements StateProcessor<SatReasonerContext> {

	private static final SatEncoding BIPOLAR_ENCODING = new BipolarSatEncoding();

	private static final SatEncoding K_BIPOLAR_ENCODING = new KBipolarSatEncoding();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.processor.StateProcessor#process(java.lang
	 * .Object, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void process(SatReasonerContext context, AbstractDialecticalFramework adf) {
		SatEncodingContext encodingContext = context.getEncodingContext();
		SatSolverState solverState = context.getSolverState();
		solverState.add(BIPOLAR_ENCODING.encode(encodingContext));
		if (!adf.bipolar()) {
			solverState.add(K_BIPOLAR_ENCODING.encode(encodingContext, Interpretation.empty(adf)));
		}
	}

}

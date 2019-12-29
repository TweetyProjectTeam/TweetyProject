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
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.transform.FixPartialTransform;

/**
 * @author Mathias Hofer
 *
 */
public class SatPartialKBipolarInterpretationProcessor implements InterpretationProcessor<SatReasonerContext> {

	private static final SatEncoding BIPOLAR_ENCODING = new BipolarSatEncoding();

	private static final SatEncoding K_BIPOLAR_ENCODING = new KBipolarSatEncoding();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.processor.InterpretationProcessor#process(java.lang.Object, net.sf.tweety.arg.adf.semantics.Interpretation, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Interpretation process(SatReasonerContext context, Interpretation interpretation,
			AbstractDialecticalFramework adf) {
		SatSolverState state = context.getSolverState();
		SatEncodingContext encodingContext = context.getEncodingContext();
		AbstractDialecticalFramework transformed = adf.transform(new FixPartialTransform(interpretation));
		encodingContext.setAdf(transformed);
//		if (transformed.kBipolar() <= 5) {
//			state.add(BIPOLAR_ENCODING.encode(encodingContext));
//			System.out.println("kbip: " + transformed.kBipolar());
//			final Interpretation EMPTY_INTERPRETATION = new Interpretation(adf);
			state.add(K_BIPOLAR_ENCODING.encode(encodingContext, interpretation));
//			System.out.println("encoded");
//		}
		encodingContext.setAdf(adf);
		return interpretation;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.processor.InterpretationProcessor#updateState(net.sf.tweety.arg.adf.reasoner.SatReasonerContext, net.sf.tweety.arg.adf.semantics.Interpretation, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void updateState(SatReasonerContext context, Interpretation processed, AbstractDialecticalFramework adf) {
		// TODO Auto-generated method stub
		
	}

}

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
package net.sf.tweety.arg.adf.reasoner.strategy;

import net.sf.tweety.arg.adf.reasoner.strategy.sat.BipolarSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.KBipolarSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.LargerInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.RefineLargerSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.RefineUnequalSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncodingContext;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * @author Mathias Hofer
 *
 */
public class SatSearchSpace extends SearchSpace {

	private static final SatEncoding BIPOLAR_ENCODING = new BipolarSatEncoding();

	private static final SatEncoding K_BIPOLAR_ENCODING = new KBipolarSatEncoding();

	private static final SatEncoding LARGER_INTERPRETATION = new LargerInterpretationSatEncoding();

	private static final SatEncoding REFINE_LARGER = new RefineLargerSatEncoding();

	private static final SatEncoding REFINE_UNEQUAL = new RefineUnequalSatEncoding();

	private SatSolverState solverState;

	private SatEncodingContext encodingContext;

	private boolean closed = false;

	/**
	 * @param solverState
	 *            the state of the sat solver
	 * @param encodingContext
	 *            the context of the sat encoding
	 */
	public SatSearchSpace(SatSolverState solverState, SatEncodingContext encodingContext,
			AbstractDialecticalFramework adf, boolean kBipolar) {
		super(adf);
		this.solverState = solverState;
		this.encodingContext = encodingContext;
		if (kBipolar) {
			solverState.add(BIPOLAR_ENCODING.encode(encodingContext));
			if (!adf.bipolar()) {
				final Interpretation EMPTY_INTERPRETATION = new Interpretation(adf);
				solverState.add(K_BIPOLAR_ENCODING.encode(encodingContext, EMPTY_INTERPRETATION));
			}
		}
	}

	/**
	 * @param solverState
	 *            the state of the sat solver
	 * @param encodingContext
	 *            the context of the sat encoding
	 */
	public SatSearchSpace(SatSolverState solverState, SatEncodingContext encodingContext,
			AbstractDialecticalFramework adf, SearchSpace subSpace) {
		this(solverState, encodingContext, adf, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace#updateLarger(net.sf.
	 * tweety.arg.adf.semantics.Interpretation)
	 */
	@Override
	public void updateLarger(Interpretation interpretation) {
		solverState.add(REFINE_LARGER.encode(encodingContext, interpretation));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace#updateLarger2(net.sf.
	 * tweety.arg.adf.semantics.Interpretation)
	 */
	@Override
	public void updateSpecificLarger(Interpretation interpretation) {
		solverState.add(LARGER_INTERPRETATION.encode(encodingContext, interpretation));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace#updateUnequal(net.sf.
	 * tweety.arg.adf.semantics.Interpretation)
	 */
	@Override
	public void updateUnequal(Interpretation interpretation) {
		solverState.add(REFINE_UNEQUAL.encode(encodingContext, interpretation));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace#close()
	 */
	@Override
	public void close() {
		closed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace#candidate()
	 */
	@Override
	public Interpretation candidate() {
		if (!closed) {
			net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = solverState.witness();
			if (witness != null) {
				return encodingContext.interpretationFromWitness(witness);
			}
		}
		return null;
	}

}

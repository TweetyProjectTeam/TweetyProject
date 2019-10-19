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
package net.sf.tweety.arg.adf.reasoner.strategy.admissible;

import net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.conflictfree.ConflictFreeReasonerStrategy;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncodingContext;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.VerifyAdmissibleSatEncoding;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class SatAdmissibleReasonerStrategy implements AdmissibleReasonerStrategy {

	private ConflictFreeReasonerStrategy conflictFreeStrategy;

	private IncrementalSatSolver satSolver;

	private static final SatEncoding VERIFY_ADMISSIBLE_ENCODING = new VerifyAdmissibleSatEncoding();

	public SatAdmissibleReasonerStrategy(ConflictFreeReasonerStrategy conflictFreeStrategy,
			IncrementalSatSolver satSolver) {
		this.conflictFreeStrategy = conflictFreeStrategy;
		this.satSolver = satSolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.ReasonerStrategy#createSearchSpace()
	 */
	@Override
	public SearchSpace createSearchSpace(AbstractDialecticalFramework adf) {
		return conflictFreeStrategy.createSearchSpace(adf);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.admissible.AdmissibleReasonerStrategy#verifyAdmissible(net.sf.tweety.arg.adf.semantics.Interpretation, net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace)
	 */
	@Override
	public boolean verifyAdmissible(Interpretation candidate, SearchSpace searchSpace) {
		AbstractDialecticalFramework adf = searchSpace.getAbstractDialecticalFramework();
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		try (SatSolverState verificationState = satSolver.createState()) {
			verificationState.add(VERIFY_ADMISSIBLE_ENCODING.encode(encodingContext, candidate));
			boolean notAdmissible = verificationState.satisfiable();
			return !notAdmissible;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.admissible.
	 * AdmissibleReasonerStrategy#nextAdmissible()
	 */
	@Override
	public Interpretation nextAdmissible(SearchSpace searchSpace) {
		Interpretation conflictFree = null;
		while ((conflictFree = conflictFreeStrategy.next(searchSpace)) != null) {
			boolean admissible = verifyAdmissible(conflictFree, searchSpace);
			searchSpace.updateUnequal(conflictFree);
			if (admissible) {
				return conflictFree;
			}
		}
		return null;
	}

}

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
package net.sf.tweety.arg.adf.reasoner.strategy.naive;

import net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.conflictfree.ConflictFreeReasonerStrategy;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class DefaultNaiveReasonerStrategy implements NaiveReasonerStrategy {

	private ConflictFreeReasonerStrategy conflictFreeStrategy;

	/**
	 * @param satSolver
	 */
	public DefaultNaiveReasonerStrategy(ConflictFreeReasonerStrategy conflictFreeStrategy) {
		this.conflictFreeStrategy = conflictFreeStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.ReasonerStrategy#createSearchSpace(net.sf.
	 * tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public SearchSpace createSearchSpace(AbstractDialecticalFramework adf) {
		return conflictFreeStrategy.createSearchSpace(adf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.naive.NaiveReasonerStrategy#
	 * nextNaive(net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace)
	 */
	@Override
	public Interpretation nextNaive(SearchSpace searchSpace) {
		Interpretation conflictFree = conflictFreeStrategy.next(searchSpace);
		if (conflictFree != null) {
			Interpretation naive = maximize(conflictFree, searchSpace);			
			if (naive != null) {
				searchSpace.updateLarger(naive);
				return naive;
			}
		}
		return null;
	}

	private Interpretation maximize(Interpretation candidate, SearchSpace searchSpace) {
		AbstractDialecticalFramework adf = searchSpace.getAbstractDialecticalFramework();
		SearchSpace maximizeSearchSpace = conflictFreeStrategy.createSearchSpace(adf);
		maximizeSearchSpace.updateSpecificLarger(candidate);
		Interpretation naive = candidate;
		while ((candidate = conflictFreeStrategy.next(maximizeSearchSpace)) != null) {
			maximizeSearchSpace.updateSpecificLarger(candidate);
			naive = candidate;
		}
		return naive;
	}
}

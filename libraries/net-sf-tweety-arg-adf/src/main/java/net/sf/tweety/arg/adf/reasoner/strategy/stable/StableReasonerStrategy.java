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
package net.sf.tweety.arg.adf.reasoner.strategy.stable;

import net.sf.tweety.arg.adf.reasoner.ReasonerStrategy;
import net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace;
import net.sf.tweety.arg.adf.semantics.Interpretation;

/**
 * @author Mathias Hofer
 *
 */
public interface StableReasonerStrategy extends ReasonerStrategy {
	
	public Interpretation nextStable(SearchSpace searchSpace);
	
	public boolean verifyStable(Interpretation candidate, SearchSpace searchSpace);
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.ReasonerStrategy#next(net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace)
	 */
	@Override
	default Interpretation next(SearchSpace searchSpace) {
		return nextStable(searchSpace);
	}

}

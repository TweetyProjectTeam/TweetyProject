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

import net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.ground.GroundReasonerStrategy;
import net.sf.tweety.arg.adf.reasoner.strategy.model.ModelReasonerStrategy;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class DefaultStableReasonerStrategy implements StableReasonerStrategy{

	private ModelReasonerStrategy modelStrategy;
	
	private GroundReasonerStrategy groundStrategy;
	
	/**
	 * @param modelStrategy
	 * @param groundStrategy
	 */
	public DefaultStableReasonerStrategy(ModelReasonerStrategy modelStrategy, GroundReasonerStrategy groundStrategy) {
		super();
		this.modelStrategy = modelStrategy;
		this.groundStrategy = groundStrategy;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.ReasonerStrategy#createSearchSpace(net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public SearchSpace createSearchSpace(AbstractDialecticalFramework adf) {
		return modelStrategy.createSearchSpace(adf);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.stable.StableReasonerStrategy#nextStable(net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace)
	 */
	@Override
	public Interpretation nextStable(SearchSpace searchSpace) {
		Interpretation model = null;
		while ((model = modelStrategy.next(searchSpace)) != null) {
			if (verifyStable(model, searchSpace)) {
				return model;
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.strategy.stable.StableReasonerStrategy#verifyStable(net.sf.tweety.arg.adf.semantics.Interpretation, net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace)
	 */
	@Override
	public boolean verifyStable(Interpretation candidate, SearchSpace searchSpace) {
		AbstractDialecticalFramework adf = searchSpace.getAbstractDialecticalFramework();
		AbstractDialecticalFramework reduct = adf.omegaReduct(candidate);	
		SearchSpace groundSpace = groundStrategy.createSearchSpace(reduct);
		Interpretation ground = groundStrategy.next(groundSpace);
		boolean stable = candidate.equals(ground);
		return stable;
	}
	
}

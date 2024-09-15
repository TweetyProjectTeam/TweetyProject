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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.cl.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.logics.cl.semantics.RankingFunction;
import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.syntax.Conditional;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Abstract ancestor for all reasoner for conditional logic.
 *
 * @author Matthias Thimm
 *
 */
public abstract class AbstractConditionalLogicReasoner implements QualitativeReasoner<ClBeliefSet,PlFormula>, ModelProvider<Conditional,ClBeliefSet,RankingFunction>{

	/** Default */
	public AbstractConditionalLogicReasoner(){
		super();
	}
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Boolean query(ClBeliefSet beliefbase, PlFormula formula) {
		return this.getModel(beliefbase).rank(formula) == 0;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Collection<RankingFunction> getModels(ClBeliefSet bbase) {
		Collection<RankingFunction> ocfs = new HashSet<RankingFunction>();
		ocfs.add(this.getModel(bbase));
		return ocfs;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public abstract RankingFunction getModel(ClBeliefSet bbase);
}

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
package net.sf.tweety.logics.cl.reasoner;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.ModelProvider;
import net.sf.tweety.commons.QualitativeReasoner;
import net.sf.tweety.logics.cl.semantics.RankingFunction;
import net.sf.tweety.logics.cl.syntax.ClBeliefSet;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Abstract ancestor for all reasoner for conditional logic.
 * 
 * @author Matthias Thimm
 *
 */
public abstract class AbstractConditionalLogicReasoner implements QualitativeReasoner<ClBeliefSet,PropositionalFormula>, ModelProvider<Conditional,ClBeliefSet,RankingFunction>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.QualitativeReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Boolean query(ClBeliefSet beliefbase, PropositionalFormula formula) {		
		return this.getModel(beliefbase).rank(formula) == 0;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModels(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public Collection<RankingFunction> getModels(ClBeliefSet bbase) {
		Collection<RankingFunction> ocfs = new HashSet<RankingFunction>();
		ocfs.add(this.getModel(bbase));
		return ocfs;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModel(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public abstract RankingFunction getModel(ClBeliefSet bbase);
}

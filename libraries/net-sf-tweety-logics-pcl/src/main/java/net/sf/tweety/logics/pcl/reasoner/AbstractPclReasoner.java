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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pcl.reasoner;

import java.util.Collection;

import net.sf.tweety.commons.ModelProvider;
import net.sf.tweety.commons.QuantitativeReasoner;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.pcl.syntax.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Abstract class for all PCL reasoners.
 * @author Matthias Thimm
 */
public abstract class AbstractPclReasoner implements QuantitativeReasoner<PclBeliefSet,PropositionalFormula>, ModelProvider<ProbabilisticConditional,PclBeliefSet,ProbabilityDistribution<PossibleWorld>>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public abstract Double query(PclBeliefSet beliefbase, PropositionalFormula formula);

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModels(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public abstract Collection<ProbabilityDistribution<PossibleWorld>> getModels(PclBeliefSet bbase);
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModel(net.sf.tweety.commons.BeliefBase)
	 */
	public abstract ProbabilityDistribution<PossibleWorld> getModel(PclBeliefSet beliefbase);
}

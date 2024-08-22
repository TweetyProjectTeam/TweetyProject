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
package org.tweetyproject.logics.pcl.reasoner;

import java.util.Collection;

import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.QuantitativeReasoner;
import org.tweetyproject.logics.pcl.semantics.ProbabilityDistribution;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Abstract class for all PCL reasoners.
 * @author Matthias Thimm
 */
public abstract class AbstractPclReasoner implements QuantitativeReasoner<PclBeliefSet,PlFormula>, ModelProvider<ProbabilisticConditional,PclBeliefSet,ProbabilityDistribution<PossibleWorld>>{

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Reasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public abstract Double query(PclBeliefSet beliefbase, PlFormula formula);

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public abstract Collection<ProbabilityDistribution<PossibleWorld>> getModels(PclBeliefSet bbase);
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	public abstract ProbabilityDistribution<PossibleWorld> getModel(PclBeliefSet beliefbase);

    /** Default Constructor */
    public AbstractPclReasoner(){}
}

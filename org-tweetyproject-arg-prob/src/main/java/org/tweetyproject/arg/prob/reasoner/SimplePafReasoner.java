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
package org.tweetyproject.arg.prob.reasoner;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.lotteries.SubgraphProbabilityFunction;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;

/**
 * This class implements a naive algorithm for computing
 * probabilities of extensions in probabilistic argumentation frameworks
 * from [Li, Oren, Norman. Probabilistic Argumentation Frameworks. TAFA'2011].
 * It considers all subgraphs and computes therefore exact probabilities
 * 
 * @author Matthias Thimm
 */
public class SimplePafReasoner extends AbstractPafReasoner{
	
	/**
	 * Creates a new reasoner.
	 * @param semantics semantics used for determining extensions.
	 * 
	 */
	public SimplePafReasoner(Semantics semantics) {
		super(semantics);
	}	

	/**
	 * Estimates the probability that the given set of
	 * arguments is an extension
	 * @param ext some set of arguments
	 * @return the estimated probability of the given set to be 
	 * an extension
	 */
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner#query(org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework, org.tweetyproject.arg.dung.semantics.Extension)
	 */
	public Double query(ProbabilisticArgumentationFramework paf, Extension ext){
		double prob = 0d;
		SubgraphProbabilityFunction p = paf.getSubgraphProbabilityFunction(); 
		for(DungTheory sub: p.keySet()){
			AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.getSemantics());
			if(r.getModels(sub).contains(ext))
				prob += p.probability(sub).doubleValue();
		}		
		return prob;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner#query(org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework, org.tweetyproject.arg.dung.syntax.Argument, org.tweetyproject.commons.InferenceMode)
	 */
	@Override
	public Double query(ProbabilisticArgumentationFramework beliefbase, Argument formula, InferenceMode inferenceMode) {
		double prob = 0d;
		SubgraphProbabilityFunction p = beliefbase.getSubgraphProbabilityFunction(); 
		for(DungTheory sub: p.keySet()){
			AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.getSemantics());
			if(r.query(sub,formula, inferenceMode))
				prob += p.probability(sub).doubleValue();
		}
		return prob;
	}
	
	@Override
	public boolean isInstalled() {
		return true;
	}
}

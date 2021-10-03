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
package org.tweetyproject.arg.prob.reasoner;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;

/**
 * This class implements the Monte Carlo algorithm for estimating
 * probabilities of extensions in probabilistic argumentation frameworks
 * from [Li, Oren, Norman. Probabilistic Argumentation Frameworks. TAFA'2011].
 * 
 * @author Matthias Thimm
 */
public class MonteCarloPafReasoner extends AbstractPafReasoner{

	/** The number of runs of the Monte Carlo simulation. */
	private int numberOfTrials;
	
	/**
	 * Creates a new reasoner.
	 * @param semantics semantics used for determining extensions.
	 * @param numberOfTrials The number of runs of the Monte Carlo simulation
	 * 
	 */
	public MonteCarloPafReasoner(Semantics semantics, int numberOfTrials) {
		super(semantics);
		this.numberOfTrials = numberOfTrials;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner#query(org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework, org.tweetyproject.arg.dung.semantics.Extension)
	 */
	public Double query(ProbabilisticArgumentationFramework paf, Extension ext){
		int count = 0;
		AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.getSemantics());
		for(int i = 0; i < this.numberOfTrials; i++){
			DungTheory sub = paf.sample();
			if(r.getModels(sub).contains(ext))
				count++;
		}
		return ((double)count)/this.numberOfTrials;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner#query(org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework, org.tweetyproject.arg.dung.syntax.Argument, org.tweetyproject.commons.InferenceMode)
	 */
	public Double query(ProbabilisticArgumentationFramework beliefbase, Argument formula, InferenceMode inferenceMode) {
		int count = 0;
		AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.getSemantics());
		for(int i = 0; i < this.numberOfTrials; i++){
			DungTheory sub = beliefbase.sample();
			if(r.query(sub,formula,inferenceMode))
				count++;
		}
		return ((double)count)/this.numberOfTrials;
	}	
	
	@Override
	public boolean isInstalled() {
		return true;
	}
}

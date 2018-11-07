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
package net.sf.tweety.arg.prob.reasoner;

import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.prob.syntax.ProbabilisticArgumentationFramework;
import net.sf.tweety.commons.InferenceMode;

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
	 * @param inferenceType The inference type used for estimating acceptability probability
	 * 	of single arguments (credulous or skeptical inference).
	 */
	public MonteCarloPafReasoner(Semantics semantics, int numberOfTrials) {
		super(semantics);
		this.numberOfTrials = numberOfTrials;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.reasoner.AbstractPafReasoner#query(net.sf.tweety.arg.prob.syntax.ProbabilisticArgumentationFramework, net.sf.tweety.arg.dung.semantics.Extension)
	 */
	public Double query(ProbabilisticArgumentationFramework paf, Extension ext){
		int count = 0;
		AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.getSemantics());
		for(int i = 0; i < this.numberOfTrials; i++){
			DungTheory sub = paf.sample();
			if(r.getModels(sub).contains(ext))
				count++;
		}
		return new Double(count)/this.numberOfTrials;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.reasoner.AbstractPafReasoner#query(net.sf.tweety.arg.prob.syntax.ProbabilisticArgumentationFramework, net.sf.tweety.arg.dung.syntax.Argument, net.sf.tweety.commons.InferenceMode)
	 */
	public Double query(ProbabilisticArgumentationFramework beliefbase, Argument formula, InferenceMode inferenceMode) {
		int count = 0;
		AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.getSemantics());
		for(int i = 0; i < this.numberOfTrials; i++){
			DungTheory sub = beliefbase.sample();
			if(r.query(sub,formula,inferenceMode))
				count++;
		}
		return new Double(count)/this.numberOfTrials;
	}	
}

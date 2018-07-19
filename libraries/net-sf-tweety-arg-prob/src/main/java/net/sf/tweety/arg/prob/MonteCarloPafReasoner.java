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
package net.sf.tweety.arg.prob;

import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.math.probability.Probability;

/**
 * This class implements the Monte Carlo algorithm for estimating
 * probabilities of extensions in probabilistic argumentation frameworks
 * from [Li, Oren, Norman. Probabilistic Argumentation Frameworks. TAFA'2011].
 * 
 * @author Matthias Thimm
 */
public class MonteCarloPafReasoner implements BeliefBaseReasoner<ProbabilisticArgumentationFramework>{

	/** Semantics for plain AAFs. */
	private Semantics semantics;
	/** The number of runs of the Monte Carlo simulation. */
	private int numberOfTrials;
	/** The inference type used for estimating acceptability probability
	 * 	of single arguments (credulous or skeptical inference).*/
	private int inferenceType;
	
	/**
	 * Creates a new reasoner.
	 * @param semantics semantics used for determining extensions.
	 * @param numberOfTrials The number of runs of the Monte Carlo simulation
	 * @param inferenceType The inference type used for estimating acceptability probability
	 * 	of single arguments (credulous or skeptical inference).
	 */
	public MonteCarloPafReasoner(Semantics semantics, int inferenceType, int numberOfTrials) {
		this.semantics = semantics;
		this.numberOfTrials = numberOfTrials;
		this.inferenceType = inferenceType;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(ProbabilisticArgumentationFramework paf, Formula query) {
		if(!(query instanceof Argument))
			throw new IllegalArgumentException("Formula of class argument expected");
		Argument arg = (Argument) query;
		int count = 0;
		AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.semantics);
		for(int i = 0; i < this.numberOfTrials; i++){
			DungTheory sub = paf.sample();
			if(r.query(sub,arg,this.inferenceType))
				count++;
		}
		Answer ans = new Answer(paf,query);
		ans.setAnswer(new Double(count)/this.numberOfTrials);		
		return ans;
	}

	/**
	 * Estimates the probability that the given set of
	 * arguments is an extension
	 * @param ext some set of arguments
	 * @return the estimated probability of the given set to be 
	 * an extension
	 */
	public Probability query(ProbabilisticArgumentationFramework paf, Extension ext){
		int count = 0;
		AbstractExtensionReasoner r = AbstractExtensionReasoner.getSimpleReasonerForSemantics(this.semantics);
		for(int i = 0; i < this.numberOfTrials; i++){
			DungTheory sub = paf.sample();
			if(r.getModels(sub).contains(ext))
				count++;
		}
		return new Probability(new Double(count)/this.numberOfTrials);
	}
	
}

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
package net.sf.tweety.arg.prob;

import net.sf.tweety.arg.dung.semantics.NumericalArgumentRanking;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.math.probability.Probability;

/**
 * Implements a graded semantics reasoner based on the ideas from [Thimm, Cerutti, Rienstra; 2018].
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticRankingReasoner implements BeliefBaseReasoner<DungTheory>{

	/**
	 * Number of trials for the used monte carlo search (this is a factor
	 * multiplied with the number of arguments of the actual framework)
	 */
	public static int NUMBER_OF_TRIALS = 10000;
		
	/**
	 * The probability used for all arguments to instantiate 
	 * a probabilistic argumentation framework
	 */
	private Probability p;
	
	/**
	 * The classical semantics used for evaluating subgraphs 
	 */
	private Semantics sem;
	
	/**
	 * The inference type (Semantics.CREDULOUS_INFERENCE or Semantics.SCEPTICAL_INFERENCE)
	 */
	private int inferenceType;
		
	/**
	 * Whether to use exact inference. 
	 */
	private boolean exactInference = false;
	
	/**
	 * Creates a new reasoner.
	 * @param sem The classical semantics used for evaluating subgraphs
	 * @param inferenceType The inference type (Semantics.CREDULOUS_INFERENCE or Semantics.SCEPTICAL_INFERENCE)
	 * @param p The probability used for all arguments to instantiate a probabilistic argumentation framework
	 * @param exactInference Whether to use exact inference. 
	 */
	public ProbabilisticRankingReasoner(Semantics sem,int inferenceType,Probability p, boolean exactInference) {
		this.sem = sem;
		this.inferenceType = inferenceType;
		this.p = p;
		this.exactInference = exactInference;
	}

	/**
	 * Computes the numerical ranking of the arguments of the given
	 * Dung theory.
	 * @param aaf an aaf.
	 * @return a numerical ranking
	 */
	public NumericalArgumentRanking getRanking(DungTheory aaf){
		// construct PAF
		ProbabilisticArgumentationFramework paf = new ProbabilisticArgumentationFramework(aaf);
		// set probabilities
		for(Argument a: aaf)
			paf.add(a, this.p);		
		// Estimate/compute probabilities
		BeliefBaseReasoner<ProbabilisticArgumentationFramework> reasoner;
		if(this.exactInference)
			reasoner = new NaivePafReasoner(this.sem, this.inferenceType);
		else
			reasoner = new MonteCarloPafReasoner(this.sem, this.inferenceType, ProbabilisticRankingReasoner.NUMBER_OF_TRIALS * paf.size());
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		for(Argument a: aaf)
			ranking.put(a, reasoner.query(paf,a).getAnswerDouble());
		return ranking;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(DungTheory aaf, Formula query) {
		throw new UnsupportedOperationException("Implement me");
	}

}

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
package net.sf.tweety.arg.prob.reasoner;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.semantics.NumericalArgumentRanking;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.prob.syntax.ProbabilisticArgumentationFramework;
import net.sf.tweety.commons.ModelProvider;
import net.sf.tweety.math.probability.Probability;

/**
 * Implements a graded semantics reasoner based on the ideas from [Thimm, Cerutti, Rienstra; 2018].
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticRankingReasoner implements ModelProvider<Argument,DungTheory,NumericalArgumentRanking>{

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
	public ProbabilisticRankingReasoner(Semantics sem,Probability p, boolean exactInference) {
		this.sem = sem;
		this.p = p;
		this.exactInference = exactInference;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModels(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public Collection<NumericalArgumentRanking> getModels(DungTheory bbase) {
		Collection<NumericalArgumentRanking> models = new HashSet<NumericalArgumentRanking>();
		models.add(this.getModel(bbase));
		return models;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.ModelProvider#getModel(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public NumericalArgumentRanking getModel(DungTheory aaf) {
		// construct PAF
		ProbabilisticArgumentationFramework paf = new ProbabilisticArgumentationFramework(aaf);
		// set probabilities
		for(Argument a: aaf)
			paf.add(a, this.p);		
		// Estimate/compute probabilities
		AbstractPafReasoner reasoner;
		if(this.exactInference)
			reasoner = new SimplePafReasoner(this.sem);
		else
			reasoner = new MonteCarloPafReasoner(this.sem, ProbabilisticRankingReasoner.NUMBER_OF_TRIALS * paf.size());
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		for(Argument a: aaf)
			ranking.put(a, reasoner.query(paf,a));
		return ranking;
	}
}

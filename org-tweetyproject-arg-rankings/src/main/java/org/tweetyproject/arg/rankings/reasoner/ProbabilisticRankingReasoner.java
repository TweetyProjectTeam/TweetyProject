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
package org.tweetyproject.arg.rankings.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner;
import org.tweetyproject.arg.prob.reasoner.MonteCarloPafReasoner;
import org.tweetyproject.arg.prob.reasoner.SimplePafReasoner;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.arg.rankings.semantics.NumericalArgumentRanking;
import org.tweetyproject.math.probability.Probability;

/**
 * Implements a graded semantics reasoner based on the ideas from 
 * [Thimm, Cerutti, Rienstra. Probabilistic Graded Semantics. COMMA 2018].
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticRankingReasoner extends AbstractRankingReasoner<NumericalArgumentRanking>{

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
	 * The classical semantics used for evaluating subgraphs.
	 */
	private Semantics sem;
			
	/**
	 * Whether to use exact inference. 
	 */
	private boolean exactInference = false;
	
	/**
	 * Creates a new ProbabilisticRankingReasoner.
	 * @param sem The classical semantics used for evaluating subgraphs
	 * @param p The probability used for all arguments to instantiate a probabilistic argumentation framework
	 * @param exactInference Whether to use exact inference. 
	 */
	public ProbabilisticRankingReasoner(Semantics sem,Probability p, boolean exactInference) {
		this.sem = sem;
		this.p = p;
		this.exactInference = exactInference;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Collection<NumericalArgumentRanking> getModels(ArgumentationFramework bbase) {
		Collection<NumericalArgumentRanking> models = new HashSet<NumericalArgumentRanking>();
		models.add(this.getModel(bbase));
		return models;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public NumericalArgumentRanking getModel(ArgumentationFramework aaf) {
		// construct PAF
		ProbabilisticArgumentationFramework paf = new ProbabilisticArgumentationFramework((DungTheory)aaf);
		// set probabilities
		for(Argument a: (DungTheory)aaf)
			paf.add(a, this.p);		
		// Estimate/compute probabilities
		AbstractPafReasoner reasoner;
		if(this.exactInference)
			reasoner = new SimplePafReasoner(this.sem);
		else
			reasoner = new MonteCarloPafReasoner(this.sem, ProbabilisticRankingReasoner.NUMBER_OF_TRIALS * paf.size());
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		ranking.sortingType = NumericalArgumentRanking.SortingType.DESCENDING;
		for(Argument a: (DungTheory)aaf)
			ranking.put(a, reasoner.query(paf,a));
		return ranking;
	}
}

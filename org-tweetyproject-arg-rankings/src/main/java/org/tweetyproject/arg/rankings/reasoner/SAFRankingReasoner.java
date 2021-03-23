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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.semantics.NumericalArgumentRanking;
import org.tweetyproject.arg.social.reasoner.IssReasoner;
import org.tweetyproject.arg.social.semantics.SimpleProductSemantics;
import org.tweetyproject.arg.social.semantics.SocialMapping;
import org.tweetyproject.arg.social.syntax.SocialAbstractArgumentationFramework;

/**
 * This class implements the social abstract argumentation approach as proposed
 * by [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of Ranking-Based 
 * Semantics for Abstract Argumentation. AAAI 2016]. It uses social abstract argumentation 
 * frameworks and the simple product semantics which were introduced by 
 * [Leite, Martins. Social abstract argumentation. IJCAI 2011].
 * 
 * @author Anna Gessler
 */
public class SAFRankingReasoner extends AbstractRankingReasoner<NumericalArgumentRanking> {
	/**
	 * Parameter for the simple vote aggregation function of the
	 * simple product semantics.
	 * @see org.tweetyproject.arg.social.semantics.SimpleProductSemantics
	 */
	private double epsilon;
	
	/**
	 * Precision of comparisons between values of the simple product semantics.
	 * @see org.tweetyproject.arg.social.semantics.SimpleProductSemantics
	 */
	private double precision; 
	
	/**
	 * The tolerance of the "Iterative Successive Substitution" algorithm
	 * used by the reasoner.
	 * @see org.tweetyproject.arg.social.reasoner.IssReasoner
	 */
	private double tolerance;
	
	/**
	 * Create a new SAFRankingReasoner with default parameters.
	 */
	public SAFRankingReasoner() {
		this.epsilon = 0.1;
		this.precision =  0.0001;
		this.tolerance = 0.0001;
	}
	
	/**
	 * Create a new SAFRankingReasoner with the given epsilon 
	 * for the SimpleProductSemantics.
	 * @param epsilon must be non-negative
	 */
	public SAFRankingReasoner(double epsilon) {
		this();
		this.epsilon = epsilon;
	}
	
	/**
	 * Create a new SAFRankingReasoner with the given epsilon 
	 * and the given tolerance for the SimpleProductSemantics.
	 * @param epsilon 
	 * @param tolerance 
	 */
	public SAFRankingReasoner(double epsilon, double tolerance) {
		this();
		this.epsilon = epsilon;
		this.tolerance = tolerance;
	}

	/**
	 * Create a new SAFRankingReasoner with the given epsilon, the given precision 
	 * and the given tolerance for the SimpleProductSemantics.
	 * @param epsilon 
	 * @param precision 
	 * @param tolerance 
	 */
	public SAFRankingReasoner(double epsilon, double precision, double tolerance) {
		this.epsilon = epsilon;
		this.precision = precision;
		this.tolerance = tolerance;
	}

	@Override
	public Collection<NumericalArgumentRanking> getModels(DungTheory bbase) {
		Collection<NumericalArgumentRanking> ranks = new HashSet<NumericalArgumentRanking>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public NumericalArgumentRanking getModel(DungTheory kb) {
		SocialAbstractArgumentationFramework saf = new SocialAbstractArgumentationFramework();
		saf.add(kb);
		for (Argument a : kb) {
			saf.voteUp(a, 1);
			saf.voteDown(a, 1); 
		}
		IssReasoner reasoner6 = new IssReasoner(new SimpleProductSemantics(this.epsilon, this.precision), this.tolerance);
		SocialMapping<Double> result = reasoner6.getModel(saf);
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		ranking.setSortingType(NumericalArgumentRanking.SortingType.DESCENDING);
		for (Argument a : kb)
			ranking.put(a, result.get(a));
		return ranking;
	}

}

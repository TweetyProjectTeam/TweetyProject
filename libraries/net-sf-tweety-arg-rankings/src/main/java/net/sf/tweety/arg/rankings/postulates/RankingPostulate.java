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
package net.sf.tweety.arg.rankings.postulates;

import java.util.Collection;

import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.rankings.reasoner.AbstractRankingReasoner;
import net.sf.tweety.commons.postulates.Postulate;
import net.sf.tweety.commons.postulates.PostulateEvaluatable;

/**
 * An abstract postulate for ranking-based semantics in abstract argumentation; 
 * the ancestor of all concrete postulates.
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 *
 */
public abstract class RankingPostulate implements Postulate<Argument>{

	/** The ABSTRACTION postulate **/
	public static final RankingPostulate ABSTRACTION = new RaAbstraction();
	/** The INDEPENDENCE postulate **/
	public static final RankingPostulate INDEPENDENCE = new RaIndependence();
	/** The VOIDPRECEDENCE postulate **/
	public static final RankingPostulate VOIDPRECEDENCE = new RaVoidPrecedence();
	/** The SELFCONTRADICTION postulate **/
	public static final RankingPostulate SELFCONTRADICTION = new RaSelfContradiction();
	/** The DEFENSEPRECEDENCE postulate **/
//	public static final RankingPostulate DEFENSEPRECEDENCE = new RaDefensePrecedence();
	/** The DISTDEFENSEPRECEDENCE postulate **/
//	public static final RankingPostulate DISTDEFENSEPRECEDENCE = new RaDistDefensePrecedence();
	/** The CARDINALITYPRECEDENCE postulate **/
	public static final RankingPostulate CARDINALITYPRECEDENCE = new RaCardinalityPrecedence();
	/** The TOTAL postulate **/
	public static final RankingPostulate TOTAL = new RaTotal();
	/** The NONATTACKEDEQUIVALENCE postulate **/
	public static final RankingPostulate NONATTACKEDEQUIVALENCE = new RaNonAttackedEquivalence();
	/** The QUALITYPRECEDENCE postulate **/
	public static final RankingPostulate QUALITYPRECEDENCE = new RaQualityPrecedence();
	/** The COUNTERTRANSITIVITY postulate **/
//	public static final RankingPostulate COUNTERTRANSITIVITY = new RaCounterTransitivity();
	/** The STRICTCOUNTERTRANSITIVITY postulate **/
//	public static final RankingPostulate STRICTCOUNTERTRANSITIVITY = new RaStrictCounterTransitivity();
	/** The ATTACKVSFULLDEFENSE postulate **/
	public static final RankingPostulate ATTACKVSFULLDEFENSE = new RaAttackVsFullDefense();
	/** The STRICTADDITIONOFDEFENSEBRANCH postulate **/
	public static final RankingPostulate STRICTADDITIONOFDEFENSEBRANCH = new RaStrictAdditionOfDefenseBranch();
	/** The ADDITIONOFDEFENSEBRANCH postulate **/
	public static final RankingPostulate ADDITIONOFDEFENSEBRANCH = new RaAdditionOfDefenseBranch();
	/** The ADDITIONOFATTACKBRANCH postulate **/
	public static final RankingPostulate ADDITIONOFATTACKBRANCH = new RaAdditionOfAttackBranch();
	/** The INCREASEOFATTACKBRANCH postulate **/
//	public static final RankingPostulate INCREASEOFATTACKBRANCH = new RaIncreaseOfAttackBranch();
	/** The INCREASEOFDEFENSEBRANCH postulate **/
//	public static final RankingPostulate INCREASEOFDEFENSEBRANCH = new RaIncreaseOfDefenseBranch();


	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#isApplicable(java.util.Collection)
	 */
	@Override
	public abstract boolean isApplicable(Collection<Argument> kb);

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#isSatisfied(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.postulates.PostulateEvaluatable)
	 */
	@Override
	public boolean isSatisfied(Collection<Argument> kb, PostulateEvaluatable<Argument> ev) {
		if(ev instanceof AbstractRankingReasoner<?>)
			return this.isSatisfied(kb, (AbstractRankingReasoner<ArgumentRanking>) ev);
		throw new RuntimeException("PostulateEvaluatable of type AbstractRankingReasoner expected.");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#isSatisfied(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.postulates.PostulateEvaluatable)
	 */
	public abstract boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev);
}

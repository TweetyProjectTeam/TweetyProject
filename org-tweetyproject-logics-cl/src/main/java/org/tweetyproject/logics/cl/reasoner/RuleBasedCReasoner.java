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
package org.tweetyproject.logics.cl.reasoner;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.tweetyproject.logics.cl.kappa.ConditionalStructureKappaBuilder;
import org.tweetyproject.logics.cl.kappa.KappaValue;
import org.tweetyproject.logics.cl.rules.EvaluateRule;
import org.tweetyproject.logics.cl.semantics.ConditionalStructure;
import org.tweetyproject.logics.cl.semantics.RankingFunction;
import org.tweetyproject.logics.cl.semantics.ConditionalStructure.Generator;
import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.syntax.Conditional;
import org.tweetyproject.logics.pl.semantics.NicePossibleWorld;

/**
 * This is a reasoner using c-representation and rules to solve these c-representations.
 * It contains a list of rules whereby the first rule is the rule with the highest priorization,
 * that means it is applied first. 
 *  
 * @author Tim Janus
 * @author Matthias Thimm
 */
public class RuleBasedCReasoner extends AbstractConditionalLogicReasoner{
	
	/** 
	 * A rule that is applicable by the {@link RuleBasedCReasoner} to reason a
	 * c-representation given a conditional belief base. Implementation of
	 * this interface can be added to the {@link RuleBasedCReasoner} to adapt
	 * its behavior, such that a {@link RuleBasedCReasoner} can use different rules
	 * if it can make different assumptions on the underlying belief base.
	 * 
	 * @author Tim Janus
	 */
	public static interface Rule {
		/**
		 * Sets the {@link ConditionalStructure} that is used as data basis
		 * for the rule.
		 * @param cs a conditional structure
		 */
		void setConditonalStructure(ConditionalStructure cs);
		
		/**
		 * Sets the Collection of {@link KappaValue} that is used as data basis
		 * for the rule 
		 * @param kappas a set of kappa values
		 */
		void setKappas(Collection<KappaValue> kappas);
		
		/** 
		 * Applies the rule
		 * @return true if a change occured, false otherwise
		 */
		boolean apply();
	}
	
	/** 
	 * a flag indicating if a human friend processing shall be used, that means
	 * everything is calculated no fast-evaluation, such that a human can better
	 * follow the algorithm.
	 */
	private boolean humanFriendly;
	
	/**
	 * A prioritized list of rules, the first rule is applied first and so on.
	 * If a progress is caused by applying the rule then the first rule is applied
	 * next again.
	 */
	private List<Rule> rules = new ArrayList<RuleBasedCReasoner.Rule>();
	
	public RuleBasedCReasoner() {
		this(true);
	}
	
	public RuleBasedCReasoner( boolean humanFriendly) {
		this.humanFriendly = humanFriendly;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.cl.reasoner.AbstractConditionalLogicReasoner#getModel(org.tweetyproject.logics.cl.syntax.ClBeliefSet)
	 */
	@Override
	public RankingFunction getModel(ClBeliefSet beliefset) {
		ConditionalStructure cs = new ConditionalStructure(beliefset);
		ConditionalStructureKappaBuilder builder = new ConditionalStructureKappaBuilder(!humanFriendly);
		HashMap<Conditional, KappaValue> kappas = new HashMap<Conditional, KappaValue>(builder.build(cs));
		
		// todo: Move rule creation somewhere else and make it more dynamic
		EvaluateRule rule = new EvaluateRule();
		rule.setConditonalStructure(cs);
		rule.setKappas(kappas.values());
		rules.add(rule);
		
		RankingFunction rfunc = new RankingFunction(beliefset.getMinimalSignature());
		for(NicePossibleWorld npw : cs.getPossibleWorlds()) {
			int weight = 0;
			for(Entry<Conditional, Generator> entry : cs.getWorldGenerators(npw).entrySet()) {
				if(entry.getValue() == Generator.CG_MINUS)
					weight += kappas.get(entry.getKey()).value();
			}
			rfunc.setRank(npw.getOptimizedWorld(), weight);
		}
		return rfunc;	
	}
	
	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
}

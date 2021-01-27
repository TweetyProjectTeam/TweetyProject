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
package org.tweetyproject.logics.pl.postulates;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * The "exchange" postulate for inconsistency measures: Exchanging consistent parts
 * of a knowledge base with equivalent ones should not change the inconsistency value.
 * 
 * @author Anna Gessler
 */
public class ImExchange extends ImPostulate{

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.EXCHANGE
	 */
	protected ImExchange() {		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		if(kb.size() < 2)
			return false;
		
		//generate random index based on the knowledge base's hash code
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		Random rand = new Random(orderedKB.hashCode()); 
		int i = rand.nextInt(orderedKB.size());
		PlFormula f0 = orderedKB.get(0);
		PlFormula f1 = orderedKB.get(i);
		
		SatReasoner reasoner = new SatReasoner();
		if (!SatSolver.getDefaultSolver().isConsistent(f0)) 
			return false;
		if (!reasoner.isEquivalent(f0,f1)) 
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.postulates.AbstractImPostulate#isSatisfied(java.util.Collection, org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PlFormula> kb, BeliefSetInconsistencyMeasure<PlFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		Random rand = new Random(orderedKB.hashCode()); 
		int i = rand.nextInt(orderedKB.size());
		PlFormula f0 = orderedKB.get(0);
		PlFormula f1 = orderedKB.get(i);
		PlBeliefSet kb1 = new PlBeliefSet(kb);
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		kb1.remove(f0);
		kb2.remove(f1);
		double inconsistency1 = ev.inconsistencyMeasure(kb1);
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		return (inconsistency1 == inconsistency2);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Exchange";
	}
}

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
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.InterpretationIterator;

/**
 * This class implements the Hitting Set inconsistency measure as proposed in [Thimm, 2014, in preparation].
 * The inconsistency value is defined as one plus the minimal number of interpretations, s.t. every formula of
 * the belief set is satisfied by at least one interpretation. This is equivalent in the cardinality of
 * a minimal partitioning of the knowledge base such that each partition is consistent.
 * 
 * @author Matthias Thimm
 *
 * @param <B> some belief base type
 * @param <S> some formula type
 */
public class HsInconsistencyMeasure<B extends BeliefBase, S extends Formula> extends BeliefSetInconsistencyMeasure<S>{

	/** Used for iterating over interpretations of the underlying language. */
	private InterpretationIterator<S,B,? extends Interpretation<B,S>> it;
	
	/** 
	 * Creates a new inconsistency measure that uses the interpretations given
	 * by the given iterator.
	 * @param it some interpretation iterator.
	 */
	public HsInconsistencyMeasure(InterpretationIterator<S,B,? extends Interpretation<B,S>> it){
		this.it = it;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		// re-initialize interpretation iterator with correct signature
		this.it = it.reset(formulas);
		// check empty set of formulas
		if(formulas.isEmpty())
			return 0d;
		// this is not very efficient but works
		for(int card = 1; card <= formulas.size(); card++){
			Collection<Interpretation<B,S>> hittingSet = this.getHittingSet(formulas, card, new HashSet<Interpretation<B,S>>());
			if(hittingSet != null){				
				return ((double)hittingSet.size()-1);
			}
		}
		// if no hitting set has been found there is a contradictory formula and we return Infinity as inconsistency value.
		return Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Determines a hitting set of the given cardinality. If no such hitting set exists null is returned.
	 * @param formulas a collection of formulas
	 * @param card some cardinality.
	 * @param interpretations in addition to the card interpretations also use this set for satisfying formulas. 
	 * @return a hitting set or null.
	 */
	private Collection<Interpretation<B,S>> getHittingSet(Collection<S> formulas, int card, Collection<Interpretation<B,S>> interpretations){
		InterpretationIterator<S,B,? extends Interpretation<B,S>> it = this.it.reset();
		Collection<Interpretation<B,S>> newInts;
		Collection<Interpretation<B,S>> cand;
		while(it.hasNext()){
			Interpretation<B,S> i = it.next();
			if(interpretations.contains(i)) continue;
			newInts = new HashSet<Interpretation<B,S>>(interpretations);
			newInts.add(i);
			if(card > 1){
				cand = this.getHittingSet(formulas, card-1, newInts);
				if(cand != null)
					return cand;
			}else{
				if(this.isHittingSet(formulas, newInts))
					return newInts;
			}
		}
		return null;
	}
			
	/**
	 * Checks whether the given candidate is a hitting set.
	 * @param formulas some set of formulas
	 * @param candidate some set of interpretation.
	 * @return "true" if the candidate is a hitting set.
	 */
	private boolean isHittingSet(Collection<S> formulas, Collection<Interpretation<B,S>> candidate){
		boolean sat;
		for(S f: formulas){
			sat = false;
			for(Interpretation<B,S> i: candidate){
				if(i.satisfies(f)){
					sat = true;
					break;
				}				
			}
			if(!sat) return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "HS";
	}
}

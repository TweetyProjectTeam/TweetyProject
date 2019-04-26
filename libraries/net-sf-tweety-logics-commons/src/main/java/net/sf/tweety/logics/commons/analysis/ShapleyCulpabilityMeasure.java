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

import java.util.*;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.*;

/**
 * This class implements the Shapley culpability measure.
 * 
 * @author Matthias Thimm
 */
public class ShapleyCulpabilityMeasure<S extends Formula, T extends BeliefSet<S,?>> implements CulpabilityMeasure<S,T> {

	/**
	 * The inconsistency measure this Shapley culpability measure bases on.
	 */
	private BeliefSetInconsistencyMeasure<S> inconsistencyMeasure;
	
	/** Stores previously computed culpability values. */
	private Map<Pair<T,S>,Double> archive;
	
	/**
	 * Creates a new Shapley culpability measure that bases on the given
	 * inconsistency measure.
	 * @param inconsistencyMeasure an inconsistency measure.
	 */
	public ShapleyCulpabilityMeasure(BeliefSetInconsistencyMeasure<S> inconsistencyMeasure){
		this.inconsistencyMeasure = inconsistencyMeasure;
		this.archive = new HashMap<Pair<T,S>,Double>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.CulpabilityMeasure#culpabilityMeasure(net.sf.tweety.BeliefSet, net.sf.tweety.Formula)
	 */
	@Override
	public Double culpabilityMeasure(T beliefSet, S formula) {
		if(this.archive.containsKey(new Pair<T,S>(beliefSet,formula)))
			return this.archive.get(new Pair<T,S>(beliefSet,formula)); 
		Set<Pair<Collection<S>,Collection<S>>> subbases = this.getSubsets(beliefSet, formula);		
		Double result = new Double(0);
		for(Pair<Collection<S>,Collection<S>> pair : subbases){
			Double v1,v2;
			v1 = this.inconsistencyMeasure.inconsistencyMeasure(pair.getFirst());
			v2 = this.inconsistencyMeasure.inconsistencyMeasure(pair.getSecond());			
			Double temp =  v1 - v2;
			temp *= MathTools.faculty(pair.getSecond().size());
			temp *= MathTools.faculty(beliefSet.size()-pair.getFirst().size());
			temp /= MathTools.faculty(beliefSet.size());
			result += temp;
		}		
		this.archive.put(new Pair<T,S>(beliefSet,formula), result); 
		return result;
	}
		
	/**
	 * Computes all pairs (k,k') of knowledge bases k,k'\subseteq kb, such that k = k' \cup {pc}.
	 * @param kb a knowledge base.
	 * @param f a formula.
	 * @return a set of pairs of knowledge bases.
	 */
	private Set<Pair<Collection<S>,Collection<S>>> getSubsets(T kb, S f){
		Set<Pair<Collection<S>,Collection<S>>> result = new HashSet<Pair<Collection<S>,Collection<S>>>();
		Set<Set<S>> subsets = new SetTools<S>().subsets(kb);
		for(Set<S> subset: subsets)
			if(!subset.contains(f)){
				Pair<Collection<S>,Collection<S>> pair = new Pair<Collection<S>,Collection<S>>();
				Collection<S> first = new HashSet<S>(subset);
				first.add(f);
				pair.setFirst(first);
				pair.setSecond(new HashSet<S>(subset));
				result.add(pair);
			}
		return result;
	}
}

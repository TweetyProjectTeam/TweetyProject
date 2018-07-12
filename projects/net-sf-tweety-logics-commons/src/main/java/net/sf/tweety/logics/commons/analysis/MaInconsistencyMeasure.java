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
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.SetTools;

/**
 * This class models the I_M inconsistency measure from e.g. [Grant,Hunter,2011a]. It takes
 * as inconsistency value the number of maximal consistent subsets plus the number of formulas
 * that are self-contradicting minus 1.
 * 
 * @author Matthias Thimm
 */
public class MaInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public MaInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Double scs = 0d;
		for(S f: formulas)
			if(!this.enumerator.isConsistent(f))
				scs++;
		// we compute the number of max consistent subsets through the minimal
		// inconsistent subsets, which is (probably) faster
		return scs + this.numMaxConsistentFormulas(this.enumerator.minimalInconsistentSubsets(formulas)) - 1;
	}

	
	/**
	 * Computes the number of maximal consistent subsets by computing minimal hitting
	 * sets from minimal inconsistent sets.  
	 * @param muses the set of minimal consistent subsets
	 * @return the number of maximal consistent subsets
	 */
	private double numMaxConsistentFormulas(Collection<Collection<S>> muses){
		SetTools<S> setTools = new SetTools<S>();
		//Convert to sets
		Set<Set<S>> mSets = new HashSet<Set<S>>();
		for(Collection<S> mus: muses)
			mSets.add(new HashSet<S>(mus));
		Set<Set<S>> hSets = setTools.permutations(mSets);
		double result = 0;
		boolean nonMin;
		//check for set minimality
		for(Set<S> h1: hSets){
			nonMin = false;
			for(Set<S> h2: hSets){
				if(h1 != h2 && h1.containsAll(h2)){
					nonMin = true;
					break;
				}
			}
			if(!nonMin){
				//System.out.println(h1);
				result++;
			}
		}		
		return result;
	}
}

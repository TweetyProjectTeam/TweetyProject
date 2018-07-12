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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.SetTools;

/**
 * Abstract implementation for MUes enumerators.
 * 
 * @author Matthias Thimm
 *
 * @param <S> the type of formulas
 */
public abstract class AbstractMusEnumerator<S extends Formula> implements MusEnumerator<S> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public abstract Collection<Collection<S>> minimalInconsistentSubsets(Collection<S> formulas);
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#minimalCorrectionSubsets(java.util.Collection)
	 */
	public Set<Set<S>> minimalCorrectionSubsets(Collection<S> formulas){
		// we use the duality of minimal inconsistent subsets, minimal correction sets, and maximal consistent
		// subsets to compute the set of maximal consistent subsets
		Collection<Collection<S>> mis = this.minimalInconsistentSubsets(formulas);
		// makes sets out of this
		Set<Set<S>> mi_sets = new HashSet<Set<S>>();
		for(Collection<S> m: mis)
			mi_sets.add(new HashSet<S>(m));
		SetTools<S> settools = new SetTools<S>();
		// get the minimal correction sets, i.e. irreducible hitting sets of minimal inconsistent subsets
		Set<Set<S>> md_sets =  settools.irreducibleHittingSets(mi_sets);
		return md_sets;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#maximalConsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<S>> maximalConsistentSubsets(Collection<S> formulas){
		Set<Set<S>> md_sets = this.minimalCorrectionSubsets(formulas);
		// every maximal consistent subset is the complement of a minimal correction set
		Set<Collection<S>> result = new HashSet<Collection<S>>();;
		Set<S> tmp;
		for(Collection<S> ms: md_sets){
			tmp = new HashSet<S>(formulas);
			tmp.removeAll(ms);
			result.add(tmp);
		}			
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(net.sf.tweety.BeliefSet)
	 */
	@Override
	public boolean isConsistent(BeliefSet<S> beliefSet){
		return this.isConsistent((Collection<S>) beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(net.sf.tweety.Formula)
	 */
	@Override
	public boolean isConsistent(S formula){
		Collection<S> c = new HashSet<S>();
		c.add(formula);
		return this.isConsistent(c);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<S> formulas){
		return this.minimalInconsistentSubsets(formulas).isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.MusEnumerator#getMiComponents(java.util.Collection)
	 */
	public Collection<Collection<S>> getMiComponents(Collection<S> formulas){
		List<Collection<S>> comp = new LinkedList<Collection<S>>(this.minimalInconsistentSubsets(formulas));
		boolean changed;
		do{
			changed = false;
			for(int i = 0; i < comp.size(); i++){
				for(int j = i+1; j< comp.size(); j++){
					if(!Collections.disjoint(comp.get(i), comp.get(j))){
						changed = true;
						comp.get(i).addAll(comp.get(j));
						comp.remove(j);
						break;
					}					
				}
				if(changed) break;
			}
		}while(changed);
		return comp;
	}
}

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
package net.sf.tweety.logics.pl.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.commons.util.Triple;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.SpecialFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * Implements the forgetting-based inconsistency measure from
 * [Besnard. Forgetting-based Inconsistency Measure. SUM 2016]
 * 
 * The implementation is a brute-force search approach without much optimization. 
 * 
 * @author Matthias Thimm
 *
 */
public class FbInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PlFormula>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> formulas) {
		PlFormula f = new Conjunction(formulas);
		Set<Triple<Proposition,Integer,SpecialFormula>> subs = new HashSet<Triple<Proposition,Integer,SpecialFormula>>();
		for(Proposition p: f.getAtoms())
			for(int i = 1; i <= f.numberOfOccurrences(p); i++){
				subs.add(new Triple<Proposition,Integer,SpecialFormula>(p,i,new Contradiction()));
				subs.add(new Triple<Proposition,Integer,SpecialFormula>(p,i,new Tautology()));
			}
		SubsetIterator<Triple<Proposition,Integer,SpecialFormula>> it = new IncreasingSubsetIterator<>(subs);
		while(it.hasNext()){
			Set<Triple<Proposition,Integer,SpecialFormula>> current = it.next();
			if(this.hasDuplicate(current))
				continue;
			int size = current.size();
			List<Triple<Proposition,Integer,SpecialFormula>> order = this.order(current);
			PlFormula r = f;
			for(Triple<Proposition,Integer,SpecialFormula> sub: order)
				r = r.replace(sub.getFirst(), sub.getThird(),sub.getSecond());
			if(SatSolver.getDefaultSolver().isConsistent(r))
				return new Double(size);	
		}
		return new Double(Integer.MAX_VALUE);		
	}	
	
	/**
	 * Checks whether the selection of substitutions is consistent (no proposition to be
	 * replaced by + and - at the same time).
	 * @param current
	 * @return
	 */
	private boolean hasDuplicate(Set<Triple<Proposition,Integer,SpecialFormula>> current){
		for(Triple<Proposition,Integer,SpecialFormula> elem1: current)
			for(Triple<Proposition,Integer,SpecialFormula> elem2: current)
				if(elem1 != elem2 && elem1.getFirst().equals(elem2.getFirst()) && elem1.getSecond().equals(elem2.getSecond()))
					return true;
		return false;		
	}
	
	/**
	 * Orders the substitutions in decreasing order.
	 * @param current
	 * @return
	 */
	private List<Triple<Proposition,Integer,SpecialFormula>> order(Set<Triple<Proposition,Integer,SpecialFormula>> current){
		// do selection sort
		if(current.size() <= 1)
			return new LinkedList<Triple<Proposition,Integer,SpecialFormula>>(current);
		int max = 0;
		Triple<Proposition,Integer,SpecialFormula> maxElem = null;
		for(Triple<Proposition,Integer,SpecialFormula> elem: current){
			if(elem.getSecond() > max){
				max = elem.getSecond();
				maxElem = elem;
			}
		}
		current.remove(maxElem);
		List<Triple<Proposition,Integer,SpecialFormula>> result = this.order(current);
		result.add(0, maxElem);		
		return result;
	}
}

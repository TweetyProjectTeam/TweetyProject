/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.analysis;

import java.util.Collection;

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * Implements the forgetting-based inconsistency measure from
 * [Besnard. Forgetting-based Inconsistency Measure. SUM 2016]
 * 
 * The implementation is a depth-first search approach without much optimization. 
 * 
 * @author Matthias Thimm
 *
 */
public class FbInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PropositionalFormula>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PropositionalFormula> formulas) {
		PropositionalFormula f = new Conjunction(formulas);
		return new Double(this.dfs_im(f, 0, Integer.MAX_VALUE));
	}	
	
	/**
	 * Searches for a sequence of substitutions of occurrences of propositions
	 * with minimal length.
	 * @param f some formula
	 * @param depth the current depth
	 * @param current_min the best value found so far
	 * @return the minimal length 
	 */
	private int dfs_im(PropositionalFormula f, int depth, int current_min){
		if(SatSolver.getDefaultSolver().isConsistent(f))
			return depth;
		if(depth+1 >= current_min)
			return current_min;
		for(Proposition p: f.getAtoms()){
			for(int i = 1; i <= f.numberOfOccurrences(p); i++){
				// test replacing it by tautology
				PropositionalFormula r = f.replace(p, new Tautology(),i);
				int depth2 = dfs_im(r,depth+1, current_min);
				if(depth2 < current_min)
					current_min = depth2;
				// test replacing it by contradiction
				r = f.replace(p, new Contradiction(),i);
				depth2 = dfs_im(r,depth+1, current_min);
				if(depth2 < current_min)
					current_min = depth2;
			}
		}
		return current_min;
	}
}

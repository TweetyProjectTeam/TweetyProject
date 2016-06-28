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
import java.util.HashSet;

import net.sf.tweety.logics.commons.analysis.AbstractMusEnumerator;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This class implements the inconsistency measure I_{P_m} proposed in 
 * [Jabbour, Raddaoui. Measuring Inconsistency Through Minimal Proofs. ECSQARU 2013].
 * The implementation here follows the characterization of I_{P_m} using Proposition 9 in the above paper.
 * There, I_{P_m} (K)= \sum_{x\in Var(K)}|Pm(x)|*|Pm(-x)| where Pm(x) is the set of all (not necessarily consistent)
 * minimal proofs of x in K that mention x.
 *  
 * @author Matthias Thimm
 *
 */
public class PmInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PropositionalFormula>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PropositionalFormula> formulas) {
		PropositionalSignature sig = new PropositionalSignature();
		for(PropositionalFormula f: formulas)
			sig.addSignature(f.getSignature());
		double result = 0;
		for(Proposition x: sig){
			result += this.getNumOfMinmalProofs(x, formulas) * this.getNumOfMinmalProofs(new Negation(x), formulas);
		}
		return result;
	}
	
	/**
	 * Returns the number of minimal proofs of f in formulas.
	 * @param f some formula (either a proposition or the negation of a proposition)
	 * @param formulas a set of formulas (the knowledge base)
	 * @return the number of minimal proofs of f in formulas.
	 */
	private int getNumOfMinmalProofs(PropositionalFormula f, Collection<PropositionalFormula> formulas){
		// we use Proposition 1 of [Jabbour, Raddaoui. Measuring Inconsistency Through Minimal Proofs. ECSQARU 2013] and
		// compute minimal inconsistent subsets of formulas\cup\{\neg f\} in order to extract minimal proofs.
		AbstractMusEnumerator<PropositionalFormula> musEnum = PlMusEnumerator.getDefaultEnumerator();
		Collection<PropositionalFormula> extKb = new HashSet<PropositionalFormula>(formulas);
		PropositionalFormula negF = new Negation(f); 
		extKb.add(negF);
		Collection<Collection<PropositionalFormula>> muses = musEnum.minimalInconsistentSubsets(extKb);
		int result = 0;
		for(Collection<PropositionalFormula> mus: muses){
			if(mus.contains(negF)) result++;
			else
				if(mus.size() == 1 && mus.iterator().next().getLiterals().contains(f)) result++;
				else
					if(mus.size() == 1 && mus.iterator().next().getLiterals().contains(negF)) result++;
		}
		return result;
	}

}

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
package net.sf.tweety.logics.pl.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.KernelProvider;
import net.sf.tweety.commons.QualitativeReasoner;
import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Abstract class for propositional logic reasoners.
 * @author Matthias Thimm
 */
public abstract class AbstractPropositionalLogicReasoner implements QualitativeReasoner<PlBeliefSet,PropositionalFormula>, KernelProvider<PropositionalFormula>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.QualitativeReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public abstract Boolean query(PlBeliefSet beliefbase, PropositionalFormula formula);

	/**
	 * Checks whether the first formula entails the second.
	 * @param formula some formula
	 * @param formula a formula
	 * @return true if the second formula is entailed be the first formula.
	 */
	public boolean query(PropositionalFormula formula, PropositionalFormula formula2) {
		PlBeliefSet s = new PlBeliefSet();
		s.add(formula);
		return this.query(s,formula2);
	}
	
	/**
	 * Checks whether the two formulas are equivalent
	 * @param p1
	 * @param p2
	 * @return
	 */
	public boolean isEquivalent(PropositionalFormula p1, PropositionalFormula p2) {
		PlBeliefSet s1 = new PlBeliefSet();
		s1.add(p1);
		PlBeliefSet s2 = new PlBeliefSet();
		s2.add(p2);
		return this.query(s1,p2) && this.query(s2, p1);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.KernelProvider#getKernels(java.util.Collection, net.sf.tweety.commons.Formula)
	 */
	public Collection<Collection<PropositionalFormula>> getKernels(Collection<PropositionalFormula> formulas, PropositionalFormula formula){
		Collection<Collection<PropositionalFormula>> kernels = new HashSet<Collection<PropositionalFormula>>();
		if(!this.query(new PlBeliefSet(formulas), formula)) return kernels;
		SubsetIterator<PropositionalFormula> it = new IncreasingSubsetIterator<PropositionalFormula>(new HashSet<PropositionalFormula>(formulas));
		boolean superSetOfKernel;
		//double i=0;
		//double pow = Math.pow(2, formulas.size());
		while(it.hasNext()){			
			Set<PropositionalFormula> candidate = it.next();
			//System.out.println(++i + " - " + (i/pow * 100) + "% - " + kernels + " - " + candidate.size());
			superSetOfKernel = false;
			for(Collection<PropositionalFormula> kernel: kernels){
				if(candidate.containsAll(kernel)){
					superSetOfKernel = true;
					break;
				}
			}			
			if(!superSetOfKernel)
				if(this.query(new PlBeliefSet(candidate), formula))
					kernels.add(candidate);
		}		
		return kernels;		
	}
}

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
package org.tweetyproject.logics.pl.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.KernelProvider;
import org.tweetyproject.commons.QualitativeReasoner;
import org.tweetyproject.commons.util.IncreasingSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Abstract class for propositional logic reasoners.
 * @author Matthias Thimm
 */
public abstract class AbstractPlReasoner implements QualitativeReasoner<PlBeliefSet,PlFormula>, KernelProvider<PlFormula>{

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public abstract Boolean query(PlBeliefSet beliefbase, PlFormula formula);

	/**
	 * Checks whether the first formula entails the second.
	 * @param formula some formula
	 * @param formula2 a formula
	 * @return true if the second formula is entailed be the first formula.
	 */
	public boolean query(PlFormula formula, PlFormula formula2) {
		PlBeliefSet s = new PlBeliefSet();
		s.add(formula);
		return this.query(s,formula2);
	}
	
	/**
	 * Checks whether the two formulas are equivalent
	 * @param p1 a formula
	 * @param p2 a formula
	 * @return true iff the two formulas are equivalent
	 */
	public boolean isEquivalent(PlFormula p1, PlFormula p2) {
		PlBeliefSet s1 = new PlBeliefSet();
		s1.add(p1);
		PlBeliefSet s2 = new PlBeliefSet();
		s2.add(p2);
		return this.query(s1,p2) && this.query(s2, p1);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.KernelProvider#getKernels(java.util.Collection, org.tweetyproject.commons.Formula)
	 */
	public Collection<Collection<PlFormula>> getKernels(Collection<PlFormula> formulas, PlFormula formula){
		Collection<Collection<PlFormula>> kernels = new HashSet<Collection<PlFormula>>();
		if(!this.query(new PlBeliefSet(formulas), formula)) return kernels;
		SubsetIterator<PlFormula> it = new IncreasingSubsetIterator<PlFormula>(new HashSet<PlFormula>(formulas));
		boolean superSetOfKernel;
		//double i=0;
		//double pow = Math.pow(2, formulas.size());
		while(it.hasNext()){			
			Set<PlFormula> candidate = it.next();
			//System.out.println(++i + " - " + (i/pow * 100) + "% - " + kernels + " - " + candidate.size());
			superSetOfKernel = false;
			for(Collection<PlFormula> kernel: kernels){
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

    /** Default Constructor */
    public AbstractPlReasoner(){}
}

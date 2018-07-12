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
package net.sf.tweety.logics.pl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.KernelProvider;
import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Naive classical inference  (checks all interpretations for satisfiability).
 * 
 * @author Matthias Thimm
 */
public class NaiveReasoner implements BeliefBaseReasoner<PlBeliefSet>,KernelProvider<PropositionalFormula> {

	/**
	 * Checks whether the first set entails the second.
	 * @param formulas some set of formulas
	 * @param formula a formula
	 * @return true if the second formula is entailed be the first set of formulas.
	 */
	public boolean entails(Collection<PropositionalFormula> formulas, PropositionalFormula formula) {
		PropositionalSignature signature = new PropositionalSignature();
		for(PropositionalFormula f: formulas)
			signature.addAll(f.getAtoms());
		signature.addAll(formula.getAtoms());
		Set<PossibleWorld> possibleWorlds = PossibleWorld.getAllPossibleWorlds(signature);
		for(PossibleWorld w: possibleWorlds)
			if(w.satisfies(formulas))
				if(!w.satisfies(formula))
					return false;
		return true;		
	}
	
	/**
	 * Checks whether the first formula entails the second.
	 * @param formula some formula
	 * @param formula a formula
	 * @return true if the second formula is entailed be the first formula.
	 */
	public boolean entails(PropositionalFormula formula, PropositionalFormula formula2) {
		Collection<PropositionalFormula> s = new HashSet<PropositionalFormula>();
		s.add(formula);
		return this.entails(s,formula2);
	}
	
	/**
	 * Checks whether the two formulas are equivalent
	 * @param p1
	 * @param p2
	 * @return
	 */
	public boolean isEquivalent(PropositionalFormula p1, PropositionalFormula p2) {
		Collection<PropositionalFormula> s1 = new HashSet<PropositionalFormula>();
		s1.add(p1);
		Collection<PropositionalFormula> s2 = new HashSet<PropositionalFormula>();
		s2.add(p2);
		return this.entails(s1,p2) && this.entails(s2, p1);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(PlBeliefSet bs, Formula query) {
		if(!(query instanceof PropositionalFormula))
			throw new IllegalArgumentException("Classical inference is only defined for propositional queries.");
		Answer answer = new Answer(bs,query);
		if(this.entails(bs, (PropositionalFormula) query)){
			answer.setAnswer(true);
			answer.appendText("The answer is: true");			
		}else{
			answer.setAnswer(false);
			answer.appendText("The answer is: false");			
			
		}
		return answer;		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.KernelProvider#getKernels(java.util.Collection, net.sf.tweety.commons.Formula)
	 */
	public Collection<Collection<PropositionalFormula>> getKernels(Collection<PropositionalFormula> formulas, PropositionalFormula formula){
		Collection<Collection<PropositionalFormula>> kernels = new HashSet<Collection<PropositionalFormula>>();
		if(!this.entails(formulas, formula)) return kernels;
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
				if(this.entails(candidate, formula))
					kernels.add(candidate);
		}		
		return kernels;		
	}

}

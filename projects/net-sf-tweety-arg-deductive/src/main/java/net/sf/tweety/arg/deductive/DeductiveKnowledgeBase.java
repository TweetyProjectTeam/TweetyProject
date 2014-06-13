/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.arg.deductive;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.pl.ClassicalEntailment;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Instances of this class represent deductive knowledge bases,
 * i.e. sets of propositional formulas.
 * 
 * @author Matthias Thimm
 */
public class DeductiveKnowledgeBase extends PlBeliefSet{

	/**
	 * Creates a new (empty) knowledge base.
	 */
	public DeductiveKnowledgeBase(){
		super();
	}
	
	/**
	 * Creates a new knowledge base with the given
	 * set of formulas.
	 * @param formulas a set of formulas.
	 */
	public DeductiveKnowledgeBase(Collection<? extends PropositionalFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Computes all deductive arguments for the given claim. 
	 * @param claim a propositional formula.
	 * @return the set of all deductive arguments for the given claim.
	 */
	public Set<DeductiveArgument> getDeductiveArguments(PropositionalFormula claim){
		Set<DeductiveArgument> arguments = new HashSet<DeductiveArgument>();
		SetTools<PropositionalFormula> setTools = new SetTools<PropositionalFormula>();
		for(int card = 1; card <= this.size(); card++){
			Set<Set<PropositionalFormula>> sets = setTools.subsets(this, card);
			for(Set<PropositionalFormula> set: sets){
				// test if we already have a subset in arguments
				boolean properSet = true;
				for(DeductiveArgument arg: arguments)
					if(set.containsAll(arg.getSupport())){
						properSet = false;
						break;
					}
				if(!properSet) continue;
				// check for consistency
				PlBeliefSet candidate = new PlBeliefSet(set);
				if(!candidate.isConsistent()) continue;
				// check for entailment
				ClassicalEntailment entailment = new ClassicalEntailment();
				if(entailment.entails(candidate, claim))
					arguments.add(new DeductiveArgument(candidate,claim));
			}
		}
		return arguments;
	}
	
	/**
	 * Computes all deductive arguments.
	 * @return the set of all deductive arguments.
	 */
	public Set<DeductiveArgument> getDeductiveArguments(){
		Set<DeductiveArgument> arguments = new HashSet<DeductiveArgument>();
		SetTools<PropositionalFormula> setTools = new SetTools<PropositionalFormula>();
		for(int card = 1; card <= this.size(); card++){
			Set<Set<PropositionalFormula>> sets = setTools.subsets(this, card);
			for(Set<PropositionalFormula> set: sets){				
				// check for consistency
				PlBeliefSet candidate = new PlBeliefSet(set);
				if(!candidate.isConsistent())
					continue;
				// test if we already have a subset in arguments with equivalent claim
				for(DeductiveArgument arg: arguments){
					ClassicalEntailment entailment = new ClassicalEntailment();
					if(entailment.entails(set, arg.getClaim()) && entailment.entails(arg.getClaim(),set))
						continue;
				}
				arguments.add(new DeductiveArgument(set, new Conjunction(set)));
			}
		}		
		return arguments;
	}
}

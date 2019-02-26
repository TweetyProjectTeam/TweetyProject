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
package net.sf.tweety.arg.deductive.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.pl.reasoner.SimpleReasoner;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

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
	public DeductiveKnowledgeBase(Collection<? extends PlFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Computes all deductive arguments for the given claim. 
	 * @param claim a propositional formula.
	 * @return the set of all deductive arguments for the given claim.
	 */
	public Set<DeductiveArgument> getDeductiveArguments(PlFormula claim){
		Set<DeductiveArgument> arguments = new HashSet<DeductiveArgument>();
		SetTools<PlFormula> setTools = new SetTools<PlFormula>();
		for(int card = 1; card <= this.size(); card++){
			Set<Set<PlFormula>> sets = setTools.subsets(this, card);
			for(Set<PlFormula> set: sets){
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
				if(!SatSolver.getDefaultSolver().isConsistent(candidate)) continue;
				// check for entailment
				SimpleReasoner reasoner = new SimpleReasoner();
				if(reasoner.query(candidate, claim))
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
		SetTools<PlFormula> setTools = new SetTools<PlFormula>();
		for(int card = 1; card <= this.size(); card++){
			Set<Set<PlFormula>> sets = setTools.subsets(this, card);
			for(Set<PlFormula> set: sets){				
				// check for consistency
				PlBeliefSet candidate = new PlBeliefSet(set);
				if(!SatSolver.getDefaultSolver().isConsistent(candidate))
					continue;
				// test if we already have a subset in arguments with equivalent claim
				for(DeductiveArgument arg: arguments){
					SimpleReasoner reasoner = new SimpleReasoner();					
					if(reasoner.isEquivalent(arg.getClaim(),new Conjunction(set)))
						continue;
				}
				arguments.add(new DeductiveArgument(set, new Conjunction(set)));
			}
		}		
		return arguments;
	}
}

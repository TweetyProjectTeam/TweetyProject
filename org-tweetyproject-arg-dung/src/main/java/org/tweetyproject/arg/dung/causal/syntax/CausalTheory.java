/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.causal.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class describes an {@link DungTheory abstract argumentation framework} that was induced by a {@link CausalKnowledgeBase}
 * 
 * @see "Argumentation-based Causal and Counterfactual Reasoning" by
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, published at 1st International Workshop on Argumentation 
 * for eXplainable AI (ArgXAI, co-located with COMMA ’22), September 12, 2022
 * 
 * @see DungTheory
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class CausalTheory extends DungTheory {

	public CausalKnowledgeBase KnowledgeBase;
	
	public CausalTheory(CausalKnowledgeBase knowledgeBase) {
		KnowledgeBase = knowledgeBase;

		for(var subSetAssumptions : new SetTools<PlFormula>().subsets(knowledgeBase.Assumptions)) {
			for(var conclusion : knowledgeBase.getConclusions(subSetAssumptions)) {
				try {
					var argument = new CausalArgument(knowledgeBase, subSetAssumptions, conclusion);
					add(argument);
				}catch(IllegalArgumentException e) {
					// not possible
					continue;
				}
			}
		}

		for(var arg1 : this.getNodes()) {
			var cArg1 = (CausalArgument) arg1;
			for(var arg2 : this.getNodes()) {
				var cArg2 = (CausalArgument) arg2;
				if(checkUndercut(cArg1, cArg2)) {
					addAttack(cArg1, cArg2);
				}
			}
		}
	}
	
	public Set<CausalArgument> getArguments(){
		var output = new HashSet<CausalArgument>();
		for(var argument : getNodes()) {
			output.add((CausalArgument) argument);
		}
		return output;
	}
	
	public boolean addAttack(CausalArgument attacker, CausalArgument attacked) {
		if(!checkUndercut((CausalArgument) attacker, (CausalArgument) attacked)) {
			throw new IllegalArgumentException("the attacking argument does not undercut the attacked argument");
		}
		
		return super.addAttack((Argument) attacker, (Argument) attacked);
	}
	
	@Override
	public boolean addAttack(Argument attacker, Argument attacked) {
		if(!(attacker instanceof CausalArgument && attacked instanceof CausalArgument)) {
			throw new IllegalArgumentException("argument is not of type CausalArgument");
		}
		
		return addAttack((CausalArgument) attacker, (CausalArgument) attacked);
	}
	
	@Override
	public boolean add(Argument argument) {
		if(argument instanceof CausalArgument) {
			if(((CausalArgument) argument).KnowledgeBase.equals(KnowledgeBase)) {
				return super.add(argument);
			}
			else {
				throw new IllegalArgumentException("argument is not defined on the same KnowledgeBase as the framework");
			}
		}else {
			throw new IllegalArgumentException("argument is not of type CausalArgument");
		}
	}
	
	private boolean checkUndercut(CausalArgument attacker, CausalArgument victim) {
		for(var premise : victim.Premises) {
			if(attacker.Conclusion.complement().equals(premise)) {
				return true;
			}
		}
		
		return false;
	}
}

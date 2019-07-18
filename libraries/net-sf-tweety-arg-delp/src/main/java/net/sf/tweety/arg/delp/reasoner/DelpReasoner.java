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
package net.sf.tweety.arg.delp.reasoner;

import net.sf.tweety.arg.delp.semantics.ComparisonCriterion;
import net.sf.tweety.arg.delp.semantics.DelpAnswer;
import net.sf.tweety.arg.delp.semantics.DelpAnswer.Type;
import net.sf.tweety.arg.delp.semantics.DialecticalTree;
import net.sf.tweety.arg.delp.semantics.EmptyCriterion;
import net.sf.tweety.arg.delp.syntax.DefeasibleLogicProgram;
import net.sf.tweety.arg.delp.syntax.DefeasibleRule;
import net.sf.tweety.arg.delp.syntax.DelpArgument;
import net.sf.tweety.arg.delp.syntax.DelpRule;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.commons.util.rules.Derivation;
import net.sf.tweety.logics.fol.syntax.FolFormula;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This reasoner performs default dialectical reasoning
 * on some given DeLP.
 *
 * @author Matthias Thimm
 *
 */
public class DelpReasoner implements Reasoner<DelpAnswer.Type,DefeasibleLogicProgram,FolFormula> {

	/**
	 * The comparison criterion is initialized with the "empty criterion"
	 */
	private ComparisonCriterion comparisonCriterion = new EmptyCriterion();

	/**
	 * Creates a new DelpReasoner for the given delp.	 * 
	 * @param comparisonCriterion a comparison criterion used for inference
	 */
	public DelpReasoner(ComparisonCriterion comparisonCriterion) {
		this.comparisonCriterion = comparisonCriterion;
	}

	/**
	 * returns the comparison criterion used in this program
	 * @return the comparison criterion used in this program
	 */
	public ComparisonCriterion getComparisonCriterion() {
		return comparisonCriterion;
	}

	/**
	 * Computes the subset of the arguments of this program, that are warrants.
	 * @param delp a delp
	 * @return a set of <code>DelpArgument</code> that are warrants
	 */
    public Set<DelpArgument> getWarrants(DefeasibleLogicProgram delp){
    	DefeasibleLogicProgram groundDelp = delp.ground();
        Set<DelpArgument> all_arguments = groundDelp.ground().getArguments();
		return all_arguments.stream()
                .filter(argument -> isWarrant(groundDelp,argument))
                .collect(Collectors.toSet());
	}

	/**
	 * Checks whether the given argument is a warrant regarding a given set of arguments
	 * @param groundDelp a grounded DeLP
	 * @param argument a DeLP argument
	 * 
	 * @return <source>true</source> iff <source>argument</source> is a warrant given <source>arguments</source>.
	 */
	private boolean isWarrant(DefeasibleLogicProgram groundDelp, DelpArgument argument){
		DialecticalTree dtree = new DialecticalTree(argument);
		Deque<DialecticalTree> stack = new ArrayDeque<>();
		stack.add(dtree);
		while(!stack.isEmpty()){
			DialecticalTree dtree2 = stack.pop();
			stack.addAll(dtree2.getDefeaters(groundDelp,comparisonCriterion));
		}
		return dtree.getMarking().equals(DialecticalTree.Mark.UNDEFEATED);
	}
	
	/**
	 * Returns all arguments with the given conclusion from the delp.
	 * @param delp some delp.
	 * @param f a formula
	 * @return all arguments with the given conclusion from the delp.
	 */
	public static Set<DelpArgument> getArgumentsWithConclusion(DefeasibleLogicProgram delp, FolFormula f){
		Collection<DelpRule> allRules = new HashSet<DelpRule>();
		allRules.addAll(delp);
		Set<Derivation<DelpRule>> allDerivations = Derivation.allDerivations(allRules, f);
		Set<DelpArgument> arguments = new HashSet<>();
		for(Derivation<DelpRule> derivation: allDerivations){
			Set<DefeasibleRule> rules = derivation.stream()
                    .filter(rule -> rule instanceof DefeasibleRule)
                    .map(rule -> (DefeasibleRule) rule)
                    .collect(Collectors.toSet());
            // consistency check: rules have to be consistent with strict knowledge part
			if(delp.isConsistent(rules))
				arguments.add(new DelpArgument(rules,(FolFormula)derivation.getConclusion()));
		}
		// subargument test
		Set<DelpArgument> result = new HashSet<>();
		for(DelpArgument argument1: arguments){
			boolean is_minimal = true;
			for(DelpArgument argument2: arguments){
				if(argument1.getConclusion().equals(argument2.getConclusion()) && argument2.isStrongSubargumentOf(argument1)){
					is_minimal = false;
					break;
				}
			}
			if(is_minimal) result.add(argument1);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Type query(DefeasibleLogicProgram delp, FolFormula f) {
		// check query:		
		if(!f.isLiteral())
			throw new IllegalArgumentException("Formula is expected to be a literal: "+f);
		if(!f.isGround())
			throw new IllegalArgumentException("Formula is expected to be ground: "+f);

		DefeasibleLogicProgram groundDelp = delp.ground();
		// get all arguments for f
		boolean warrant = false;		
		Set<DelpArgument> args = getArgumentsWithConclusion(groundDelp, f);
		for(DelpArgument arg: args){			
			DialecticalTree dtree = new DialecticalTree(arg);
			Deque<DialecticalTree> stack = new ArrayDeque<>();
			stack.add(dtree);
			while(!stack.isEmpty()){
				DialecticalTree dtree2 = stack.pop();
				stack.addAll(dtree2.getDefeaters(groundDelp,comparisonCriterion));
			}
			if(dtree.getMarking().equals(DialecticalTree.Mark.UNDEFEATED)){
				warrant = true;
				break;
			}
		}
		// get all arguments for ~f (if f is not already warranted)
		boolean comp_warrant = false;
		if(!warrant){		
			args = getArgumentsWithConclusion(groundDelp, (FolFormula) f.complement());
			for(DelpArgument arg: args){
				DialecticalTree dtree = new DialecticalTree(arg);
				Deque<DialecticalTree> stack = new ArrayDeque<>();
				stack.add(dtree);
				while(!stack.isEmpty()){
					DialecticalTree dtree2 = stack.pop();
					stack.addAll(dtree2.getDefeaters(groundDelp,comparisonCriterion));
				}
				if(dtree.getMarking().equals(DialecticalTree.Mark.UNDEFEATED)){
					comp_warrant = true;
					break;
				}
			}
		}		
		if(warrant){
			return Type.YES;
		}else if(comp_warrant){
			return Type.NO;
		}else{
			return Type.UNDECIDED;
		}		
	}	
}

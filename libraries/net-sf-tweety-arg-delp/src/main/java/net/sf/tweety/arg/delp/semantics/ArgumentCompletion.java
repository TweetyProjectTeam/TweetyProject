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
package net.sf.tweety.arg.delp.semantics;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.delp.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.commons.util.*;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class models the completion of an argument in the framework of defeasible logic programming. This class extends <source>DelpArgument</source>
 * by explicitly enumerating the strict rules used in the defeasible derivation of the argument's conclusion. In general, there may exist many
 * argument completions for one argument (if there are several alternative strict rules), but every argument completion uniquely defines the argument.
 * This class is used when computing the generalized specificity between two arguments (class <source>GeneralizedSpecificity</source>), as there, the
 * completed arguments have to be used.
 *
 * @author Matthias Thimm
 *
 */
class ArgumentCompletion extends DelpArgument{
	/**
	 * The completion of the argument, i.e., the set of strict rules used in the derivation
	 * of the conclusion of the argument.
	 */
	private Set<StrictRule> completion = new HashSet<>();

	/**
	 * Constructor; initializes this argument completion with the given DeLP argument.
	 * @param argument the argument for this completion
	 */
	private ArgumentCompletion(DelpArgument argument){
		super(argument.getSupport(),argument.getConclusion());
	}

	/**
	 * This static method computes all argument completions for the given parameter <source>argument</source> using
	 * the strict rules from the parameter <source>delp</source>
	 * @param argument a DeLP argument
	 * @param delp a defeasible logic program
	 * @return a set of argument completions for <source>argument</source>
	 */
	public static Set<ArgumentCompletion> getCompletions(DelpArgument argument, DefeasibleLogicProgram delp){
		//initialize the result set
		final Set<ArgumentCompletion> completions = new HashSet<>();

		//the completions are incrementally built on this stack.
		//every element of this stack is a pair <rules,literals> with
		//- "rules" is a list of rules (strict and defeasible) used in this argument
		//- "literals" is a stack of literals that are not yet derived by the rules in "A"
		Deque<Pair<List<DelpRule>,Deque<FolFormula>>> stack = new ArrayDeque<>();

		//the initial elements on the stack each consist of a rule with the conclusion as head
		//and all its body literals
		Set<DelpRule> initial_rules = argument.getRulesWithHead(argument.getConclusion());
		initial_rules.addAll(delp.getRulesWithHead(argument.getConclusion()));

		for (DelpRule rule : initial_rules) {
			List<DelpRule> list = new ArrayList<>();
			list.add(rule);
			Deque<FolFormula> s = rule.getPremise().stream()
                    .map(f -> (FolFormula) f)
                    .collect(Collectors.toCollection(ArrayDeque::new));
            Pair<List<DelpRule>, Deque<FolFormula>> v = new Pair<>(list, s);
			stack.push(v);
		}

		//while there are still elements to be examined
		while(!stack.isEmpty()){

			//pop the first element of the stack
			Pair<List<DelpRule>,Deque<FolFormula>> v = stack.pop();
			List<DelpRule> rules =  v.getFirst();
			Deque<FolFormula> literals = v.getSecond();

			//if the current element's stack "literals" is empty, the argument is
			//complete, because the conclusion can be derived. A new argument completion
			//is added to the result set
			if(literals.isEmpty()){
				ArgumentCompletion completion = new ArgumentCompletion(argument);
                rules.stream()
                        .filter(r -> r instanceof StrictRule)
                        .forEach(r -> completion.addStrictRule((StrictRule) r));
				completions.add(completion);
			}else{
				//pop one literal
				FolFormula lit = literals.pop();
				//if the literal is a fact, no rule is needed for that literal and
				//the element can be put back on the stack (without that literal)
				if(delp.contains(new DelpFact(lit)))
					stack.push(v);
				else{
					//look for all rules (strict and defeasible) with the head
					//being the current literal
					Set<DelpRule> sub_rules = argument.getRulesWithHead(lit);
					sub_rules.addAll(delp.getRulesWithHead(lit));
                    //for each found rule a new "partial" argument completion is put on the stack.
					//if no rule was found the current element is just dropped
                    for (DelpRule r : sub_rules) {
                        List<DelpRule> new_rules = new ArrayList<>(rules);
                        new_rules.add(r);
                        Deque<FolFormula> s = new ArrayDeque<>();
                        s.addAll(literals);
                        //add the body literals to the stack "literals"; but only
                        //if there isn't already a rule for the literal
                        for (Formula f : r.getPremise()) {
                            FolFormula e = (FolFormula) f;
                            if (!s.contains(e)) {
                                boolean add = true;
                                for (DelpRule new_rule : new_rules) {
                                    if ((new_rule).getConclusion().equals(e)) {
                                        add = false;
                                        break;
                                    }
                                }
                                if (add) s.add(e);
                            }
                        }
                        Pair<List<DelpRule>, Deque<FolFormula>> n = new Pair<>(new_rules, s);
                        stack.push(n);
                    }
				}
			}
		}
		return completions;
	}

    @Override
    public Set<DelpRule> getRulesWithHead(FolFormula l){
        return Stream.concat(
                completion.stream()
                        .filter(rule -> rule.getConclusion().equals(l)),
                super.getRulesWithHead(l).stream()
        ).collect(Collectors.toSet());
    }

	//Misc Methods

	public String toString(){
        return super.toString()+"{"
                + completion.stream().map(Object::toString).collect(Collectors.joining(","))
                + "}";
	}

	/**
	 * add a strict rule to this argument completion
	 * @param srule a strict rule
	 */
    private void addStrictRule(StrictRule srule){
		completion.add(srule);
	}

}

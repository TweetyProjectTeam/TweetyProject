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
package org.tweetyproject.arg.delp.semantics;

import java.util.*;

import org.tweetyproject.arg.delp.syntax.*;
import org.tweetyproject.commons.util.*;
import org.tweetyproject.logics.fol.syntax.*;

/**
 * This class implements the generalized specificity criterion used to compare two arguments. Using this criterion, an argument is more specific
 * (better) than another argument if the former uses more facts or less rules.
 * <br>
 * <br>See
 * <br>
 * <br>[1] Stolzenburg, F. and Garcia, A. and Chesnevar, Carlos I. and Simari, Guillermo R.. Computing Generalized Specificity.
 * In Journal of Non-Classical Logics, 2003. Volume 13(1):87-113.
 * <br>
 * <br>for more information.
 *
 * @author Matthias Thimm
 *
 */
public final class GeneralizedSpecificity extends ComparisonCriterion {

    // indicates whether an activation set is trivial or not
    private enum ActSetType {TRIVIAL, NON_TRIVIAL}

	@Override
	public Result compare(DelpArgument argument1, DelpArgument argument2, DefeasibleLogicProgram context) {
		//Compute argument completions
		Set<ArgumentCompletion> argComplete1 = ArgumentCompletion.getCompletions(argument1, context);
		Set<ArgumentCompletion> argComplete2 = ArgumentCompletion.getCompletions(argument2, context);
		//Get activation sets
		Set<Set<FolFormula>> ntActSets1 = ntActSets(argComplete1);
		Set<Set<FolFormula>> ntActSets2 = ntActSets(argComplete2);
		//test whether the activation sets of one argument activates the other
		boolean actSetTest1 = actSetTest(ntActSets1,argument2,context);
		boolean actSetTest2 = actSetTest(ntActSets2,argument1,context);
		//evaluate the activation behaviour
		if(actSetTest1 && !actSetTest2)
			return ComparisonCriterion.Result.IS_BETTER;
		if(actSetTest2 && !actSetTest1)
			return ComparisonCriterion.Result.IS_WORSE;
		return ComparisonCriterion.Result.NOT_COMPARABLE;
	}

	/**
	 * Computes the activation sets of the given argument completion.
     * See [1] for an explanation of the algorithm.
	 * @param argument an argument completion
	 * @return the set of activation sets of <source>argument</source>
	 */
	private Set<Set<FolFormula>> ntActSets(ArgumentCompletion argument){
		Set<Set<FolFormula>> ntActSets = new HashSet<>();
		Deque<Triple<Set<FolFormula>,List<FolFormula>,ActSetType>> stack = new ArrayDeque<>();
		List<FolFormula> literals = new ArrayList<>();
		literals.add(argument.getConclusion());
		Triple<Set<FolFormula>,List<FolFormula>,ActSetType> initial = new Triple<>
                (new HashSet<>(), literals, ActSetType.TRIVIAL);
		stack.push(initial);
		while(!stack.isEmpty()){
			Triple<Set<FolFormula>,List<FolFormula>,ActSetType> next = stack.pop();
			if((next.getThird() == ActSetType.NON_TRIVIAL)&&(next.getSecond().size()==0))
				ntActSets.add(next.getFirst());
			if(next.getSecond().size() > 0){
				FolFormula lit = next.getSecond().get(0);
				Triple<Set<FolFormula>,List<FolFormula>,ActSetType> v;
				literals = new ArrayList<>(next.getSecond());
				literals.remove(lit);
				Set<FolFormula> nLiterals = new HashSet<>(next.getFirst());
				nLiterals.add(lit);
				if(next.getThird() == ActSetType.NON_TRIVIAL)
					v = new Triple<>(nLiterals, literals, ActSetType.NON_TRIVIAL);
				else
					v = new Triple<>(nLiterals, literals, ActSetType.TRIVIAL);
				stack.push(v);
                for (DelpRule rule : argument.getRulesWithHead(lit)) {
                    literals = new ArrayList<>(literals);
                    nLiterals = new HashSet<>(next.getFirst());
                    for (FolFormula fol : rule.getPremise()) {
                        if (!literals.contains(fol))
                            literals.add(fol);
                    }
                    if (next.getThird() == ActSetType.NON_TRIVIAL)
                        v = new Triple<>(nLiterals, literals, ActSetType.NON_TRIVIAL);
                    else if (rule instanceof StrictRule)
                        v = new Triple<>(nLiterals, literals, ActSetType.NON_TRIVIAL);
                    else v = new Triple<>(nLiterals, literals, ActSetType.NON_TRIVIAL);
                    stack.push(v);
                }
			}
		}
		return ntActSets;
	}

	/**
	 * Computes the activation sets of all given argument completions
	 * @param argumentCompletions a set of argument completions
	 * @return the set of all activation sets for all argument completions
	 */
	private Set<Set<FolFormula>> ntActSets(Set<ArgumentCompletion> argumentCompletions){
		Set<Set<FolFormula>> ntActSets = new HashSet<>();
        for (ArgumentCompletion argumentCompletion : argumentCompletions)
            ntActSets.addAll(ntActSets(argumentCompletion));
		return ntActSets;
	}

	/**
	 * Test whether all given activation sets activate the given argument.
	 * @param ntActSets a set of activation sets
	 * @param arg a DeLP argument
	 * @param delp a defeasible logic program
	 * @return <source>true</source> iff all activation sets activate the given argument
	 */
	private boolean actSetTest(Set<Set<FolFormula>> ntActSets, DelpArgument arg, DefeasibleLogicProgram delp){
        for (Set<FolFormula> ntActSet : ntActSets)
            if (!this.isActivated(arg, ntActSet, delp))
                return false;
		return true;
	}

	/**
	 * Test whether the given argument is activated by the given activation set.
	 * @param arg a DeLP argument
	 * @param activationSet an activation set
	 * @param delp a defeasible logic program
	 * @return <source>true</source> iff the argument gets activated by the given activation set
	 */
	private boolean isActivated(DelpArgument arg, Set<FolFormula> activationSet, DefeasibleLogicProgram delp){
		return delp.getStrictClosure(activationSet, arg.getSupport(),false).contains(arg.getConclusion());
	}

    /** Default Constructor */
    public GeneralizedSpecificity(){}
}

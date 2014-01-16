package net.sf.tweety.arg.delp.semantics;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.arg.delp.*;
import net.sf.tweety.arg.delp.syntax.*;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.util.*;

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
public class GeneralizedSpecificity extends ComparisonCriterion {

	/**
	 * Indicates that an activation set is trivial
	 */
	private static final int ACTSET_TYPE_TRIVIAL = 0;
	/**
	 * Indicates that an activation set is non-trivial
	 */
	private static final int ACTSET_TYPE_NON_TRIVIAL = 1;

	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.ComparisonCriterion#compare(net.sf.tweety.argumentation.delp.DelpArgument, net.sf.tweety.argumentation.delp.DelpArgument, net.sf.tweety.argumentation.delp.DefeasibleLogicProgram)
	 */
	@Override
	public int compare(DelpArgument argument1, DelpArgument argument2, DefeasibleLogicProgram context) {
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
			return ComparisonCriterion.IS_BETTER;
		if(actSetTest2 && !actSetTest1)
			return ComparisonCriterion.IS_WORSE;
		return ComparisonCriterion.NOT_COMPARABLE;
	}

	/**
	 * Computes the activation sets of the given argument completion. See [1] for an explanation of the algorithm.
	 * @param argument an argument completion
	 * @return the set of activation sets of <source>argument</source>
	 */
	private Set<Set<FolFormula>> ntActSets(ArgumentCompletion argument){
		Set<Set<FolFormula>> ntActSets = new HashSet<Set<FolFormula>>();
		Stack<Triple<Set<FolFormula>,List<FolFormula> ,Integer>> stack = new Stack<Triple<Set<FolFormula>,List<FolFormula> ,Integer>>();
		List<FolFormula> literals = new ArrayList<FolFormula>();
		literals.add(argument.getConclusion());
		Triple<Set<FolFormula>,List<FolFormula>,Integer> initial = new Triple<Set<FolFormula>,List<FolFormula> ,Integer>
							(new HashSet<FolFormula>(),literals,new Integer(GeneralizedSpecificity.ACTSET_TYPE_TRIVIAL));
		stack.push(initial);
		while(!stack.isEmpty()){
			Triple<Set<FolFormula>,List<FolFormula>,Integer> next = stack.pop();
			if((next.getThird().intValue() == GeneralizedSpecificity.ACTSET_TYPE_NON_TRIVIAL)&& (next.getSecond().size()==0))
				ntActSets.add(next.getFirst());
			if(next.getSecond().size() > 0){
				FolFormula lit = next.getSecond().get(0);
				Triple<Set<FolFormula>,List<FolFormula>,Integer> v;
				literals = new ArrayList<FolFormula>(next.getSecond());
				literals.remove(lit);
				Set<FolFormula> nLiterals = new HashSet<FolFormula>(next.getFirst());
				nLiterals.add(lit);
				if(next.getThird().intValue() == GeneralizedSpecificity.ACTSET_TYPE_NON_TRIVIAL)
					v = new Triple<Set<FolFormula>,List<FolFormula>,Integer>(nLiterals,literals,new Integer(GeneralizedSpecificity.ACTSET_TYPE_NON_TRIVIAL));
				else
					v = new Triple<Set<FolFormula>,List<FolFormula>,Integer>(nLiterals,literals,new Integer(GeneralizedSpecificity.ACTSET_TYPE_TRIVIAL));
				stack.push(v);
				Set<DelpRule> rules = argument.getRulesWithHead(lit);
				Iterator<DelpRule> nested = rules.iterator();
				while(nested.hasNext()){
					DelpRule rule = (DelpRule)nested.next();
					literals = new ArrayList<FolFormula>(literals);
					nLiterals = new HashSet<FolFormula>(next.getFirst());
					for(Formula next_e: rule.getPremise()){
						if(!literals.contains(next_e))
							literals.add((FolFormula)next_e);
					}
					if(next.getThird().intValue() == GeneralizedSpecificity.ACTSET_TYPE_NON_TRIVIAL)
						v = new Triple<Set<FolFormula>,List<FolFormula>,Integer>(nLiterals,literals,new Integer(GeneralizedSpecificity.ACTSET_TYPE_NON_TRIVIAL));
					else if(rule instanceof StrictRule)
						v = new Triple<Set<FolFormula>,List<FolFormula>,Integer>(nLiterals,literals,new Integer(GeneralizedSpecificity.ACTSET_TYPE_TRIVIAL));
					else v = new Triple<Set<FolFormula>,List<FolFormula>,Integer>(nLiterals,literals,new Integer(GeneralizedSpecificity.ACTSET_TYPE_NON_TRIVIAL));
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
		Set<Set<FolFormula>> ntActSets = new HashSet<Set<FolFormula>>();
		Iterator<ArgumentCompletion> it = argumentCompletions.iterator();
		while(it.hasNext())
			ntActSets.addAll(ntActSets(it.next()));
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
		Iterator<Set<FolFormula>> it = ntActSets.iterator();
		while(it.hasNext()){
			if(!this.isActivated(arg,it.next(),delp))
				return false;
		}
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
}

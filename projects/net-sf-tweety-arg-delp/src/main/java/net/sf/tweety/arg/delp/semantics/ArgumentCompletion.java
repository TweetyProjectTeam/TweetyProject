package net.sf.tweety.arg.delp.semantics;

import java.util.*;

import net.sf.tweety.arg.delp.*;
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
public class ArgumentCompletion extends DelpArgument{
	/**
	 * The completion of the argument, i.e., the set of strict rules used in the derivation
	 * of the conclusion of the argument.
	 */
	Set<StrictRule> completion = new HashSet<StrictRule>();

	/**
	 * Constructor; initializes the conclusion with the given literal
	 * @param conclusion the conclusion for this argument completion
	 */
	public ArgumentCompletion(FolFormula conclusion){
		super(conclusion);
	}

	/**
	 * Constructor; initializes the conclusion and the support with the given parameters
	 * @param support the support (= set of defeasible rules) for this argument completion
	 * @param conclusion the conclusion for this argument completion
	 */
	public ArgumentCompletion(Set<DefeasibleRule> support, FolFormula conclusion){
		super(support,conclusion);
	}

	/**
	 * Constructor; initializes this argument completion with the given DeLP argument.
	 * @param argument the argument for this completion
	 */
	public ArgumentCompletion(DelpArgument argument){
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
		Set<ArgumentCompletion> completions = new HashSet<ArgumentCompletion>();

		//the completions are incrementally built on this stack.
		//every element of this stack is a pair <rules,literals> with
		//- "rules" is a list of rules (strict and defeasible) used in this argument
		//- "literals" is a stack of literals that are not yet derived by the rules in "A"
		Stack<Pair<List<DelpRule>,Stack<FolFormula>>> stack = new Stack<Pair<List<DelpRule>,Stack<FolFormula>>>();

		//the initial elements on the stack each consist of a rule with the conclusion as head
		//and all its body literals
		Set<DelpRule> initial_rules = argument.getRulesWithHead(argument.getConclusion());
		initial_rules.addAll(delp.getRulesWithHead(argument.getConclusion()));

		Iterator<DelpRule> it = initial_rules.iterator();
		while(it.hasNext()){
			DelpRule rule = it.next();
			List<DelpRule> list = new ArrayList<DelpRule>();
			list.add(rule);
			Stack<FolFormula> s = new Stack<FolFormula>();
			for(Formula f: rule.getPremise())
				s.add((FolFormula)f);
			Pair<List<DelpRule>,Stack<FolFormula>> v = new Pair<List<DelpRule>,Stack<FolFormula>>(list,s);
			stack.push(v);
		}

		//while there are still elements to be examined
		while(!stack.isEmpty()){

			//pop the first element of the stack
			Pair<List<DelpRule>,Stack<FolFormula>> v = stack.pop();
			List<DelpRule> rules =  v.getFirst();
			Stack<FolFormula> literals = v.getSecond();

			//if the current element's stack "literals" is empty, the argument is
			//complete, because the conclusion can be derived. A new argument completion
			//is added to the result set
			if(literals.isEmpty()){
				ArgumentCompletion completion = new ArgumentCompletion(argument);
				it = rules.iterator();
				while(it.hasNext()){
					DelpRule r = (DelpRule) it.next();
					if (r instanceof StrictRule)
						completion.addStrictRule((StrictRule)r);
				}
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
					it = sub_rules.iterator();
					//for each found rule a new "partial" argument completion is put on the stack.
					//if no rule was found the current element is just dropped
					while(it.hasNext()){
						DelpRule r = it.next();
						List<DelpRule> new_rules = new ArrayList<DelpRule>(rules);
						new_rules.add(r);
						Stack<FolFormula> s = new Stack<FolFormula>();
						s.addAll(literals);
						//add the body literals to the stack "literals"; but only
						//if there isn't already a rule for the literal
						for(Formula f: r.getPremise()){
							FolFormula e = (FolFormula) f;
							if(!s.contains(e)){
								boolean add = true;
								Iterator<DelpRule> it_r = new_rules.iterator();
								while(it_r.hasNext()){
									if((it_r.next()).getConclusion().equals(e)){
										add = false;
										break;
									}
								}
								if (add) s.add(e);
							}
						}
						Pair<List<DelpRule>,Stack<FolFormula>> n = new Pair<List<DelpRule>,Stack<FolFormula>>(new_rules,s);
						stack.push(n);
					}
				}
			}
		}
		return completions;
	}

	/* (non-Javadoc)
	 * @see edu.cs.ai.thimm.uacs.defeasiblelogicprogramming.DeLPArgument#getRulesWithHead(edu.cs.ai.thimm.uacs.logic.Literal)
	 */
	public Set<DelpRule> getRulesWithHead(FolFormula l){
		Set<DelpRule> rules = new HashSet<DelpRule>();
		Iterator<StrictRule> s_it = completion.iterator();
		while(s_it.hasNext()){
			StrictRule rule = s_it.next();
			if(rule.getConclusion().equals(l))
				rules.add(rule);
		}
		rules.addAll(super.getRulesWithHead(l));
		return rules;
	}

	//Misc Methods

	/* (non-Javadoc)
	 * @see edu.cs.ai.thimm.uacs.defeasiblelogicprogramming.DeLPArgument#toString()
	 */
	public String toString(){
		String s = super.toString()+"+{";
		Iterator<StrictRule> it = completion.iterator();
		if(it.hasNext())
			s += it.next();
		while(it.hasNext())
			s+= ","+it.next();
		s += "}";
		return s;
	}

	/**
	 * add a strict rule to this argument completion
	 * @param srule a strict rule
	 */
	public void addStrictRule(StrictRule srule){
		completion.add(srule);
	}

	/**
	 * returns the completion of the argument, i.e., the set of strict rules.
	 * @return the completion of the argument, i.e., the set of strict rules.
	 */
	public Set<StrictRule> getCompletion() {
		return completion;
	}

	/**
	 * sets the completion of the argument, i.e., the set of strict rules.
	 * @param completion a set of strict rules
	 */
	public void setCompletion(Set<StrictRule> completion) {
		this.completion = completion;
	}

}

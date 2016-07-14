package net.sf.tweety.arg.aspic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import net.sf.tweety.arg.aspic.semantics.AspicAttack;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.commons.util.rules.DerivationGraph;


/**
 * @author Nils Geilen
 *
 *	According to Modgil and Prakken
 *	(http://www.cs.uu.nl/groups/IS/archive/henry/ASPICtutorial.pdf)
 *	this represents an argumentation theory (AS, KB) with
 *		- AS argumentation system (e.g. inference rules)
 *		- KB knowledge base
 *  and/or a corresponding structured argumentation framework (A,C,<=) with
 *		- A set of arguments
 *		- C set of attacks
 *		- <= order on the arguments
 *	and/or a correspondong abstract argumentation framework (A, D) with
 *		- A set of arguments
 *		- D defeat relationship
 */
public class AspicArgumentationTheory implements BeliefBase {
	
	/**
	 * The inference rules this system's arguments will be created from,
	 * correesponds to an argumentation theory (AS, KB) with
	 *		- AS argumentation system = rules with premises (p -> c)
	 *		- KB knowledge base = rules without premises (-> c)
	 */
	private Collection<AspicInferenceRule> rules = new ArrayList<>();
	/**
	 * This system's arguments created from the inference rules
	 */
	private Collection<AspicArgument> args = new HashSet<>();
	/**
	 * An order over this system's arguments, needed for their defeat relation
	 */
	private Comparator<AspicArgument> order ;
	
	/**
	 * Adds an additional inference rule
	 * @param rule	the rule to be added
	 */
	public void addRule(AspicInferenceRule rule) {
		rules.add(rule);
	}
	
	/**
	 * @return	a dung theory constructed out of this system's arguments and their 
	 * 			defeat relation according to order
	 */
	public DungTheory asDungTheory(){
		DungTheory dung_theory = new DungTheory();
		dung_theory.addAll(args);
		dung_theory.addAllAttacks(AspicAttack.determineAttackRelations(args, order));
		return dung_theory;
	}
	
	/**
	 * Adds arg to this system's arguments if it is not already part of it
	 * @param arg	a argument to be added
	 * @return	an argument identical to arg
	 */
	public AspicArgument addArgument(AspicArgument arg) {
		for(AspicArgument a : args)
			if(a.equals(arg))
				return a;
		args.add(arg);
		return arg;
	}
	
	/**
	 * Expands this systems's inference rules into a tree arguments
	 */
	public void expand() {
		DerivationGraph<AspicFormula, AspicInferenceRule> rule_graph = new DerivationGraph<>();
		rule_graph.allDerivations(rules);
		//rule_graph.printTrees(System.out);
		
		for (DigraphNode<AspicInferenceRule> node : rule_graph) {
			addArgument(new AspicArgument(node, this));
		}
	}
	
	/**
	 * Sets a new order over the arguments
	 * @param order	the new order
	 */
	public void setOrder(Comparator<AspicArgument> order) {
		this.order = order;
	}

	/**
	 * @return	the order over the systems's arguments
	 */
	public Comparator<AspicArgument> getOrder() {
		return order;
	}

	/**
	 * @return	the arguments constructed from this systems's inference rules
	 */
	public Collection<AspicArgument> getArguments() {
		return args;
	}

	/**
	 * @return	the inference rules
	 */
	public Collection<AspicInferenceRule> getRules() {
		return rules;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArgumentationSystem [rules=" + rules + ",\n args=" + args + "]";
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

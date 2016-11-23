/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import net.sf.tweety.arg.aspic.ruleformulagenerator.RuleFormulaGenerator;
import net.sf.tweety.arg.aspic.semantics.AspicAttack;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.DefeasibleInferenceRule;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.syntax.StrictInferenceRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.commons.util.rules.DerivationGraph;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;


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
 *	and/or a corresponding abstract argumentation framework (A, D) with
 *		- A set of arguments
 *		- D defeat relationship
 *
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class AspicArgumentationTheory<T extends Invertable> implements BeliefBase {
	
	/**
	 * The inference rules this system's arguments will be created from,
	 * correesponds to an argumentation theory (AS, KB) with
	 *		- AS argumentation system = rules with premises (p -> c)
	 *		- KB knowledge base = rules without premises (-> c)
	 */
	private Collection<InferenceRule<T>> rules = new ArrayList<>();
	/**
	 * An order over this system's arguments, needed for their defeat relation
	 */
	private Comparator<AspicArgument<T>> order ;
	/**
	 * Used to transform ASPIC inference rules into words of the language they range over
	 */
	private RuleFormulaGenerator<T> rfgen ;
	
	/**
	 * Constructs a new ASPIC argumentation theory
	 * @param rfgen	function to map defeasible rules to labels
	 */
	public AspicArgumentationTheory( RuleFormulaGenerator<T> rfgen) {
		super();
		this.rfgen = rfgen;
	}

	/**
	 * Set a new generator to transform rules into words of the language they range over
	 * @param rfg	is the new formula generator
	 */
	public void setRuleFormulaGenerator(RuleFormulaGenerator<T> rfg) {
		rfgen = rfg;
	}
	
	/**
	 * Adds an additional inference rule
	 * @param rule	the rule to be added
	 */
	public void addRule(InferenceRule<T> rule) {
		rules.add(rule);
	}
	
	/**
	 * Adds an additional axiom, i.e. a strict rule without premise
	 * @param axiom	the axiom's conclusion
	 */
	public void addAxiom(T axiom) {
		InferenceRule<T> r = new StrictInferenceRule<>();
		r.setConclusion(axiom);
		rules.add(r);
	}
	
	/**
	 * Adds an additional ordinary, i.e. a defeasible inference rule without premise
	 * @param prem	the premise's conclusion
	 */
	public void addOrdinaryPremise(T prem) {
		InferenceRule<T> r = new DefeasibleInferenceRule<>();
		r.setConclusion(prem);
		rules.add(r);
	}
	
	/**
	 * This method transfers this Aspic+ theory into a Dung style srhumentation system
	 * @return	a dung theory constructed out of this system's arguments and their 
	 * 			defeat relation according to order
	 */
	public DungTheory asDungTheory(){
		Collection<AspicArgument<T>> args = getArguments();
		DungTheory dung_theory = new DungTheory();
		dung_theory.addAll(args);
		dung_theory.addAllAttacks(AspicAttack.determineAttackRelations(args, order, rfgen));
		return dung_theory;
	}
	
	/**
	 * Expands this systems's inference rules into a tree arguments
	 * @return	the arguments constructed from this systems's inference rules
	 */
	public Collection<AspicArgument<T>> getArguments() {
		Collection<AspicArgument<T>> args = new HashSet<>();
		DerivationGraph<T, InferenceRule<T>> rule_graph = new DerivationGraph<>();
		rule_graph.allDerivations(rules);
		
		for (DigraphNode<InferenceRule<T>> node : rule_graph) {
			args.add(new AspicArgument<T>(node, args));
		}
		return args;
	}
	

	
	/**
	 * Sets a new order over the arguments
	 * @param order	the new order
	 */
	public void setOrder(Comparator<AspicArgument<T>> order) {
		this.order = order;
	}

	/**
	 * @return	the order over the systems's arguments
	 */
	public Comparator<AspicArgument<T>> getOrder() {
		return order;
	}


	/**
	 * @return	the inference rules
	 */
	public Collection<InferenceRule<T>> getRules() {
		return rules;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArgumentationSystem [rules=" + rules + "]";
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

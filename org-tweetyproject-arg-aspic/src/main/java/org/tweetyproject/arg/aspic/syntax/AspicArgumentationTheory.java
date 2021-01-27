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
package org.tweetyproject.arg.aspic.syntax;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.aspic.ruleformulagenerator.RuleFormulaGenerator;
import org.tweetyproject.arg.aspic.semantics.AspicAttack;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.rules.RuleSet;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * @author Nils Geilen
 * @author Matthias Thimm
 *
 *         According to Modgil and Prakken
 *         (http://www.cs.uu.nl/groups/IS/archive/henry/ASPICtutorial.pdf) this
 *         represents an argumentation theory (AS, KB) with - AS argumentation
 *         system (e.g. inference rules) - KB knowledge base and/or a
 *         corresponding structured argumentation framework (A,C,&lt;=) with - A
 *         set of arguments - C set of attacks - &lt;= order on the arguments
 *         and/or a corresponding abstract argumentation framework (A, D) with -
 *         A set of arguments - D defeat relationship
 *
 * @param <T> is the type of the language that the ASPIC theory's rules range
 *            over
 */
public class AspicArgumentationTheory<T extends Invertable> extends RuleSet<InferenceRule<T>> implements BeliefBase {

	private static final long serialVersionUID = 6158985937661828210L;

	/**
	 * An order over this system's arguments, needed for their defeat relation
	 */
	private Comparator<AspicArgument<T>> order;
	/**
	 * Used to transform ASPIC inference rules into words of the language they range
	 * over
	 */
	private RuleFormulaGenerator<T> rfgen;

	/**
	 * Constructs a new ASPIC argumentation theory
	 * 
	 * @param rfgen function to map defeasible rules to labels
	 */
	public AspicArgumentationTheory(RuleFormulaGenerator<T> rfgen) {
		super();
		this.rfgen = rfgen;
	}

	/**
	 * Set a new generator to transform rules into words of the language they range
	 * over
	 * 
	 * @param rfg is the new formula generator
	 */
	public void setRuleFormulaGenerator(RuleFormulaGenerator<T> rfg) {
		rfgen = rfg;
	}

	/**
	 * Returns the generator to transform rules into words of the language they
	 * range over
	 * 
	 * @return the formula generator
	 */
	public RuleFormulaGenerator<T> getRuleFormulaGenerator() {
		return this.rfgen;
	}

	/**
	 * Adds an additional inference rule
	 * 
	 * @param rule the rule to be added
	 */
	public void addRule(InferenceRule<T> rule) {
		this.add(rule);
	}

	/**
	 * Adds an additional axiom, i.e. a strict rule without premise
	 * 
	 * @param axiom the axiom's conclusion
	 */
	public void addAxiom(T axiom) {
		InferenceRule<T> r = new StrictInferenceRule<>();
		r.setConclusion(axiom);
		this.add(r);
	}

	/**
	 * Adds an additional ordinary, i.e. a defeasible inference rule without premise
	 * 
	 * @param prem the premise's conclusion
	 */
	public void addOrdinaryPremise(T prem) {
		InferenceRule<T> r = new DefeasibleInferenceRule<>();
		r.setConclusion(prem);
		this.add(r);
	}

	/**
	 * This method transfers this Aspic+ theory into a Dung style argumentation
	 * system
	 * 
	 * @return a dung theory constructed out of this system's arguments and their
	 *         defeat relation according to order
	 */
	public DungTheory asDungTheory() {
		return this.asDungTheory(false);
	}

	/**
	 * This method transfers this Aspic+ theory into a Dung style argumentation
	 * system
	 * 
	 * @param simplifyArgumentStructure whether the argument class should be
	 *                                  simplified; if "false" then
	 *                                  <code>AspicArgument</code> is used as the
	 *                                  argument class. If "true" then arguments are
	 *                                  simplified by assigning names to each
	 *                                  argument (the structure of the arguments is
	 *                                  therefore lost).
	 * @return a dung theory constructed out of this system's arguments and their
	 *         defeat relation according to order
	 */
	public DungTheory asDungTheory(boolean simplifyArgumentStructure) {
		Collection<AspicArgument<T>> args = getArguments();
		DungTheory dung_theory = new DungTheory();
		dung_theory.addAll(args);
		dung_theory.addAllAttacks(AspicAttack.determineAttackRelations(args, order, rfgen));
		if (!simplifyArgumentStructure)
			return dung_theory;
		DungTheory dung_theory2 = new DungTheory();
		Map<Argument, Argument> old2new = new HashMap<>();
		int idx = 0;
		for (Argument a : dung_theory) {
			Argument b = new Argument("A" + idx++);
			old2new.put(a, b);
			dung_theory2.add(b);
		}
		for (Attack att : dung_theory.getAttacks()) {
			dung_theory2.add(new Attack(old2new.get(att.getAttacker()), old2new.get(att.getAttacked())));
		}
		return dung_theory2;
	}

	public Set<InferenceRule<T>> groundFolRules() {
		Signature sig = this.getMinimalSignature();
		if (!(sig instanceof FolSignature)) 
			return this;
		
		FolSignature fsig = (FolSignature) sig;
		Set<InferenceRule<T>> ground_rules = new HashSet<InferenceRule<T>>();
		for (InferenceRule<T> r : this) {
			ground_rules.addAll((Set<InferenceRule<T>>) r.allGroundInstances(fsig.getConstants()));
		}
		return ground_rules;
	}

	/**
	 * Expands this systems's inference rules into a tree arguments
	 * 
	 * @return the arguments constructed from this systems's inference rules
	 */
	public Collection<AspicArgument<T>> getArguments() {
		Collection<AspicArgument<T>> args = new HashSet<>();
		Collection<Collection<AspicArgument<T>>> subs = new HashSet<>();
		Collection<Collection<AspicArgument<T>>> new_subs = new HashSet<>();
		Collection<AspicArgument<T>> argsForPrem = new HashSet<>();
		
		Set<InferenceRule<T>> rules = this.groundFolRules();
		for (InferenceRule<T> rule : rules) {
			if (rule.isFact()) 
				args.add(new AspicArgument<T>(rule));
		}
		boolean changed;
		do {
			changed = false;
			for (InferenceRule<T> rule : rules) {
				subs.clear();
				boolean continueWithNextRule = false;
				for (T prem : rule.getPremise()) {
					argsForPrem.clear();
					for (AspicArgument<T> arg : args) {
						if (arg.getConclusion().equals(prem) && !arg.getAllConclusions().contains(rule.getConclusion()))
							argsForPrem.add(arg);
					}
					if (argsForPrem.isEmpty()) {
						continueWithNextRule = true;
						break;
					} else {
						if (subs.isEmpty()) {
							for (AspicArgument<T> subarg : argsForPrem) {
								Collection<AspicArgument<T>> subargset = new HashSet<AspicArgument<T>>();
								subargset.add(subarg);
								subs.add(subargset);
							}
						} else {
							new_subs.clear();
							for (AspicArgument<T> subarg : argsForPrem) {
								for (Collection<AspicArgument<T>> s : subs) {
									Collection<AspicArgument<T>> newS = new HashSet<>(s);
									newS.add(subarg);
									new_subs.add(newS);
								}
							}
							subs.clear();
							subs.addAll(new_subs);
						}
					}
				}
				if (continueWithNextRule)
					continue;
				for (Collection<AspicArgument<T>> subargset : subs)
					changed = args.add(new AspicArgument<T>(rule, subargset)) || changed;
			}
		} while (changed);
		return args;
	}

	/**
	 * Sets a new order over the arguments
	 * 
	 * @param order the new order
	 */
	public void setOrder(Comparator<AspicArgument<T>> order) {
		this.order = order;
	}

	/**
	 * @return the order over the systems's arguments
	 */
	public Comparator<AspicArgument<T>> getOrder() {
		return order;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArgumentationSystem [rules=" + super.toString() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.BeliefBase#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		if (this.isEmpty())
			return null;
		Signature sig = this.iterator().next().getSignature();
		for (InferenceRule<T> rule : this)
			sig.addSignature(rule.getSignature());
		return sig;
	}
}

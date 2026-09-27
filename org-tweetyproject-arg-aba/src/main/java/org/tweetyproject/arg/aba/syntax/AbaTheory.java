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
package org.tweetyproject.arg.aba.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 *
 * An implementation of Assumption Based Argumentation.
 *
 * @param <T> is the type of the language that the ABA theory's rules range over
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 */
public class AbaTheory<T extends Formula> implements BeliefBase {

	/** Default */
	public AbaTheory() {
	}


	/**
	 * The inference rules
	 */
	private Collection<InferenceRule<T>> rules = new HashSet<>();
	/**
	 * The assumptions used in this theory when no explicit set of assumptions is
	 * given
	 */
	private Collection<Assumption<T>> assumptions = new HashSet<>();

	/**
	 * The negation relation: maps each formula to its contraries
	 */
	private Map<T, Set<T>> negations = new HashMap<>();

	/**
	 * Return all deductions that can be derived from this theory
	 * @return all deductions that can be derived from this theory
	 */
	public Collection<Deduction<T>> getAllDeductions() {
		return getAllDeductions(getAssumptions());
	}

	/**
	 * Return all deductions that can be derived from this theory
	 * @param assumptions the set of assumptions used for the derivation
	 * @return all deductions that can be derived from this theory
	 */
	public Collection<Deduction<T>> getAllDeductions(Collection<Assumption<T>> assumptions) {
		Set<Deduction<T>> args = new HashSet<>();
		for (AbaRule<T> rule : getRules()) {
			if (rule.isFact()) {
				args.add(new Deduction<T>("", rule));
			}
		}
		for (Assumption<T> a : assumptions) {
			args.add(new Deduction<T>("", a));
		}
		boolean changed;
		do {
			changed = false;
			for (AbaRule<T> rule : getRules()) {
				Collection<Collection<Deduction<T>>> subs = new HashSet<>();
				boolean continueWithNextRule = false;
				for (T prem : rule.getPremise()) {
					Collection<Deduction<T>> argsForPrem = new HashSet<>();
					for (Deduction<T> arg : args)
						if (arg.getConclusion().equals(prem) && !arg.getAllConclusions().contains(rule.getConclusion()))
							argsForPrem.add(arg);
					if (argsForPrem.isEmpty()) {
						continueWithNextRule = true;
						break;
					} else {
						if (subs.isEmpty()) {
							for (Deduction<T> subarg : argsForPrem) {
								Collection<Deduction<T>> subargset = new HashSet<Deduction<T>>();
								subargset.add(subarg);
								subs.add(subargset);
							}
						} else {
							Collection<Collection<Deduction<T>>> new_subs = new HashSet<>();
							for (Deduction<T> subarg : argsForPrem) {
								for (Collection<Deduction<T>> s : subs) {
									Collection<Deduction<T>> newS = new HashSet<>(s);
									newS.add(subarg);
									new_subs.add(newS);
								}
							}
							subs = new_subs;
						}
					}
				}
				if (continueWithNextRule)
					continue;
				for (Collection<Deduction<T>> subargset : subs)
					changed = args.add(new Deduction<T>("", rule, subargset)) || changed;
			}
		} while (changed);
		return args;
	}

	/**
	 * A closure is the set of assumptions that can be derived from a set of
	 * assumptions via inference rules.
	 *
	 * @param assumptions a set of assumptions
	 * @return the closure of assumptions
	 */
	public Collection<Assumption<T>> getClosure(Collection<Assumption<T>> assumptions) {
		Set<T> derivable = getDerivable(assumptions);
		Set<Assumption<T>> cl = new HashSet<>();
		for (Assumption<T> assumption : this.getAssumptions()) {
			if (derivable.contains(assumption.getConclusion()))
				cl.add(assumption);
		}
		return cl;
	}

	/**
	 * Computes all formulas that can be derived from a set of assumptions via
	 * inference rules, by forward chaining to a fixpoint.
	 *
	 * @param assumptions a set of assumptions
	 * @return the formulas derivable from assumptions
	 */
	public Set<T> getDerivable(Collection<Assumption<T>> assumptions) {
		Set<T> derivable = new HashSet<>();
		for (Assumption<T> a : assumptions)
			derivable.add(a.getConclusion());
		Collection<InferenceRule<T>> open = new ArrayList<>(getRules());
		boolean changed;
		do {
			changed = false;
			Iterator<InferenceRule<T>> it = open.iterator();
			while (it.hasNext()) {
				InferenceRule<T> rule = it.next();
				if (derivable.containsAll(rule.getPremise())) {
					derivable.add(rule.getConclusion());
					it.remove();
					changed = true;
				}
			}
		} while (changed);
		return derivable;
	}

	/**
	 * Computes the minimal supports of all derivable formulas, i.e. the
	 * subset-minimal sets of assumptions each formula can be derived from, by a
	 * fixpoint over the rules.
	 *
	 * @return a map from each derivable formula to its minimal supports
	 */
	public Map<T, Set<Set<Assumption<T>>>> getMinimalSupports() {
		Map<T, Set<Set<Assumption<T>>>> supports = new HashMap<>();
		for (Assumption<T> a : getAssumptions())
			addMinimalSupport(supports, a.getConclusion(), Collections.singleton(a));
		boolean changed;
		do {
			changed = false;
			for (InferenceRule<T> rule : getRules())
				for (Set<Assumption<T>> s : combineSupports(supports, rule))
					changed = addMinimalSupport(supports, rule.getConclusion(), s) || changed;
		} while (changed);
		return supports;
	}

	/**
	 * All unions of one current support per premise of the rule
	 */
	private Set<Set<Assumption<T>>> combineSupports(Map<T, Set<Set<Assumption<T>>>> supports, InferenceRule<T> rule) {
		Set<Set<Assumption<T>>> result = new HashSet<>();
		result.add(new HashSet<>());
		for (T premise : rule.getPremise()) {
			Set<Set<Assumption<T>>> premiseSupports = supports.get(premise);
			if (premiseSupports == null)
				return Collections.emptySet();
			Set<Set<Assumption<T>>> next = new HashSet<>();
			for (Set<Assumption<T>> s : result)
				for (Set<Assumption<T>> p : premiseSupports) {
					Set<Assumption<T>> union = new HashSet<>(s);
					union.addAll(p);
					next.add(union);
				}
			result = next;
		}
		return result;
	}

	/**
	 * Adds s as a support of formula unless a subset already is one, and drops
	 * the supports that s is a proper subset of
	 */
	private boolean addMinimalSupport(Map<T, Set<Set<Assumption<T>>>> supports, T formula, Set<Assumption<T>> s) {
		Set<Set<Assumption<T>>> current = supports.computeIfAbsent(formula, f -> new HashSet<>());
		for (Set<Assumption<T>> c : current)
			if (s.containsAll(c))
				return false;
		Iterator<Set<Assumption<T>>> it = current.iterator();
		while (it.hasNext())
			if (it.next().containsAll(s))
				it.remove();
		current.add(s);
		return true;
	}

	/**
	 * A set of assumptions is closed iff it equals its closure.
	 *
	 * @param assumptions a set of assumptions
	 * @return true iff the set of assumptions is closed under this argumentation
	 *         theory
	 */
	public boolean isClosed(Collection<Assumption<T>> assumptions) {
		Collection<Assumption<T>> cl = getClosure(assumptions);
		return cl.size() == assumptions.size() && cl.containsAll(assumptions) && assumptions.containsAll(cl);

	}

	/**
	 * An ABA theory is flat iff all subsets of its argumentation set are closed.
	 * By monotonicity it suffices to check the sets A \ {a} for each assumption a.
	 *
	 * @return true iff the theory is flat
	 */
	public boolean isFlat() {
		Collection<Assumption<T>> all = getAssumptions();
		Set<T> formulas = new HashSet<>();
		for (Assumption<T> a : all)
			formulas.add(a.getConclusion());
		boolean assumptionIsHead = false;
		for (InferenceRule<T> r : getRules())
			if (formulas.contains(r.getConclusion())) {
				assumptionIsHead = true;
				break;
			}
		if (!assumptionIsHead)
			return true;
		for (Assumption<T> a : all) {
			Collection<Assumption<T>> rest = new HashSet<>(all);
			rest.remove(a);
			if (!isClosed(rest))
				return false;
		}
		return true;
	}

	/**
	 * Add to theory
	 * @param rule an assumption or an inference rule or a negation that is added to
	 *             the theory
	 */
	@SuppressWarnings("unchecked")
	public void add(Formula rule) {
		if (rule instanceof Assumption)
			assumptions.add((Assumption<T>) rule);
		else if (rule instanceof InferenceRule)
			rules.add((InferenceRule<T>) rule);
		else if (rule instanceof Negation) {
			Negation<T> n = (Negation<T>) rule;
			addNegation(n.formula, n.negation);
		}
	}

	/** Add to theory
	 * @param rules assumptions or inference rules or negations that are added to the theory
	 */
	public void add(Formula... rules) {
		for (Formula f : rules)
			add(f);
	}

	/**
	 * Add assumption to theory
	 * @param assumption a formula that is used as an assumption in the theory
	 */
	public void addAssumption(T assumption) {
		assumptions.add(new Assumption<>(assumption));
	}

	/**
	 * Adds a negation of form not formula = negation
	 *
	 * @param formula  a formula
	 * @param negation it's complement
	 */
	public void addNegation(T formula, T negation) {
		negations.computeIfAbsent(formula, k -> new HashSet<>()).add(negation);
	}

	/**
	 * Checks whether the given two formulas are negations of each other
	 *
	 * @param formula  a formula
	 * @param negation a formula
	 * @return true iff the two formulas are negations of each other
	 */
	public boolean negates(T negation, T formula) {
		return getContraries(formula).contains(negation);
	}

	/**
	 * Returns the contraries of a formula, i.e. all c with "not formula = c".
	 *
	 * @param formula a formula
	 * @return the contraries of formula; empty if it has none
	 */
	public Set<T> getContraries(T formula) {
		return Collections.unmodifiableSet(negations.getOrDefault(formula, Collections.emptySet()));
	}

	/**
	 * Check if attacker attacks attacked
	 * @param atter the attacking deduction
	 * @param atted the attacked assumption
	 * @return true iff atter attacks atted
	 */
	public boolean attacks(Deduction<T> atter, T atted) {
		return negates(atter.getConclusion(), atted);
	}

	/**
	 *
	 * Return the rules
	 * @return the rules
	 */
	public Collection<InferenceRule<T>> getRules() {
		return rules;
	}

	/**
	 * Return the assumptions
	 * @return the assumptions
	 */
	public Collection<Assumption<T>> getAssumptions() {
		return assumptions;
	}

	/**
	 * Return the negations
	 * @return the negations
	 */
	public Collection<Negation<T>> getNegations() {
		Set<Negation<T>> result = new HashSet<>();
		for (Map.Entry<T, Set<T>> e : negations.entrySet())
			for (T c : e.getValue())
				result.add(new Negation<>(e.getKey(), c));
		return result;
	}

	/**
	 * Returns the ground theory: every rule, assumption and negation is replaced by
	 * its ground instances over the constants of the minimal signature. The
	 * reasoning methods assume a ground theory. Theories that are not first-order
	 * are returned unchanged.
	 *
	 * @return the ground theory
	 */
	@SuppressWarnings("unchecked")
	public AbaTheory<T> ground() {
		Signature sig = this.getMinimalSignature();
		if (!(sig instanceof FolSignature))
			return this;
		Set<Constant> constants = ((FolSignature) sig).getConstants();
		AbaTheory<T> result = new AbaTheory<>();
		for (InferenceRule<T> r : rules)
			result.rules.addAll((Set<InferenceRule<T>>) r.allGroundInstances(constants));
		for (Assumption<T> a : assumptions)
			result.assumptions.addAll((Set<Assumption<T>>) a.allGroundInstances(constants));
		for (Negation<T> n : getNegations())
			for (Negation<T> g : (Set<Negation<T>>) n.allGroundInstances(constants))
				result.add(g);
		return result;
	}

	/**
	 * Set Assumption
	 * @param assumptions the assumptions to set
	 */
	public void setAssumptions(Collection<Assumption<T>> assumptions) {
		this.assumptions = assumptions;
	}

	/**
	 * Checks whether a set of assumptions attacks another set of assumptions.
	 *
	 * @param attackers set of assumptions
	 * @param attackeds set of assumptions
	 * @return true iff the first set of assumptions attacks the second set
	 */
	public boolean attacks(Collection<Assumption<T>> attackers, Collection<Assumption<T>> attackeds) {
		Set<T> derivable = getDerivable(attackers);
		for (Assumption<T> a : attackeds) {
			for (T c : getContraries(a.getConclusion()))
				if (derivable.contains(c))
					return true;
		}
		return false;
	}

	/**
	 * Checks whether a set of arguments is conflict-free.
	 *
	 * @param ext a set of arguments
	 * @return true iff ext is conflict-free
	 */
	public boolean isConflictFree(Collection<Assumption<T>> ext) {
		return !attacks(ext, ext);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.commons.BeliefBase#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		Signature sig;
		if (!rules.isEmpty())
			sig = rules.iterator().next().getSignature();
		else if (!assumptions.isEmpty())
			sig = assumptions.iterator().next().getSignature();
		else if (!negations.isEmpty())
			sig = getNegations().iterator().next().getSignature();
		else {
			return null;
		}
		for (InferenceRule<T> r : rules)
			sig.addSignature(r.getSignature());
		for (Assumption<T> a : assumptions)
			sig.addSignature(a.getSignature());
		for (Negation<T> n : getNegations())
			sig.addSignature(n.getSignature());
		return sig;
	}

	/**
	 * Return a Dung Theory derived from this ABA theory
	 * @return a Dung Theory derived from this ABA theory
	 */
	public DungTheory asDungTheory() {
		if (!isFlat())
			throw new RuntimeException("Only flat ABA theories can be transformed into Dung theories.");
		Collection<Deduction<T>> deductions = getAllDeductions();
		int id = 0;
		DungTheory dt = new DungTheory();
		Map<Deduction<T>, Argument> argmap = new HashMap<>();
		for (Deduction<T> d : deductions) {
			Argument arg = d.getRule() instanceof Assumption<?> ? new Argument(d.getConclusion().toString())
					: new Argument("arg_" + id++);
			dt.add(arg);
			argmap.put(d, arg);
		}
		for (Deduction<T> attacker : deductions)
			for (Deduction<T> attacked : deductions)
				for (T ass : attacked.getAssumptions())
					if (attacks(attacker, ass)) {
						dt.add(new Attack(argmap.get(attacker), argmap.get(attacked)));
						break;
					}
		return dt;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ABATheory [rules=" + rules + ", assumptions=" + assumptions + ", negations=" + getNegations() + "]";
	}

}

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
package net.sf.tweety.arg.aba.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.aba.semantics.AbaExtension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * 
 * An implementation of Assumption Based Argumentation.
 *
 * @param <T> is the type of the language that the ABA theory's rules range over
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 */
public class AbaTheory<T extends Formula> implements BeliefBase {

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
	 * The negation relation
	 */
	private Collection<Negation<T>> negations = new HashSet<>();

	/**
	 * @return all deductions that can be derived from this theory
	 */
	public Collection<Deduction<T>> getAllDeductions() {
		return getAllDeductions(assumptions);
	}

	/**
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
		Collection<Deduction<T>> deductions = getAllDeductions(assumptions);
		Set<Assumption<T>> cl = new HashSet<>();
		for (Assumption<T> assumption : this.getAssumptions()) {
			for (Deduction<T> deduction : deductions) {
				if (assumption.getConclusion().equals(deduction.getConclusion())) {
					cl.add(assumption);
				}
			}
		}
		return cl;

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
	 * An ABA theory is flat iff all subsets of its argumentation set are closed
	 * 
	 * @return true iff the theory is flat
	 */
	public boolean isFlat() {
		Collection<Collection<Assumption<T>>> powerset = toPowerSet(getAssumptions());
		for (Collection<Assumption<T>> asss : powerset) {
			if (!isClosed(asss))
				return false;
		}
		return true;
	}

	/**
	 * Computes the power set of a collection
	 * 
	 * @param set a collection
	 * @return a power set
	 */
	private static <S> Collection<Collection<S>> toPowerSet(Collection<S> set) {
		Collection<Collection<S>> powerset = new HashSet<>();
		powerset.add(set);
		for (int i = 0; i < set.size(); i++) {
			List<S> list = new ArrayList<>(set);
			list.remove(i);
			powerset.addAll(toPowerSet(list));
		}
		return powerset;
	}

	/**
	 * @param rule an assumption or an inference rule or a negation that is added to
	 *             the theory
	 */
	@SuppressWarnings("unchecked")
	public void add(Formula rule) {
		if (rule instanceof Assumption)
			assumptions.add((Assumption<T>) rule);
		else if (rule instanceof InferenceRule)
			rules.add((InferenceRule<T>) rule);
		else if (rule instanceof Negation)
			negations.add((Negation<T>) rule);
	}

	/**
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
		negations.add(new Negation<>(formula, negation));
	}

	/**
	 * Checks whether the given two formulas are negations of each other
	 * 
	 * @param formula  a formula
	 * @param negation a formula
	 * @return true iff the two formulas are negations of each other
	 */
	public boolean negates(T negation, T formula) {
		return getNegations().contains(new Negation<>(formula, negation));
	}

	/**
	 * @param atter the attacking deduction
	 * @param atted the attacked assumption
	 * @return true iff atter attacks atted
	 */
	public boolean attacks(Deduction<T> atter, T atted) {
		return negates(atter.getConclusion(), atted);
	}

	/**
	 * @return the rules
	 */
	public Collection<InferenceRule<T>> getRules() {
		return this.groundFolRules(rules);
	}

	/**
	 * @return the assumptions
	 */
	public Collection<Assumption<T>> getAssumptions() {
		return this.groundFolAssumptions(assumptions);
	}

	/**
	 * @return the negations
	 */
	public Collection<Negation<T>> getNegations() {
		return this.groundFolNegations(negations);
	}

	@SuppressWarnings("unchecked")
	private Collection<InferenceRule<T>> groundFolRules(Collection<InferenceRule<T>> rules) {
		Signature sig = this.getMinimalSignature();
		if (!(sig instanceof FolSignature))
			return rules;

		FolSignature fsig = (FolSignature) sig;
		Set<InferenceRule<T>> ground_rules = new HashSet<InferenceRule<T>>();
		for (InferenceRule<T> r : rules) {
			ground_rules.addAll((Set<InferenceRule<T>>) r.allGroundInstances(fsig.getConstants()));
		}
		return ground_rules;
	}

	@SuppressWarnings("unchecked")
	private Collection<Assumption<T>> groundFolAssumptions(Collection<Assumption<T>> assumptions) {
		Signature sig = this.getMinimalSignature();
		if (!(sig instanceof FolSignature))
			return assumptions;

		FolSignature fsig = (FolSignature) sig;
		Set<Assumption<T>> ground_assumptions = new HashSet<Assumption<T>>();
		for (Assumption<T> a : assumptions) {
			ground_assumptions.addAll((Collection<? extends Assumption<T>>) a.allGroundInstances(fsig.getConstants()));
		}
		return ground_assumptions;

	}

	@SuppressWarnings("unchecked")
	private Collection<Negation<T>> groundFolNegations(Collection<Negation<T>> negations) {
		Signature sig = this.getMinimalSignature();
		if (!(sig instanceof FolSignature))
			return negations;
		FolSignature fsig = (FolSignature) sig;
		Set<Negation<T>> ground_negations = new HashSet<Negation<T>>();
		for (Negation<T> n : negations) {
			ground_negations.addAll((Collection<? extends Negation<T>>) n.allGroundInstances(fsig.getConstants()));
		}
		return ground_negations;
	}

	/**
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
	 * @return
	 */
	public boolean attacks(Collection<Assumption<T>> attackers, Collection<Assumption<T>> attackeds) {
		for (Deduction<T> d : getAllDeductions(attackers)) {
			for (Assumption<T> a : attackeds) {
				if (negates(d.getConclusion(), a.getConclusion()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a set of arguments defends an argument.
	 * 
	 * @param defendor the defending set
	 * @param defended the assumption which shall be defended
	 * @return true iff defendor defends defended
	 */
	public boolean defends(Collection<Assumption<T>> defendor, Assumption<T> defended) {
		Collection<Assumption<T>> defedl = Arrays.asList(defended);
		for (Collection<Assumption<T>> ext : getAllExtensions()) {
			if (isClosed(ext) && attacks(ext, defedl) && !attacks(defendor, ext))
				return false;
		}
		return true;
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

	/**
	 * Computes all possible extensions.
	 * 
	 * @return the powerset of the assumptions
	 */
	public Collection<Collection<Assumption<T>>> getAllExtensions() {
		return toPowerSet(getAssumptions());
	}

	/**
	 * Computes all context-free extensions.
	 * 
	 * @return all context-free extensions
	 */
	public Collection<Collection<Assumption<T>>> getAllConflictFreeExtensions() {
		Collection<Collection<Assumption<T>>> result = new HashSet<>();
		for (Collection<Assumption<T>> ext : toPowerSet(getAssumptions())) {
			if (isConflictFree(ext))
				result.add(ext);
		}
		return result;
	}

	/**
	 * Checks whether a set of arguments is admissible.
	 * 
	 * @param ext the set
	 * @return true iff ext is admissible
	 */
	public boolean isAdmissible(AbaExtension<T> ext) {
		if (!isConflictFree(ext))
			return false;
		if (!isClosed(ext))
			return false;
		for (Collection<Assumption<T>> as : toPowerSet(getAssumptions())) {
			if (isClosed(as) && attacks(as, ext) && !attacks(ext, as))
				return false;
		}
		return true;
	}

	/**
	 * Computes all admissible extensions.
	 * 
	 * @return all admissible extensions
	 */
	public Collection<AbaExtension<T>> getAllAdmissbleExtensions() {
		Collection<AbaExtension<T>> result = new HashSet<>();
		for (Collection<Assumption<T>> ext : toPowerSet(getAssumptions())) {
			AbaExtension<T> ext2 = new AbaExtension<T>(ext);
			if (isAdmissible(ext2))
				result.add(ext2);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.BeliefBase#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		Signature sig;
		if (!rules.isEmpty())
			sig = rules.iterator().next().getSignature();
		else if (!assumptions.isEmpty())
			sig = assumptions.iterator().next().getSignature();
		else if (!negations.isEmpty())
			sig = negations.iterator().next().getSignature();
		else {
			return null;
		}
		for (InferenceRule<T> r : rules)
			sig.addSignature(r.getSignature());
		for (Assumption<T> a : assumptions)
			sig.addSignature(a.getSignature());
		for (Negation<T> n : negations)
			sig.addSignature(n.getSignature());
		return sig;
	}

	/**
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
		return "ABATheory [rules=" + rules + ", assumptions=" + assumptions + ", negations=" + negations + "]";
	}

}

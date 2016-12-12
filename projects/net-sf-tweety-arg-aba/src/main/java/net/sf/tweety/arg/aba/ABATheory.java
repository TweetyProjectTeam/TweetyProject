package net.sf.tweety.arg.aba;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.aba.semantics.ABAAttack;
import net.sf.tweety.arg.aba.syntax.ABARule;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.Deduction;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.commons.util.rules.DerivationGraph;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *
 * @param <T>
 *            is the type of the language that the ABA theory's rules range over
 */
public class ABATheory<T extends Invertable> implements BeliefBase {

	/**
	 * The inference rules
	 */
	private Collection<InferenceRule<T>> rules = new HashSet<>();
	/**
	 * The assumptions used in this theory wehen no explicit set of assumptions
	 * is given
	 */
	private Collection<Assumption<T>> assumptions = new HashSet<>();

	/**
	 * @return all deductions that can be derived from this theory
	 */
	public Collection<Deduction<T>> getAllDeductions() {
		return getAllDeductions(assumptions);
	}

	/**
	 * @param assumptions
	 *            the set of assumptions used for the derivation
	 * @return all deductions that can be derived from this theory
	 */
	public Collection<Deduction<T>> getAllDeductions(Collection<Assumption<T>> assumptions) {
		Set<Deduction<T>> result = new HashSet<>();
		DerivationGraph<T, ABARule<T>> graph = new DerivationGraph<>();
		Collection<ABARule<T>> allrules = new HashSet<>();
		allrules.addAll(rules);
		allrules.addAll(assumptions);
		graph.allDerivations(allrules);
		for (DigraphNode<ABARule<T>> leaf : graph.getLeafs())
			createDeduction(leaf, result);
		return result;
	}

	/**
	 * Recursively creates a deduction and all of its subdeductions
	 * 
	 * @param node
	 * @param set
	 * @return the created deduction
	 */
	private Deduction<T> createDeduction(DigraphNode<ABARule<T>> node, Set<Deduction<T>> set) {
		Deduction<T> result = new Deduction<>("");
		result.setRule(node.getValue());
		for (DigraphNode<ABARule<T>> parent : node.getParents()) {
			result.addSubDeduction(createDeduction(parent, set));
		}
		set.add(result);
		return result;
	}

	/**
	 * A closure is the set of assumptions that can be derived from a set of
	 * assumptions via inference rules
	 * 
	 * @param assumptions
	 *            a set of assumptions
	 * @return the closure of assumptions
	 */
	public Collection<Assumption<T>> getClosure(Collection<Assumption<T>> assumptions) {
		Collection<Deduction<T>> deductions = getAllDeductions(assumptions);
		Set<Assumption<T>> cl = new HashSet<>();
		for (Assumption<T> assumption : this.assumptions) {
			for (Deduction<T> deduction : deductions) {
				if (assumption.getConclusion().equals(deduction.getConclusion())) {
					cl.add(assumption);
				}
			}
		}
		return cl;
	}

	/**
	 * A set of assumptions is closed iff it equals its closure
	 * 
	 * @param assumptions
	 *            a set of assumptions
	 * @return true iff the set of assumptions is closed under this
	 *         argumentation theory
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
		Collection<Collection<Assumption<T>>> powerset = toPowerSet(assumptions);
		for (Collection<Assumption<T>> asss : powerset) {
			if (!isClosed(asss))
				return false;
		}
		return true;
	}

	/**
	 * Computes the power set of a collection
	 * 
	 * @param set
	 *            a collection
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
	 * @param rule
	 *            an assumption or an inference rule that is added to the theory
	 */
	public void add(ABARule<T> rule) {
		if (rule instanceof Assumption)
			assumptions.add((Assumption<T>) rule);
		else if (rule instanceof InferenceRule)
			rules.add((InferenceRule<T>) rule);
	}

	/**
	 * @param assumption
	 *            a formula that is used as an assumption in the theory
	 */
	public void addAssumption(T assumption) {
		assumptions.add(new Assumption<>(assumption));
	}

	/**
	 * @return the rules
	 */
	public Collection<InferenceRule<T>> getRules() {
		return rules;
	}

	/**
	 * @return the assumptions
	 */
	public Collection<Assumption<T>> getAssumptions() {
		return assumptions;
	}

	/**
	 * @param assumptions
	 *            the assumptions to set
	 */
	public void setAssumptions(Collection<Assumption<T>> assumptions) {
		this.assumptions = assumptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return a Dung Theory derived from this ABA theory
	 */
	public DungTheory asDungTheory() {
		if (!isFlat())
			throw new RuntimeException("Only flat ABA theories can be transformed into Dung theories.");
		Collection<Deduction<T>> ds = getAllDeductions();
		int id = 0;
		DungTheory dt = new DungTheory();
		Map<Deduction<T>, Argument> argmap = new HashMap<>();
		for (Deduction<T> d : ds) {
			Argument arg = d.getRule() instanceof Assumption<?> ? new Argument(d.getConclusion().toString())
					: new Argument("arg_" + id++);
			dt.add(arg);
			argmap.put(d, arg);
		}
		for (Deduction<T> atter : ds)
			for (Deduction<T> atted : ds)
				for (T ass : atted.getAssumptions())
					if (ABAAttack.attacks(atter, new Assumption<>(ass))) {
						dt.add(new Attack(argmap.get(atter), argmap.get(atted)));
						break;
					}

		return dt;
	}

}

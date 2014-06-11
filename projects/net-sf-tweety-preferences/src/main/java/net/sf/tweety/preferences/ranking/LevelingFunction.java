package net.sf.tweety.preferences.ranking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.opt.solver.*;
import net.sf.tweety.math.term.IntegerVariable;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;
import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.Relation;
import net.sf.tweety.util.Triple;

/**
 * This class is meant to provide leveling functions to given preference orders
 * and vice versa.
 * 
 * TODO exception handling for invalid preference orders (total preorder)
 * 
 * @author Bastian Wolf
 * @param <T>
 *            the generic type used for this leveling function
 * 
 */

public class LevelingFunction<T> extends Functions<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * constructs a new, empty leveling function caller can use Map-method
	 * putAll to fill this empty leveling function
	 */
	public LevelingFunction() {
		super();
	}

	/**
	 * this constructor creates a leveling function using a given preference
	 * order
	 * 
	 * @param po
	 *            the given preference order
	 */
	public LevelingFunction(PreferenceOrder<T> po) {

		// empty hash map for the integer variables
		Map<T, IntegerVariable> intVar = new HashMap<T, IntegerVariable>();

		// new optimization problem
		// Set<Triple<IntegerVariable, IntegerVariable, Relation>> optIneq = new
		// HashSet<Triple<IntegerVariable, IntegerVariable, Relation>>();
		OptimizationProblem opt = new OptimizationProblem(
				OptimizationProblem.MINIMIZE);

		// creates a new integer variable for each domain element of the given
		// preference order
		for (final T e : po.getDomainElements()) {
			intVar.put(e, new IntegerVariable(e.toString(), true));
		}

		// iterator over the preference orders relations
		Iterator<Triple<T, T, Relation>> it = po.iterator();

		while (it.hasNext()) {
			Triple<T, T, Relation> tempRel = it.next();

			// empty initialized IntegerVariables for ech relation
			IntegerVariable tempVarF = null;
			IntegerVariable tempVarS = null;

			// mapping the relations between each two elements into the integer
			// variables hash map
			if (po.contains(tempRel)) {
				tempVarF = intVar.get(tempRel.getFirst());
				tempVarS = intVar.get(tempRel.getSecond());
				// switch (tempRel.getThird()) {
				if (tempRel.getThird().equals(Relation.LESS)) {
					// case Relation.LESS:
					opt.add(new Inequation(tempVarF, tempVarS, Inequation.LESS));
				} else if (tempRel.getThird().equals(Relation.LESS_EQUAL)) {
					// case LESS_EQUAL:
					opt.add(new Inequation(tempVarF, tempVarS,
							Inequation.LESS_EQUAL));
					// default:
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		// try{

		// setting the term for the solver
		List<Term> terms = new LinkedList<Term>();

		for (Entry<T, IntegerVariable> e : intVar.entrySet()) {
			Term t = e.getValue();
			terms.add(t);
		}

		Iterator<Term> termIt = terms.listIterator();

		// using this term for the target function
		if (termIt.hasNext()) {
			Term t = termIt.next();
			while (termIt.hasNext()) {
				t = t.add(termIt.next());
			}
			opt.setTargetFunction(t);
		}

		LpSolve solver = new LpSolve();
		Map<Variable, Term> solution = solver.solve(opt);
		Map<T, Integer> sol = new HashMap<T, Integer>();
		for (Entry<Variable, Term> e : solution.entrySet()) {
			//TODO: check the following cast
			@SuppressWarnings("unchecked")
			T key = (T) e.getKey().toString();
			Integer val = (int) e.getValue().doubleValue();
			sol.put(key, val);
		}
		this.clear();
		this.putAll(sol);

		// } catch (StringIndexOutOfBoundsException e){
		// System.err.println("You're input preference order seems to be invalid, please check it.");
		// }

	}

	/**
	 * returns this leveling function
	 * 
	 * @return this leveling function
	 */
	public Map<T, Integer> getLevelingFunction() {
		return this;
	}

	/**
	 * returns a new RankingFunction based on this LevelingFunction
	 */
	public RankingFunction<T> getRankingFunction() {
		return new RankingFunction<T>(this);
	}

	/**
	 * this method returns a preference order made out of this leveling function
	 * 
	 * @return a preference order out of a given leveling function
	 */
	public PreferenceOrder<T> generatePreferenceOrder() {
		Set<Triple<T, T, Relation>> tempPO = new HashSet<Triple<T, T, Relation>>();

		Map<T, Integer> in = this;

		for (Entry<T, Integer> f : in.entrySet()) {
			for (Entry<T, Integer> s : in.entrySet()) {

				if (!f.getKey().equals(s.getKey())) {
					if (f.getValue() < s.getValue()) {
						Triple<T, T, Relation> rel = new Triple<T, T, Relation>(
								f.getKey(), s.getKey(), Relation.LESS);
						tempPO.add(rel);
					} else if (f.getValue() == s.getValue()) {
						Triple<T, T, Relation> rel = new Triple<T, T, Relation>(
								f.getKey(), s.getKey(), Relation.LESS_EQUAL);
						tempPO.add(rel);
					} else
						continue;
				}
			}
		}
		PreferenceOrder<T> po = new PreferenceOrder<T>(tempPO);
		return po;
	}

	/**
	 * weakens the given element in the leveling function
	 * 
	 * @param element
	 *            the element being weakened
	 */
	public void weakenElement(T element) {

		HashMap<T, Integer> lf = this;
		int level = getElementsByValue(this.get(element)).size();
		int val = this.get(element);

		if (level > 1) {
			for (Entry<T, Integer> e : this.entrySet()) {
				if (e.getValue() > val
						|| e.getKey().toString().equals(element.toString())) {
					lf.put(e.getKey(), e.getValue() + 1);
				}
			}
		} else if (level == 1) {
			for (Entry<T, Integer> f : this.entrySet()) {
				if (f.getKey().toString().equals(element.toString())) {
					lf.put(f.getKey(), f.getValue() + 1);
				}
			}
		}
	}

	/**
	 * strengthens the given element in the leveling function
	 * 
	 * @param element
	 *            the element being strengthened
	 */
	public void strengthenElement(T element) {

		HashMap<T, Integer> lf = this;
		int level = getElementsByValue(this.get(element)).size();
		int val = this.get(element);

		if (level > 1) {
			for (Entry<T, Integer> e : this.entrySet()) {
				if (e.getValue() >= val
						&& !e.getKey().toString().equals(element.toString())) {
					lf.put(e.getKey(), e.getValue() - 1);
				}
			}
		} else if (level == 1) {
			for (Entry<T, Integer> f : this.entrySet()) {
				if (f.getKey().toString().equals(element.toString())) {
					lf.put(f.getKey(), f.getValue() + 1);
				}
			}

		}
	}
}

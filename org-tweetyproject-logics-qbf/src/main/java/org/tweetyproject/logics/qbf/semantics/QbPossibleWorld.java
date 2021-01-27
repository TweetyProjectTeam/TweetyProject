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
package org.tweetyproject.logics.qbf.semantics;

import java.util.*;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.commons.util.*;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.logics.qbf.syntax.ExistsQuantifiedFormula;
import org.tweetyproject.logics.qbf.syntax.ForallQuantifiedFormula;

/**
 * This class represents a possible world of quantified boolean logic, i.e. some set
 * of propositions.
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 */
public class QbPossibleWorld extends InterpretationSet<Proposition,PlBeliefSet,PlFormula> implements Comparable<PossibleWorld> {
	/**
	 * Creates a new empty possible world.
	 */
	public QbPossibleWorld() {
		this(new HashSet<Proposition>());
	}

	/**
	 * Creates a new possible world with the given set of propositions.
	 * 
	 * @param propositions the propositions that are true in this possible world
	 */
	public QbPossibleWorld(Collection<? extends Proposition> propositions) {
		super(propositions);
	}

	/**
	 * Checks whether this interpretation satisfies the given formula.
	 */
	public boolean satisfies(PlFormula formula) throws IllegalArgumentException {
		if(formula instanceof Contradiction)
			return false;
		if(formula instanceof Tautology)
			return true;
		if(formula instanceof Proposition)
			return this.contains(formula);
		if(formula instanceof Negation)
			return !this.satisfies(((Negation)formula).getFormula());
		if(formula instanceof Conjunction){
			Conjunction c = (Conjunction) formula;
			for(PlFormula f : c)
				if(!this.satisfies(f))
					return false;
			return true;
		}
		if(formula instanceof ExclusiveDisjunction){
			Conjunction c = ((ExclusiveDisjunction) formula).toCnf();
			for(PlFormula f : c)
				if(!this.satisfies(f))
					return false;
			return true;
		}
		if(formula instanceof Disjunction){
			Disjunction d = (Disjunction) formula;
			for(PlFormula f: d)
				if(this.satisfies(f))
					return true;
			return false;
		}
		if(formula instanceof Implication) {
			Implication i = (Implication) formula;
			if (this.satisfies((PlFormula) i.getFormulas().getFirst()))
				if (!this.satisfies((PlFormula) i.getFormulas().getSecond()))
						return false;
			return true;
		}
		if(formula instanceof Equivalence) {
			Equivalence e = (Equivalence) formula;
			PlFormula a = e.getFormulas().getFirst();
			PlFormula b = e.getFormulas().getSecond();
			if (this.satisfies((PlFormula) a)) {
				if (!this.satisfies((PlFormula) b))
					return false;
			}
			else {
				if (this.satisfies((PlFormula) b))
					return false;
			}
			return true;	
		}
		if (formula instanceof ExistsQuantifiedFormula) {
			ExistsQuantifiedFormula e = (ExistsQuantifiedFormula) formula;
			if (e.getQuantifierVariables().isEmpty())
				return this.satisfies(e.getFormula());
			Proposition v = e.getQuantifierVariables().iterator().next();
			Set<Proposition> remainingVariables = e.getQuantifierVariables();
			remainingVariables.remove(v);
			if (remainingVariables.isEmpty()) {
				Set<PlFormula> result = substitute(e.getFormula(), v);
				for (PlFormula s : result) {
					if (this.satisfies(s))
						return true;
				}

			} else {
				Set<PlFormula> result = substitute(e.getFormula(), v);
				for (PlFormula s : result) {
					if (this.satisfies(new ExistsQuantifiedFormula(s, remainingVariables)))
						return true;
				}
			}
			return false;
		} if (formula instanceof ForallQuantifiedFormula) {
			ForallQuantifiedFormula e = (ForallQuantifiedFormula) formula;
			if (e.getQuantifierVariables().isEmpty())
				return this.satisfies(e.getFormula());
			Proposition v = e.getQuantifierVariables().iterator().next();
			Set<Proposition> remainingVariables = e.getQuantifierVariables();
			remainingVariables.remove(v);

			Set<PlFormula> result = substitute(e.getFormula(), v);
			for (PlFormula s : result) {
				if (!this.satisfies(new ForallQuantifiedFormula(s, remainingVariables)))
					return false;
			}
			return true;
		} else
			throw new IllegalArgumentException("Quantified boolean formula " + formula + " is of unknown type.");
	}
	
	/**
	 * Checks whether this interpretation satisfies the given formula.
	 */
	public boolean satisfies(Collection<PlFormula> formulas) throws IllegalArgumentException {
		for(Formula f: formulas)
			if(!(f instanceof PlFormula))
				throw new IllegalArgumentException();
			else if(!this.satisfies((PlFormula)f))
				return false;
		return true;
	}
	
	/**
	 * Checks whether this interpretation satisfies the given formula.
	 */
	public boolean satisfies(PlBeliefSet beliefBase) throws IllegalArgumentException {
		return this.satisfies((Collection<PlFormula>) beliefBase);
	}

	/**
	 * Returns the set of all possible worlds for the
	 * given propositional signature.
	 * @param signature a propositional signature.
	 * @return the set of all possible worlds for the
	 * given propositional signature.
	 */
	public static Set<QbPossibleWorld> getAllPossibleWorlds(Collection<Proposition> signature) {
		Set<QbPossibleWorld> possibleWorlds = new HashSet<QbPossibleWorld>();
		Set<Set<Proposition>> propositions = new SetTools<Proposition>().subsets(signature);
		for (Set<Proposition> p : propositions)
			possibleWorlds.add(new QbPossibleWorld(p));
		return possibleWorlds;
	}
	
	/**
	 * Returns the set of all possible worlds for the
	 * given propositional signature.
	 * @param signature a propositional signature.
	 * @return the set of all possible worlds for the
	 * given propositional signature.
	 */
	public static Set<QbPossibleWorld> getAllPossibleWorlds(PlSignature signature){
		return getAllPossibleWorlds(signature.toCollection());
	}

	/**
	 * Substitutes all occurrences of the proposition v 
	 * with the possible truth values and returns
	 * a set of the possible substitutions.
	 * @param f a formula
	 * @param v a proposition
	 * @return all possible substituted formulas
	 */
	public Set<PlFormula> substitute(PlFormula f, Proposition v) {
		Set<PlFormula> result = new HashSet<PlFormula>();
		if (f instanceof SpecialFormula) 
			result.add(f);
		else if (f instanceof Proposition) {
			if (f.equals(v)) {
				result.add(new Tautology());
				result.add(new Contradiction());
			} else
				result.add(f);
		}
		else if (f instanceof Negation) {
			Negation n = (Negation) f;
			Set<PlFormula> r = substitute(n.getFormula(), v);
			for (PlFormula pr : r)
				result.add(new Negation(pr));
		}
		else if (f instanceof Conjunction) {
			Conjunction c = (Conjunction) f;
			Set<Set<PlFormula>> substitutedFormulas = new HashSet<Set<PlFormula>>();
			for (PlFormula x : c) {
				Set<PlFormula> r = substitute(x, v);
				substitutedFormulas.add(r);
			}
			Set<Set<PlFormula>> permutations = new SetTools<PlFormula>().permutations(substitutedFormulas);
			for (Set<PlFormula> rs : permutations)
				result.add(new Conjunction(rs));
		}
		else if (f instanceof Disjunction) {
			Disjunction c = (Disjunction) f;
			Set<Set<PlFormula>> substitutedFormulas = new HashSet<Set<PlFormula>>();
			for (PlFormula x : c) {
				Set<PlFormula> r = substitute(x, v);
				substitutedFormulas.add(r);
			}
			Set<Set<PlFormula>> permutations = new SetTools<PlFormula>().permutations(substitutedFormulas);
			for (Set<PlFormula> rs : permutations)
				result.add(new Disjunction(rs));
		}
		else if (f instanceof Implication) {
			Implication i = (Implication) f;
			Set<PlFormula> left = substitute(i.getFormulas().getFirst(), v);
			Set<PlFormula> right = substitute(i.getFormulas().getSecond(), v);
			Set<Set<PlFormula>> substitutedFormulas = new HashSet<Set<PlFormula>>();
			substitutedFormulas.add(left);
			substitutedFormulas.add(right);
			Set<Set<PlFormula>> permutations = new SetTools<PlFormula>().permutations(substitutedFormulas);
			for (Set<PlFormula> rs : permutations) {
				Iterator<PlFormula> it = rs.iterator();
				result.add(new Implication(it.next(),it.next())); 
			} 
		}
		else if (f instanceof Equivalence) {
			Equivalence e = (Equivalence) f;
			Set<PlFormula> left = substitute(e.getFormulas().getFirst(), v);
			Set<PlFormula> right = substitute(e.getFormulas().getSecond(), v);
			Set<Set<PlFormula>> substitutedFormulas = new HashSet<Set<PlFormula>>();
			substitutedFormulas.add(left);
			substitutedFormulas.add(right);
			Set<Set<PlFormula>> permutations = new SetTools<PlFormula>().permutations(substitutedFormulas);
			for (Set<PlFormula> rs : permutations) {
				Iterator<PlFormula> it = rs.iterator();
				result.add(new Implication(it.next(),it.next())); 
			} 
			
		}
		else if (f instanceof ExistsQuantifiedFormula) {
			ExistsQuantifiedFormula e = (ExistsQuantifiedFormula) f;
			Set<PlFormula> r = substitute(e.getFormula(), v);
			for (PlFormula pr : r)
				result.add(new ExistsQuantifiedFormula(pr,e.getQuantifierVariables()));
		}
		else if (f instanceof ForallQuantifiedFormula) {
			ForallQuantifiedFormula a = (ForallQuantifiedFormula) f;
			Set<PlFormula> r = substitute(a.getFormula(), v);
			for (PlFormula pr : r)
				result.add(new ForallQuantifiedFormula(pr,a.getQuantifierVariables()));
		}
		else
			throw new IllegalArgumentException();
		return result;
	}

	@Override
	public int compareTo(PossibleWorld o) {
		if(this.hashCode() < o.hashCode())
			return -1;
		if(this.hashCode() > o.hashCode())
			return 1;
		return 0;
	}

}

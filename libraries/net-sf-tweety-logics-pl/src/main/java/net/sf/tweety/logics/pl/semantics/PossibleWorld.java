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
package net.sf.tweety.logics.pl.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.commons.util.*;
import net.sf.tweety.logics.pl.syntax.*;


/**
 * This class represents a possible world of propositional logic, i.e.
 * some set of propositions.
 * 
 * @author Matthias Thimm
 */
public class PossibleWorld extends InterpretationSet<Proposition,PlBeliefSet,PlFormula> implements Comparable<PossibleWorld> {
	
	/**
	 * Creates a new empty possible world.
	 */
	public PossibleWorld(){
		this(new HashSet<Proposition>());
	}
	
	/**
	 * Creates a new possible world with the given set of propositions.
	 * @param propositions the propositions that are true in this possible world
	 */
	public PossibleWorld(Collection<? extends Proposition> propositions){
		super(propositions);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.Formula)
	 */
	@Override
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
		throw new IllegalArgumentException("Propositional formula " + formula + " is of unknown type.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.AbstractInterpretation#satisfies(java.util.Collection)
	 */
	@Override
	public boolean satisfies(Collection<PlFormula> formulas) throws IllegalArgumentException {
		for(Formula f: formulas)
			if(!(f instanceof PlFormula))
				throw new IllegalArgumentException();
			else if(!this.satisfies((PlFormula)f))
				return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Interpretation#satisfies(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
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
	public static Set<PossibleWorld> getAllPossibleWorlds(Collection<Proposition> signature){
		Set<PossibleWorld> possibleWorlds = new HashSet<PossibleWorld>();
		Set<Set<Proposition>> propositions = new SetTools<Proposition>().subsets(signature);
		for(Set<Proposition> p: propositions)
			possibleWorlds.add(new PossibleWorld(p));
		return possibleWorlds;
	}
	
	/**
	 * Returns the set of all possible worlds for the
	 * given propositional signature.
	 * @param signature a propositional signature.
	 * @return the set of all possible worlds for the
	 * given propositional signature.
	 */
	public static Set<PossibleWorld> getAllPossibleWorlds(PlSignature signature){
		return getAllPossibleWorlds(signature.toCollection());
	}
	
	/**
	 * Returns the complete conjunction representing this possible world wrt.
	 * 	the give signature
	 * @param a propositional signature
	 * @return the complete conjunction representing this possible world wrt.
	 * 	the give signature
	 */
	public PlFormula getCompleteConjunction(PlSignature sig){
		Conjunction c = new Conjunction();
		for(Proposition p: this)
			c.add(p);
		Collection<Proposition> remaining = new HashSet<Proposition>(sig.toCollection());
		remaining.removeAll(this);
		for(Proposition p: remaining)
			c.add(new Negation(p));		
		return c;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PossibleWorld arg0) {
		if(this.hashCode() < arg0.hashCode())
			return -1;
		if(this.hashCode() > arg0.hashCode())
			return 1;
		return 0;
	}

}

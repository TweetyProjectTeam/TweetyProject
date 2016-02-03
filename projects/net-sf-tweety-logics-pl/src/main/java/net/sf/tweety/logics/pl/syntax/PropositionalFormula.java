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
package net.sf.tweety.logics.pl.syntax;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.commons.syntax.interfaces.ClassicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.math.probability.Probability;

/**
 * This class represents the common ancestor for propositional formulae.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public abstract class PropositionalFormula implements ClassicalFormula {

	@Override
	public Class<PropositionalPredicate> getPredicateCls() {
		return PropositionalPredicate.class;
	}
	
	@Override
	public PropositionalSignature getSignature() {
		return new PropositionalSignature();
	}

	@Override
	public abstract Set<Proposition> getAtoms();

	/**
	 * Returns all literals, i.e. all formulas of the form "a" or "!a"
	 * where "a" is a proposition, that appear in this formula.
	 * @return all literals appearing in this formula.
	 */
	public abstract Set<PropositionalFormula> getLiterals();
	
	@Override
	public Conjunction combineWithAnd(Conjuctable f){
		if(!(f instanceof PropositionalFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a propositional formula.");
		return new Conjunction(this,(PropositionalFormula)f);
	}
	
	@Override
	public Disjunction combineWithOr(Disjunctable f){
		if(!(f instanceof PropositionalFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a propositional formula.");
		return new Disjunction(this,(PropositionalFormula)f);
	}
	
	/**
	 * This method collapses all associative operations appearing
	 * in this term, e.g. every a||(b||c) becomes a||b||c.
	 * @return the collapsed formula.
	 */
	public abstract PropositionalFormula collapseAssociativeFormulas();
	
	@Override
	public abstract Set<PropositionalPredicate> getPredicates();
	
	/**
	 * Removes duplicates (identical formulas) from conjunctions and disjunctions and
	 * duplicate negations.
	 * @return an equivalent formula without duplicates.
	 */
	public abstract PropositionalFormula trim();
	
	/**
	 * Returns this formula's probability in the uniform distribution. 
	 * @return this formula's probability in the uniform distribution.
	 */
	@Override
	public Probability getUniformProbability(){
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds(this.getSignature());
		int cnt = 0;
		for(PossibleWorld world: worlds)
			if(world.satisfies(this))
				cnt++;
		return new Probability(new Double(cnt)/new Double(worlds.size()));
	}
	
    /**
     * This method returns this formula in negation normal form (NNF).
     * A formula is in NNF iff negations occur only directly in front of a proposition.
     * @return the formula in NNF.
     */
	public abstract PropositionalFormula toNnf();
	
	/**
     * This method returns this formula in conjunctive normal form (CNF).
     * A formula is in CNF iff it is a conjunction of disjunctions and in NNF.
     * @return the formula in CNF.
     */
	public abstract Conjunction toCnf();
	
	/**
	 * Returns the set of models of this formula wrt. a signature
	 * that only contains the propositions appearing in this formula.
	 * @return  the set of models of this formula wrt. a signature
	 * that only contains the propositions appearing in this formula.
	 */
	public Set<PossibleWorld> getModels(){		
		return this.getModels(this.getSignature());
	}
	
	/**
	 * Returns the set of models of this formula wrt. the given signature.
	 * @param sig some propositional signature
	 * @return the set of models of this formula wrt. the given signature.
	 */
	public abstract Set<PossibleWorld> getModels(PropositionalSignature sig);
	
    /**
	 * This method returns this formula in disjunctive normal form (DNF).
	 * A formula is in DNF iff it is a disjunction of conjunctive clauses.
	 * @return the formula in DNF.
	 */
	public PropositionalFormula toDnf(){
		PropositionalFormula nnf = this.toNnf();
	    // DNF( P || Q) = DNF(P) || DNF(Q)
	    if(nnf instanceof Disjunction) {
	      Disjunction d = (Disjunction) nnf;
	      Disjunction dnf = new Disjunction();
	      for(PropositionalFormula f : d) {
	        dnf.add( f.toDnf() );
	      }
	    return dnf;
	    }
    
    /* DNF( P_1 && P_2 && ... && P_k) is calculated as follows:
     * 1. DNF(P_1) = P_11 || P_12 || ... || P_1l
     *    DNF(P_2) = P_21 || P_22 || ... || P_2m
     *    ...
     *    DNF(P_k) = P_k1 || P_k2 || ... || P_kn
     *    each P_ij is a conjunction of literals (propositions or negations of propositions)
     * 2. the dnf is then the disjunction of all possible permutations of distributed conjunctions of P_ij, eg:
     *    DNF(P) = p1 || p2 || p3
     *    DNF(Q) = q1 || q2
     *    DNF(R) = r1
     *    DNF(P && Q && R) = (p1 && q1 && r1) || (p1 && q2 && r1) || 
     *                       (p2 && q1 && r1) || (p2 && q2 && r1) || 
     *                       (p3 && q1 && r1) || (p3 && q2 && r1)
     */
    if(nnf instanceof Conjunction) {
      Conjunction c = (Conjunction) nnf;
      Set<Set<PropositionalFormula>> disjunctions = new HashSet<Set<PropositionalFormula>>();
      for(PropositionalFormula f : c) {
        PropositionalFormula fdnf = f.toDnf().collapseAssociativeFormulas();
        Set<PropositionalFormula> elems = new HashSet<PropositionalFormula>();
        disjunctions.add( elems );
        if(fdnf instanceof Disjunction) {
          elems.addAll( (Disjunction)fdnf );
        } else {
          elems.add( fdnf );
        }
      }
      
     // the dnf is the disjunction of all possible combinations of distributed conjunctions
      Set<Set<PropositionalFormula>> permutations = new SetTools< PropositionalFormula >().permutations( disjunctions );
      Disjunction dnf = new Disjunction();
      for(Set<PropositionalFormula> elems : permutations) {
        dnf.add( new Conjunction( elems ) );
      }
      return dnf;
    }
    return nnf;
  }
  
	@Override
	public ClassicalFormula complement(){
		if(this instanceof Negation)
			return ((Negation)this).getFormula();
		return new Negation(this);
	}

	@Override
	public boolean isLiteral() {
		return false;
	}
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract PropositionalFormula clone();
}

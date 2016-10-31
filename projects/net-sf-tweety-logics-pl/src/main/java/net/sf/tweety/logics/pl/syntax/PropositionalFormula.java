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

import java.util.Collection;
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
		    return dnf.collapseAssociativeFormulas();
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
	      return dnf.collapseAssociativeFormulas();
	    }
	    return nnf.collapseAssociativeFormulas();
	  }

	/**
	 * Checks whether this formula (which must be a conjunction of literals) is
	 * resolvable with the given formulas (which is also a conjunction of literals,
	 * i.e. whether they contains exactly one complementary literal.
	 * @param other a conjunction of literals
	 * @return "true" iff this formula is resolvable with the other formula.
	 */
	public boolean resolvableWith(PropositionalFormula other){
		if(!this.isConjunctiveClause() || !other.isConjunctiveClause())
			throw new IllegalArgumentException("Formula must be a conjunctive clause");
		Conjunction c1 = (Conjunction) this;
		Conjunction c2 = (Conjunction) other;
		int numOfComplementaryLiterals = 0;
		for(PropositionalFormula p1: c1.getFormulas())
			for(PropositionalFormula p2: c2.getFormulas())
				if(p2.equals(p1.complement()))
					numOfComplementaryLiterals++;
		return numOfComplementaryLiterals == 1;
	}
	
	/**
	 * Resolves this formula with the given one (both have to be conjunctive 
	 * clauses) and returns some resolvent.
	 * @param other a conjunction of formulas
	 * @return some resolvent.
	 */
	public Conjunction resolveWith(PropositionalFormula other){
		if(!this.resolvableWith(other))
			throw new IllegalArgumentException("Formulas cannot be resolved");
		
		Conjunction c1 = (Conjunction) this;
		Conjunction c2 = (Conjunction) other;
		
		Set<PropositionalFormula> result = new HashSet<PropositionalFormula>();
		result.addAll(c1.getFormulas());
		result.addAll(c2.getFormulas());
		for(PropositionalFormula p1: c1.getFormulas())
			for(PropositionalFormula p2: c2.getFormulas())
				if(p2.equals(p1.complement())){
					result.remove(p2);
					result.remove(p1);
					return new Conjunction(result);
				}
		// this should not happen
		throw new RuntimeException("Unexpected error in resolving formulas.");			
	}
		
	/**
	 * This method returns this formula in Blake canonical form. A formula
	 * is in Blake canonical form iff it is the disjunction of its prime
	 * implicants.
	 * @return the formula in Blake canonical form
	 */
	public PropositionalFormula toBlakeCanonicalForm(){
		// first we obtain the DNF
		PropositionalFormula f = this.toDnf();
		// special case: only one conjunctive clause
		if(!(f instanceof Disjunction))
			return f;
		Set<PropositionalFormula> implicants = new HashSet<PropositionalFormula>(((Disjunction)f).getFormulas());
		// check that every implicant is represented as a conjunction
		Set<PropositionalFormula> tmp = new HashSet<PropositionalFormula>();
		for(PropositionalFormula i: implicants)
			if(!(i instanceof Conjunction)){
				Set<PropositionalFormula> k = new HashSet<PropositionalFormula>();
				k.add(i);
				tmp.add(new Conjunction(k));
			}else tmp.add(i);
		implicants = tmp;
		if(implicants.size() == 1)
			return f;
		// resolve implicants until this is no more possible
		boolean changed;
		do{
			changed = false;
			for(PropositionalFormula p1: implicants){
				for(PropositionalFormula p2: implicants){
					if(p1 != p2){
						if(p1.resolvableWith(p2) && !implicants.contains(p1.resolveWith(p2))){
							//implicants.remove(p1);
							//implicants.remove(p2);
							implicants.add(p1.resolveWith(p2));
							changed = true;
						}
					}
					if(changed) break;
				}
				if(changed) break;
			}
		}while(changed);		
		//remove non-minimal implicants
		do{
			changed = false;
			for(PropositionalFormula p1: implicants){
				for(PropositionalFormula p2: implicants){
					if(p1 != p2){
						if(((Conjunction)p1).getFormulas().containsAll( ((Conjunction)p2).getFormulas())){
							implicants.remove(p1);
							changed = true;
						}
					}
					if(changed) break;
				}
				if(changed) break;
			}
		}while(changed);
		return new Disjunction(implicants);
	}

	/**
	 * Returns the set of prime implicants of this formula.
	 * @return the set of prime implicants of this formula.
	 */
	public Collection<PropositionalFormula> getPrimeImplicants(){
		return ((Disjunction)this.toBlakeCanonicalForm()).getFormulas();
	}
	
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
	
    @Override
	public ClassicalFormula complement(){
		if(this instanceof Negation)
			return ((Negation)this).getFormula();
		return new Negation(this);
	}

    /**
     * Checks whether this formula is a conjunctive clause,
     * i.e. whether it is a conjunction of literals.
     * @return "true" iff this formula is a conjunctive clause.
     */
    public boolean isConjunctiveClause(){
    	return false;
    }
    
    /**
     * Returns the number of occurrences of the given proposition
     * within this formula
     * @param p some proposition
     * @return the number of occurrences of the given proposition
     * within this formula
     */
    public abstract int numberOfOccurrences(Proposition p);
        
    /**
     * Replaces the ith instance of the proposition p by f.
     * @param p some proposition
     * @param f some formula
     * @param i the index of the proposition
     * @return a new formula with the ith instance of the proposition p replaced by f.
     */
    public abstract PropositionalFormula replace(Proposition p, PropositionalFormula f, int i);
    
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

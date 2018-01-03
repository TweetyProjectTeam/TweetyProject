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
package net.sf.tweety.logics.fol.syntax;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.math.probability.Probability;


/**
 * The common abstract class for formulas of first-order logic.
 * 
 * NOTE: "RelationalFormula" and "FolFormula" differ in their meaning as follows:
 * <ul>
 * 	<li>A relational formula is any formula over a first-order signature, i.e. even a conditional</li>
 *  <li>A first-order formula is the actual first-order formula in the classical sense.</li>
 * </ul>
 * @author Matthias Thimm
 * @author Tim Janus
 */
public abstract class FolFormula extends RelationalFormula {

	@Override
	public Conjunction combineWithAnd(Conjuctable f){
		if(!(f instanceof FolFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a first-order formula.");
		return new Conjunction(this,(FolFormula)f);
	}
	
	@Override
	public Disjunction combineWithOr(Disjunctable f){
		if(!(f instanceof FolFormula))
			throw new IllegalArgumentException("The given formula " + f + " is not a first-order formula.");
		return new Disjunction(this,(FolFormula)f);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#complement()
	 */
	@Override
	public RelationalFormula complement(){
		if(this instanceof Negation) return ((Negation)this).getFormula();
		return new Negation(this);
	}	
	
	@Override
	public Set<Variable> getQuantifierVariables() {
		return new HashSet<Variable>();
	}
	
	/**
	 * Makes a disjunctive normal form of this formula.
	 * @return the DNF of this formula
	 */
	public FolFormula toDnf(){
		if(this.isDnf()) return this;
		if(this.containsQuantifier()) throw new UnsupportedOperationException("Cannot convert quantified formula into DNF.");
		FolFormula nnf = this.toNnf();

    // DNF( P || Q) = DNF(P) || DNF(Q)
    if(nnf instanceof Disjunction) {
      Disjunction d = (Disjunction) nnf;
      Disjunction dnf = new Disjunction();
      for(RelationalFormula f : d) {
        if(f instanceof FolFormula)
          dnf.add( ((FolFormula)f).toDnf() );
        else throw new IllegalStateException("Can not convert disjunctions containing non-first-order formulae to NNF.");
      }
      return (FolFormula)dnf.collapseAssociativeFormulas();
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
      Set<Set<RelationalFormula>> disjunctions = new HashSet<Set<RelationalFormula>>();
      for(RelationalFormula f : c) {
        if(! (f instanceof FolFormula)) throw new IllegalStateException("Can not convert conjunctions containing non-first-order formulae to NNF.");
        RelationalFormula fdnf = ((FolFormula)f).toDnf();
        Set<RelationalFormula> elems = new HashSet<RelationalFormula>();
        if(fdnf instanceof Disjunction) {
          elems.addAll( (Disjunction)fdnf );
        } else {
          elems.add( fdnf );
        }
        disjunctions.add( elems );
      }
      
     // the dnf is the disjunction of all possible combinations of distributed conjunctions
      Set<Set<RelationalFormula>> permutations = new SetTools< RelationalFormula >().permutations( disjunctions );
      Disjunction dnf = new Disjunction();
      for(Set<RelationalFormula> elems : permutations) {
        dnf.add( new Conjunction( elems ) );
      }
      return (FolFormula)dnf.collapseAssociativeFormulas();
    }
    return nnf;
	}
	
	/**
	 * Makes the negation normal form of this formula.
	 * @return the NNF of this formula
	 */
	public abstract FolFormula toNnf();
	 
	/**
     * This method collapses all associative operations appearing
     * in this term, e.g. every a||(b||c) becomes a||b||c.
     * @return the collapsed formula.
     */
	public abstract RelationalFormula collapseAssociativeFormulas();

	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability(){
		Set<Variable> vars = this.getUnboundVariables();
		Map<Variable,Constant> map = new HashMap<Variable,Constant>();
		int i = 0;
		FolSignature sig = this.getSignature();
		for(Variable var: vars){
			Constant c = new Constant("d" + i++);
			map.put(var, c);
			sig.add(c);
		}
		FolFormula groundFormula = (FolFormula) this.substitute(map);
		HerbrandBase hBase = new HerbrandBase(sig);
		Collection<HerbrandInterpretation> allWorlds = hBase.allHerbrandInterpretations();
		int cnt = 0;
		for(HerbrandInterpretation hInt: allWorlds)
			if(hInt.satisfies(groundFormula))
				cnt++;
		return new Probability(new Double(cnt)/new Double(allWorlds.size()));
	}
	
	/**
	 * Checks whether this formula is in disjunctive normal form.
	 * @return "true" iff this formula is in disjunctive normal form.
	 */
	public abstract boolean isDnf();
	
	@Override
	public abstract FolFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	@Override
	public abstract FolFormula clone();
	
	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		sig.addAll(this.getTerms(Constant.class));
		sig.addAll(this.getFunctors());
		sig.addAll(this.getPredicates());
		return sig;
	}

	

}

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
package net.sf.tweety.logics.commons.syntax;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.MapTools;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.ClassicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.QuantifiedFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.math.probability.Probability;

/**
 * This is the abstract base class for relational formulas, 
 * i.e. formulas that are build on first-order signatures.
 *
 * NOTE: "RelationalFormula" and "FolFormula" differ in their meaning as follows:
 *<ul>
 * <li>A relational formula is any formula over a first-order signature, i.e. even a conditional</li>
 * <li>A first-order formula is the actual first-order formula in the classical sense.</li>
 *</ul>
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 * @author Anna Gessler
 */
public abstract class RelationalFormula implements ClassicalFormula, QuantifiedFormula {
	
	/**
	 * @return the relational formula
	 */
	@Override
	public RelationalFormula getFormula() {
		return this;
	}
	
	/**
	 * @return all atoms that appear in this formula.
	 */
	@Override
	public abstract Set<? extends Atom> getAtoms();
	
	/**
	 * @return all functors that appear in this formula.
	 */
	public abstract Set<Functor> getFunctors();
	
	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return Predicate.class;
	}
	
	/**
	 * Substitutes all occurrences of term "v" in this formula
	 * by term "t" and returns the new formula.
	 * NOTE: if "v" is a variable and bound to a quantifier then "v" is not substituted
	 * 		in that quantifiers inner formula.
	 * @param v the term to be substituted.
	 * @param t the term to substitute.
	 * @return a formula where every occurrence of "v" is replaced
	 * 		by "t".
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 *    (NOTE: this exception is only thrown when "v" actually appears in this
	 *    formula).
	 */
	@Override
	public abstract RelationalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	/**
	 * Substitutes all occurrences of all terms "v" in map.keyset() in this formula
	 * by map.get(v) and returns the new formula.<br>
	 * NOTE: variables bound to quantifiers are not substituted in their inner formulas
	 * 		even if they appear in the map.
	 * @param map a mapping defining which terms to be substituted.
	 * @return a formula where every term in map.keyset() has been replaced by map.get(v).
	 * @throws IllegalArgumentException if any term and its mapping are of different sorts
	 *    (NOTE: this exception is only thrown when the variable actually appears in this
	 *    formula).
	 */
	@Override
	public RelationalFormula substitute(Map<? extends Term<?>, ? extends Term<?>> map)
			throws IllegalArgumentException {
		RelationalFormula f = this;
		for(Term<?> v: map.keySet())
			f = f.substitute(v,map.get(v));
		return f;
	}
	
	/**
	 * Substitutes all occurrences of term "v" in this formula
	 * by term "t" and at the same time replaces all occurrences of term "t"
	 * by term "v" and eventually returns the new formula.
	 * NOTE: if "v" is a variable and bound to a quantifier then "v" is not substituted
	 * 		in that quantifiers inner formula.
	 * @param v a term.
	 * @param t a term.
	 * @return a new relational formula with both "v" and "t" exchanged.
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 */
	@Override
	public RelationalFormula exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		if(!v.getSort().equals(t.getSort()))
			throw new IllegalArgumentException("Terms '" + v + "' and '" + t + "' are of different sorts.");
		Constant temp = new Constant("$TEMP$", v.getSort());
		RelationalFormula rf = this.substitute(v, temp);
		rf = rf.substitute(t, v);
		rf = rf.substitute(temp, t);
		// remove temporary constant from signature
		v.getSort().remove(temp);	
		return rf;
	}
	
	/**
	 * Computes all possible substitutions, i.e. maps from variables to terms, of unbound
	 * variables of this formula to terms in "terms".
	 * @param terms a set of terms.
	 * @return a set of maps from variables to terms.
	 * @throws IllegalArgumentException if there is an unbound variable in this formula for
	 * 		which there is no term in "terms" with the same sort.
	 */
	public Set<Map<Variable,Term<?>>> allSubstitutions(Collection<? extends Term<?>> terms) throws IllegalArgumentException{
		Set<Variable> variables = this.getUnboundVariables();
		//partition variables by sorts
		Map<Sort,Set<Variable>> sorts_variables = new HashMap<Sort,Set<Variable>>();		
		for(Variable v: variables){
			if(!sorts_variables.containsKey(v.getSort()))
				sorts_variables.put(v.getSort(), new HashSet<Variable>());
			sorts_variables.get(v.getSort()).add(v);
		}
		//partition terms by sorts
		Map<Sort,Set<Term<?>>> sorts_terms = Sort.sortTerms(terms);
		//combine the partitions
		Map<Set<Variable>,Set<Term<?>>> relations = new HashMap<Set<Variable>,Set<Term<?>>>();
		for(Sort s: sorts_variables.keySet()){
			if(!sorts_terms.containsKey(s))
				throw new IllegalArgumentException("There is no term of sort " + s + " to substitute.");
			relations.put(sorts_variables.get(s), sorts_terms.get(s));			
		}
		return new MapTools<Variable,Term<?>>().allMaps(relations);				
	}
	
	/**
	 * Computes all ground instances of this relational formula wrt. the given set of constants, i.e.
	 * every formula where each occurrence of some unbound variable is replaced by some constant.
	 * @param constants a set of constants
	 * @return a set of ground instances of this formula
	 * @throws IllegalArgumentException if there is an unbound variable in this formula for
	 * 		which there is no constant in "constants" with the same sort.
	 */
	public Set<RelationalFormula> allGroundInstances(Set<Constant> constants) throws IllegalArgumentException{
		Set<Map<Variable,Term<?>>> maps = this.allSubstitutions(constants);
		Set<RelationalFormula> result = new HashSet<RelationalFormula>();
		for(Map<Variable,Term<?>> map: maps)
			result.add(this.substitute(map));		
		return result;
	}
	
	/**
	 * Returns the ratio of worlds not satisfying this formula to
	 *  worlds satisfying this formula.
	 * @return the ratio of worlds not satisfying this formula to
	 *  worlds satisfying this formula.
	 */
	public double getSatisfactionRatio(){
		return this.complement().getUniformProbability().doubleValue() / this.getUniformProbability().doubleValue();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.ClassicalFormula#getUniformProbability()
	 */
	@Override
	public abstract Probability getUniformProbability();
	
	/**
	 * Checks whether this formula is ground, i.e. whether there appears
	 * no variable in this formula.
	 * @return "true" if this formula is ground.
	 */
	@Override
	public boolean isGround(){
		return this.getTerms(Variable.class).isEmpty();
	}

	/**
	 * Tests whether this formula is well-formed, i.e. whether<br>
	 * - there are no two variables with the same name but different sort<br>
	 * - there are no two constants with the same name but different sort<br>
	 * - there are no two predicates with the same name but different
	 *   arguments<br>
	 * - there are no two functors with the same name but different
	 *   sort or arguments <br>
	 * - every atom is complete, i.e. has a complete list of arguments<br>
	 * - every functional term is complete, i.e. has a complete list of arguments<br>
	 * - no variable bound by a quantifier is again bound by another quantifier within the
	 *   first quantifier range.<br>
	 * - every functor has arity greater zero (otherwise it should be modeled by a constant
	 * @return "true" if this formula is well-formed
	 */
	@Override
	public boolean isWellFormed(){
		//there are no two variables with the same name but different sort
		for(Variable v: this.getTerms(Variable.class))
			for(Variable w: this.getTerms(Variable.class))
				if(v.get().equals(w.get()))
					if(!v.getSort().equals(w.getSort()))
						return false;
		//there are no two constants with the same name but different sort
		for(Constant c: this.getTerms(Constant.class))
			for(Constant d: this.getTerms(Constant.class))
				if(c.get().equals(d.get()))
					if(!c.getSort().equals(d.getSort()))
						return false;
		//there are no two predicates with the same name but different arguments
		for(Predicate p: this.getPredicates())
			for(Predicate q: this.getPredicates())
				if(p.getName().equals(q.getName())){
					if(p.getArity() != q.getArity())
						return false;
					for(int i = 0; i < p.getArity(); i++)
						if(!p.getArgumentTypes().get(i).equals(q.getArgumentTypes().get(i)))
							return false;					
				}
		//there are no two functors with the same name but different sort or arguments
		//and every functor has arity greater zero (otherwise it should be modeled by a constant
		for(Functor f: this.getFunctors())
			if(f.getArity() == 0){
				return false;
			}else{
				for(Functor g: this.getFunctors())
					if(f.getName().equals(g.getName())){
						if(!f.getTargetSort().equals(g.getTargetSort()))
							return false;
						if(f.getArity() != g.getArity())
							return false;
						for(int i = 0; i < f.getArity(); i++)
							if(!f.getArgumentTypes().get(i).equals(g.getArgumentTypes().get(i)))
								return false;					
				}
			}
		//every atom is complete, i.e. has a complete list of arguments
		for(Atom a: this.getAtoms())
			if(!a.isComplete())
				return false;
		//every functional term is complete, i.e. has a complete list of arguments
		for(FunctionalTerm t: this.getTerms(FunctionalTerm.class))
			if(!t.isComplete())
				return false;
		//no variable bound by a quantifier is again bound by another quantifier within the first quantifier's range.
		if(!this.isWellBound()) return false;		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Formula#getSignature()
	 */
	@Override
	public abstract Signature getSignature();
	
	@Override
	public <C extends Term<?>> boolean containsTermsOfType(Class<C> cls) {
		return !getTerms(cls).isEmpty();
	}
	
	@Override
	public abstract RelationalFormula complement();
	
	@Override
	public abstract Conjuctable combineWithOr(Disjunctable f);
	
	@Override
	public abstract Disjunctable combineWithAnd(Conjuctable f);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();	
	
	@Override
	public abstract RelationalFormula clone();
	
}

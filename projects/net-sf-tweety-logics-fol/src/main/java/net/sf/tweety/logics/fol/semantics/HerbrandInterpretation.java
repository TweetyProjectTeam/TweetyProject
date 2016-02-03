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
package net.sf.tweety.logics.fol.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.commons.util.*;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.*;
import net.sf.tweety.logics.fol.syntax.*;


/**
 * A Herbrand interpretation is an interpretation for a first-order signature,
 * stating all ground atoms that are true in the interpretation.
 * <br/>
 * NOTE: We only allow Herbrand interpretations for signatures without
 *   function symbols.
 * @author Matthias Thimm
 */
public class HerbrandInterpretation extends InterpretationSet<FOLAtom> {
	
	/**
	 * Creates a new empty Herbrand interpretation
	 */
	public HerbrandInterpretation(){
		this(new HashSet<FOLAtom>());
	}
	
	/**
	 * Creates a new Herbrand interpretation with the given
	 * set of atoms
	 * @param atoms the set of true atoms in this Herbrand interpretation.
	 */
	public HerbrandInterpretation(Collection<? extends FOLAtom> atoms){
		super(atoms);
	}
	
	/**
	 * Checks whether this Herbrand interpretation satisfies
	 * the given formula.
	 * @param formula a formula.
	 * @return "true" if this interpretation satisfies "f".
	 * @throws IllegalArgumentException if "f" is not closed.
	 */
	@Override
	public boolean satisfies(Formula formula) throws IllegalArgumentException{
		if(!(formula instanceof FolFormula)) throw new IllegalArgumentException("Formula " + formula + " is not a first-order formula.");
		FolFormula f = (FolFormula) formula;
		if(!f.isClosed()) throw new IllegalArgumentException("FolFormula " + f + " is not closed.");
		if(f instanceof Tautology){
			return true;
		}			
		if(f instanceof Contradiction){
			return false;
		}			
		if(f instanceof FOLAtom){
			return this.contains(f);
		}					
		if(f instanceof Disjunction){
			Disjunction d = (Disjunction) f;
			for(RelationalFormula rf: d)
				if(this.satisfies(rf)) return true;
			return false;
		}
		if(f instanceof Conjunction){
			Conjunction c = (Conjunction) f;
			for(RelationalFormula rf: c)
				if(!this.satisfies(rf)) return false;
			return true;
		}
		if(f instanceof Negation){
			Negation n = (Negation) f;
			return !this.satisfies(n.getFormula());
		}
		if(f instanceof ExistsQuantifiedFormula){
			ExistsQuantifiedFormula e = (ExistsQuantifiedFormula) f;
			if(e.getQuantifierVariables().isEmpty()) return this.satisfies(e.getFormula());
			Variable v = e.getQuantifierVariables().iterator().next();
			Set<Variable> remainingVariables = e.getQuantifierVariables();
			remainingVariables.remove(v);
			if(remainingVariables.isEmpty()){
				for(Constant c: v.getSort().getTerms(Constant.class))
					if(this.satisfies(e.getFormula().substitute(v, c)))
						return true;
			}else{
				for(Constant c: v.getSort().getTerms(Constant.class)){
					if(this.satisfies(new ExistsQuantifiedFormula(e.getFormula().substitute(v, c),remainingVariables)))
						return true;
					}
			}
			return false;
		}
		if(f instanceof ForallQuantifiedFormula){
			ForallQuantifiedFormula e = (ForallQuantifiedFormula) f;
			if(e.getQuantifierVariables().isEmpty()) return this.satisfies(e.getFormula());
			Variable v = e.getQuantifierVariables().iterator().next();
			Set<Variable> remainingVariables = e.getQuantifierVariables();
			remainingVariables.remove(v);
			for(Constant c: v.getSort().getTerms(Constant.class)){
				if(!this.satisfies(new ForallQuantifiedFormula(e.getFormula().substitute(v, c),remainingVariables)))
					return false;
			}
			return true;
		}		
		throw new IllegalArgumentException("FolFormula " + f + " is of unknown type.");
	}

	/**
	 * Checks whether this interpretation is syntactically equivalent to the given
	 * interpretation and the given equivalence classes, i.e. whether this interpretation can be
	 * translated to the other one by substituting constants from the same equivalence
	 * classes
	 * @param other a Herbrand interpretation.
	 * @param equivalenceClasses a set of sets of constants.
	 * @return "true" iff the two interpretations are syntactically equivalent.
	 */
	public boolean isSyntacticallyEquivalent(HerbrandInterpretation other, Collection<? extends Collection<? extends Constant>> equivalenceClasses){
		// check for obvious cases
		if(this.size() != other.size())
			return false;
		if(this.equals(other))
			return true;
		// retrieve all appearing constants and
		// check whether the appearing predicates coincide
		Set<Constant> constants = new HashSet<Constant>();
		Set<Predicate> predicates1 = new HashSet<Predicate>();
		Set<Predicate> predicates2 = new HashSet<Predicate>();
		for(FOLAtom a: this){
			constants.addAll(a.getTerms(Constant.class));
			predicates1.add(a.getPredicate());
		}
		for(FOLAtom a: other){
			constants.addAll(a.getTerms(Constant.class));
			predicates2.add(a.getPredicate());
		}
		if(!predicates1.equals(predicates2))
			return false;
		// for every subset of constants (all other constants are not mapped
		for(Set<Constant> constantsSubset: new SetTools<Constant>().subsets(constants)){		
			// project equivalence classes to appearing constants
			// and for every projected equivalence class retrieve every possible mapping
			Set<Set<Map<Term<?>,Term<?>>>> subMaps = new HashSet<Set<Map<Term<?>,Term<?>>>>();
			for(Collection<? extends Constant> eqClass: equivalenceClasses){
				Set<Constant> prjClass = new HashSet<Constant>(constantsSubset);
				prjClass.retainAll(eqClass);
				if(prjClass.isEmpty()) continue;
				Set<Map<Term<?>,Term<?>>> subsubMaps = new HashSet<Map<Term<?>,Term<?>>>();
				for(Set<Set<Constant>> bipartition: new SetTools<Constant>().getBipartitions(prjClass)){
					Iterator<Set<Constant>> it = bipartition.iterator();
					Set<Constant> partition1 = it.next();
					Set<Constant> partition2 = it.next();
					Set<Map<Term<?>,Term<?>>> maps = new MapTools<Term<?>,Term<?>>().allMaps(partition1, partition2);
					// remove every map where two different key is assignet the same value
					for(Map<Term<?>,Term<?>> map: maps)
						if(MapTools.isInjective(map))
							subsubMaps.add(map);
				}			
				subMaps.add(subsubMaps);
			}
			// permute the maps
			subMaps = new SetTools<Map<Term<?>,Term<?>>>().permutations(subMaps);
			// now combine every set of maps and check whether this yields an equivalence
			for(Set<Map<Term<?>,Term<?>>> maps: subMaps){
				Map<Term<?>,Term<?>> completeMap = new MapTools<Term<?>,Term<?>>().combine(maps);
				if(this.exchange(completeMap).equals(other))
					return true;				
			}		
		}
		return false;
	}
	
	/**
	 * Checks whether this Herbrand interpretation satisfies each of
	 * the formulas in the given set of first-order formulas.
	 * @param formulas a set of first-order formulas.
	 * @return "true" if this interpretation satisfies the given set of formulas.
	 * @throws IllegalArgumentException if at least one formula does not correspond
	 * 		to the expected language.
	 */
	public boolean satisfies(Set<FolFormula> formulas) throws IllegalArgumentException{
		for(FolFormula f: formulas)
			if(!this.satisfies(f)) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.BeliefBase)
	 */
	@Override
	public boolean satisfies(BeliefBase beliefBase) throws IllegalArgumentException{
		if(!(beliefBase instanceof FolBeliefSet))
			throw new IllegalArgumentException("First-order knowledge base expected.");
		FolBeliefSet folkb = (FolBeliefSet) beliefBase;
		for(Formula f: folkb)
			if(!this.satisfies(f)) return false;
		return true;
	}
	
	/**
	 * Substitutes every occurrence of "t1" by "t2" and vice versa and returns the
	 * new interpretation. 
	 * @param t1 a term.
	 * @param t2 a term.
	 * @return a Herbrand interpretation
	 */
	public HerbrandInterpretation exchange(Term<?> t1, Term<?> t2){
		Set<FOLAtom> atoms = new HashSet<FOLAtom>();
		Constant tempConstant = new Constant("__TEMP__");
		for(Formula f: this){
			FOLAtom a = ((FOLAtom) f).substitute(t1, tempConstant);
			a = a.substitute(t2, t1);
			a = a.substitute(tempConstant, t2);
			atoms.add(a);
		}		
		return new HerbrandInterpretation(atoms);
	}
	
	/**
	 * For every mapping t1 -> t2, this method substitutes every
	 * occurrence of "t1" by "t2" and vice versa and returns the new interpretation
	 * @param mapping a mapping of terms.
	 * @return a Herbrand interpretation.
	 */
	public HerbrandInterpretation exchange(Map<Term<?>,Term<?>> mapping){
		HerbrandInterpretation result = new HerbrandInterpretation(this);
		for(Term<?> t: mapping.keySet())
			result = result.exchange(t, mapping.get(t));
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return super.toString();
	}

}

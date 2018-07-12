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
package net.sf.tweety.logics.ml.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.commons.util.*;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.*;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.ml.syntax.ModalFormula;
import net.sf.tweety.logics.ml.syntax.Necessity;
import net.sf.tweety.logics.ml.syntax.Possibility;


/**
 * Modified version of HerbrandInterpretation that allows for modal formulas.
 * 
 * A Herbrand interpretation is an interpretation for a first-order signature,
 * stating all ground atoms that are true in the interpretation.
 * <br/>
 * NOTE: We only allow Herbrand interpretations for signatures without
 *   function symbols.
 * @author Matthias Thimm
 * @author Anna Gessler
 * @see net.sf.tweety.logics.fol.semantics.HerbrandInterpretation
 */
public class ModalHerbrandInterpretation extends InterpretationSet<FOLAtom,FolFormula> {
	
	/**
	 * Creates a new empty Herbrand interpretation
	 */
	public ModalHerbrandInterpretation(){
		this(new HashSet<FOLAtom>());
	}
	
	/**
	 * Creates a new Herbrand interpretation with the given
	 * set of atoms
	 * @param atoms the set of true atoms in this Herbrand interpretation.
	 */
	public ModalHerbrandInterpretation(Collection<? extends FOLAtom> atoms){
		super(atoms);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Interpretation#satisfies(net.sf.tweety.commons.Formula)
	 */
	@Override
	public boolean satisfies(FolFormula formula) throws IllegalArgumentException {
		return satisfies(formula, new HashSet<Interpretation<FolFormula>>());
	}
	
	/**
	 * Checks whether this Herbrand interpretation satisfies
	 * the given formula.
	 * @param formula a formula.
	 * @return "true" if this interpretation satisfies "f".
	 * @throws IllegalArgumentException if "f" is not closed.
	 */
	//@Override
	public boolean satisfies(Formula formula, Set<Interpretation<FolFormula>> successors) throws IllegalArgumentException{
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
				if(this.satisfies((FolFormula) rf)) return true;
			return false;
		}
		if(f instanceof Conjunction){
			Conjunction c = (Conjunction) f;
			for(RelationalFormula rf: c)
				if(!this.satisfies((FolFormula) rf)) return false;
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
			
			Set<Constant> constants = v.getSort().getTerms(Constant.class);
			//If a variable is of sort _Any, search for other occurrences of the variable
			//to find the sort and possible constants for this variable
			if (v.getSort().equals(Sort.ANY)) {
				for (Atom a : f.getAtoms()) {
					List<Term<?>> args =((FOLAtom)a).getArguments();
					for (int i = 0; i < args.size(); i++) {
						Term<?> t = args.get(i);
						if (t instanceof Variable && ((Variable)t).get().equals(v.get()) && !a.getPredicate().getArgumentTypes().get(i).equals(Sort.ANY)) { 
							Sort s = a.getPredicate().getArgumentTypes().get(i);
							constants = s.getTerms(Constant.class); 
							break; 
						}
					}
				}
			} 
			
			if(remainingVariables.isEmpty()){
				for(Constant c: constants)
					if(this.satisfies(e.getFormula().substitute(v, c)))
						return true;
			}else{
				for(Constant c: constants){
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
			
			Set<Constant> constants = v.getSort().getTerms(Constant.class);
			//If a variable is of sort _Any, search for other occurrences of the variable
			//to find the sort and possible constants for this variable
			if (v.getSort().equals(Sort.ANY)) {
				for (Atom a : f.getAtoms()) {
					List<Term<?>> args =((FOLAtom)a).getArguments();
					for (int i = 0; i < args.size(); i++) {
						Term<?> t = args.get(i);
						if (t instanceof Variable && ((Variable)t).get().equals(v.get()) && !a.getPredicate().getArgumentTypes().get(i).equals(Sort.ANY)) { 
							Sort s = a.getPredicate().getArgumentTypes().get(i);
							constants = s.getTerms(Constant.class); 
							break; 
						}
					}
				}
			} 
			
			for(Constant c: constants){
				if(!this.satisfies(new ForallQuantifiedFormula(e.getFormula().substitute(v, c),remainingVariables)))
					return false;
			}
			return true;
		}
		if (f instanceof Implication) {
			Implication i = (Implication) f;
			if (this.satisfies((FolFormula) i.getFormulas().getFirst()) && !this.satisfies((FolFormula) i.getFormulas().getSecond()))
				return false;
			return true;
		}
		if (f instanceof Equivalence) {
			Equivalence e = (Equivalence) f;
			RelationalFormula a = e.getFormulas().getFirst();
			RelationalFormula b = e.getFormulas().getFirst();
			
			if (this.satisfies((FolFormula) a)) {
				if (!this.satisfies((FolFormula) b))
					return false;
			}
			else {
				if (this.satisfies((FolFormula) b))
					return false;
			}
			return true;	
		}
		if (formula instanceof Necessity) {
			for (Interpretation<FolFormula> j : successors) {
				if (!j.satisfies((FolFormula) ((ModalFormula) formula).getFormula())) {
					return false; }

			}
		} 
		if (formula instanceof Possibility) {
			boolean satisfied = false;
			for (Interpretation<FolFormula> j : successors) {
				if (j.satisfies((FolFormula) ((ModalFormula) formula).getFormula())) {
					satisfied = true;
					break;
				}
			}
			if (!satisfied) {
				return false; }
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
	public boolean isSyntacticallyEquivalent(ModalHerbrandInterpretation other, Collection<? extends Collection<? extends Constant>> equivalenceClasses){
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
		for(FolFormula f: folkb)
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
	public ModalHerbrandInterpretation exchange(Term<?> t1, Term<?> t2){
		Set<FOLAtom> atoms = new HashSet<FOLAtom>();
		Constant tempConstant = new Constant("__TEMP__");
		for(Formula f: this){
			FOLAtom a = ((FOLAtom) f).substitute(t1, tempConstant);
			a = a.substitute(t2, t1);
			a = a.substitute(tempConstant, t2);
			atoms.add(a);
		}		
		return new ModalHerbrandInterpretation(atoms);
	}
	
	/**
	 * For every mapping t1 -> t2, this method substitutes every
	 * occurrence of "t1" by "t2" and vice versa and returns the new interpretation
	 * @param mapping a mapping of terms.
	 * @return a Herbrand interpretation.
	 */
	public ModalHerbrandInterpretation exchange(Map<Term<?>,Term<?>> mapping){
		ModalHerbrandInterpretation result = new ModalHerbrandInterpretation(this);
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

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
package net.sf.tweety.logics.rpcl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.AbstractInterpretation;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.MathTools;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Instances of this class represent reference worlds, i.e. sets of instance assignment. Each reference world
 * describes a set of Herbrand interpretations.
 * 
 * @author Matthias Thimm
 */
public class ReferenceWorld extends AbstractInterpretation implements Map<Predicate,InstanceAssignment>{

	/**
	 * Logger.
	 */
	static private Logger log = LoggerFactory.getLogger(ReferenceWorld.class); 
	
	/**
	 * The instance assignments of this reference worlds
	 */
	private Map<Predicate,InstanceAssignment> assignments;
		
	/**
	 * The equivalence classes this reference world bases on.
	 */
	private Collection<? extends Collection<? extends Constant>> equivalenceClasses;
	
	/**
	 * The set of predicates this reference world bases on.
	 */
	private Collection<Predicate> predicates;
	
	/**
	 * The span number of this reference world; saved for efficiency.
	 */
	private Integer spanNumber = null;
	
	/**
	 * Multiplicators for formulas; saved for efficiency.
	 */
	private Map<FolFormula,Integer> multiplicators = new HashMap<FolFormula,Integer>();
	
	/**
	 * Creates a new reference world for the given equivalence classes.
	 * @param equivalenceClasses a set of set of constants.
	 * @param predicates a set of predicates
	 */
	public ReferenceWorld(Collection<? extends Collection<? extends Constant>> equivalenceClasses, Collection<Predicate> predicates){
		super();
		this.assignments = new HashMap<Predicate,InstanceAssignment>();
		this.equivalenceClasses = equivalenceClasses;
		this.predicates = predicates;
	}
	
	/**
	 * Retrieves the number of true instances assigned for the
	 * given predicate and the given equivalence class of constants.
	 * @param predicate a predicate.
	 * @param constants a set of constants.
	 * @return the number of true instances.
	 */
	public Integer get(Predicate predicate, Collection<? extends Constant> constants){
		return this.get(predicate).get(constants);
	}
	
	/**
	 * Returns the span number of this reference world, i.e. the number
	 * of Herbrand interpretations this world refers to.
	 * @return  the span number of this reference world.
	 */
	public Integer spanNumber(){
		if(this.spanNumber != null)
			return this.spanNumber;
		Integer spanNumber = 1;
		for(Predicate p: this.keySet())
			for(Collection<? extends Constant> constants: this.get(p).keySet()){
				spanNumber *= MathTools.binomial(constants.size(), this.get(p, constants));
			}
		this.spanNumber = spanNumber;
		return spanNumber;
	}
	
	/**
	 * Returns the multiplicator of this reference world for the given formula,
	 * i.e. the number of interpretations that map to this world and satisfy the formula.
	 * @param f a fol formula.
	 * @return the multiplicator of this reference world for the given formula.
	 */
	public Integer getMultiplicator(FolFormula f){
		if(this.multiplicators.containsKey(f))
			return this.multiplicators.get(f);
		if(f instanceof Tautology)
			return this.spanNumber();
		// we need f to be in disjunctive normal form
		f = f.toDnf();
		List<FolFormula> disjuncts = new ArrayList<FolFormula>();
		if(!(f instanceof Disjunction))
			disjuncts.add(f);
		else for(RelationalFormula disj: (Disjunction) f)
			disjuncts.add((FolFormula)disj);
		Integer result = 0;
		for(FolFormula g: disjuncts)
			result += this.getMultiplicatorForConjunction(g);
		for(int i = 0; i < disjuncts.size(); i++)
			for(int j = i+1; j < disjuncts.size();j++)
				result -= this.getMultiplicatorForConjunction(disjuncts.get(i).combineWithAnd(disjuncts.get(j)));
		this.multiplicators.put(f, result);
		return result;
	}
	
	/**
	 * Returns the multiplicator of this reference world for the given formula
	 * (which is a conjunction of literals or a single literal),
	 * i.e. the number of interpretations that map to this world and satisfy the formula.
	 * @param f a fol formula (either a conjunction of literals or a single literal).
	 * @return the multiplicator of this reference world for the given formula.
	 */
	private Integer getMultiplicatorForConjunction(FolFormula f){
		Integer result = 1;
		for(Predicate p: this.predicates)
			for(Collection<? extends Constant> equivalenceClass: this.equivalenceClasses)
				result *= MathTools.binomial(
						equivalenceClass.size()-this.getNumberOfOccurences(f, p, equivalenceClass, true)-this.getNumberOfOccurences(f, p, equivalenceClass, false),
						this.get(p, equivalenceClass)-this.getNumberOfOccurences(f, p, equivalenceClass, true));
		log.debug("Determined multiplicator for formula '" + f + "': " + result);
		return result;
	}
	
	/**
	 * Determines the number of occurrences of instances of predicate "p" with a constant in "constants".
	 * The boolean positive determines whether this instance is positive (or negative) and "f" is to be assumed
	 * either a single literal or a conjunction of literals. Furthermore "p" is assumed to be unary.
	 * @param f a fol formula.
	 * @param p a predicate.
	 * @param constants a set of constants.
	 * @param positive whether the instances are to be positive.
	 * @return an integer describing the number of occurrences of instances of predicate "p" with a constant in "constants".
	 */
	private Integer getNumberOfOccurences(FolFormula f, Predicate p, Collection<? extends Constant> constants, boolean positive){
		Integer result = 0;
		if(f instanceof Tautology)
			result = this.spanNumber;
		else if(f instanceof Contradiction)
			result = 0;
		else if(f instanceof FOLAtom){
			FOLAtom a = (FOLAtom) f;
			result = (a.getPredicate().equals(p) && constants.contains(a.getArguments().get(0)) && positive)?(1):(0);
		}else if(f instanceof Negation){
			result = this.getNumberOfOccurences(((Negation)f).getFormula(), p, constants, !positive);
		}else if(f instanceof Conjunction){
			result = 0;
			for(RelationalFormula g: (Conjunction)f ){
				result += this.getNumberOfOccurences((FolFormula)g, p, constants, positive);
			}			
		}else 
			throw new IllegalArgumentException("The given formula is neither a literal nor a conjunction.");
		log.debug("Determined number of occurences of '" + f + "' in reference world '" + this + "' in " + ((positive)?("positive"):("negative")) + " form: " + result );
		return result;
	}
		
	/**
	 * Determines the reference world of the given interpretation wrt. the
	 * given set of equivalence classes and the given set of predicates.. 
	 * @param i a Herbrand interpretation.
	 * @param constants the set of equivalence classes.
	 * @param predicates a set of predicates.
	 * @return a reference world.
	 */
	public static ReferenceWorld getMapping(HerbrandInterpretation i,Set<Predicate> predicates, Set<Set<Constant>> constants){
		ReferenceWorld world = new ReferenceWorld(constants,predicates);
		for(Predicate p: predicates){
			if(p.getArity()>1)
				throw new IllegalArgumentException("Reference worlds only defined for unary predicates.");
			InstanceAssignment ia = new InstanceAssignment(p);
			for(Set<Constant> c: constants){
				Integer value = 0;				
				for(FOLAtom a: i)
					if(a.getPredicate().equals(p))
						if(c.contains(a.getArguments().get(0)))
							value++;				
				ia.put(c, value);
			}
			world.put(p, ia);
		}			
		return world;
	}
	
	/**
	 * Determines the set of all reference worlds wrt. the given set of
	 * predicates and equivalence classes.
	 * @param predicates a set of predicates.
	 * @param constants a set of set of constants.
	 * @return a set of reference worlds.
	 */
	public static Set<ReferenceWorld> enumerateReferenceWorlds(Set<Predicate> predicates, Set<Set<Constant>> constants){
		Set<ReferenceWorld> worlds = new HashSet<ReferenceWorld>();
		Set<Set<InstanceAssignment>> assignments = new HashSet<Set<InstanceAssignment>>();
		for(Predicate p: predicates){
			if(p.getArity()>1)
				throw new IllegalArgumentException("Reference worlds only defined for unary predicates.");
			assignments.add(InstanceAssignment.enumerateInstanceAssignments(p, constants));
		}
		assignments = new HashSet<Set<InstanceAssignment>>(new SetTools<InstanceAssignment>().permutations(assignments));
		for(Set<InstanceAssignment> ias: assignments){
			ReferenceWorld world = new ReferenceWorld(constants,predicates);
			for(InstanceAssignment ia: ias)
				world.put(ia.getPredicate(), ia);
			worlds.add(world);
		}
		return worlds;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.Formula)
	 */
	@Override
	public boolean satisfies(Formula formula) throws IllegalArgumentException {
		if(!(formula instanceof FolFormula)) throw new IllegalArgumentException("Formula " + formula + " is not a first-order formula.");
		FolFormula f = (FolFormula) formula;
		if(!f.isClosed()) throw new IllegalArgumentException("FolFormula " + f + " is not closed.");
		Integer multiplicator = this.getMultiplicator(f);
		return multiplicator > 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.BeliefBase)
	 */
	@Override
	public boolean satisfies(BeliefBase beliefBase)	throws IllegalArgumentException {
		if(!(beliefBase instanceof FolBeliefSet))
			throw new IllegalArgumentException("First-order knowledge base expected.");
		FolBeliefSet folkb = (FolBeliefSet) beliefBase;
		for(Formula f: folkb)
			if(!this.satisfies(f)) return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.assignments.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return this.assignments.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return this.assignments.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<Predicate, InstanceAssignment>> entrySet() {
		return this.assignments.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public InstanceAssignment get(Object key) {
		return this.assignments.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.assignments.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<Predicate> keySet() {
		return this.assignments.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public InstanceAssignment put(Predicate key, InstanceAssignment value) {
		return this.assignments.put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Predicate, ? extends InstanceAssignment> m) {
		this.assignments.putAll(m);		
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public InstanceAssignment remove(Object key) {
		return this.assignments.remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.assignments.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<InstanceAssignment> values() {
		return this.assignments.values();
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.assignments.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((assignments == null) ? 0 : assignments.hashCode());
		result = prime
				* result
				+ ((equivalenceClasses == null) ? 0 : equivalenceClasses
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferenceWorld other = (ReferenceWorld) obj;
		if (assignments == null) {
			if (other.assignments != null)
				return false;
		} else if (!assignments.equals(other.assignments))
			return false;
		if (equivalenceClasses == null) {
			if (other.equivalenceClasses != null)
				return false;
		} else if (!equivalenceClasses.equals(other.equivalenceClasses))
			return false;
		return true;
	}
}

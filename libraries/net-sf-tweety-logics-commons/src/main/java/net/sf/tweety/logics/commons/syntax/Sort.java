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
package net.sf.tweety.logics.commons.syntax;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.interfaces.LogicStructure;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * A sort of first-order logic. i.e. a set of constant objects and a set of variables that represent
 * constants of this sort.
 * Note: The sort names "Thing" and "_Any" are reserved for the default sort and 
 * for the sort that represents all sorts. They should not be used when creating new sorts.
 * 
 * @author Matthias Thimm, Tim Janus, Anna Gessler
 */
public class Sort implements LogicStructure {
	
	/**
	 * The name of the sort
	 */
	private String name;
	
	/**
	 * The set of constants of this sort
	 */
	private Set<Constant> constants;
		
	/**
	 * The set of variables of this sort
	 */
	private Set<Variable> variables;
	
	/**
	 * Default sort for unsorted first-order logics
	 */
	public static final Sort THING = new Sort("Thing");
	
	/**
	 * Default sort for terms of equality/inequality predicates.
	 * Note: This sort is always equal to any other sort.
	 */
	public static final Sort ANY = new Sort("_Any");
	
	/**
	 * Ctor: Creates an empty Sort with the given name.
	 * @param name	The name of the Sort
	 */
	public Sort(String name){
		this.constants = new HashSet<Constant>();
		this.variables = new HashSet<Variable>();
		this.name = name;
	}
	
	/**
	 * Ctor: Creates a Sort with the given name and the given constants.
	 * @param name		The name of the Sort
	 * @param constants	A set of constants which are members of the sort.
	 */
	public Sort(String name, Set<Constant> constants){
		this(name);
		this.constants.addAll(constants);
	}
	
	/**
	 * Copy-Ctor creates a deep copy of the Sort
	 * @param other	The Sort that acts as copy source
	 */
	public Sort(Sort other) {
		this.name = other.name;
		this.constants = new HashSet<Constant>(other.constants);
		this.variables = new HashSet<Variable>(other.variables);
	}
	
	/**
	 * Sorts the set of given terms by their sorts, i.e.
	 * the set of terms is partitioned wrt. their sorts and
	 * set as value of the sort's key.
	 * @param terms a set of terms.
	 * @return a map which maps from sorts to terms of their sort.
	 */
	public static Map<Sort,Set<Term<?>>> sortTerms(Collection<? extends Term<?>> terms){
		Map<Sort,Set<Term<?>>> sorts = new HashMap<Sort,Set<Term<?>>>();		
		for(Term<?> t: terms){
			if(!sorts.containsKey(t.getSort()))
				sorts.put(t.getSort(), new HashSet<Term<?>>());
			sorts.get(t.getSort()).add(t);
		}
		return sorts;
	}
	
	/**
	 * Adds the given term to this sort.
	 * @param term
	 */
	public void add(Term<?> term){
		if(term instanceof Constant){
			this.constants.add((Constant) term);
		}else if(term instanceof Variable){
			this.variables.add((Variable) term);
		}
	}
	
	/**
	 * Removes the given term from this sort.
	 * @param term a term, either a variable or a constant.
	 * @return "true" if the given term has actually been removed.
	 */
	public boolean remove(Term<?> term){
		if(term instanceof Constant)
			return this.constants.remove(term);
		else if(term instanceof Variable)
			return this.variables.remove(term);
		throw new IllegalArgumentException("Term has to be either variable or constant.");
	}
	
	public String getName(){
		return this.name;
	}
	
	@Override
	public String toString(){
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;		
		result = prime * result + ((name == null) ? 0 : name.hashCode());		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (this == Sort.ANY || obj == Sort.ANY)
			return true;		//The sort ANY represents all possible sorts and is 
								//therefore considered equal to any given sort.
		Sort other = (Sort) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;		
		return true;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.addAll(constants);
		reval.addAll(variables);
		return reval;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = null;
		if(cls == Constant.class)
			reval = (Set<C>) Collections.unmodifiableSet(constants);
		else if(cls == Variable.class)
			reval = (Set<C>) Collections.unmodifiableSet(variables);
		else
			reval = new HashSet<C>();
		return reval;
	}

	@Override
	public <C extends Term<?>> boolean containsTermsOfType(Class<C> cls) {
		return !getTerms(cls).isEmpty();
	}
	
	@Override
	public Sort clone() {
		return new Sort(this);
	}
}

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
package org.tweetyproject.arg.aspic.syntax;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.MapTools;
import org.tweetyproject.commons.util.rules.Rule;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This stands for an inference rule or for a premise if premises has length 0.
 * If this is a premise and defeasible it is an ordinary premise else it is an axiom.
 *
 * @param <T> is the type of the language that the ASPIC theory's rules range over
 * @author Nils Geilen
 */
public abstract class InferenceRule<T extends Invertable> implements Rule<T, T>, ComplexLogicalFormula {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + ((premises == null) ? 0 : premises.hashCode());
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
		InferenceRule<?> other = (InferenceRule<?>) obj;
		if (conclusion == null) {
			if (other.conclusion != null)
				return false;
		} else if (!conclusion.equals(other.conclusion))
			return false;
		if (premises == null) {
			return other.premises == null;
		} else return premises.equals(other.premises);
	}
	/**
	 * The rule's conclusion
	 */
	private T conclusion;
	/**
	 * The rule's premises
	 */
	private Collection<T> premises = new ArrayList<>();
	
	/**
	 * Identifying name. used e.g. for formula generation
	 */
	private String name;
	
	/**
	 * Creates an empty instance
	 */
	public InferenceRule(){
		
	}
	
	/**
	 * Constructs a new inference rule of rule p -&gt; c if strict or p =&gt; c else
	 * @param conclusion	^= p
	 * @param premise	^= c
	 */
	public InferenceRule(T conclusion, Collection<T> premise) {
		this.conclusion = conclusion;
		this.premises = premise;
	}

	/**
	 * @return	true iff this rule is defeasible
	 */
	public abstract boolean isDefeasible();
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aspic.syntax.AspicWord#toString()
	 */
	@Override
	public String toString() {
		StringWriter sw =  new StringWriter();
		//sw.write("(");
		if(getName()!=null)
			sw.write(getName()+": ");
		Iterator<T> i = premises.iterator();
		if(i.hasNext())
			sw.write(i.next().toString());		
		while(i.hasNext())
			sw.write(", "+i.next());		
		if(isDefeasible())
			sw.write(" => ");
		else
			sw.write(" -> ");
		sw.write(conclusion+"");
		//	sw.write(")");
		return sw.toString();
		
	}

	/**
	 * @return	a strict instance of this rule
	 */
	public StrictInferenceRule<T> toStrict() {
		StrictInferenceRule<T> result = new StrictInferenceRule<>(conclusion, premises);
		result.setName(name);
		return result;
	}
	
	/**
	 * @return	a defeasible instance of this rule
	 */
	public DefeasibleInferenceRule<T> toDefeasible() {
		DefeasibleInferenceRule<T> result = new DefeasibleInferenceRule<>(conclusion, premises);
		result.setName(name);
		return result;
	}
	
	/**
	 * Returns the name if it has one, else it returns the hashcode
	 * @return	an identifier for this rule
	 */
	public String getIdentifier() {
		if (getName() == null)
			return ""+hashCode();
		else
			return getName();
	}
	
	/**
	 * Returns the name
	 * @return	this rul's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name
	 * @param name	new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.util.rules.Rule#isFact()
	 */
	@Override
	public boolean isFact() {
		return premises.isEmpty() && conclusion != null;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.util.rules.Rule#isConstraint()
	 */
	@Override
	public boolean isConstraint() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.util.rules.Rule#setConclusion(org.tweetyproject.commons.Formula)
	 */
	@Override
	public void setConclusion(T conclusion) {
		this.conclusion = conclusion;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.util.rules.Rule#addPremise(org.tweetyproject.commons.Formula)
	 */
	@Override
	public void addPremise(T premise) {
		this.premises.add(premise);	
	}
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.util.rules.Rule#addPremises(java.util.Collection)
	 */
	@Override
	public void addPremises(Collection<? extends T> premises) {
		this.premises.addAll(premises);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aspic.syntax.AspicWord#getSignature()
	 */
	@Override
	public Signature getSignature() {
		Signature sig = conclusion.getSignature();
		for (T w: premises)
			sig.addSignature(w.getSignature());
		return sig;
	}
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends T> getPremise() {
		return premises;
	}
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.util.rules.Rule#getConclusion()
	 */
	@Override
	public T getConclusion() {
		return conclusion;
	}
	/**
	 * 
	 * @param constants constans
	 * @return all ground instances
	 */
	public Set<InferenceRule<T>> allGroundInstances(Set<Constant> constants) {
		Set<Map<Variable, Term<?>>> maps = this.allSubstitutions(constants);
		Set<InferenceRule<T>> result = new HashSet<>();
		for (Map<Variable, Term<?>> map : maps)
			result.add(this.substitute(map));
		return result;
	}
	
	@Override
	public abstract InferenceRule<T> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;

	@Override
	public InferenceRule<T> substitute(Map<? extends Term<?>, ? extends Term<?>> map) throws IllegalArgumentException {
		InferenceRule<T> f = this.clone();
		for (Term<?> v : map.keySet())
			f = f.substitute(v, map.get(v));
		return f;
	}
	
	public abstract InferenceRule<T> clone();
	
	/**
	 * Computes all possible substitutions, i.e. maps from variables to terms, of
	 * unbound variables of this formula's inner fol formulas to terms in "terms".
	 * 
	 * @param terms a set of terms.
	 * @return a set of maps from variables to terms.
	 * @throws IllegalArgumentException if there is an unbound variable in this
	 *                                  formula for which there is no term in
	 *                                  "terms" with the same sort.
	 */
	public Set<Map<Variable, Term<?>>> allSubstitutions(Collection<? extends Term<?>> terms)
			throws IllegalArgumentException {
		Set<Variable> variables = this.getUnboundVariables();
		// partition variables by sorts
		Map<Sort, Set<Variable>> sorts_variables = new HashMap<>();
		for (Variable v : variables) {
			if (!sorts_variables.containsKey(v.getSort()))
				sorts_variables.put(v.getSort(), new HashSet<>());
			sorts_variables.get(v.getSort()).add(v);
		}
		// partition terms by sorts
		Map<Sort, Set<Term<?>>> sorts_terms = Sort.sortTerms(terms);
		// combine the partitions
		Map<Set<Variable>, Set<Term<?>>> relations = new HashMap<>();
		for (Sort s : sorts_variables.keySet()) {
			if (!sorts_terms.containsKey(s))
				throw new IllegalArgumentException("There is no term of sort " + s + " to substitute.");
			relations.put(sorts_variables.get(s), sorts_terms.get(s));
		}
		return new MapTools<Variable, Term<?>>().allMaps(relations);
	}
	/**
	 * 
	 * @return the unbound variables
	 */
	public Set<Variable> getUnboundVariables() {
		return this.getTerms(Variable.class);
	}
	
	@Override
	public InferenceRule<T> exchange(Term<?> v, Term<?> t) throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isGround() {
		return this.getTerms(Variable.class).isEmpty();
	}

	@Override
	public boolean isWellFormed() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends Atom> getAtoms() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return Predicate.class;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public <C extends Term<?>> boolean containsTermsOfType(Class<C> cls) {
		return !getTerms(cls).isEmpty();
	}
	
	@Override
	public Set<? extends Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			predicates.addAll(((RelationalFormula) this.getPremise()).getPredicates());
			predicates.addAll(this.getConclusion().getPredicates());
		}
		return predicates;
	}
	
	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			for (T x : this.getPremise())
				reval.addAll(((RelationalFormula) x).getTerms());
			reval.addAll(((RelationalFormula) this.getConclusion()).getTerms());
		}
		return reval;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			for (Term<?> arg : this.getTerms()) {
				if (arg.getClass().equals(cls)) {
					@SuppressWarnings("unchecked")
					C castArg = (C) arg;
					reval.add(castArg);
				}
				// recursively add terms for all inner functional terms
				reval.addAll(arg.getTerms(cls));
			}
		}
		return reval;
	}
	
}

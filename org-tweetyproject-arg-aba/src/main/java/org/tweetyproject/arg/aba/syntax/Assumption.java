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
package org.tweetyproject.arg.aba.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * This class models an assumption of an ABA theory.
 * 
 * @param <T> is the type of the language that the ABA theory's rules range over
 * @author Nils Geilen (geilenn@uni-koblenz.de) 
 */
public class Assumption<T extends Formula> extends AbaRule<T> {
	/**
	 * The assumed formula
	 */
	T assumption;

	/**
	 * Creates a new assumption
	 * 
	 * @param assumption the assumed formula
	 */
	public Assumption(T assumption) {
		super();
		this.assumption = assumption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.util.rules.Rule#isFact()
	 */
	@Override
	public boolean isFact() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.util.rules.Rule#isConstraint()
	 */
	@Override
	public boolean isConstraint() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.commons.util.rules.Rule#setConclusion(org.tweetyproject.commons.
	 * Formula)
	 */
	@Override
	public void setConclusion(T conclusion) {
		assumption = conclusion;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.util.rules.Rule#addPremise(org.tweetyproject.commons.
	 * Formula)
	 */
	@Override
	public void addPremise(T premise) {
		throw new RuntimeException("Cannot add Premise to Assumtion");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.util.rules.Rule#addPremises(java.util.Collection)
	 */
	@Override
	public void addPremises(Collection<? extends T> premises) {
		throw new RuntimeException("Cannot add Premise to Assumtion");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.util.rules.Rule#getSignature()
	 */
	@Override
	public Signature getSignature() {
		return assumption.getSignature();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends T> getPremise() {
		return new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.util.rules.Rule#getConclusion()
	 */
	@Override
	public T getConclusion() {
		return assumption;
	}

	/**
	 * Returns the inner formula of this assumption.
	 * 
	 * @return the inner formula of this assumption.
	 */
	public T getFormula() {
		return assumption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.aba.syntax.ABARule#isAssumption()
	 */
	@Override
	public boolean isAssumption() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return assumption.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assumption == null) ? 0 : assumption.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assumption other = (Assumption) obj;
		if (assumption == null) {
			if (other.assumption != null)
				return false;
		} else if (!assumption.equals(other.assumption))
			return false;
		return true;
	}

	@Override
	public Set<? extends Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		Signature sig = this.getSignature();
		if (sig instanceof FolSignature) {
			predicates.addAll(((RelationalFormula) assumption).getPredicates());
		}
		return predicates;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		Signature sig = assumption.getSignature();
		if (sig instanceof FolSignature) {
			reval.addAll(((RelationalFormula) assumption).getTerms());
		}
		return reval;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		Signature sig = assumption.getSignature();
		if (sig instanceof FolSignature) {
			for (Term<?> arg : ((RelationalFormula) assumption).getTerms()) {
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

	@Override
	public AbaElement<T> clone() {
		return new Assumption<T>(this.assumption);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Assumption<T> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		Assumption<T> a = new Assumption<T>(assumption);
		Signature sig = assumption.getSignature();
		if (sig instanceof FolSignature) {
			a = new Assumption<T>((T) (((RelationalFormula) assumption).substitute(v, t)));
		}
		return a;
	}

}

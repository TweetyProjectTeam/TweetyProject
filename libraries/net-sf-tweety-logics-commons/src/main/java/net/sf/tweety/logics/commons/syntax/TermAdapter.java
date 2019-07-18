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

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * Abstract base class implementing the substitute(), getSort(), getTerms(), and
 * containsTermsOfType() methods in a way it is useful for terms.
 * 
 * @author Tim Janus, Matthias Thimm
 *
 * @param <T>	The type of the value saved in the term, this might be a string
 * 				if the term is a constant or a variable, or it might be an integer
 * 				if the term is a number term.
 */
public abstract class TermAdapter<T> implements Term<T>{

	protected T value;
	
	/** the type of the term */
	private Sort sort;
	
	/**
	 * Default-Ctor: Creates an TermAdapter with the Sort "Thing"
	 * @param value the value of this term
	 */
	public TermAdapter(T value) {
		this(value, Sort.THING);
	}
	
	/**
	 * Ctor: Creates a TermAdapter with the given Sort
	 * @param value the value of this term
	 * @param sort	The Sort (Type) of the TermAdapter instance
	 */
	public TermAdapter(T value, Sort sort) {
		this.sort = sort;
		set(value);
		sort.add(this);
	}
	
	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.add(this);
		return reval;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		if(this.getClass().equals(cls)) {
			@SuppressWarnings("unchecked")
			C castThis = (C) this;
			reval.add(castThis);
		}
		return reval;
	}

	@Override
	public <C extends Term<?>> boolean containsTermsOfType(Class<C> cls) {
		return !getTerms(cls).isEmpty();
	}

	@Override
	public Term<?> substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		if(!v.getSort().equals(t.getSort()))
			throw new IllegalArgumentException("Cannot replace " + v + " by " + t + " because " + v +
					" is of sort " + v.getSort() + " while " + t + " is of sort " + t.getSort() + ".");
		if(v.equals(this)) return t;
		return this;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
		if(get() != null) {
			result += get().hashCode();
		}
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
		Term<?> other = (Term<?>) obj;
		if (sort == null) {
			if (other.getSort() != null)
				return false;
		} else if (!sort.equals(other.getSort()))
			return false;
		return get().equals(other.get());
	}
	
	@Override
	public abstract TermAdapter<?> clone();

	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
	@Override
	public void set(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		return this.value;
	}
}

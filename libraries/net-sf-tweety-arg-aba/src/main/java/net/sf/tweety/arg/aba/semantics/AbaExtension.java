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
package net.sf.tweety.arg.aba.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.arg.aba.syntax.ABATheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.AbstractInterpretation;
import net.sf.tweety.commons.Formula;

/**
 * An set of assumptions.
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas
 */
public class AbaExtension<T extends Formula> extends AbstractInterpretation<ABATheory<T>,Assumption<T>> implements Collection<Assumption<T>> {

	/** the assumptions */
	private Collection<Assumption<T>> assumptions;

	/**
	 * Default constructor
	 */
	public AbaExtension() {
		this.assumptions = new HashSet<>();
	}
	
	/**
	 * Creates a new extension with the given assumptions.
	 * @param assumptions
	 */
	public AbaExtension(Collection<Assumption<T>> assumptions) {
		this();
		this.assumptions.addAll(assumptions);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Interpretation#satisfies(net.sf.tweety.commons.Formula)
	 */
	@Override
	public boolean satisfies(Assumption<T> formula) throws IllegalArgumentException {
		return this.assumptions.contains(formula);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Interpretation#satisfies(net.sf.tweety.commons.BeliefBase)
	 */
	@Override
	public boolean satisfies(ABATheory<T> beliefBase) throws IllegalArgumentException {
		throw new IllegalArgumentException("Satisfaction of belief bases by extensions is undefined.");
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return this.assumptions.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.assumptions.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.assumptions.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<Assumption<T>> iterator() {
		return this.assumptions.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.assumptions.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	@Override
	public <R> R[] toArray(R[] a) {
		return this.assumptions.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(Assumption<T> e) {
		return this.assumptions.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.assumptions.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.assumptions.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends Assumption<T>> c) {
		return this.assumptions.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.assumptions.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.assumptions.retainAll(c);
	}

	@Override
	public void clear() {
		this.assumptions.clear();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assumptions == null) ? 0 : assumptions.hashCode());
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
		AbaExtension<?> other = (AbaExtension<?>) obj;
		if (assumptions == null) {
			if (other.assumptions != null)
				return false;
		} else if (!assumptions.equals(other.assumptions))
			return false;
		return true;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.assumptions.toString();
	}
}

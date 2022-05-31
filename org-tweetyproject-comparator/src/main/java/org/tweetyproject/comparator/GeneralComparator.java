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
package org.tweetyproject.comparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import org.tweetyproject.commons.AbstractInterpretation;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;



/**
 * This abstract class is the common ancestor for semantical approaches to
 * ranking, i.e. relations that allow a more fine-grained comparison by
 * e.g. utilizing numerical values for arguments.
 * 
 * @author Matthias Thimm
 */
public abstract class GeneralComparator<T extends Formula, R extends BeliefBase> extends AbstractInterpretation<R,T> implements Comparator<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(T arg0, T arg1) {
		// Recall that if arg0 is less than arg1 (arg0 < arg1) then
		// arg0 is more preferred than arg1
		if (this.isStrictlyLessAcceptableThan(arg0, arg1))
			return -1;
		if (this.isStrictlyMoreAcceptableThan(arg0, arg1))
			return 1;
		return 0;
	}

	/**
	 * Returns "true" iff a is strictly more acceptable than b, i.e. a &lt; b 
	 * 
	 * @param a some comparable element
	 * @param b some comparable element
	 * @return "true" iff a is strictly more acceptable than b
	 */
	public boolean isStrictlyMoreAcceptableThan(T a, T b) {
		return !this.isIncomparable(a, b) && !this.isStrictlyLessOrEquallyAcceptableThan(a, b);
	}

	/**
	 * Returns "true" iff a is strictly less acceptable than b, i.e. a &gt; b 
	 * 
	 * @param a some comparable element
	 * @param b some comparable element
	 * @return "true" iff a is strictly less acceptable than b
	 */
	public boolean isStrictlyLessAcceptableThan(T a, T b) {
		return !this.isIncomparable(a, b) && !this.isStrictlyLessOrEquallyAcceptableThan(b, a);
		
	}

	/**
	 * Returns "true" iff a is strictly more acceptable than b or a is equally
	 * acceptable as b, i.e. a &lt;= b (or a ~ b)
	 * 
	 * @param a some comparable element
	 * @param b some comparable element
	 * @return "true" iff a is strictly more acceptable than b or a is equally
	 *         acceptable as b, "false" otherwise or if a and b are incomparable
	 */
	public boolean isStrictlyMoreOrEquallyAcceptableThan(T a, T b) {
		return this.isStrictlyLessOrEquallyAcceptableThan(b, a) || this.isEquallyAcceptableThan(a, b);
	}

	/**
	 * Returns "true" iff a is equally acceptable as b, 
	 * i.e. a = b (or a ~ b) 
	 * 
	 * @param a some comparable element
	 * @param b some comparable element
	 * @return "true" iff a is equally acceptable as b, "false" otherwise or if a and b are incomparable
	 */
	public boolean isEquallyAcceptableThan(T a, T b) {
		return this.isStrictlyLessOrEquallyAcceptableThan(a, b) && this.isStrictlyLessOrEquallyAcceptableThan(b, a);
	}

	/**
	 * Returns the set of all comparable elements a from the given set that are maximally
	 * accepted, i.e. where there is no other comparable element that is strictly more
	 * acceptable.
	 * 
	 * @param args a set of comparable element
	 * @return the set of all comparable element a that are maximally accepted
	 */
	public Collection<T> getMaximallyAcceptedArguments(Collection<T> args) {
		Collection<T> result = new HashSet<T>();
		for (T a : args) {
			boolean isMaximal = true;
			for (T b : args)
				if (this.isStrictlyMoreAcceptableThan(b, a)) {
					isMaximal = false;
					break;
				}
			if (isMaximal)
				result.add(a);
		}
		return result;
	}

	/**
	 * Returns the set of all comparable elements a from the given set that are minimally
	 * accepted, i.e. where there is no other comparable element that is strictly less
	 * acceptable.
	 * 
	 * @param args a set of comparable elements
	 * @return the set of all comparable elements a that are minimally accepted
	 */
	public Collection<T> getMinimallyAcceptedArguments(Collection<T> args) {
		Collection<T> result = new HashSet<T>();
		for (T a : args) {
			boolean isMinimal = true;
			for (T b : args)
				if (this.isStrictlyLessAcceptableThan(b, a)) {
					isMinimal = false;
					break;
				}
			if (isMinimal)
				result.add(a);
		}
		return result;
	}

	/**
	 * Checks whether this ranking is equivalent to the other one wrt. the given set
	 * of comparable elements.
	 * 
	 * @param other some ranking
	 * @param args  some comparable elements
	 * @return "true" if both rankings are equivalent.
	 */
	public boolean isEquivalent(GeneralComparator<T,R> other, Collection<T> args) {
		for (T a : args)
			for (T b : args) {
				if (this.isStrictlyLessOrEquallyAcceptableThan(a, b)
						&& !other.isStrictlyLessOrEquallyAcceptableThan(a, b))
					return false;
				if (!this.isStrictlyLessOrEquallyAcceptableThan(a, b)
						&& other.isStrictlyLessOrEquallyAcceptableThan(a, b))
					return false;
			}
		return true;
	}

	/**
	 * Returns "true" iff a is strictly less acceptable than b or a is equally
	 * acceptable as b or a and b are not comparable, i.e. a &gt;= b (or a ~ b)
	 * 
	 * @param a some comparable element
	 * @param b some comparable element
	 * @return "true" iff a is strictly less acceptable than b or a is equally
	 *         acceptable as b
	 */
	public abstract boolean isStrictlyLessOrEquallyAcceptableThan(T a, T b);

	/**
	 * Returns "true" iff a and b are incomparable (i.e. this ranking is a partial
	 * ranking).
	 * 
	 * @param a comparable element
	 * @param b comparable element
	 * @return "true" iff a and b are incomparable
	 */
	public abstract boolean isIncomparable(T a, T b);

	/**
	 * @return true if this ranking contains incomparable arguments, false otherwise
	 */
	public abstract boolean containsIncomparableArguments();


}

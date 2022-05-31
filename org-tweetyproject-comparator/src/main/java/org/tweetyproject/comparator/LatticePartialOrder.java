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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;

/**
 * This class models comparable element ranking by representing the acceptability of
 * comparable elements in a graph-based structure.
 * 
 * @author Matthias Thimm
 * @param <T>
 *
 */
public class LatticePartialOrder<T extends Formula, R extends BeliefBase> extends GeneralComparator<T, R> {

	/** The actual order of comparable elements */
	private Order<T> order;
	private Collection<T> args;
	
	public Order<T> getOrder(){
		return this.order;
	}
	public Collection<T> getArgs(){
		return this.args;
	}

	/**
	 * Creates a new argument ranking with the given comparable elements which are initially
	 * all incomparable.
	 * 
	 * @param args a set of arguments
	 */
	public LatticePartialOrder(Collection<T> args) {
		this.order = new Order<>(args);
		this.args = args;
	}

	/**
	 * Defines comparable element "a" to be strictly less or equally acceptable than
	 * comparable element "b".
	 * 
	 * @param a some comparable element
	 * @param b some comparable element
	 */
	public void setStrictlyLessOrEquallyAcceptableThan(T a, T b) {
		this.order.setOrderedBefore(a, b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.dung.semantics.ArgumentRanking#
	 * isStrictlyLessOrEquallyAcceptableThan(org.tweetyproject.arg.dung.syntax.Argument,
	 * org.tweetyproject.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean isStrictlyLessOrEquallyAcceptableThan(T a, T b) {
		return !isIncomparable(a, b) && this.order.isOrderedBefore(a, b);
	}

	@Override
	public boolean isIncomparable(T a, T b) {
		return !this.order.isComparable(a, b);
	}

	@Override
	public boolean containsIncomparableArguments() {
		for (T a : this.order.getElements()) 
			for (T b : this.order.getElements()) 
				if (this.isIncomparable(a, b)) 
					return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.dung.semantics.AbstractArgumentationInterpretation#
	 * getArgumentsOfStatus(org.tweetyproject.arg.dung.semantics.ArgumentStatus)
	 */

	
	public boolean isSame(LatticePartialOrder<T,R> ra) {
		if(!this.getArgs().equals(ra.getArgs()))
			return false;
		for(T a: this.getArgs()) {
			for(T b: this.getArgs()) {
				if(ra.compare(a, b) != this.compare(a,  b))
					return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.dung.semantics.AbstractArgumentationInterpretation#toString
	 * ()
	 */
	@Override
	public String toString() {
		String result = "[";
		if (!this.containsIncomparableArguments()) {
			List<T> args = new ArrayList<T>(this.order.getElements());
			Collections.sort(args, new LatticeComparator(this));
			for (int i = args.size() - 1; i > 0; i--) {
				T a = args.get(i);
				T b = args.get(i - 1);
				if (i == args.size() - 1)
					result += a;
				if (this.isEquallyAcceptableThan(a, b))
					result += " = " + b;
				else
					result += " > " + b;
			}
		} else {
			String incomparables = "";
			for (T a : this.order.getElements()) {
				for (T b : this.order.getElements()) {
					if (this.isStrictlyMoreAcceptableThan(a, b))
						result += "" + a + ">" + b + ", ";
					else if (this.isEquallyAcceptableThan(a, b) && !a.equals(b))
						result += "" + a + "=" + b + ", ";
					else if (this.isIncomparable(a, b))
						incomparables += "" + a + "?" + b + ", ";
				}
			}
			result += incomparables; // print incomparable arguments last
			if (result.length() > 1)
				result = result.substring(0, result.length() - 2);
		}
		return result += "]";
	}

	/**
	 * Comparator for comparing arguments on the basis of a given
	 * LatticeArgumentRanking. Fails for incomparable arguments.
	 * 
	 * @author Anna Gessler
	 *
	 */
	private class LatticeComparator implements Comparator<T> {
		/**
		 * The ranking that is associated with this comparator.
		 */
		private LatticePartialOrder<T,R> order;

		public LatticeComparator(LatticePartialOrder<T,R> order) {
			this.order = order;
		}

		@Override
		public int compare(T a, T b) {
			if (order.isIncomparable(a, b))
				throw new IllegalArgumentException("Incomparable arguments " + a + ", " + b);
			else if (order.isStrictlyLessAcceptableThan(a, b))
				return -1;
			else if (order.isStrictlyMoreAcceptableThan(a, b))
				return 1;
			else
				return 0;
		}
	}

	@Override
	public boolean satisfies(T formula) throws IllegalArgumentException {

		return false;
	}
	@Override
	public boolean satisfies(R beliefBase) throws IllegalArgumentException {

		return false;
	}



}

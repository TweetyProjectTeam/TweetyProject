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
package org.tweetyproject.arg.rankings.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.graphs.orders.Order;

/**
 * This class models argument ranking by representing the acceptability of
 * arguments in a graph-based structure.
 * 
 * @author Matthias Thimm
 *
 */
public class LatticeArgumentRanking extends ArgumentRanking {

	/** The actual order of arguments */
	private Order<Argument> order;
	private Collection<Argument> args;
	
	public Order<Argument> getOrder(){
		return this.order;
	}
	public Collection<Argument> getArgs(){
		return this.args;
	}

	/**
	 * Creates a new argument ranking with the given arguments which are initially
	 * all incomparable.
	 * 
	 * @param args a set of arguments
	 */
	public LatticeArgumentRanking(Collection<Argument> args) {
		this.order = new Order<>(args);
		this.args = args;
	}

	/**
	 * Defines argument "a" to be strictly less or equally acceptable than
	 * argument "b".
	 * 
	 * @param a some argument
	 * @param b some argument
	 */
	public void setStrictlyLessOrEquallyAcceptableThan(Argument a, Argument b) {
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
	public boolean isStrictlyLessOrEquallyAcceptableThan(Argument a, Argument b) {
		return !isIncomparable(a, b) && this.order.isOrderedBefore(a, b);
	}

	@Override
	public boolean isIncomparable(Argument a, Argument b) {
		return !this.order.isComparable(a, b);
	}

	@Override
	public boolean containsIncomparableArguments() {
		for (Argument a : this.order.getElements()) 
			for (Argument b : this.order.getElements()) 
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
	@Override
	public Extension getArgumentsOfStatus(ArgumentStatus status) {
		if (status.equals(ArgumentStatus.IN))
			return new Extension(this.getMaximallyAcceptedArguments(this.order.getElements()));
		if (status.equals(ArgumentStatus.OUT))
			return new Extension(this.getMinimallyAcceptedArguments(this.order.getElements()));
		Collection<Argument> undec = new HashSet<>(this.order.getElements());
		undec.removeAll(this.getMaximallyAcceptedArguments(this.order.getElements()));
		undec.removeAll(this.getMinimallyAcceptedArguments(this.order.getElements()));
		return new Extension(undec);
	}
	
	public boolean isSame(LatticeArgumentRanking ra) {
		if(!this.getArgs().equals(ra.getArgs()))
			return false;
		for(Argument a: this.getArgs()) {
			for(Argument b: this.getArgs()) {
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
			List<Argument> args = new ArrayList<Argument>(this.order.getElements());
			Collections.sort(args, new LatticeComparator(this));
			for (int i = args.size() - 1; i > 0; i--) {
				Argument a = args.get(i);
				Argument b = args.get(i - 1);
				if (i == args.size() - 1)
					result += a;
				if (this.isEquallyAcceptableThan(a, b))
					result += " = " + b;
				else
					result += " > " + b;
			}
		} else {
			String incomparables = "";
			for (Argument a : this.order.getElements()) {
				for (Argument b : this.order.getElements()) {
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
	private class LatticeComparator implements Comparator<Argument> {
		/**
		 * The ranking that is associated with this comparator.
		 */
		private LatticeArgumentRanking order;

		public LatticeComparator(LatticeArgumentRanking order) {
			this.order = order;
		}

		@Override
		public int compare(Argument a, Argument b) {
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

}

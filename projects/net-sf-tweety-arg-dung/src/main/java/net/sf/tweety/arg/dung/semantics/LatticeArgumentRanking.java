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
package net.sf.tweety.arg.dung.semantics;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.graphs.orders.Order;

/**
 * This class models argument ranking by representing the acceptability of arguments in 
 * a graph-based structure.
 * 
 * @author Matthias Thimm
 *
 */
public class LatticeArgumentRanking extends ArgumentRanking {

	/** The actual order */
	private Order<Argument> order;
	
	/**
	 * Creates a new argument ranking with the given arguments which 
	 * are initially all incomparable.
	 * @param args
	 */
	public LatticeArgumentRanking(Collection<Argument> args){
		this.order = new Order<>(args);
	}
	
	/**
	 * Defines "a" to be strictly less or equally acceptable than "b".
	 * @param a some argument
	 * @param b some argument
	 */
	public void setStrictlyLessOrEquallyAcceptableThan(Argument a, Argument b){
		this.order.setOrderedBefore(a, b);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.semantics.ArgumentRanking#isStrictlyLessOrEquallyAcceptableThan(net.sf.tweety.arg.dung.syntax.Argument, net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean isStrictlyLessOrEquallyAcceptableThan(Argument a, Argument b) {
		return this.order.isOrderedBefore(a, b);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.semantics.AbstractArgumentationInterpretation#getArgumentsOfStatus(net.sf.tweety.arg.dung.semantics.ArgumentStatus)
	 */
	@Override
	public Extension getArgumentsOfStatus(ArgumentStatus status) {
		if(status.equals(ArgumentStatus.IN))
			return new Extension(this.getMaximallyAcceptedArguments(this.order.getElements()));
		if(status.equals(ArgumentStatus.OUT))
			return new Extension(this.getMinimallyAcceptedArguments(this.order.getElements()));
		Collection<Argument> undec = new HashSet<>(this.order.getElements());
		undec.removeAll(this.getMaximallyAcceptedArguments(this.order.getElements()));
		undec.removeAll(this.getMinimallyAcceptedArguments(this.order.getElements()));
		return new Extension(undec);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.semantics.AbstractArgumentationInterpretation#toString()
	 */
	@Override
	public String toString() {
		return this.order.toString();
	}

}

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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.syntax.adf;

import java.util.Set;

import net.sf.tweety.arg.adf.semantics.link.Link;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;

/**
 * An ADF without arguments, acceptance conditions or links.
 * 
 * @author Mathias Hofer
 *
 */
enum EmptyAbstractDialecticalFramework implements AbstractDialecticalFramework {
	INSTANCE;

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#getArguments()
	 */
	@Override
	public Set<Argument> getArguments() {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#getLinks()
	 */
	@Override
	public Set<Link> links() {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#link(net.sf.tweety.arg.adf.syntax.Argument, net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Link link(Argument parent, Argument child) {
		throw new IllegalArgumentException();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#linksFromParents(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksTo(Argument child) {
		return Set.of();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#size()
	 */
	@Override
	public int size() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#linksToChildren(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Link> linksFrom(Argument parent) {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#parents(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Argument> parents(Argument child) {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#children(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Set<Argument> children(Argument parent) {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#kBipolar(int)
	 */
	@Override
	public int kBipolar() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#getAcceptanceCondition(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public AcceptanceCondition getAcceptanceCondition(Argument argument) {
		throw new IllegalArgumentException();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#incomingDegree(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public int incomingDegree(Argument arg) {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#outgoingDegree(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public int outgoingDegree(Argument arg) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework#contains(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public boolean contains(Argument arg) {
		return false;
	}

}

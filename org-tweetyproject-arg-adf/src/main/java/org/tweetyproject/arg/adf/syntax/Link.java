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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.syntax;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.graphs.DirectedEdge;

/**
 * A link (parent, child) of an ADF: the parent occurs in the acceptance
 * condition of the child. Links carry no type; see {@link LinkType}.
 *
 * @author Lars Bengel
 */
public class Link extends DirectedEdge<Argument> {

	/**
	 * Creates a new link.
	 *
	 * @param parent the argument occurring in the condition of the child
	 * @param child  the argument whose condition mentions the parent
	 */
	public Link(Argument parent, Argument child) {
		super(parent, child);
	}

	/**
	 * @return the parent, i.e. the argument occurring in the condition
	 */
	public Argument getParent() {
		return getNodeA();
	}

	/**
	 * @return the child, i.e. the argument whose condition is concerned
	 */
	public Argument getChild() {
		return getNodeB();
	}

}

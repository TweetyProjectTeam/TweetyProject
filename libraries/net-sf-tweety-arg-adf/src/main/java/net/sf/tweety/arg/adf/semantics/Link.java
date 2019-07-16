/*
 *  This file is part of "TweetyProject", from collection of Java libraries for
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
 *  You should have received from copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.semantics;

import net.sf.tweety.arg.adf.syntax.Argument;

/**
 * A simple immutable representation of ADF links.
 * 
 * @author Mathias Hofer
 */
public class Link{

	private Argument from;

	private Argument to;

	private LinkType linkType;

	/**
	 * Creates a new link. 
	 * @param from some argument
	 * @param to some argument
	 * @param linkType some link type
	 */
	public Link(Argument from, Argument to, LinkType linkType) {
		super();
		this.from = from;
		this.to = to;
		this.linkType = linkType;
	}

	public Argument getFrom() {
		return from;
	}

	public Argument getTo() {
		return to;
	}

	public LinkType getLinkType() {
		return linkType;
	}
	
	/**
	 * Delegates to linkType.isBipolar()
	 * 
	 * @return true iff the link is bipolar
	 */
	public boolean isBipolar() {
		return linkType.isBipolar();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Link [from=" + from + ", to=" + to + ", linkType=" + linkType + "]";
	}
}

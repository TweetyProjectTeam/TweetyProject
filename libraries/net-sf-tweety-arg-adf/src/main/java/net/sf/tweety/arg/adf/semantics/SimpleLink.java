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

import java.util.Objects;

import net.sf.tweety.arg.adf.syntax.Argument;

/**
 * A simple immutable representation of ADF links.
 * 
 * @author Mathias Hofer
 */
public final class SimpleLink implements Link {

	private final Argument from;

	private final Argument to;

	private final LinkType linkType;

	/**
	 * @param from
	 * @param to
	 * @param linkType
	 */
	public SimpleLink(Argument from, Argument to, LinkType linkType) {
		this.from = Objects.requireNonNull(from);
		this.to = Objects.requireNonNull(to);
		this.linkType = Objects.requireNonNull(linkType);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return linkType + "(" + from + "," + to + ")";
	}
}

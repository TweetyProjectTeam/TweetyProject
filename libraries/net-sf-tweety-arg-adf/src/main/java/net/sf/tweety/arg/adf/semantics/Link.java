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
	 * @param from
	 * @param to
	 * @param attacking
	 * @param supporting
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
	
	public boolean isAttacking() {
		return linkType == LinkType.ATTACKING;
	}
	
	public boolean isSupporting() {
		return linkType == LinkType.SUPPORTING;
	}
	
	public boolean isDependent() {
		return linkType == LinkType.DEPENDENT;
	}
	
	public boolean isRedundant() {
		return linkType == LinkType.REDUNDANT;
	}
	
	/**
	 * Delegates to linkType.isBipolar()
	 * 
	 * @return
	 */
	public boolean isBipolar() {
		return linkType != LinkType.DEPENDENT;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Link [from=" + from + ", to=" + to + ", linkType=" + linkType + "]";
	}
}

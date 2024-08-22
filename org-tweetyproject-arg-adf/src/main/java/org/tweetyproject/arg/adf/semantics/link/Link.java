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
package org.tweetyproject.arg.adf.semantics.link;

import org.tweetyproject.arg.adf.syntax.Argument;

/**
 * Link interface
 * @author Mathias Hofer
 *
 */
public interface Link {
	/**
	 *
	 * @param from from
	 * @param to to
	 * @param type type
	 * @return Link of
	 */
	static Link of(Argument from, Argument to, LinkType type) {
		return new SimpleLink(from, to, type);
	}
	/**
	 *
	 * @return Argument getFrom
	 */
	Argument getFrom();
	/**
	 *
	 * @return Argument getTo
	 */
	Argument getTo();
	/**
	 *
	 * @return LinkType getTyp
	 */
	LinkType getType();

}

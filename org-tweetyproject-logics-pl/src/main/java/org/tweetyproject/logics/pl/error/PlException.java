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
package org.tweetyproject.logics.pl.error;

import org.tweetyproject.logics.commons.error.LanguageException;

/**
 * An Exception for the propositional language, it is thrown if a developer
 * tries to create illegal propositional statements like a predicate with an
 * arity greater than zero.
 *
 * @author Tim Janus
 */
public class PlException extends LanguageException {

	/** kill warning */
	private static final long serialVersionUID = 843894579984076905L;
	/**
	 * Constructor
	 * @param reason LanguageExceptionReason
	 */
	public PlException(LanguageExceptionReason reason) {
		this(reason, "");
	}
	/**
	 *Constructor
	 * @param reason LanguageExceptionReason
	 * @param info info
	 */
	public PlException(LanguageExceptionReason reason, String info) {
		super("Propositional-Logic", reason, info);
	}
}

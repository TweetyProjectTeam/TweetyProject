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
package org.tweetyproject.logics.pl.syntax;

import org.tweetyproject.logics.commons.error.LanguageException.LanguageExceptionReason;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.pl.error.PlException;

/**
 * A specialized predicate for propositional logic that only allows an identifier
 * but has no arguments and therefore has an arity of zero.
 * 
 * @author Tim Janus
 */
public class PlPredicate extends Predicate {
	
	/** Default-Ctor for dynamic instantiation */
	public PlPredicate() {
		this("");
	}
	
	/**
	 * Ctor: Creates a new propositional predicate with the given
	 * name.
	 * @param name	The name of the predicate
	 */
	public PlPredicate(String name) {
		super(name, 0);
	}
	
	@Override
	public void addArgumentType(Sort argType) {
		throw new PlException(LanguageExceptionReason.LER_ILLEGAL_PREDICATE,
				"The predicates must not have any arguments.");
	}
	
	@Override
	public PlPredicate clone() {
		return new PlPredicate(this.getName());
	}
}

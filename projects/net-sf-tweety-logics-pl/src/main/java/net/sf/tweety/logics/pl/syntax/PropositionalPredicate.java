/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.syntax;

import net.sf.tweety.logics.commons.error.LanguageException.LanguageExceptionReason;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.pl.error.PropositionalException;

/**
 * A specialized predicate for propositional logic that only allows an identifier
 * but has no arguments and therefore has an arity of zero.
 * 
 * @author Tim Janus
 */
public class PropositionalPredicate extends Predicate {
	
	/** Default-Ctor for dynamic instantiation */
	public PropositionalPredicate() {
		this("");
	}
	
	/**
	 * Ctor: Creates a new propositional predicate with the given
	 * name.
	 * @param name	The name of the predicate
	 */
	public PropositionalPredicate(String name) {
		super(name, 0);
	}
	
	@Override
	public void setArity(int arity) {
		if(arity != 0) {
			throw new PropositionalException(LanguageExceptionReason.LER_ILLEGAL_PREDICATE,
					"The arity must be zero.");
		}
	}
	
	@Override
	public void addArgumentType(Sort argType) {
		throw new PropositionalException(LanguageExceptionReason.LER_ILLEGAL_PREDICATE,
				"The predicates must not have any arguments.");
	}
	
	@Override
	public PropositionalPredicate clone() {
		return new PropositionalPredicate(this.getName());
	}
}

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
package net.sf.tweety.lp.asp.syntax;

import net.sf.tweety.logics.commons.error.LanguageException;
import net.sf.tweety.logics.commons.error.LanguageException.LanguageExceptionReason;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;


/**
 * A predicate has a name and an arity. 
 * @author Tim Janus
 */
public class DLPPredicate extends Predicate {
	
	/** Default-Ctor: Used for dynamic instantiation */
	public DLPPredicate() {}
	
	/**
	 * Ctor: Generates a predicate with the given name and arity zero.
	 * @param name		The name of the predicate
	 */
	public DLPPredicate(String name) {
		this(name, 0);
	}
	
	/**
	 * Creates a predicate with the given name and the given arity
	 * @param name		The name of the predicate
	 * @param arity		The arity of the predicate
	 */
	public DLPPredicate(String name, int arity) {
		super(name, arity);
	}
	
	@Override
	public void addArgumentType(Sort sort) {
		if(!Sort.THING.equals(sort)) {
			throw new LanguageException("DLP", LanguageExceptionReason.LER_ILLEGAL_PREDICATE, "DLP-Predicates are typeless");
		} else {
			super.addArgumentType(sort);
		}
	}
}

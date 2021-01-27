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
package org.tweetyproject.logics.commons.syntax.interfaces;

import java.util.Set;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.logics.commons.syntax.Predicate;

/**
 * A formula of a logical language
 * @author Tim Janus
 */
public interface SimpleLogicalFormula extends Formula {
	/**
	 * Processes the set of all atoms which appear in this formula
	 * @return	The set of all atoms
	 */
	Set<? extends Atom> getAtoms();
	
	/** 
	 * Processes the set of all predicates which appear in this 
	 * formula
	 * @return	all predicates that appear in this formula
	 */
	Set<? extends Predicate> getPredicates();
	
	/**
	 * @return The class description of the predicate used by this formula.
	 */
	Class<? extends Predicate> getPredicateCls();
	
	/** @return true if the formula represents a literal in the language or false otherwise */
	boolean isLiteral();
	
	@Override
	int hashCode();
	
	@Override
	boolean equals(Object other);
	
	/**
	 * Creates a deep copy of this formula
	 * @return the cloned formula
	 */
	SimpleLogicalFormula clone();
}

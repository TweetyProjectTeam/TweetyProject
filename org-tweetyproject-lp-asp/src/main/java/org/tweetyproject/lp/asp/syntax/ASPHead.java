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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.Collection;

/**
 * This class is a common base class for ASP formulas
 * that can be part of an ASP rule head.
 * 
 * @author Anna Gessler
 */
public abstract class ASPHead extends ASPElement {
	/**
	 * @return true if the head is empty, false otherwise
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns all literals in this element in form of a SortedSet. 
	 * Literals are atoms or strict negations of atoms.
	 * @return all the literals used in the rule element 
	 */
	public abstract Collection<? extends ASPLiteral> getLiterals();

}

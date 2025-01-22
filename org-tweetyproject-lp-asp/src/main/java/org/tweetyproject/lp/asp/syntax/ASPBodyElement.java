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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.SortedSet;

import org.tweetyproject.logics.commons.syntax.interfaces.Term;

/**
 * This class is a common base class for ASP formulas
 * that can be part of an ASP rule body (premise of a rule): 
 * 
 * <ul>
 * <li> Literals (i.e. atoms or strictly negated atoms) </li>
 * <li> Built-in atoms (i.e. terms connected by comparative operators like == and !=) </li>
 * <li> Aggregate atoms </li>
 * <li> Default negations of literals or aggregates (i.e. 'not a' a for a literal or aggregate a)</li>
 * </ul>
 * 
 * Note: In the ASP-Core-2 standard, the formulas represented by this
 * class are also referred to as 'Literals'. In the TweetyProject ASP library,
 * literals are only atoms or the strict negations of atoms. 
 *
 * @author Tim Janus
 * @author Anna Gessler
 */
public abstract class ASPBodyElement extends ASPElement {
	
	/**
	 * Returns all literals in this element in form of a SortedSet. 
	 * Literals are atoms or strict negations of atoms.
	 * @return all the literals used in the rule element 
	 */
	public abstract SortedSet<ASPLiteral> getLiterals();
	
	@Override
	public abstract ASPBodyElement substitute(Term<?> t, Term<?> v);

    /** Default Constructor */
    public ASPBodyElement(){}
}

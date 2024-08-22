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

import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;

/**
 * This class defines common functionality for literals,
 * meaning atoms or strictly negated atoms. In the ASP-Core-2
 * standard, the formulas represented by this class are
 * referred to as 'classical atoms'.
 * 
 * @author Anna Gessler
 * @author Tim Janus
 *
 */
public abstract class ASPLiteral extends ASPBodyElement implements Atom, Invertable, Comparable<ASPLiteral> {
	
	/**
	 * @return The atom representing the literal.
	 */
	public abstract ASPAtom getAtom();
	
	/**
	 * Creates a copy of the literal and adds the
	 * given term as argument to the end of the argument
	 * list.
	 * @param term	the new argument.
	 * @return A copy of the literal containing the given term as new argument.
	 */
	public abstract ASPLiteral cloneWithAddedTerm(Term<?> term);
	
	@Override
	public boolean isLiteral() {
		return true;
	}
	
	@Override
	public abstract ASPLiteral complement();

    /** Default Constructor */
    public ASPLiteral(){}
}

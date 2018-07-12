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
package net.sf.tweety.lp.asp.syntax;

import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This interface defines common functionality for an ELP literal.
 * literals are atoms or strictly negated atoms.
 * 
 * @author Tim Janus
 * 
 */
public interface DLPLiteral extends DLPElement, Atom, Invertable, Comparable<DLPLiteral> {

	/**
	 * Creates a copy of the literal and adds the
	 * given term as argument to the end of the argument
	 * list.
	 * @param term	the new argument.
	 * @return A copy of the literal containing the given term as new argument.
	 */
	DLPLiteral cloneWithAddedTerm(Term<?> term);
	
	/**
	 * @return The atom representing the literal.
	 */
	DLPAtom getAtom();
	
	@Override
	DLPLiteral complement();
	
	@Override
	DLPLiteral substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
}

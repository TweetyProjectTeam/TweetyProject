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
package org.tweetyproject.arg.adf.syntax.pl;

import org.tweetyproject.arg.adf.syntax.pl.Literals.NamedAtom;
import org.tweetyproject.arg.adf.syntax.pl.Literals.TransientAtom;
import org.tweetyproject.arg.adf.syntax.pl.Literals.UnnamedAtom;

/**
 * @author Mathias Hofer
 *
 */
public interface Literal {

	boolean isTransient();

	boolean isPositive();

	/**
	 * @return this if it is an atom, or else the encapsulated atom if the
	 *         literal is a negation
	 */
	Literal getAtom();

	/**
	 * @return the name of the literal, can be null
	 */
	String getName();

	/**
	 * Returns the negation of this literal.
	 * <p>
	 * The following properties hold for every literal l:
	 * <p>
	 * <ul>
	 * <li>l.neg().neg() == l
	 * <li>l.neg().atom() == l
	 * </ul>
	 * <p>
	 * The following must however not hold for every literal l:
	 * <p>
	 * <ul>
	 * <li>l.neg() == l.neg()
	 * </ul>
	 * <p>
	 * 
	 * @return the negation of this literal
	 */
	Literal neg();

	static Literal create() {
		return new UnnamedAtom();
	}

	static Literal create(String name) {
		if (name == null) {
			return new UnnamedAtom();
		}
		return new NamedAtom(name);
	}
	
	static Literal createTransient() {
		return new TransientAtom();
	}

}

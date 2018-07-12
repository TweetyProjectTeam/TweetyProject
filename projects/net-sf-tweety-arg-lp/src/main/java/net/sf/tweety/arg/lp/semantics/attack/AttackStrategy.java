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
package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;


/**
 * This interface is the common denominator for notions of attack between two arguments.
 * The implementation is analogous to the Attack interface in the "deductive" package.
 * @author Sebastian Homann
 */
public interface AttackStrategy {

	/**
	 * Returns "true" iff the first argument attacks the second argument.
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff <code>a</code> attacks <code>b</code>.
	 */
	public boolean attacks(Argument a, Argument b);
	
	/**
	 * Returns the abbreviated identifier of this notion of attack, i.e. "a" for attack
	 * @return a short identifier
	 */
	public String toAbbreviation();
}

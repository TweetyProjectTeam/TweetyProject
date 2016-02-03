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
package net.sf.tweety.arg.deductive.semantics.attacks;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;

/**
 * This interface is the common interface for notions of attack between two arguments.
 * @author Matthias Thimm
 */
public interface Attack {

	/**
	 * Returns "true" iff the first argument is attacked by the second argument.
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff <code>a</code> is attacked by <code>b</code>.
	 */
	public boolean isAttackedBy(DeductiveArgument a, DeductiveArgument b);
}

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
package org.tweetyproject.logics.dl.syntax;

import org.tweetyproject.logics.commons.syntax.Constant;

/**
 * This class models an individual in description logic, also known as an
 * object. Individuals correspond to constants in first-order logic.
 *
 * <p>Individuals are used to represent specific entities or objects in the domain
 * of discourse. In description logic, individuals are treated as constants with
 * fixed interpretations.</p>
 *
 * @see Constant
 * @see org.tweetyproject.logics.dl.syntax.ConceptAssertion
 * @see org.tweetyproject.logics.dl.syntax.RoleAssertion
 *
 * @author Anna Gessler
 *
 */
public class Individual extends Constant {

	/**
	 * Constructs a new individual with the given name.
	 *
	 * @param name the name of the individual
	 */
	public Individual(String name) {
		super(name);
	}

	/**
	 * Constructs a new individual by copying another constant.
	 *
	 * @param other the constant to copy
	 */
	public Individual(Constant other) {
		super(other);
	}
}

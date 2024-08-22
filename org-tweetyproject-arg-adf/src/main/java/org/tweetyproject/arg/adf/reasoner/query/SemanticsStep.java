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
package org.tweetyproject.arg.adf.reasoner.query;

import org.tweetyproject.arg.adf.reasoner.sat.execution.Semantics;

/**
 * SemanticsStep class
 * @author Mathias Hofer
 *
 */
public interface SemanticsStep {

	/**
	 *
	 * Return conflictFree
	 * @return conflictFree
	 */
	ConditionlessTaskStep conflictFree();

	/**
	 *
	 * Return naive
	 * @return naive
	 */
	ConditionlessTaskStep naive();

	/**
	 *
	 * Return admissible
	 * @return admissible
	 */
	ConditionlessTaskStep admissible();

	/**
	 *
	 * Return preferred
	 * @return preferred
	 */
	ConditionlessTaskStep preferred();

	/**
	 *
	 * Return stable
	 * @return stable
	 */
	ConditionlessTaskStep stable();

	/**
	 *
	 * return complete
	 * @return complete
	 */
	ConditionlessTaskStep complete();

	/**
	 *
	 * Return model
	 * @return model
	 */
	ConditionlessTaskStep model();

	/**
	 *
	 * Return ground
	 * @return ground
	 */
	ConditionlessTaskStep ground();

	/**
	 *
	 * Return custom
	 * @param semantics semantics
	 * @return custom
	 */
	ConditionlessTaskStep custom(Semantics semantics);

}

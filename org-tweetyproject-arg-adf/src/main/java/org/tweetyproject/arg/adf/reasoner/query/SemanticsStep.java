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
 * @author Mathias Hofer
 *
 */
public interface SemanticsStep {

	/**
	 * 
	 * @return conflictFree
	 */
	ConditionlessTaskStep conflictFree();
	
	/**
	 * 
	 * @return naive
	 */
	ConditionlessTaskStep naive();
	
	/**
	 * 
	 * @return admissible
	 */
	ConditionlessTaskStep admissible();
	
	/**
	 * 
	 * @return preferred
	 */
	ConditionlessTaskStep preferred();
	
	/**
	 * 
	 * @return stable
	 */
	ConditionlessTaskStep stable();
	
	/**
	 * 
	 * @return complete
	 */
	ConditionlessTaskStep complete();
	
	/**
	 * 
	 * @return model
	 */
	ConditionlessTaskStep model();
	
	/**
	 * 
	 * @return ground
	 */
	ConditionlessTaskStep ground();

	/**
	 * 
	 * @param semantics semantics
	 * @return custom
	 */
	ConditionlessTaskStep custom(Semantics semantics);
	
}

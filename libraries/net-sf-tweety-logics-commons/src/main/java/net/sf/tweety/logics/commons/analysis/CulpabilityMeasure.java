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
package net.sf.tweety.logics.commons.analysis;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;

/**
 * Classes implementing this interface represent culpability measures, i.e.
 * measure that assign to each conditional of a conditional belief base a degree
 * of responsibility for causing an inconsistency.
 * 
 * @author Matthias Thimm
 */
public interface CulpabilityMeasure<S extends Formula, T extends BeliefSet<S,?>> {
	
	/**
	 * Returns the degree of responsibility of the given formula to cause
	 * inconsistency in the given belief set (NOTE: the formula should be
	 * in the given belief set).
	 * @param beliefSet a belief set.
	 * @param formula a formula
	 * @return a Double indicating the degree of inconsistency (NOTE: if the given formula
	 * does not appear in the given belief set the degree is defined to be zero).
	 */
	public Double culpabilityMeasure(T beliefSet, S conditional);
}

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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

/**
 * This interface defines methods, which define how a system should decide if certain criteria are fulfilled.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public interface DecisionMaker {

	/**
	 * Method to decide, whether in regard to the specified criteria the result should be TRUE or FALSE.
	 * @param criteriaIsMetA A criteria, which is taken into account for the decision.
	 * @param criteriaIsMetB Another criteria, which is taken into account for the decision.
	 * @return TRUE if the decision is positive. FALSE if not.
	 */
	public boolean decide(boolean criteriaIsMetA, boolean criteriaIsMetB);

	/**
	 * @return TRUE if the criteria A should be true, in order to come to a positive decision
	 */
	public boolean getShallCriteriaBeTrueA();
	/**
	 * @return TRUE if the criteria B should be true, in order to come to a positive decision
	 */
	public boolean getShallCriteriaBeTrueB();
}

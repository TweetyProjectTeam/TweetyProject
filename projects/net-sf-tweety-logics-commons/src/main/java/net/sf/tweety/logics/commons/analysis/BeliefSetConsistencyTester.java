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

import java.util.Collection;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent. 
 * 
 * @author Matthias Thimm
 */
public interface BeliefSetConsistencyTester<S extends Formula> extends ConsistencyTester<BeliefSet<S>> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.ConsistencyTester#isConsistent(net.sf.tweety.BeliefBase)
	 */
	public boolean isConsistent(BeliefSet<S> beliefSet);
	
	/**
	 * Checks whether the given collection of formulas is consistent.
	 * @param formulas a collection of formulas.
	 * @return "true" iff the given collection of formulas is consistent.
	 */
	public boolean isConsistent(Collection<S> formulas);
	
	/**
	 * Checks whether the given formula is consistent.
	 * @param formula a formulas.
	 * @return "true" iff the formula is consistent.
	 */
	public boolean isConsistent(S formula);
}

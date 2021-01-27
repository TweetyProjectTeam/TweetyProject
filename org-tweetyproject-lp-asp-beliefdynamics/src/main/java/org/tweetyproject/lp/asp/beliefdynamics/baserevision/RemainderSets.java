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
package org.tweetyproject.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.commons.Formula;

/**
 * This class represents the set of remainder sets constructed
 * from a belief base.
 *  
 * @author Sebastian Homann
 *
 * @param <T> the type of formulas these remainder sets are based upon
 */
public abstract class RemainderSets<T extends Formula> extends HashSet<Collection<T>> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns the belief base that seeded this remainder set. 
	 * @return a belief base
	 */
	public abstract Collection<T> getSourceBeliefBase();
}

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
package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class represents a multiple indicator utility function, i.e. a function
 * that ranks a set of propositions to 1 if this function's focal set
 * is part of the set, and 0 otherwise.
 * 
 * @author Matthias Thimm
 *
 */
public class MultipleIndicatorUtilityFunction implements UtilityFunction {

	/**
	 * The focal set of this function.
	 */
	private Set<Proposition> focalSet;
	
	/**
	 * Creates a new multiple indicator utility function for the given focal set.
	 * @param focalSet a collection of propositions.
	 */
	public MultipleIndicatorUtilityFunction(Collection<? extends Proposition> focalSet){
		this.focalSet = new HashSet<Proposition>(focalSet);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sas.UtilityFunction#rank(java.util.Collection)
	 */
	@Override
	public int rank(Collection<? extends Proposition> propositions) {
		if(propositions.containsAll(this.focalSet)) return 1;
		return 0;
	}

}

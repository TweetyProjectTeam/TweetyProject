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
package net.sf.tweety.machinelearning.assoc;

import java.util.Collection;

/**
 * Abstract layer for association rule miners, bundles common methods.
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of items
 */
public abstract class AbstractAssociationRuleMiner<T> implements AssociationRuleMiner<T>{
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.assoc.AssociationRuleMiner#mineRules(java.util.Collection)
	 */
	@Override
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database) {
		return this.mineRules(database, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.assoc.AssociationRuleMiner#mineRules(java.util.Collection, int)
	 */
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database, int conclusion_limit){
		return this.mineRules(database, conclusion_limit, Integer.MAX_VALUE);
	}
}

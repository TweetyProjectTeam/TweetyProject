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
package org.tweetyproject.machinelearning.assoc;

import java.util.Collection;

/**
 * Abstract layer for association rule miners, bundling common methods for mining association rules.
 *
 * <p>
 * This abstract class implements the `AssociationRuleMiner` interface and provides
 * common functionality for mining association rules from a database of itemsets.
 * It offers overloaded methods for mining rules with varying levels of constraints
 * on the conclusions and overall limits.
 * </p>
 *
 * <p>
 * Subclasses should implement specific rule mining algorithms by extending this class
 * and providing their own implementations of the abstract methods.
 * </p>
 *
 * @param <T> the type of items contained in the itemsets used for mining association rules.
 *
 * @author Matthias Thimm
 */
public abstract class AbstractAssociationRuleMiner<T> implements AssociationRuleMiner<T> {

    /**
     * Default constructor for the `AbstractAssociationRuleMiner` class.
     *
     * <p>
     * This constructor is used to create an instance of the abstract class.
     * Since this is an abstract class, this constructor will be called by
     * subclasses when they are instantiated.
     * </p>
     */
    public AbstractAssociationRuleMiner() {
        // Default constructor with no implementation.
    }

	/* (non-Javadoc)
	 * @see org.tweetyproject.machinelearning.assoc.AssociationRuleMiner#mineRules(java.util.Collection)
	 */
	@Override
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database) {
		return this.mineRules(database, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.machinelearning.assoc.AssociationRuleMiner#mineRules(java.util.Collection, int)
	 */
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database, int conclusion_limit){
		return this.mineRules(database, conclusion_limit, Integer.MAX_VALUE);
	}
}

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
 * Interface for algorithms mining association rules 
 * from transaction databases.
 * @author Matthias Thimm
 *
 * @param <T> the type of items
 */
public interface AssociationRuleMiner<T extends Object> extends FrequentPatternMiner<T>{	
	
	/**
	 * Mines a set of association rules from the given database.
	 * @param database some database
	 * @return a set of association rules.
	 */
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database);
	
	/**
	 * Mines a set of association rules from the given database. Only those rules
	 * are mined where the conclusion has the maximal given number of elements.
	 * @param database some database
	 * @param conclusion_limit the maximal size of elements in the conclusion of the mined rules.
	 * @return a set of association rules.
	 */
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database, int conclusion_limit);
	
	/**
	 * Mines a set of association rules from the given database. Only those rules
	 * are mined where the conclusion and in total has the maximal given number of elements,
	 * respectively.
	 * @param database some database
	 * @param conclusion_limit the maximal size of elements in the conclusion of the mined rules.
	 * @param total_limit the total number of elements that may appear in the mined rules.
	 * @return a set of association rules.
	 */
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database, int conclusion_limit, int total_limit);
}

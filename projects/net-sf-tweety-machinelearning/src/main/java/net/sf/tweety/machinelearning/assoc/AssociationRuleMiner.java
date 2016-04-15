/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.machinelearning.assoc;

import java.util.Collection;

/**
 * Interface for algorithms mining association rules 
 * from transaction databases.
 * @author Matthias Thimm
 *
 * @param <T> the type of items
 */
public interface AssociationRuleMiner<T extends Object> {

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
	 * @param the maximal size of elements in the conclusion of the mined rules.
	 * @return a set of association rules.
	 */
	public Collection<AssociationRule<T>> mineRules(Collection<Collection<T>> database, int conclusion_limit);
}

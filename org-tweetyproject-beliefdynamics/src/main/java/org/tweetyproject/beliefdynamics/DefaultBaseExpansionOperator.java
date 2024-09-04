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
package org.tweetyproject.beliefdynamics;

import java.util.*;

import org.tweetyproject.commons.*;

/**
 * This class implements the default base expansion operator, ie. an operator
 * that returns the union of a set of formulas and a formula.
 *
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public class DefaultBaseExpansionOperator<T extends Formula> implements BaseExpansionOperator<T> {
	/**Default */
	public DefaultBaseExpansionOperator() {
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.beliefdynamics.BaseExpansionOperator#expand(java.util.Collection, org.tweetyproject.Formula)
	 */
	@Override
	public Collection<T> expand(Collection<T> base, T formula) {
		Set<T> expandedCollection = new HashSet<T>();
		expandedCollection.addAll(base);
		expandedCollection.add(formula);
		return expandedCollection;
	}
}

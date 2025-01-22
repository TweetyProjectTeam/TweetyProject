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

import java.util.Collection;
import java.util.List;

import org.tweetyproject.commons.Formula;

/**
 * Implements the list based method of the Revision interface by iterative calling
 * the revision method which revise two belief bases. Acts as a base class for iterative
 * revision processes.
 *
 * @author Tim Janus
 *
 * @param <T>	The type of the belief bases
 */
public abstract class CredibilityRevisionIterative<T extends Formula>
	extends CredibilityRevision<T> {

		/**Default*/

	public CredibilityRevisionIterative() {
	}


	@Override
	public Collection<T> revise(List<Collection<T>> ordererList) {
		if(ordererList == null || ordererList.size() == 0)
			throw new IllegalArgumentException("The parameter 'orderList' must not be empty.");

		Collection<T> p1 = ordererList.get(0);
		for(int i=1; i<ordererList.size(); ++i) {
			Collection<T> p2 = ordererList.get(i);
			p1 = revise(p1,p2);
		}
		return p1;
	}

}

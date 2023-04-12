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
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import org.tweetyproject.arg.dung.serialisibility.ContainerTransitionStateAnalysis;
import org.tweetyproject.graphs.util.GraphUtil;

/**
 * This class represents an comparator, which defines if 2 analysis are equivalent, by comparing their graphs wrt to isomorphism. 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class IsomorphEquivalence implements ISerializingComparator {

	@Override
	public boolean isEquivalent(ContainerTransitionStateAnalysis analysis1, ContainerTransitionStateAnalysis analysis2) {
		return GraphUtil.isIsomorphic(analysis1.getGraphResulting(), analysis2.getGraphResulting());
	}

}

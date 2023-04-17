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

import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationGraph;

/**
 * This interface defines methods to analyze the equivalence of two graphs representing the generation process of serializing sets of arguments to extensions. 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public interface ISerializingComparator {

	/**
     * compute whether the given analyses are equivalent wrt. the kernel
     * @param analysis1 An analysis of the serialisable extensions.
     * @param analysis2 An analysis of the serialisable extensions.
     * @return true if both analyses are equivalent wrt. to the kernel
     */
	public boolean isEquivalent(SerialisationGraph analysis1, SerialisationGraph analysis2);
	
}

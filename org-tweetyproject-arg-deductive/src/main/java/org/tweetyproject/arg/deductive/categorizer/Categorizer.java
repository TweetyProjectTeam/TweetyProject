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
package org.tweetyproject.arg.deductive.categorizer;

import org.tweetyproject.arg.deductive.semantics.ArgumentTree;

/**
 * Classes implementing this interface represent categorizer in the sense
 * of Definition 8.10 in<br>
 * <br>
 * Philippe Besnard and Anthony Hunter. A logic-based theory of deductive arguments.
 * In Artificial Intelligence, 128(1-2):203-235, 2001.
 * 
 * @author Matthias Thimm
 */
public interface Categorizer {

	/** This method categorizes the given argument tree. In general,
	 * the semantics of this function is that a higher value of 
	 * this categorization means a higher belief in the claim
	 * of the root argument of the argument tree.
	 * @param argumentTree some argument tree.
	 * @return the categorization of the argument tree.
	 */
	public double categorize(ArgumentTree argumentTree);
}

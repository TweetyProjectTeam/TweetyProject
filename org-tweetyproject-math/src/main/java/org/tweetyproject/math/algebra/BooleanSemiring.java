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

package org.tweetyproject.math.algebra;

/**
 * This class represents a Boolean semiring. In this semiring, the addition operation corresponds to logical OR (||),
 * the multiplication operation corresponds to logical AND (&&), the additive identity is false,
 * and the multiplicative identity is true.
 * 
 * @author Sandra Hoffmann
 */

public class BooleanSemiring extends Semiring<Boolean> {
    public BooleanSemiring() {
        super((a, b) -> a || b, (a, b) -> a && b, false,true);
    }

	@Override
	public Boolean getRandomElement() {
		return random.nextBoolean();
	}
}

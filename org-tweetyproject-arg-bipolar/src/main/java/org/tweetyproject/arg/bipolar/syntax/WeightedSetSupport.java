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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.syntax;

/**
 * Represents a weighted support relationship between two argument sets.
 * <p>
 * This class extends the {@link SetSupport} class to include a weight parameter,
 * which quantifies the strength or significance of the support relationship.
 * </p>
 */
public class WeightedSetSupport extends SetSupport {

    /** The weight of the support relationship. */
    public double cp;

    /**
     * Constructs a new {@code WeightedSetSupport} instance.
     *
     * @param supporter The set of arguments providing support.
     *                  This is the argument set that supports the other set.
     * @param supported The set of arguments being supported.
     *                  This is the argument set that is being supported.
     * @param cp The weight of the support relationship, indicating the strength or significance of the support.
     *           This value should be a positive number, where a higher value represents a stronger support.
     */
    public WeightedSetSupport(ArgumentSet supporter, ArgumentSet supported, double cp) {
        super(supporter, supported);
        this.cp = cp;
    }
}


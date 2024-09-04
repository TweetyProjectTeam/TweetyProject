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

package org.tweetyproject.arg.bipolar.syntax;

/**
 * This interface captures common methods of different interpretations of the attack relation in
 * bipolar abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public interface Attack extends BipolarEntity{
   /**
     * Returns the entity that is being attacked.
     * <p>
     * This method provides access to the argument or set of arguments that is the target of the attack.
     * </p>
     *
     * @return the entity that is attacked
     */
    BipolarEntity getAttacked();

    /**
     * Returns the entity that is initiating the attack.
     * <p>
     * This method provides access to the argument or set of arguments that is performing the attack.
     * </p>
     *
     * @return the entity that is attacking
     */
    BipolarEntity getAttacker();

    /**
     * Provides a string representation of the attack.
     * <p>
     * This method returns a string that describes the attack relation, typically including
     * the identities of the attacker and the attacked entity.
     * </p>
     *
     * @return a string representation of the attack
     */

    public String toString();
}

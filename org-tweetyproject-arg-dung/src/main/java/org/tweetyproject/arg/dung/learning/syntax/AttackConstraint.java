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

package org.tweetyproject.arg.dung.learning.syntax;

import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * Represents a constraint on attacks within an argumentation framework. This interface defines the structure for
 * attack constraints, which are conditions that must be satisfied for an attack to be considered valid or applicable.
 * @param <T> the type of condition that must be satisfied for this constraint to hold. This could be a logical
 * condition, a numerical threshold, a rule-based condition, or any other form that fits the implementation needs.
 * @author Lars Bengel
 */
public interface AttackConstraint<T> {
    /**
     * Retrieves the condition associated with this attack constraint. The condition defines the criteria under
     * which the constraint is considered to be satisfied.
     *
     * @return The condition of type T associated with this attack constraint.
     */
    public T getCondition();
    /**
     * Retrieves the argument associated with this constraint. Typically, this is the target or source of an attack
     * in an argumentation framework, depending on the context in which this constraint is applied.
     *
     * @return The {@link Argument} associated with this constraint, possibly representing a participant in the attack.
     */
    public Argument getArgument();
}

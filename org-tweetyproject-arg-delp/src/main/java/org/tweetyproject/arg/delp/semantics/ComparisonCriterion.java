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
package org.tweetyproject.arg.delp.semantics;

import org.tweetyproject.arg.delp.syntax.*;

/**
 * This class is the superclass for all comparison criteria between two
 * arguments in defeasible logic programming.
 *
 * @author Matthias Thimm
 *
 */
public abstract class ComparisonCriterion {
/**
 * Enumeration of factory types for creating instances of {@link ComparisonCriterion} subclasses.
 */
public enum Factory {
    /**
     * Factory type for creating an instance of {@link EmptyCriterion}.
     */
    EMPTY,

    /**
     * Factory type for creating an instance of {@link GeneralizedSpecificity}.
     */
    GEN_SPEC,

    /**
     * A placeholder factory type that is not currently used in the {@link #create(String)} method but may be
     * implemented for future use.
     */
    PRIORITY;

    /**
     * Creates an instance of a {@link ComparisonCriterion} subclass based on the provided name.
     * <p>
     * The name should correspond to one of the factory types defined in this enum. If the name does not match
     * any of the defined factory types, an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param name The name of the factory type, which should be one of the enum constants ({@code EMPTY},
     *             {@code GEN_SPEC}, or {@code PRIORITY}).
     * @return An instance of the corresponding {@link ComparisonCriterion} subclass.
     * @throws IllegalArgumentException if the provided name does not match any of the defined factory types.
     */
    public static ComparisonCriterion create(String name) {
        switch (Factory.valueOf(name)) {
            case EMPTY:
                return new EmptyCriterion();
            case GEN_SPEC:
                return new GeneralizedSpecificity();
            case PRIORITY:
                // Implement this case if PriorityCriterion is added in the future.
                throw new UnsupportedOperationException("Factory type PRIORITY is not implemented yet.");
            default:
                throw new IllegalArgumentException("Cannot create comparison criterion from " + name);
        }
    }
}

    /**
     * Enumeration representing the possible results of a comparison between two
     * items.
     */
    public enum Result {
        /**
         * Indicates that the first item is better than the second item.
         */
        IS_BETTER,

        /**
         * Indicates that the two items cannot be compared due to lack of a common basis
         * or criterion.
         */
        NOT_COMPARABLE,

        /**
         * Indicates that the first item is worse than the second item.
         */
        IS_WORSE,

        /**
         * Indicates that the two items are equal in terms of the comparison criterion.
         */
        IS_EQUAL
    }

    /**
     * This method returns the relation of <code>argument1</code> to
     * <code>argument2</code>
     * given <code>context</code>.
     *
     * @param argument1 a DeLP argument
     * @param argument2 a DeLP argument
     * @param context   a defeasible logic program as context
     * @return
     *         <br>
     *         - Result.IS_BETTER iff <code>argument1</code> is better than
     *         <code>argument2</code>
     *         <br>
     *         - Result.IS_WORSE iff <code>argument1</code> is worse than
     *         <code>argument2</code>
     *         <br>
     *         - Result.IS_EQUAL iff <code>argument1</code> and
     *         <code>argument2</code> are in the same equivalence class
     *         <br>
     *         - Result.NOT_COMPARABLE iff <code>argument1</code> and
     *         <code>argument2</code> are not comparable
     */
    public abstract Result compare(DelpArgument argument1,
            DelpArgument argument2,
            DefeasibleLogicProgram context);

    /** Default Constructor */
    public ComparisonCriterion() {
    }
}

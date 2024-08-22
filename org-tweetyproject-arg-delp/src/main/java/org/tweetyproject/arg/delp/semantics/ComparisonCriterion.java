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
 * This class is the superclass for all comparison criteria between two arguments in defeasible logic programming.
 *
 * @author Matthias Thimm
 *
 */
public abstract class ComparisonCriterion {

    /**
     * To select a subclass and create an instance throw a static factory method.
     */
    public enum Factory { EMPTY, GEN_SPEC, PRIORITY;
        public static ComparisonCriterion create(String name) {
            switch (Factory.valueOf(name)) {
                case EMPTY:
                    return new EmptyCriterion();
                case GEN_SPEC:
                    return new GeneralizedSpecificity();
                default:
                    throw new IllegalArgumentException("Cannot create comparison criterion from "+name);
            }
        }
    }

    public enum Result { IS_BETTER, NOT_COMPARABLE, IS_WORSE, IS_EQUAL }

	/**
	 * This method returns the relation of <code>argument1</code> to <code>argument2</code>
     * given <code>context</code>.
	 * @param argument1 a DeLP argument
	 * @param argument2 a DeLP argument
	 * @param context a defeasible logic program as context
	 * @return
	 * 	<br>- Result.IS_BETTER iff <code>argument1</code> is better than <code>argument2</code>
	 *  <br>- Result.IS_WORSE iff <code>argument1</code> is worse than <code>argument2</code>
	 *  <br>- Result.IS_EQUAL iff <code>argument1</code> and <code>argument2</code> are in the same equivalence class
	 *  <br>- Result.NOT_COMPARABLE iff <code>argument1</code> and <code>argument2</code> are not comparable
	 */
    public abstract Result compare(DelpArgument argument1,
                                   DelpArgument argument2,
                                   DefeasibleLogicProgram context);

    /** Default Constructor */
    public ComparisonCriterion(){}
}

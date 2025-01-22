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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.semantics.link;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
/**
 * Interface representing a strategy for determining the type of link between
 * arguments in a structured argumentation framework.
 *
 * The strategy defines how to compute the relationship (link) between a parent
 * argument and its child based on their acceptance conditions. The link can be
 * classified as attacking, supporting, dependent, or redundant.
 *
 * @author Sebastian
 */
public interface LinkStrategy {

    /**
     * Computes the type of link between a parent argument and its child's
     * acceptance condition.
     *
     * @param parent    the parent argument in the argumentation framework
     * @param childAcc  the acceptance condition of the child argument
     * @return the computed {@link LinkType} representing the relationship between
     *         the parent argument and the child's acceptance condition
     */
    LinkType compute(Argument parent, AcceptanceCondition childAcc);

    /**
     * Computes the type of link between a parent argument and its child's
     * acceptance condition, taking into account an additional assumption.
     *
     * @param parent      the parent argument in the argumentation framework
     * @param childAcc    the acceptance condition of the child argument
     * @param assumption  the assumption or interpretation that may influence
     *                    the computation of the link type
     * @return the computed {@link LinkType} representing the relationship between
     *         the parent argument, the child's acceptance condition, and the given
     *         assumption
     */
    LinkType compute(Argument parent, AcceptanceCondition childAcc, Interpretation assumption);
}

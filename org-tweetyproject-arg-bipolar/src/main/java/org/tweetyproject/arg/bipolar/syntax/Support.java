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
 * This interface captures common methods of different interpretations of the support relation in
 * bipolar abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public interface Support extends BipolarEntity {

    /**
     * Returns the argument that is supported by the supporter.
     * <p>
     * This method retrieves the argument that is being supported by the current supporter argument.
     * </p>
     *
     * @return the supported argument
     */
    BipolarEntity getSupported();

    /**
     * Returns the argument that provides support to another argument.
     * <p>
     * This method retrieves the argument that is providing support to the current supported argument.
     * </p>
     *
     * @return the supporter argument
     */
    BipolarEntity getSupporter();

    /**
     * Sets the conditionality of the support, represented by a probability value.
     * <p>
     * The conditionality or strength of the support is represented by a conditional probability
     * value, which reflects how strongly the supporter influences the supported argument.
     * </p>
     *
     * @param c the conditional probability of the support
     */
    void setConditionality(double c);

    /**
     * Returns the conditional probability of the support.
     * <p>
     * This method retrieves the conditional probability associated with the support, indicating
     * the strength of the support.
     * </p>
     *
     * @return the conditional probability of the support
     */
    double getConditionalProbability();

    /**
     * Returns a string representation of the support relation.
     * <p>
     * This method provides a textual description of the support relation, including information
     * about the supporter, the supported argument, and the conditional probability.
     * </p>
     *
     * @return a string representation of the support relation
     */
    String toString();
}

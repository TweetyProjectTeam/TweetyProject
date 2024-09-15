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
package org.tweetyproject.machinelearning;

import java.util.Vector;

import libsvm.svm_node;
/**
 * A default observation is a vector of double values, representing
 * an observation for machine learning tasks.
 *
 * @author Matthias Thimm
 */
public class DefaultObservation extends Vector<Double> implements Observation {

    /** For serialization */
    private static final long serialVersionUID = -6600763202428596342L;

    /**
     * Default constructor for the `DefaultObservation` class.
     *
     * <p>
     * This constructor initializes an empty `DefaultObservation` object
     * that can be populated with double values representing an observation.
     * </p>
     */
    public DefaultObservation() {
        // Default constructor with no implementation needed.
    }

    /**
     * Converts this observation into an array of `svm_node` objects.
     *
     * <p>
     * The conversion is intended for use in SVM-based machine learning
     * algorithms. Each value in the observation is transformed into an
     * `svm_node` where the index corresponds to the position of the value
     * in the vector, and the value is the actual double value.
     * </p>
     *
     * @return an array of `svm_node` objects representing this observation.
     */
    @Override
    public svm_node[] toSvmNode() {
        svm_node[] obs = new svm_node[this.size()];
        int idx = 0;
        for (Double d : this) {
            obs[idx] = new svm_node();
            obs[idx].index = idx + 1;
            obs[idx].value = d;
            idx++;
        }
        return obs;
    }
}

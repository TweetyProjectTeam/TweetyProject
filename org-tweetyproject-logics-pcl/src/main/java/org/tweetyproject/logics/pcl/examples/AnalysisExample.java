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
package org.tweetyproject.logics.pcl.examples;

import java.io.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pcl.analysis.*;
import org.tweetyproject.logics.pcl.syntax.*;
import org.tweetyproject.math.opt.rootFinder.OptimizationRootFinder;

/**
 * Example code illustrating the use of inconsistency measures and repairing approaches for probabilistic conditional logic.
 *
 * <p>
 * This class demonstrates how to evaluate the inconsistency of a probabilistic conditional logic belief set and
 * how to apply different repairing techniques to make the belief set consistent.
 * </p>
 *
 *
 * @author Your Name
 */
public class AnalysisExample {

    /**
     * Main method illustrating how to compute the inconsistency measure, culpability, and repair a probabilistic belief set.
     *
     * @param args Command line arguments (not used)
     * @throws FileNotFoundException If the specified belief set file is not found
     * @throws ParserException If an error occurs while parsing the belief set
     * @throws IOException If an I/O error occurs while reading the belief set file
     */
    public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {

        // Parse the belief set from a PCL file
        PclBeliefSet beliefSet = (PclBeliefSet) new org.tweetyproject.logics.pcl.parser.PclParser().parseBeliefBaseFromFile("/path/to/test.pcl");

        // Define the root finder to be used for distance minimization and culpability measures
        OptimizationRootFinder rootFinder = null; // TODO: Root finder to be defined

        // Define the inconsistency measure based on distance minimization
        BeliefSetInconsistencyMeasure<ProbabilisticConditional> dist = new DistanceMinimizationInconsistencyMeasure(rootFinder);

        // Define the culpability measure using the mean distance approach
        MeanDistanceCulpabilityMeasure cp = new MeanDistanceCulpabilityMeasure(rootFinder, false);

        // Print the belief set and its inconsistency measure
        System.out.println(beliefSet);
        System.out.println(dist.inconsistencyMeasure(beliefSet));

        // Print the culpability measure for each probabilistic conditional in the belief set
        for (ProbabilisticConditional pc : beliefSet)
            System.out.println(pc + "\t" + cp.culpabilityMeasure(beliefSet, pc));

        // Define the machine shops for repairing the belief set
        PenalizingCreepingMachineShop ms = new PenalizingCreepingMachineShop(rootFinder);
        BalancedMachineShop ms2 = new BalancedMachineShop(cp);

        // Repair the belief set using the different machine shop approaches
        System.out.print(ms.repair(beliefSet));
        System.out.print(ms2.repair(beliefSet));
    }

    /** Default Constructor */
    public AnalysisExample() {
    }
}


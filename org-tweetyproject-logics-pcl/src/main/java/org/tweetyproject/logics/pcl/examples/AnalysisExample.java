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
 * Example code illustrating the use of inconsistency measures and repairing approaches.
 *
 */
public class AnalysisExample {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{


		PclBeliefSet beliefSet = (PclBeliefSet) new org.tweetyproject.logics.pcl.parser.PclParser().parseBeliefBaseFromFile("/Users/mthimm/Desktop/test.pcl");

		OptimizationRootFinder rootFinder = null; // TODO to be defined
		BeliefSetInconsistencyMeasure<ProbabilisticConditional> dist = new DistanceMinimizationInconsistencyMeasure(rootFinder);
		MeanDistanceCulpabilityMeasure cp = new MeanDistanceCulpabilityMeasure(rootFinder,false);
		System.out.println(beliefSet);
		System.out.println(dist.inconsistencyMeasure(beliefSet));

		for(ProbabilisticConditional pc: beliefSet)
			System.out.println(pc + "\t" + cp.culpabilityMeasure(beliefSet, pc));

		PenalizingCreepingMachineShop ms = new PenalizingCreepingMachineShop(rootFinder);
		BalancedMachineShop ms2 = new BalancedMachineShop(cp);
		System.out.print(ms.repair(beliefSet));
		System.out.print(ms2.repair(beliefSet));
	}

    /** Default Constructor */
    public AnalysisExample(){}
}

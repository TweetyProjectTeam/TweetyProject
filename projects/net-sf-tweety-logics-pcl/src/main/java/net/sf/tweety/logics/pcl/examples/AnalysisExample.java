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
package net.sf.tweety.logics.pcl.examples;

import java.io.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.analysis.*;
import net.sf.tweety.logics.pcl.syntax.*;

public class AnalysisExample {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		//TweetyLogging.logLevel = TweetyConfiguration.LogLevel.ERROR;
		//TweetyLogging.initLogging();

		PclBeliefSet beliefSet = (PclBeliefSet) new net.sf.tweety.logics.pcl.parser.PclParser().parseBeliefBaseFromFile("/Users/mthimm/Desktop/test.pcl");

		BeliefSetInconsistencyMeasure<ProbabilisticConditional> dist = new DistanceMinimizationInconsistencyMeasure();
		MeanDistanceCulpabilityMeasure cp = new MeanDistanceCulpabilityMeasure(false);
		System.out.println(beliefSet);
		System.out.println(dist.inconsistencyMeasure(beliefSet));

		for(ProbabilisticConditional pc: beliefSet)
			System.out.println(pc + "\t" + cp.culpabilityMeasure(beliefSet, pc));

		PenalizingCreepingMachineShop ms = new PenalizingCreepingMachineShop();
		BalancedMachineShop ms2 = new BalancedMachineShop(cp);
		System.out.print(ms.repair(beliefSet));
		System.out.print(ms2.repair(beliefSet));
	}
}

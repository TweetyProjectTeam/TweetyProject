package net.sf.tweety.logics.pcl.test;

import java.io.*;

import net.sf.tweety.*;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.pcl.*;
import net.sf.tweety.logics.pcl.analysis.*;
import net.sf.tweety.logics.pcl.syntax.*;

public class AnalysisTest {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		//TweetyLogging.logLevel = TweetyConfiguration.LogLevel.ERROR;
		//TweetyLogging.initLogging();		
		
		PclBeliefSet beliefSet = (PclBeliefSet) new net.sf.tweety.logics.pcl.parser.PclParser().parseBeliefBaseFromFile("/Users/mthimm/Desktop/test.pcl");
	
		InconsistencyMeasure<PclBeliefSet> dist = new DistanceMinimizationInconsistencyMeasure();
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

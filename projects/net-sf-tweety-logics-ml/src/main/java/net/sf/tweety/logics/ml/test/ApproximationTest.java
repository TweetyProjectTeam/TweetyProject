package net.sf.tweety.logics.ml.test;

import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.util.Pair;
import net.sf.tweety.logics.ml.*;
import net.sf.tweety.logics.ml.syntax.MlnFormula;

public class ApproximationTest {

	public static void main(String[] args) throws ParserException, IOException, InterruptedException{
		
		Pair<MarkovLogicNetwork,FolSignature> ex = MlnTest.iterateExamples(1, 3);
		SimpleSamplingMlnReasoner appReasoner = new SimpleSamplingMlnReasoner(ex.getFirst(),ex.getSecond(), 0.0001, 1000);
		NaiveMlnReasoner naiReasoner = new NaiveMlnReasoner(ex.getFirst(),ex.getSecond());
		naiReasoner.setTempDirectory("/Users/mthimm/Desktop/tmp/");
		for(MlnFormula f: ex.getFirst()){
			for(RelationalFormula groundFormula: f.getFormula().allGroundInstances(ex.getSecond().getConstants())){
				System.out.println(appReasoner.query(groundFormula).getAnswerDouble() + "\t" + naiReasoner.query(groundFormula).getAnswerDouble());
				Thread.sleep(10000);
				//break;
			}
		}
		
//		Pair<MarkovLogicNetwork,FolSignature> ex = MlnTest.iterateExamples(1, 3);
//		ApproximateNaiveMlnReasoner appReasoner = new ApproximateNaiveMlnReasoner(ex.getFirst(),ex.getSecond(), -1, 100000);
//		for(MlnFormula f: ex.getFirst()){
//			for(RelationalFormula groundFormula: f.getFormula().allGroundInstances(ex.getSecond().getConstants())){
//				System.out.println(appReasoner.query(groundFormula).getAnswerDouble());
//				break;
//			}
//		}
	}
}

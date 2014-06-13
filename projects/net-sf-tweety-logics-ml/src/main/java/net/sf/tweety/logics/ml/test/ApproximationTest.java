/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.ml.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
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

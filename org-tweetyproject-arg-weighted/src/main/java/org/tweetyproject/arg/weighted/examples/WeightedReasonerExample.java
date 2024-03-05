/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.weighted.examples;


import java.util.Collection;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.weighted.reasoner.SimpleWeightedAdmissibleReasoner;
import org.tweetyproject.arg.weighted.reasoner.SimpleWeightedCompleteReasoner;
import org.tweetyproject.arg.weighted.reasoner.SimpleWeightedConflictFreeReasoner;
import org.tweetyproject.arg.weighted.reasoner.SimpleWeightedGroundedReasoner;
import org.tweetyproject.arg.weighted.reasoner.SimpleWeightedPreferredReasoner;
import org.tweetyproject.arg.weighted.reasoner.SimpleWeightedStableReasoner;
import org.tweetyproject.arg.weighted.syntax.WeightedArgumentationFramework;
import org.tweetyproject.math.algebra.BottleneckSemiring;
import org.tweetyproject.math.algebra.WeightedSemiring;

/**
 * @author Sandra Hoffmann
 *
 */
public class WeightedReasonerExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");

		//construct weighted AF
		WeightedArgumentationFramework<Double> weightedDungAF = new WeightedArgumentationFramework<>(new WeightedSemiring());
		weightedDungAF.add(a);
		weightedDungAF.add(b);
		weightedDungAF.add(c);
		weightedDungAF.add(d);
		weightedDungAF.add(e);

		
		weightedDungAF.add(new Attack(a,b),7.0);
		weightedDungAF.add(new Attack(c,b),8.0);
		weightedDungAF.add(new Attack(d,c),8.0);
		weightedDungAF.add(new Attack(c,d),9.0);
		weightedDungAF.add(new Attack(d,e),5.0);
		weightedDungAF.add(new Attack(e,e),6.0);

		
		//Conflict Free Reasoner
		SimpleWeightedConflictFreeReasoner<Double> weightedCfReasoner = new SimpleWeightedConflictFreeReasoner<>();
		System.out.println("0 conflict free Sets: " +weightedCfReasoner.getModels(weightedDungAF, 0.0));
		System.out.println("15 conflict free Sets: " +weightedCfReasoner.getModels(weightedDungAF, 15.0));

		
		//Admissibility Reasoner
		SimpleAdmissibleReasoner admReasoner = new SimpleAdmissibleReasoner();
		SimpleWeightedAdmissibleReasoner<Double> weightedAdmReasoner = new SimpleWeightedAdmissibleReasoner<>();
		DungTheory dungTheory = new DungTheory(weightedDungAF);
		System.out.println("unweighted admissible Sets: " +admReasoner.getModels(dungTheory));
		System.out.println("0 alpha 0 gamma admissible Sets: " +weightedAdmReasoner.getModels(weightedDungAF, 0.0, 0.0));
		System.out.println("15 alpha 0 gamma admissible Sets: " +weightedAdmReasoner.getModels(weightedDungAF, 15.0, 0.0));
		System.out.println("11 alpha 1 gamma admissible Sets " +weightedAdmReasoner.getModels(weightedDungAF, 11.0, 1.0));
		
		//Complete Reasoner
		SimpleCompleteReasoner completeReasoner = new SimpleCompleteReasoner();
		SimpleWeightedCompleteReasoner<Double> weightedComReasoner = new SimpleWeightedCompleteReasoner<>();
		System.out.println("unweighted complete Sets: " +completeReasoner.getModels(dungTheory));
		System.out.println("0 alpha 0 gamma complete Sets: " +weightedComReasoner.getModels(weightedDungAF, 0.0, 0.0));
		System.out.println("0 alpha 1 gamma complete Sets: " +weightedComReasoner.getModels(weightedDungAF, 0.0, 1.0));
		System.out.println("11 alpha 1 gamma complete Sets: " +weightedComReasoner.getModels(weightedDungAF, 11.0, 1.0));
		
		//Preferred Reasoner
		SimplePreferredReasoner preferredReasoner = new SimplePreferredReasoner();
		SimpleWeightedPreferredReasoner<Double> weightedPrefReasoner = new SimpleWeightedPreferredReasoner<>();
		System.out.println("unweighted preferred Sets: " +preferredReasoner.getModels(dungTheory));
		System.out.println("0 alpha 0 gamma preferred Sets: " +weightedPrefReasoner.getModels(weightedDungAF, 0.0, 0.0));
		System.out.println("0 alpha 1 gamma preferred Sets: " +weightedPrefReasoner.getModels(weightedDungAF, 0.0, 1.0));
		
		//Stable Reasoner
		SimpleStableReasoner stableReasoner = new SimpleStableReasoner();
		SimpleWeightedStableReasoner<Double> weightedStabReasoner = new SimpleWeightedStableReasoner<>();
		System.out.println("unweighted stable Sets: " +stableReasoner.getModels(dungTheory));
		System.out.println("0 alpha 0 gamma stable Sets: " +weightedStabReasoner.getModels(weightedDungAF, 0.0, 0.0));
		System.out.println("0 alpha 1 gamma stable Sets: " +weightedStabReasoner.getModels(weightedDungAF, 0.0, 1.0));
		
		//grounded Reasoner
		SimpleGroundedReasoner groundedReasoner = new SimpleGroundedReasoner();
		SimpleWeightedGroundedReasoner<Double> weightedgrdReasoner = new SimpleWeightedGroundedReasoner<>();
		System.out.println("unweighted grounded Set: " +groundedReasoner.getModel(dungTheory));
		System.out.println("0 alpha 0 gamma grounded Set: " +weightedgrdReasoner.getModel(weightedDungAF, 0.0, 0.0));
		
	}
		

}

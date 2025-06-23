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
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.examples;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.reasoner.EAFAgreementReasoner;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;

/**
 * 
 */
public class EAFAgreementReasonerExample {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Argument a = new Argument("a");
		Argument r = new Argument("r");
		
		DungTheory af = new DungTheory();
		
		af.add(a);
		af.add(r);
		
		af.addAttack(a,r);
		af.addAttack(r,a);


		String constEAF1 = "[](in(a))";
		String constEAF2 = "[](und(a))";
		EpistemicArgumentationFramework eaf1 = new EpistemicArgumentationFramework(af, constEAF1);
		EpistemicArgumentationFramework eaf2 = new EpistemicArgumentationFramework(af, constEAF2);	

		EAFAgreementReasoner agreementReasoner = new EAFAgreementReasoner(eaf1);
		
		//add second EAF with the same constraint
		System.out.println("The agreement Reasoner can identify wether multiple agents with the same underlying AF agree on the labeling status of an argument.");
		agreementReasoner.addEAF(constEAF1);
		System.out.println("\nFor the following AF:");
		System.out.println(af.prettyPrint());
		System.out.print("Two agents with the same underlying believe: ");
		System.out.println(eaf1.getConstraint());
		System.out.print("Believe that argument a should be credulously labelled in under complete semantics: ");
		System.out.println(agreementReasoner.query("in(a)", InferenceMode.CREDULOUS, Semantics.CO));

		System.out.println("\nThe Agreement Reasoner can list all EAFs that it contains:");
		agreementReasoner.listEAFs();
		System.out.println("EAFs can be added, deleted or changed.");
		agreementReasoner.changeEAF(1, eaf2);
		System.out.print("If one agent changes its believe to: ");
		System.out.println(eaf2.getConstraint());
		System.out.print("Believe that argument a should be credulously labelled in under complete semantics: ");
		System.out.println(agreementReasoner.query("in(a)", InferenceMode.CREDULOUS, Semantics.CO));
		
		//Majority Voting
		System.out.println("\nThe Agreement Reasoner supports majority voting");
		String constEAF3 = "[](!und(a))";
		agreementReasoner.addEAF(constEAF3);
		System.out.println("Given the following EAFs");
		agreementReasoner.listEAFs();
		System.out.print("Would the majority vote that a should be credulously labelled in under complete semantics: ");
		System.out.println(agreementReasoner.majorityVote("in(a)", InferenceMode.CREDULOUS, Semantics.CO));
		System.out.print("Would the majority also vote that a should be skeptically labelled in under complete semantics: ");
		System.out.println(agreementReasoner.majorityVote("in(a)", InferenceMode.SKEPTICAL, Semantics.CO));
		

	}

}

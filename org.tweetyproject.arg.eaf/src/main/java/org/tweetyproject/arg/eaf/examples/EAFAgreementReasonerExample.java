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


		String constEAF1 = "[](a)";
		String constEAF2 = "[](und(a))";
		EpistemicArgumentationFramework eaf1 = new EpistemicArgumentationFramework(af, constEAF1);
		EpistemicArgumentationFramework eaf2 = new EpistemicArgumentationFramework(af, constEAF2);	

		EAFAgreementReasoner agreementReasoner = new EAFAgreementReasoner(eaf1);
		
		//add second EAF with the same constraint
		agreementReasoner.addEAF(constEAF1);
		System.out.println(agreementReasoner.query("in(a)", InferenceMode.CREDULOUS, Semantics.CO));

		agreementReasoner.listEAFs();
		agreementReasoner.changeEAF(1, eaf2);
		System.out.println(agreementReasoner.query("in(a)", InferenceMode.CREDULOUS, Semantics.CO));
		
		//Majority Voting
		
		String constEAF3 = "[](a) || [](!a)";
		agreementReasoner.addEAF(constEAF3);
		System.out.println(agreementReasoner.majorityVote("in(a)", InferenceMode.SKEPTICAL, Semantics.CO));
		

	}

}

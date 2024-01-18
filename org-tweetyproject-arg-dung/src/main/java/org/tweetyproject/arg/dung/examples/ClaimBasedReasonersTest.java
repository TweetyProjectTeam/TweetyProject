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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.SimpleClInheritedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClNaiveReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClPreferredReaonser;
import org.tweetyproject.arg.dung.reasoner.SimpleClSemistableReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClStableReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClStagedReasoner;
import org.tweetyproject.arg.dung.semantics.ClaimSet;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Claim;
import org.tweetyproject.arg.dung.syntax.ClaimArgument;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;
/**
 * test for claim based  reasoners
 * @author Sebastian Franke
 *
 */
public class ClaimBasedReasonersTest {
/**
 * main method
 * @param args arguments
 */
	public static void main(String[] args) {

		ClaimBasedTheory ex = new ClaimBasedTheory();
		Claim c1 = new Claim("c1");
		Claim c2 = new Claim("c2");

        ClaimArgument a = new ClaimArgument("a", c1);
        ClaimArgument b = new ClaimArgument("b", c2);
        ClaimArgument c = new ClaimArgument("c", c1);
        ClaimArgument d = new ClaimArgument("d", c2);

        ex.add(a);
        ex.add(b);
        ex.add(c);
        ex.add(d);
        ex.addAttack(a, b);
        ex.addAttack(b, a);
        ex.addAttack(d, c);
        ex.addAttack(c, d);


        
        SimpleClInheritedReasoner re1 = new SimpleClInheritedReasoner(Semantics.CO);
        System.out.println("inherited complete: " + print(re1.getModels(ex)));
        SimpleClPreferredReaonser re2 = new SimpleClPreferredReaonser();
        System.out.println("cl-preferred: " + print(re2.getModels(ex)));
        SimpleClStableReasoner re3 = new SimpleClStableReasoner();
        System.out.println("cl-stable: " + print(re3.getModels(ex)));
        SimpleClSemistableReasoner re4 = new SimpleClSemistableReasoner();
        System.out.println("cl-semistable: " + print(re4.getModels(ex)));
        SimpleClStagedReasoner re5 = new SimpleClStagedReasoner();
        System.out.println("cl-staged: " + print(re5.getModels(ex)));
        SimpleClNaiveReasoner re6 = new SimpleClNaiveReasoner();
        System.out.println("cl-naive: " + print(re6.getModels(ex)));
        
        
        
        
	}

	/**
	 * 
	 * @param input input for pretty print
	 * @return the printe version
	 */
	public static Set<Set<String>> print(Set<ClaimSet> input) {
		HashSet<Set<String>> result = new HashSet<Set<String>>();
		for(ClaimSet set : input) {
			HashSet<String> stringSet = new HashSet<String>();
			for(ClaimArgument arg : set) {
				stringSet.add(arg.getClaim().toString());
			}
			result.add(stringSet);
		}
		return result;
	}
}

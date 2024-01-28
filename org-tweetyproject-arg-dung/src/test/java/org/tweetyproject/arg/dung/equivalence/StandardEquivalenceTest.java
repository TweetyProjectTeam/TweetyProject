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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Tests to verify the code in the class {@link StandardEquivalence}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class StandardEquivalenceTest {

	@Test
	void testIsEquivalentDungTheoryDungTheory() {
		//Arrange
		var frameEQ1 = new DungTheory();
		var frameEQ2 = new DungTheory();
		var frameNotEQ1 = new DungTheory();
		var frameNotEQ2 = new DungTheory();
		
		createFrameworksAdmEQ(frameEQ1, frameEQ2);
		createFrameworksAdmNotEQ(frameNotEQ1, frameNotEQ2);
		
		var equivalence = new StandardEquivalence(AbstractExtensionReasoner
				.getSimpleReasonerForSemantics(Semantics.ADM));
		
		//Act
		//Assert
		Assert.assertTrue(equivalence.isEquivalent(frameEQ1, frameEQ2));
		Assert.assertFalse(equivalence.isEquivalent(frameNotEQ1, frameNotEQ2));
	}

	@Test
	void testIsEquivalentCollectionOfDungTheory() {
					
				//Arrange
				var frameEQ1 = new DungTheory();
				var frameEQ2 = new DungTheory();
				var frameNotEQ1 = new DungTheory();
				var frameNotEQ2 = new DungTheory();
				
				var framesEQ = new LinkedList<DungTheory>();
				framesEQ.add(frameEQ1);
				framesEQ.add(frameEQ2);
				var framesNotEQ = new LinkedList<DungTheory>();
				framesNotEQ.add(frameNotEQ1);
				framesNotEQ.add(frameNotEQ2);
				
				createFrameworksAdmEQ(frameEQ1, frameEQ2);
				createFrameworksAdmNotEQ(frameNotEQ1, frameNotEQ2);
				
				var equivalence = new StandardEquivalence(AbstractExtensionReasoner
						.getSimpleReasonerForSemantics(Semantics.ADM));
				
				//Act
				//Assert
				Assert.assertTrue(equivalence.isEquivalent(framesEQ));
				Assert.assertFalse(equivalence.isEquivalent(framesNotEQ));
				
				
				
	}
	
	private void createFrameworksAdmNotEQ(DungTheory out_framework1, DungTheory out_framework2) {
		var a = new Argument("a");
		var b = new Argument("b");
		var c = new Argument("c");
		var d = new Argument("d");
		var e = new Argument("e");
		out_framework1.add(new Argument[] {a,b,c,d,e});
		out_framework2.add(new Argument[] {a,b,c,d,e});
		
		out_framework1.add(new Attack[] {new Attack(a, b),new Attack(b, c), new Attack(c, d), new Attack(e, c)});
		out_framework2.add(new Attack[] {new Attack(a, b),new Attack(b, c), new Attack(c, d), new Attack(b, e)});
		
		/*
		SerialisationAnalysisPlotter.plotAnalyses(new Semantics[] {Semantics.ADM}, 
				new DungTheory[] {out_framework1, out_framework2}, "Ex", 2000, 1000);
				*/
	}
	
	private void createFrameworksAdmEQ(DungTheory out_framework1, DungTheory out_framework2) {
		
		var a = new Argument("a");
		var b = new Argument("b");
		var c = new Argument("c");
		var d = new Argument("d");
		out_framework1.add(new Argument[] {a,b,c,d});
		out_framework2.add(new Argument[] {a,b,c,d});
		
		out_framework1.add(new Attack[] {new Attack(a, b),new Attack(a, c), new Attack(d, d)});
		out_framework2.add(new Attack[] {new Attack(b, b),new Attack(c, c), new Attack(c, d)});
		//Extension for ADM: {{},{a}}, CO: {{a}}, GR: {{a}}, for both frameworks
	}

}

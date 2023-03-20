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
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Iterator;

import org.tweetyproject.arg.dung.reasoner.serialisable.*;

/**
 * This class summarises examples displaying the usage of {@link org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner} 
 * for different types of serialisable semantics.
 * <br>
 * <br> See
 * <br>
 * <br> Matthias Thimm. Revisiting initial sets in abstract argumentation.
 * <br> Argument & Computation 13 (2022) 325–360 
 * <br> DOI 10.3233/AAC-210018
 * <br>
 * <br> and
 * <br>
 * <br> Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract Argumentation.
 * <br> Computational Models of Argument (2022)
 * <br> DOI: 10.3233/FAIA220143
 * 
 * @author Julian Sander
 *
 */
public class SerialisableExtensionReasonerExample {

	/**
	 * Builds an example argumentation framework
	 * @return AF: ({a,b,c,d,e,f},{(a,a),(a,b),(b,a),(b,d),(d,c),(e,c),(e,f),(f,e)})
	 */
	public static DungTheory buildExample1() {
		// AF: ({a,b,c,d,e,f},{(a,a),(a,b),(b,a),(b,d),(d,c),(e,c),(e,f),(f,e)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(d);
		af.add(e);
		af.add(f);
		af.add(new Attack(a,a));
		af.add(new Attack(a,b));
		af.add(new Attack(b,a));
		af.add(new Attack(b,d));
		af.add(new Attack(d,c));
		af.add(new Attack(e,c));
		af.add(new Attack(e,f));
		af.add(new Attack(f,e));
		
		return af;
	}
	
	/**
	 * Builds an example argumentation framework
	 * @return AF: ({a,b,c,d,e,f},{(a,b),(a,c),(b,a),(b,c),(c,e),(d,e),(e,d),(e,f),(f,e)})
	 */
	public static DungTheory buildExample2() {
		// AF: ({a,b,c,d,e,f},{(a,b),(a,c),(b,a),(b,c),(c,e),(d,e),(e,d),(e,f),(f,e)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(d);
		af.add(e);
		af.add(f);
		af.add(new Attack(a,b));
		af.add(new Attack(a,c));
		af.add(new Attack(b,a));
		af.add(new Attack(b,c));
		af.add(new Attack(c,e));
		af.add(new Attack(d,e));
		af.add(new Attack(e,d));
		af.add(new Attack(e,f));
		af.add(new Attack(f,e));
		
		return af;
	}
	
	/**
	 * Builds an example argumentation framework
	 * @return AF: ({a,b,c,d},{(a,b),(b,a),(c,d),(d,b)})
	 */
	public static DungTheory buildExample3() {
		// AF: ({a,b,c,d},{(a,b),(b,a),(c,d),(d,b)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(d);
		af.add(new Attack(a,b));
		af.add(new Attack(b,a));
		af.add(new Attack(c,d));
		af.add(new Attack(d,b));

		return af;
	}
	
	/**
	 * Uses the specified reasoner to deduce all extensions within the specified frameworks of the described semantic 
	 * and prints these findings in uniform style and layout.
	 * 
	 * @param reasoner Reasoner used to generate the extension of the example's framework.
	 * @param description Description of the semantics, which will be derived by the reasoner.
	 * @param examples Exemplary argumentation frameworks, to demonstrate the validity of the reasoning.
	 */
	protected static void executeExamplesInUniformLayout(SerialisableExtensionReasoner reasoner, String description, DungTheory[] examples) {
		System.out.println(description + ":");
		for (int i = 0; i < examples.length-1; i++) {
			examineFrameworkWithReasonerInUniformLayout(examples[i], reasoner);
			System.out.println("");
		}
		examineFrameworkWithReasonerInUniformLayout(examples[examples.length-1], reasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
	}
	
	/**
	 * Uses the specified reasoner to deduce all extensions within the specified framework.
	 * 
	 * @param frameWork Problem instance, for which all extensions of the semantic, specified by the choice of reasoner, shall be found.
	 * @param reasoner Reasoner of a specific semantic, to generate all extension, using the concept of serialisability.
	 */
	protected static void examineFrameworkWithReasonerInUniformLayout(DungTheory frameWork, SerialisableExtensionReasoner reasoner) {
		System.out.println("AF: " + frameWork);
		System.out.println("Extensions: " + reasoner.getModels(frameWork));
	}
	
	/**
	 * 
	 * @param args No input required.
	 */
	public static void main(String[] args) {
		DungTheory[] examples = new DungTheory[] {buildExample1(), buildExample2(), buildExample3()};
		
		SerialisedAdmissibleReasoner admReasoner = new SerialisedAdmissibleReasoner();
		executeExamplesInUniformLayout(admReasoner, "Admissible Semantics", examples);
		
		SerialisedCompleteReasoner coReasoner = new SerialisedCompleteReasoner();
		executeExamplesInUniformLayout(coReasoner, "Complete Semantics", examples);
		
		SerialisedGroundedReasoner grReasoner = new SerialisedGroundedReasoner();
		executeExamplesInUniformLayout(grReasoner, "Grounded Semantics", examples);
		
		SerialisedPreferredReasoner prReasoner = new SerialisedPreferredReasoner();
		executeExamplesInUniformLayout(prReasoner, "Preferred Semantics", examples);
		
		SerialisedStableReasoner stReasoner = new SerialisedStableReasoner();
		executeExamplesInUniformLayout(stReasoner, "Stable Semantics", examples);
		
		SerialisedUnchallengedReasoner ucReasoner = new SerialisedUnchallengedReasoner();
		executeExamplesInUniformLayout(ucReasoner, "Unchallenged Semantics", examples);
	}	
}

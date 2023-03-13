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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
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
	 * 
	 * @param reasoner Reasoner for a specific type of serialisable semantics (e.g. {@link SerialisedAdmissibleReasoner})
	 */
	public static void example1(SerialisableExtensionReasoner reasoner) {
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
		
		System.out.println("AF: " + af);
		System.out.println("Extensions: " + reasoner.getModels(af));
	}
	/**
	 * 
	 * @param reasoner Reasoner for a specific type of serialisable semantics (e.g. {@link SerialisedAdmissibleReasoner})
	 */
	public static void example2(SerialisableExtensionReasoner reasoner) {
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
		
		System.out.println("AF: " + af);
		System.out.println("Extensions: " + reasoner.getModels(af));
	}
	
	/**
	 * 
	 * @param reasoner Reasoner for a specific type of serialisable semantics (e.g. {@link SerialisedAdmissibleReasoner})
	 */
	public static void example3(SerialisableExtensionReasoner reasoner) {
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

		
		System.out.println("AF: " + af);
		System.out.println("Extensions: " + reasoner.getModels(af));
	}
	
	/**
	 * 
	 * @param args No input required.
	 */
	public static void main(String[] args) {
		SerialisedAdmissibleReasoner admReasoner = new SerialisedAdmissibleReasoner();
		System.out.println("Admissible Semantics:");
		example1(admReasoner);
		System.out.println("");
		example2(admReasoner);
		System.out.println("");
		example3(admReasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
		
		SerialisedCompleteReasoner coReasoner = new SerialisedCompleteReasoner();
		System.out.println("Complete Semantics:");
		example1(coReasoner);
		System.out.println("");
		example2(coReasoner);
		System.out.println("");
		example3(coReasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
		
		SerialisedGroundedReasoner grReasoner = new SerialisedGroundedReasoner();
		System.out.println("Grounded Semantics:");
		example1(grReasoner);
		System.out.println("");
		example2(grReasoner);
		System.out.println("");
		example3(grReasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
		
		SerialisedPreferredReasoner prReasoner = new SerialisedPreferredReasoner();
		System.out.println("Preferred Semantics:");
		example1(prReasoner);
		System.out.println("");
		example2(prReasoner);
		System.out.println("");
		example3(prReasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
		
		SerialisedStableReasoner stReasoner = new SerialisedStableReasoner();
		System.out.println("Stable Semantics:");
		example1(stReasoner);
		System.out.println("");
		example2(stReasoner);
		System.out.println("");
		example3(stReasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
		
		SerialisedUnchallengedReasoner ucReasoner = new SerialisedUnchallengedReasoner();
		System.out.println("Unchallenged Semantics:");
		example1(ucReasoner);
		System.out.println("");
		example2(ucReasoner);
		System.out.println("");
		example3(ucReasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
	}
}

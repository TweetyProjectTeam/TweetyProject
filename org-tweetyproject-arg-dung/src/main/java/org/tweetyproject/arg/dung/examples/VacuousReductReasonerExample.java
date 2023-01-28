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

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleConflictFreeReasoner;
import org.tweetyproject.arg.dung.reasoner.StronglyUndisputedReasoner;
import org.tweetyproject.arg.dung.reasoner.UndisputedReasoner;
import org.tweetyproject.arg.dung.reasoner.VacuousReductReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * example for the vacuous reduct reasoner
 * @author Matthias Thimm
 *
 */
public class VacuousReductReasonerExample {
	/**
	 * 
	 * @param reasoner the reasoner 
	 */
	public static void example1(AbstractExtensionReasoner reasoner) {
		// AF: ({a,b},{(a,a),(a,b)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		af.add(a);
		af.add(b);
		af.add(new Attack(a,a));
		af.add(new Attack(a,b));
		
		System.out.println("AF: " + af);
		System.out.println("Extensions: " + reasoner.getModels(af));
	}
	/**
	 * 
	 * @param reasoner the reasoner
	 */
	public static void example2(AbstractExtensionReasoner reasoner) {
		// AF: ({a1,a2,a3,b},{(a1,a3),(a3,a2),(a2,a1),(a1,b)})
		DungTheory af = new DungTheory();
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument a3 = new Argument("a3");
		Argument b = new Argument("b");
		af.add(a1);
		af.add(a2);
		af.add(a3);
		af.add(b);
		af.add(new Attack(a1,a3));
		af.add(new Attack(a3,a2));
		af.add(new Attack(a2,a1));
		af.add(new Attack(a1,b));
		
		System.out.println("AF: " + af);
		System.out.println("Extensions: " + reasoner.getModels(af));
	}
	
	/**
	 * 
	 * @param reasoner the reasoner
	 */
	public static void example3(AbstractExtensionReasoner reasoner) {
		// AF: ({a,b,c,d},{(a,b),(b,c),(c,a),(d,d),(d,a),(d,b),(d,c)})
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
		af.add(new Attack(b,c));
		af.add(new Attack(c,a));
		af.add(new Attack(d,d));
		af.add(new Attack(d,a));
		af.add(new Attack(d,b));
		af.add(new Attack(d,c));
		
		System.out.println("AF: " + af);
		System.out.println("Extensions: " + reasoner.getModels(af));
	}
	/**
	 * 
	 * @param args arguments
	 */
	public static void main(String[] args) {
		VacuousReductReasoner udReasoner = new UndisputedReasoner();
		System.out.println("Undisputed (=cf^adm) Semantics:");
		example1(udReasoner);
		example2(udReasoner);
		example3(udReasoner);
		System.out.println("================");
		
		VacuousReductReasoner sudReasoner = new StronglyUndisputedReasoner();
		System.out.println("Strongly Undisputed (=cf^cf^adm) Semantics:");
		example1(sudReasoner);
		example2(sudReasoner);
		example3(sudReasoner);
		System.out.println("================");
		
		VacuousReductReasoner cf3Reasoner = new VacuousReductReasoner(new SimpleConflictFreeReasoner(),sudReasoner);
		System.out.println("cf^cf^cf^adm Semantics:");
		example1(cf3Reasoner);
		example2(cf3Reasoner);
		example3(cf3Reasoner);
		System.out.println("================");
		
		VacuousReductReasoner cf4Reasoner = new VacuousReductReasoner(new SimpleConflictFreeReasoner(),cf3Reasoner);
		System.out.println("cf^cf^cf^cf^adm Semantics:");
		example1(cf4Reasoner);
		example2(cf4Reasoner);
		example3(cf4Reasoner);
	}
}

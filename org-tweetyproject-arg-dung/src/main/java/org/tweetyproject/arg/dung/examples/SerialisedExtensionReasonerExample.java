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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.List;

/**
 * This class summarises examples displaying the usage of {@link SerialisedExtensionReasoner}
 * for different types of serialisable semantics.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation', Argument & Computation (2022)"
 *
 * @see "Lars Bengel and Matthias Thimm. 'Serialisable Semantics for Abstract
 * Argumentation', Proceedings of COMMA'22 (2022)"
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class SerialisedExtensionReasonerExample {

	public static void main(String[] args) {
		Collection<DungTheory> examples = List.of(new DungTheory[]{example1(), example2(), example3()});

		SerialisedExtensionReasoner admReasoner = new SerialisedExtensionReasoner(Semantics.ADM);
		executeExamples(admReasoner, examples);

		SerialisedExtensionReasoner coReasoner = new SerialisedExtensionReasoner(Semantics.CO);
		executeExamples(coReasoner, examples);

		SerialisedExtensionReasoner grReasoner = new SerialisedExtensionReasoner(Semantics.GR);
		executeExamples(grReasoner, examples);

		SerialisedExtensionReasoner prReasoner = new SerialisedExtensionReasoner(Semantics.PR);
		executeExamples(prReasoner, examples);

		SerialisedExtensionReasoner stReasoner = new SerialisedExtensionReasoner(Semantics.ST);
		executeExamples(stReasoner, examples);

		SerialisedExtensionReasoner ucReasoner = new SerialisedExtensionReasoner(Semantics.UC);
		executeExamples(ucReasoner, examples);
	}

	/**
	 * Execute the given reasoner for all given examples
	 * @param reasoner    some serialisation reasoner
	 * @param examples    exemplary argumentation frameworks
	 */
	protected static void executeExamples(SerialisedExtensionReasoner reasoner, Collection<DungTheory> examples) {
		System.out.println(reasoner.getSemantics().description() + ":");
		for (DungTheory theory : examples) {
			System.out.println("AF: " + theory);
			System.out.println("Extensions: " + reasoner.getModels(theory));
			System.out.println("Sequences: " + reasoner.getSequences(theory));
			System.out.println("Serialisation:\n" + reasoner.getSerialisationGraph(theory).prettyPrint());
		}
		System.out.println("======================================================================================================\n");
	}

	/**
	 * Builds an example argumentation framework
	 *
	 * @return AF: ({a,b,c,d,e,f},{(a,a),(a,b),(b,a),(b,d),(d,c),(e,c),(e,f),(f,e)})
	 */
	public static DungTheory example1() {
		// AF: ({a,b,c,d,e,f},{(a,a),(a,b),(b,a),(b,d),(d,c),(e,c),(e,f),(f,e)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		af.add(a,b,c,d,e,f);
		af.addAttack(a, a);
		af.addAttack(a, b);
		af.addAttack(b, a);
		af.addAttack(b, d);
		af.addAttack(d, c);
		af.addAttack(e, c);
		af.addAttack(e, f);
		af.addAttack(f, e);

		return af;
	}

	/**
	 * Builds an example argumentation framework
	 *
	 * @return AF: ({a,b,c,d,e,f},{(a,b),(a,c),(b,a),(b,c),(c,e),(d,e),(e,d),(e,f),(f,e)})
	 */
	public static DungTheory example2() {
		// AF: ({a,b,c,d,e,f},{(a,b),(a,c),(b,a),(b,c),(c,e),(d,e),(e,d),(e,f),(f,e)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		af.add(a,b,c,d,e,f);
		af.addAttack(a, b);
		af.addAttack(a, c);
		af.addAttack(b, a);
		af.addAttack(b, c);
		af.addAttack(c, e);
		af.addAttack(d, e);
		af.addAttack(e, d);
		af.addAttack(e, f);
		af.addAttack(f, e);

		return af;
	}

	/**
	 * Builds an example argumentation framework
	 *
	 * @return AF: ({a,b,c,d},{(a,b),(b,a),(c,d),(d,b)})
	 */
	public static DungTheory example3() {
		// AF: ({a,b,c,d},{(a,b),(b,a),(c,d),(d,b)})
		DungTheory af = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		af.add(a,b,c,d);
		af.addAttack(a, b);
		af.addAttack(b, a);
		af.addAttack(c, d);
		af.addAttack(d, b);

		return af;
	}
}

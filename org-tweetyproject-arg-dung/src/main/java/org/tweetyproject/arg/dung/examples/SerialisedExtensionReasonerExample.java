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
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.HashSet;

/**
 * This class summarises examples displaying the usage of {@link SerialisedExtensionReasoner}
 * for different types of serialisable semantics.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation', Argument & Computation, (2022)"
 *
 * @see "Lars Bengel and Matthias Thimm. 'Serialisable Semantics for Abstract Argumentation', Proceedings of COMMA'22, (2022)"
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class SerialisedExtensionReasonerExample {
	/**
	 * Execute the example
	 * @param args cmdline arguments
	 */
	public static void main(String[] args) {
		// Use a serialised reasoner for admissible sets
		SerialisedExtensionReasoner admReasoner = new SerialisedExtensionReasoner(Semantics.ADM);
		executeExample(admReasoner, example1());

		// Define a serialised reasoner for a novel semantics
		SerialisedExtensionReasoner novelReasoner = new SerialisedExtensionReasoner(SelectionFunction.ADMISSIBLE, TerminationFunction.UNCHALLENGED);
		executeExample(novelReasoner, example2());

		// Define a serialised reasoner via novel selection and termination functions
		// e.g. only select challenged initial sets and terminate iff reduct has no self-attacks
		SerialisedExtensionReasoner novelReasoner2 = new SerialisedExtensionReasoner(
				(ua,uc,c) -> new HashSet<>(c),
				(theory,extension) -> !theory.hasSelfLoops()
		);
		executeExample(novelReasoner2, example3());
	}

	/**
	 * Execute the given reasoner for the given example
	 * @param reasoner	some serialisation reasoner
	 * @param theory    exemplary argumentation framework
	 */
	protected static void executeExample(SerialisedExtensionReasoner reasoner, DungTheory theory) {
		System.out.println(reasoner.getSemantics().description() + ":");
		System.out.println("AF: " + theory);
		System.out.println("Extensions: " + reasoner.getModels(theory));
		System.out.println("Sequences: " + reasoner.getSequences(theory));
		System.out.println("Serialisation:\n" + reasoner.getSerialisationGraph(theory).prettyPrint());
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

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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.prob.examples;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.analysis.PAInconsistencyMeasure;
import org.tweetyproject.arg.prob.dynamics.*;
import org.tweetyproject.arg.prob.semantics.*;
import org.tweetyproject.arg.prob.syntax.PartialProbabilityAssignment;
import org.tweetyproject.math.func.EntropyFunction;
import org.tweetyproject.math.norm.*;
import org.tweetyproject.math.probability.Probability;

/**
 * Example code for the PAInconsistencyMeasure.
 *
 * @author Matthias Thimm
 *
 */
public class PAInconsistencyMeasureExample {

	/** Default */
	public PAInconsistencyMeasureExample() {

	}

	/**
	 * This method demonstrates how to calculate an inconsistency measure for
	 * argumentation theory
	 * with a partial probability assignment and apply a change operator to update
	 * the probability
	 * assignment based on argumentation semantics and entropy.
	 *
	 * The demonstration includes the following steps:
	 * 1. Constructing a argumentation theory with three arguments (A, B, and
	 * C) and three attacks.
	 * 2. Creating a partial probability assignment with a specific probability for
	 * argument A.
	 * 3. Using a PA inconsistency measure to assess the inconsistency of the
	 * partial probability assignment.
	 * 4. Applying a change operator to update the partial probability assignment
	 * based on a coherent semantics
	 * and entropy-based reasoning.
	 *
	 * @param args Command-line arguments (not used in this example).
	 */
	public static void main(String[] args) {
		DungTheory theory = new DungTheory();
		Argument a = new Argument("A");
		Argument b = new Argument("B");
		Argument c = new Argument("C");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(new Attack(a, b));
		theory.add(new Attack(b, c));
		theory.add(new Attack(a, c));

		PartialProbabilityAssignment ppa = new PartialProbabilityAssignment();
		ppa.put(a, new Probability(0.9));

		PAInconsistencyMeasure mes = new PAInconsistencyMeasure(new PNorm(2), theory, new SemiOptimisticPASemantics());

		System.out.println(mes.inconsistencyMeasure(ppa));

		ChangeOperator op = new PAUpdateOperator(new CoherentPASemantics(), new EntropyNorm<Extension<DungTheory>>(),
				new EntropyFunction());

		System.out.println(op.change(ppa, theory));
	}
}

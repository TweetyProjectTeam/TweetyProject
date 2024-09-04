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
package org.tweetyproject.logics.rpcl.examples;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.rpcl.reasoner.RpclMeReasoner;
import org.tweetyproject.logics.rpcl.semantics.AggregatingSemantics;
import org.tweetyproject.logics.rpcl.syntax.RelationalProbabilisticConditional;
import org.tweetyproject.logics.rpcl.syntax.RpclBeliefSet;
import org.tweetyproject.math.opt.solver.OctaveSqpSolver;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.math.probability.Probability;

/**
 *  Example code illustrating relational probabilistic conditional logic and reasoning with it.
 */
public class RpclMeReasonerExample {

	/** Default */
	public RpclMeReasonerExample() {
	}


	/**
	 * Example
	 * @param args cmd args
	 */
	public static void main(String[] args){
		OctaveSqpSolver.setPathToOctave("/usr/local/octave/3.8.0/bin/octave");
		Solver.setDefaultGeneralSolver(new OctaveSqpSolver());
		Predicate a = new Predicate("a", 1);
		Predicate b = new Predicate("b", 1);
		Constant c1 = new Constant("c1");
		Constant c2 = new Constant("c2");
		org.tweetyproject.logics.commons.syntax.Variable x = new org.tweetyproject.logics.commons.syntax.Variable("X");
		org.tweetyproject.logics.fol.syntax.FolAtom atomA = new org.tweetyproject.logics.fol.syntax.FolAtom(a);
		atomA.addArgument(x);
		org.tweetyproject.logics.fol.syntax.FolAtom atomB = new org.tweetyproject.logics.fol.syntax.FolAtom(b);
		atomB.addArgument(x);
		RelationalProbabilisticConditional pc = new RelationalProbabilisticConditional(atomA,atomB,new Probability(0.3));

		RpclBeliefSet bs = new RpclBeliefSet();
		bs.add(pc);

		FolSignature sig = new FolSignature();
		sig.add(a);
		sig.add(b);
		sig.add(c1);
		sig.add(c2);

		System.out.println(bs);

		RpclMeReasoner reasoner = new RpclMeReasoner(new AggregatingSemantics());


		System.out.println(reasoner.getModel(bs,sig));

		org.tweetyproject.logics.fol.syntax.FolAtom atomAC = new org.tweetyproject.logics.fol.syntax.FolAtom(a);
		atomAC.addArgument(c1);
		org.tweetyproject.logics.fol.syntax.FolAtom atomBC = new org.tweetyproject.logics.fol.syntax.FolAtom(b);
		atomBC.addArgument(c1);

		System.out.println(reasoner.query(bs,atomAC));
		System.out.println(reasoner.query(bs,atomBC));


	}
}

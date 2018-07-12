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
package net.sf.tweety.logics.rpcl.examples;

import net.sf.tweety.commons.TweetyConfiguration;
import net.sf.tweety.commons.TweetyLogging;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.rpcl.*;
import net.sf.tweety.logics.rpcl.semantics.*;
import net.sf.tweety.logics.rpcl.syntax.*;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.opt.solver.OctaveSqpSolver;
import net.sf.tweety.math.probability.*;

public class RpclMeReasonerExample {
	public static void main(String[] args){
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.ERROR;
		TweetyLogging.initLogging();
		OctaveSqpSolver.setPathToOctave("/usr/local/octave/3.8.0/bin/octave");
		Solver.setDefaultGeneralSolver(new OctaveSqpSolver());
		Predicate a = new Predicate("a", 1);
		Predicate b = new Predicate("b", 1);
		Constant c1 = new Constant("c1");		
		Constant c2 = new Constant("c2");
		net.sf.tweety.logics.commons.syntax.Variable x = new net.sf.tweety.logics.commons.syntax.Variable("X");
		net.sf.tweety.logics.fol.syntax.FOLAtom atomA = new net.sf.tweety.logics.fol.syntax.FOLAtom(a);
		atomA.addArgument(x);		
		net.sf.tweety.logics.fol.syntax.FOLAtom atomB = new net.sf.tweety.logics.fol.syntax.FOLAtom(b);
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
		
		
		System.out.println(reasoner.getMeDistribution(bs,sig));
		
		net.sf.tweety.logics.fol.syntax.FOLAtom atomAC = new net.sf.tweety.logics.fol.syntax.FOLAtom(a);
		atomAC.addArgument(c1);
		net.sf.tweety.logics.fol.syntax.FOLAtom atomBC = new net.sf.tweety.logics.fol.syntax.FOLAtom(b);
		atomBC.addArgument(c1);
		
		System.out.println(reasoner.query(bs,atomAC));
		System.out.println(reasoner.query(bs,atomBC));
		
		
	}
}

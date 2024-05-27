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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.reasoner.*;
import org.tweetyproject.logics.pl.sat.MaxSatSolver;
import org.tweetyproject.logics.pl.sat.OpenWboSolver;

/**
 * Illustrates the use of reasoner.KOptimisationReasoner
 */
public class OptimisationExample {
	/**
	 * Illustrates the use of reasoner.KOptimisationReasoner
	 * @param args na
	 */
	public static void main(String[] args) {
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
		af.addAttack(a, b);
		af.addAttack(b, c);
		af.addAttack(d, a);
		af.addAttack(b, d);
		af.addAttack(c, f);
		af.addAttack(f, c);
		af.addAttack(d, e);
		af.addAttack(e, d);
		af.addAttack(e, e);
		af.addAttack(e, f);
		
		MaxSatSolver maxsat = new OpenWboSolver("/Users/mthimm/Documents/software/misc_bins/open-wbo_2.1");
		
		KOptimisationReasoner stable_k_opt = new MaxSatKStableReasoner(maxsat);
		KOptimisationReasoner stable_k_gcf = new GCF_GreedyKApproximationReasoner();
		KOptimisationReasoner stable_k_scf = new SCF_GreedyKApproximationReasoner();
		
		KOptimisationReasoner stableast_k_opt = new MaxSatKStableAstReasoner(maxsat);
		KOptimisationReasoner stableast_k_gfr = new GFR_GreedyKApproximationReasoner();
		KOptimisationReasoner stableast_k_sfr = new SFR_GreedyKApproximationReasoner();
		
		for(Argument x: af) {
			System.out.println(x + ": " +  stable_k_opt.query(af, a) + " (OPT stable)");
			System.out.println(x + ": " +  stable_k_gcf.query(af, a) + " (GCF)");
			System.out.println(x + ": " +  stable_k_scf.query(af, a) + " (SCF)");
			System.out.println(x + ": " +  stableast_k_opt.query(af, a) + " (OPT stable*)");
			System.out.println(x + ": " +  stableast_k_gfr.query(af, a) + " (GFR)");
			System.out.println(x + ": " +  stableast_k_sfr.query(af, a) + " (SFR)");
			System.out.println();
		}
		
		
	}
}

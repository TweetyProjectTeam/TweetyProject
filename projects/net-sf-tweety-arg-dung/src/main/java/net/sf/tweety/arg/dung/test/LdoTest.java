/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung.test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.ldo.semantics.LdoInterpretation;
import net.sf.tweety.arg.dung.ldo.syntax.LdoArgument;
import net.sf.tweety.arg.dung.ldo.syntax.LdoBoxModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoDiamondModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoFormula;
import net.sf.tweety.arg.dung.ldo.syntax.LdoGraphBoxModality;
import net.sf.tweety.arg.dung.ldo.syntax.LdoGraphDiamondModality;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;

public class LdoTest {

	public static void testDivider(){
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Attack ab = new Attack(a,b);
		Attack bc = new Attack(b,c);
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(ab);
		theory.add(bc);
		
		System.out.println("Argumentation framework G=" + theory);
		System.out.println();
		
		LdoFormula f = c.getLdoFormula();
		
		System.out.println(f.getDividers(theory, Semantics.GROUNDED_SEMANTICS));
	}
	
	public static void main(String[] args){
		testDivider(); System.exit(0);
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Attack ab = new Attack(a,b);
		Attack bc = new Attack(b,c);
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(ab);
		theory.add(bc);
		
		System.out.println("Grounded semantics");
		System.out.println("Argumentation framework G=" + theory);
		System.out.println();
				
		List<LdoFormula> queries = new LinkedList<LdoFormula>();
		queries.add(a.getLdoFormula());
		queries.add(b.getLdoFormula());
		queries.add(c.getLdoFormula());
		Set<LdoArgument> refLower = new HashSet<LdoArgument>();
		refLower.add(b.getLdoArgument());
		Set<LdoArgument> refUpper = new HashSet<LdoArgument>();
		refUpper.add(a.getLdoArgument());
		refUpper.add(b.getLdoArgument());
		refUpper.add(c.getLdoArgument());
		queries.add(new LdoGraphDiamondModality(new LdoDiamondModality(b.getLdoFormula()),refLower,refUpper));
		queries.add(new LdoGraphBoxModality(new LdoDiamondModality(b.getLdoFormula()),refLower,refUpper));
		
		
		LdoInterpretation i = new LdoInterpretation(theory, Semantics.GROUNDED_SEMANTICS);
		for(LdoFormula f: queries)
			System.out.println("G models '" + f + "'?  " + i.satisfies(f));
		
		
		System.out.println();
		System.out.println("=========");
		System.out.println("EXAMPLE 3");
		System.out.println("=========");
		System.out.println();
		
		theory = new DungTheory();
		Argument alpha = new Argument("alpha");
		Argument beta = new Argument("beta");
		Argument gamma = new Argument("gamma");
		Argument delta = new Argument("delta");
		theory.add(alpha);
		theory.add(beta);
		theory.add(gamma);
		theory.add(delta);
		theory.add(new Attack(alpha,beta));
		theory.add(new Attack(beta,alpha));
		theory.add(new Attack(beta,gamma));
		theory.add(new Attack(alpha,gamma));
		theory.add(new Attack(gamma,delta));
		
		System.out.println("Preferred semantics");
		SatSolver.setDefaultSolver(new Sat4jSolver());
		System.out.println("Argumentation framework G=" + theory);
		System.out.println();
		i = new LdoInterpretation(theory, Semantics.PREFERRED_SEMANTICS);
		queries = new LinkedList<LdoFormula>();
		queries.add(new LdoDiamondModality(alpha.getLdoArgument().combineWithAnd(delta.getLdoArgument().combineWithAnd((LdoFormula)beta.getLdoArgument().complement().combineWithAnd(gamma.getLdoArgument().complement())))));
		queries.add(new LdoDiamondModality((LdoFormula)alpha.getLdoArgument().complement().combineWithAnd(delta.getLdoArgument().combineWithAnd((LdoFormula)beta.getLdoArgument().combineWithAnd(gamma.getLdoArgument().complement())))));
		queries.add(new LdoBoxModality(delta.getLdoArgument().combineWithAnd(gamma.getLdoArgument().complement())));
		
		for(LdoFormula f: queries)
			System.out.println("G models '" + f + "'?  " + i.satisfies(f));
	}
	
}

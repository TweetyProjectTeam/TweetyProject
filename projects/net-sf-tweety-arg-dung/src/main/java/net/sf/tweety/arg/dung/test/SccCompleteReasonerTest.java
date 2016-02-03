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

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.SccCompleteReasoner;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;

public class SccCompleteReasonerTest {

	public static void main(String[] args){
		SatSolver.setDefaultSolver(new Sat4jSolver());
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		Argument g = new Argument("g");
		Argument h = new Argument("h");
		Argument i = new Argument("i");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(f);
		theory.add(g);
		theory.add(h);
		theory.add(i);
		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,d));
		theory.add(new Attack(d,c));
		theory.add(new Attack(d,e));
		theory.add(new Attack(e,f));
		theory.add(new Attack(f,g));
		theory.add(new Attack(g,e));
		theory.add(new Attack(c,h));
		theory.add(new Attack(i,h));
		theory.add(new Attack(h,i));
		
		
		SccCompleteReasoner reasoner = new SccCompleteReasoner(theory);
		
		System.out.println(reasoner.getExtensions());
		
	}
}

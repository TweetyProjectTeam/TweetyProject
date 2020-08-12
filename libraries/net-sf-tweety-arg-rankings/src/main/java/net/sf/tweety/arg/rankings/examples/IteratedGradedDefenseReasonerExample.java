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
 package net.sf.tweety.arg.rankings.examples;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.reasoner.IteratedGradedDefenseReasoner;

/**
 * Example code for using the iterated graded semantics by Grossi/Modgil.
 * 
 * @author Matthias Thimm
 *
 */
public class IteratedGradedDefenseReasonerExample {

	public static void main(String[] args){
		// See [Grossi, Modgil. On the Graded Acceptability of Arguments. IJCAI 2015]
		// This is Figure 3
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		Argument g = new Argument("g");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(f);
		theory.add(g);
		theory.add(new Attack(d,c));
		theory.add(new Attack(c,a));
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(e,b));
		theory.add(new Attack(f,e));
		theory.add(new Attack(g,e));
		
		IteratedGradedDefenseReasoner reasoner = new IteratedGradedDefenseReasoner();
		
		for(int m = 1; m < theory.size(); m++)
			for(int n = 1; n < theory.size(); n++)
				System.out.println(m + "," + n + " : " + reasoner.getAllMNCompleteExtensions(theory,m, n));
	}
}

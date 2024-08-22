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
package org.tweetyproject.arg.rankings.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.SerialisableRankingReasoner;

public class SerialisableRankingReasonerExample {
	public static void main(String[] args) {
		// Construct AAF
		DungTheory aaf = new DungTheory();
		Argument a = new Argument("a");
		Argument b1 = new Argument("b1");
		Argument b2 = new Argument("b2");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		Argument g = new Argument("g");
		Argument h = new Argument("h");
		aaf.add(a);
		aaf.add(b1);
		aaf.add(b2);
		aaf.add(c);
		aaf.add(d);
		aaf.add(e);
		aaf.add(f);
		aaf.add(g);
		aaf.add(h);
		aaf.add(new Attack(a,b1));
		aaf.add(new Attack(a,b2));
		aaf.add(new Attack(b1,c));
		aaf.add(new Attack(b2,c));
		aaf.add(new Attack(c,d));
		aaf.add(new Attack(d,a));
		aaf.add(new Attack(c,e));
		aaf.add(new Attack(e,f));
		aaf.add(new Attack(g,h));
		aaf.add(new Attack(h,f));
		
		
		SerialisableRankingReasoner reasoner = new SerialisableRankingReasoner();
		
		System.out.println(reasoner.getModel(aaf));
	}

    /** Default Constructor */
    public SerialisableRankingReasonerExample(){}
}

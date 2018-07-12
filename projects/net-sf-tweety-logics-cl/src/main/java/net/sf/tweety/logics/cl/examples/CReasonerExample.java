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
package net.sf.tweety.logics.cl.examples;

import net.sf.tweety.logics.cl.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pl.syntax.*;

public class CReasonerExample {
	public static void main(String[] args){
		Proposition f = new Proposition("f");
		Proposition b = new Proposition("b");
		Proposition p = new Proposition("p");
		
		Conditional c1 = new Conditional(b,f);
		Conditional c2 = new Conditional(p,b);
		Conditional c3 = new Conditional(p,new Negation(f));
		
		ClBeliefSet bs = new ClBeliefSet();
		bs.add(c1);
		bs.add(c2);
		bs.add(c3);
		
		System.out.println(bs);
		
		BruteForceCReasoner reasoner = new BruteForceCReasoner();
		
		System.out.println(reasoner.getCRepresentation(bs));
		
	}
}

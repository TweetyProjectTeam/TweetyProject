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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.setaf.examples;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.syntax.SetAttack;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.arg.setaf.reasoners.*;


/**
 * Examples of SetAf Theorys and their semantics
 * 
 * @author Sebastian Franke
 *
 */
public class SetAfTheoryTest {
	
	public static void main(String[] args) {
		SetAf s = new SetAf();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		s.add(a);
		s.add(b);
		s.add(c);
		s.add(d);
		
		Set<Argument> a1 = new HashSet<Argument>();
		a1.add(b);
		a1.add(d);
		
		Set<Argument> a2 = new HashSet<Argument>();
		a2.add(c);
		a2.add(a);
		
		
		s.add(new SetAttack(a1, a));
		s.add(new SetAttack(a2, c));
		//s.remove(a);
		System.out.println(s.toString());
		//System.out.println(s.getComplementGraph(0));
		SimpleGroundedSetAfReasoner gr = new SimpleGroundedSetAfReasoner();
		SimpleAdmissibleSetAfReasoner ad = new SimpleAdmissibleSetAfReasoner();
		SimplePreferredSetAfReasoner pr = new SimplePreferredSetAfReasoner();
		System.out.println("grounded: " + gr.getModel(s));
		System.out.println("admissible: " + ad.getModels(s));
		System.out.println("preferred: " + pr.getModels(s));


	}

}

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
package net.sf.tweety.arg.dung.examples;

import net.sf.tweety.arg.dung.reasoner.StratifiedLabelingReasoner;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.semantics.StratifiedLabeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 * Example code for using stratified labelings by Thimm/Kern-Isberner.
 * 
 * @author Matthias Thimm
 *
 */
public class StratifiedLabelingExample {

	public static void main(String[] args){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		//Argument e = new Argument("e");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		//theory.add(e);
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,b));
		theory.add(new Attack(d,c));
		theory.add(new Attack(c,d));
		
		StratifiedLabelingReasoner reasoner = new StratifiedLabelingReasoner(Semantics.STABLE_SEMANTICS);
		
		for(StratifiedLabeling labeling: reasoner.getModels(theory)){
			System.out.println(labeling);			
		}
	}
}

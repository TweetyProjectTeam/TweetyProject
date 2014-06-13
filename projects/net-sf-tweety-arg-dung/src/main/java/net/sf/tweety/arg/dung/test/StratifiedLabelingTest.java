/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.arg.dung.test;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.StratifiedLabelingReasoner;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.semantics.StratifiedLabeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

public class StratifiedLabelingTest {

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
		
		StratifiedLabelingReasoner reasoner = new StratifiedLabelingReasoner(theory,Semantics.STABLE_SEMANTICS, Semantics.CREDULOUS_INFERENCE);
		
		for(StratifiedLabeling labeling: reasoner.getLabelings()){
			System.out.println(labeling);			
		}
	}
}

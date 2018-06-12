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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.examples;

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.AspicReasoner;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class AspicGeneratorExample {
	public static void main(String[] args) {		 
		AspicArgumentationTheory<PropositionalFormula> theory = RandomAspicArgumentationTheoryGenerator.next(10, 20, 4, 0.2);
		System.out.println(theory);
		System.out.println();
		for(AspicArgument<PropositionalFormula> arg: theory.getArguments())
			System.out.println(arg);
		System.out.println();	
		
		AspicReasoner<PropositionalFormula> reasoner = new AspicReasoner<PropositionalFormula>(Semantics.GR,Semantics.CREDULOUS_INFERENCE);
		
		PropositionalFormula query = new Proposition("A1");
		System.out.println(query + " " + reasoner.query(theory,query).getAnswerBoolean());
	}	
}

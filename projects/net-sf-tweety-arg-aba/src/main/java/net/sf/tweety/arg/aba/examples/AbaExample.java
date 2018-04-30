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
package net.sf.tweety.arg.aba.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.arg.aba.ABATheory;
import net.sf.tweety.arg.aba.FlatABAReasoner;
import net.sf.tweety.arg.aba.PreferredReasoner;
import net.sf.tweety.arg.aba.parser.ABAParser;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Shows some simple code for working with ABA
 * @author Matthias Thimm
 *
 */
public class AbaExample {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		SatSolver.setDefaultSolver(new Sat4jSolver());
		
		ABAParser<PropositionalFormula> parser = new ABAParser<PropositionalFormula>(new PlParser());
		ABATheory<PropositionalFormula> t = parser.parseBeliefBaseFromFile(AbaExample.class.getResource("/example2.aba").getFile());
		
		FlatABAReasoner<PropositionalFormula> r1 = new FlatABAReasoner<PropositionalFormula>(Semantics.PREFERRED_SEMANTICS, Semantics.CREDULOUS_INFERENCE);
		PreferredReasoner<PropositionalFormula> r2 = new PreferredReasoner<PropositionalFormula>(Semantics.CREDULOUS_INFERENCE);
		
		Assumption<PropositionalFormula> a = new Assumption<>(new Proposition("a"));
		System.out.println("a: " + r1.query(t,a).getAnswerBoolean());
		System.out.println("a: " + r2.query(t,a).getAnswerBoolean());
		
		System.out.println("as graph: " + t.asDungTheory());
	}
}

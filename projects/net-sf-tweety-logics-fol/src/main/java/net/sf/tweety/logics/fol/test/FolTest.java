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
package net.sf.tweety.logics.fol.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.FolTheoremProver;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

public class FolTest {

	public static void test() throws ParserException, IOException{
		FolParser parser = new FolParser();
		FolBeliefSet bs = new FolBeliefSet();
		FolSignature sig = new FolSignature();
		Sort animal = new Sort("Animal");
		sig.add(animal);
		List<Sort> l = new ArrayList<Sort>();
		l.add(animal);
		l.add(animal);
		sig.add(new Predicate("A",l));
		sig.add(new Constant("b",animal));
		sig.add(new Constant("c",animal));
		parser.setSignature(sig);		
		bs.add((FolFormula)parser.parseFormula("forall X: (forall Y: ((!A(X,Y) || A(Y,X)) && (!A(Y,X) || A(X,Y))))"));
		bs.add((FolFormula)parser.parseFormula("A(b,c)"));
		FolTheoremProver prover = FolTheoremProver.getDefaultProver();
		System.out.println(prover.query(bs, (FolFormula)parser.parseFormula("A(c,b)")));
	}
	
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		//test();System.exit(1);
		FolParser parser = new FolParser();		
		FolBeliefSet b = parser.parseBeliefBaseFromFile("examplebeliefbase.fologic");
		System.out.println(b);
		FolTheoremProver prover = FolTheoremProver.getDefaultProver();
		System.out.println(prover.query(b, (FolFormula)parser.parseFormula("Knows(martin,carl)")));
	}
}

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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.examples;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.writer.StandardFolWriter;

/**
 * Shows how to use the StandardFolWriter
 * @author Matthias Thimm
 *
 */
public class StandardFolWriterExample {
	public static void main(String[] args) throws ParserException, IOException {
		String filename = "/Users/mthimm/Desktop/test.fol";
				
		FolSignature sig = new FolSignature();
		Sort animal = new Sort("animal");
		sig.add(animal);
		Constant anna = new Constant("anna",animal);
		sig.add(anna);
		Constant bob = new Constant("bob",animal);
		sig.add(bob);
		Sort plant = new Sort("plant");
		sig.add(plant);
		Constant emma = new Constant("emma",plant);
		sig.add(emma);
		
		List<Sort> l = new ArrayList<Sort>();
		l.add(animal);
		Predicate flies = new Predicate("flies", l);
		sig.add(flies);
		l = new ArrayList<Sort>();
		l.add(animal);
		l.add(plant);
		Predicate eats = new Predicate("eats", l);
		sig.add(eats);
		l = new ArrayList<Sort>();
		l.add(animal);		
		Functor fatherOf = new Functor("fatherOf",l,animal);
		sig.add(fatherOf);
						
		FolBeliefSet b = new FolBeliefSet();
		b.setSignature(sig);
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		b.add(parser.parseFormula("forall X:(flies(X) => (exists Y:(eats(X,Y))))"));
		b.add(parser.parseFormula("forall X:(flies(X) => flies(fatherOf(X)))"));
		b.add(parser.parseFormula("flies(anna)"));
		b.add(parser.parseFormula("eats(bob,emma)"));
		
		//print to console
		System.out.println(b);
		
		//print to file
		StandardFolWriter writer = new StandardFolWriter(new FileWriter(filename));
		writer.printBase(b);
		writer.close();
		
		//parser file again and print tp console
		parser = new FolParser();
		System.out.println(parser.parseBeliefBaseFromFile(filename));
	}
}

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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.examples;

import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.reasoner.ClingoSolver;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * Examples for parsing simple ELP programs and for using Clingo to solve them.
 * 
 * @author Anna Gessler
 *
 */
public class ASPCore2ParserExample {

	public static void main(String[] args) throws ParseException {
		String str1 = ":- not isDead(walter), -isDead(petra).\n" + 
				"\n" + 
				"isAlive(schroedinger).\n" + 
				"\n" + 
				"isAlive(cat) :- -isDead(cat).\n" + 
				"\n" + 
				"% this is a comment\n" + 
				"\n" + 
				":- #min { 4 : isDead(cat), isAlive(cat); isAlive(cat)}.\n" + 
				"\n"
				+ "isAlive(cat)?";
		
		Program pr1 = ASPCore2Parser.parseProgram(str1);
		System.out.println("----------------\nParsed program:\n" + pr1);
		
		String str2 = "motive(harry). \n" + 
				"motive(sally). \n" + 
				"guilty(harry). \n" + 
				"innocent(Suspect) :- motive(Suspect), not guilty(Suspect). \n";
		
		Program pr2 = ASPCore2Parser.parseProgram(str2);
		System.out.println("----------------\nParsed program:\n" + pr2);
		
		ClingoSolver solver = new ClingoSolver("/home/anna/sw/asp/clingo");
		AnswerSetList as = solver.getModels(pr2);
		System.out.println(as);
		
		String es = "p(a). \n" + 
				"p(b).\n" + 
				"{q(X) : p(X)}. ";
		AnswerSetList as2 = solver.getModels(es);
		System.out.println(as2);
	}

}

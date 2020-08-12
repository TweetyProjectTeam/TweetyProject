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
package net.sf.tweety.arg.delp.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.reasoner.DelpReasoner;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import net.sf.tweety.arg.delp.syntax.DefeasibleLogicProgram;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * Shows how to parse and query a DeLP program.
 * 
 * @author Matthias Thimm
 *
 */
public class DeLPExample {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		DelpParser parser = new DelpParser();
		DefeasibleLogicProgram delp = parser.parseBeliefBaseFromFile(DeLPExample.class.getResource("/birds2.txt").getFile());
		DelpReasoner reasoner = new DelpReasoner(new GeneralizedSpecificity());
		
		FolFormula query = (FolFormula) parser.parseFormula("Fly(opus)");
		System.out.println(query + "\t" + reasoner.query(delp,query));
		
		query = (FolFormula) parser.parseFormula("Fly(tweety)");
		System.out.println(query + "\t" + reasoner.query(delp,query));
	}
}

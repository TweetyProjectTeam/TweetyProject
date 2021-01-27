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
package org.tweetyproject.arg.delp.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.tweetyproject.arg.delp.parser.DelpParser;
import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.GeneralizedSpecificity;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.syntax.FolFormula;

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

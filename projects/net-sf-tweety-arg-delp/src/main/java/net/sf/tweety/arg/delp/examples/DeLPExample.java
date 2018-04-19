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
package net.sf.tweety.arg.delp.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.arg.delp.DefeasibleLogicProgram;
import net.sf.tweety.arg.delp.DelpReasoner;
import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.ParserException;

/**
 * DeLP example code
 * @author Matthias Thimm
 *
 */
public class DeLPExample {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		DelpParser parser = new DelpParser();
		DefeasibleLogicProgram delp = parser.parseBeliefBaseFromFile(DeLPExample.class.getResource("/birds2.txt").getFile());
		DelpReasoner reasoner = new DelpReasoner(delp, new GeneralizedSpecificity());
		
		Formula query = parser.parseFormula("Fly(opus)");
		System.out.println(query + "\t" + reasoner.query(query));
		
		query = parser.parseFormula("Fly(tweety)");
		System.out.println(query + "\t" + reasoner.query(query));
	}
}

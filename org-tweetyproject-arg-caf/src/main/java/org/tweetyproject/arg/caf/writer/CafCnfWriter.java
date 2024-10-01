/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.caf.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;


/**
 * Writes an constrained argumentation framework into a file of the
 * CNF format including the constraint. Note that the order of the arguments may change
 * by using this writer.
 * 
 * @author Sandra Hoffmann
 *
 */
public class CafCnfWriter extends AbstractCafWriter{


	@Override
	public void write(ConstrainedArgumentationFramework caf, File f) throws IOException {
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.println("p af " + caf.size() + " " + caf.getAttacks().size());
		Map<Argument,Integer> map = new HashMap<Argument,Integer>();
		int idx = 1;
		for(Argument arg: caf)
			map.put(arg, idx++);
		for(Attack att: caf.getAttacks())
			writer.println(map.get(att.getAttacker()) + " -" + map.get(att.getAttacked()) + " 0");
		//map arguments in constraint to arguments in cnf format
		PlFormula constraint = caf.getConstraint();
		for(Proposition p: constraint.getAtoms()) {
			int numP = constraint.numberOfOccurrences(p);
			int idP = map.get(new Argument(p.toString()));
			for (int i = 0; i < numP; i++)
				constraint = constraint.replace(p, new Proposition(String.valueOf(idP)), i+1);			
		}
		writer.println("c " + constraint.toString());
		writer.close();		
	}
}

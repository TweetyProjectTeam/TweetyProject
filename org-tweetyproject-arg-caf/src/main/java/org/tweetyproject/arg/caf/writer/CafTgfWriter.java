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

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;

/**
 * Writes an constrained abstract argumentation framework into a file of the TGF format including the constraint given as a propositional formula.
 *
 * @author Sandra Hoffmann
 *
 */
public class CafTgfWriter extends AbstractCafWriter{

	/** Default */
	public CafTgfWriter(){

	}
	
	/**
	 * Writes the constrained argumentation framework to a file.
	 *
	 * @param caf       The ConstrainedArgumentationFramework to write.
	 * @param f         The File where the framework should be written.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 * */
	public void write(ConstrainedArgumentationFramework caf, File f) throws IOException {
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		for(Argument a: caf)
			writer.println(a.getName());
		writer.println("#");
		for(Attack att: caf.getAttacks())
			writer.println(att.getAttacker().getName() + " " + att.getAttacked().getName());
		writer.println("#");
		writer.println(caf.getConstraint().toString());
		writer.close();		
	}
}

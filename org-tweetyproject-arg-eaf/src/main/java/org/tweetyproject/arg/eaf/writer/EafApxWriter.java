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
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * Writes an epistemic abstract argumentation framework into a file of the APX format including the constraint given as a epistemic formula.
 * 
 * @author Sandra Hoffmann
 *
 */
public class EafApxWriter extends AbstractEafWriter{
	/** Default */
	public EafApxWriter(){

	}

	/**
	 * Writes the constrained argumentation framework to a file.
	 *
	 * @param eaf       The EpistemicArgumentationFramework to write.
	 * @param f         The File where the framework should be written.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 */
	public void write(EpistemicArgumentationFramework eaf, File f) throws IOException {
	    PrintWriter writer = new PrintWriter(f, "UTF-8");
		for(Argument a: eaf)
			writer.println("arg(" + a.getName() + ").");
		for(Attack att: eaf.getAttacks())
			writer.println("att(" + att.getAttacker().getName() + "," + att.getAttacked().getName() + ").");	
	    writer.println("constraint(" + eaf.getConstraint().toString()+ ").");
	    writer.close();
	}

}

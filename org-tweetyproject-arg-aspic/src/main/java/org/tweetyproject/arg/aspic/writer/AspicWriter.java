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
package org.tweetyproject.arg.aspic.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.aspic.syntax.InferenceRule;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;

/**
 * For writing ASPIC argumentation theories to disk.
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of formulas
 */
public class AspicWriter<T extends Invertable> {
	
	/** Writes the given ASPIC+ theory to disk.
	 * TODO: any order on inference rules is not yet written.
	 * 
	 * @param theory an ASPIC+ theory
	 * @param f a file
	 * @throws IOException if something went wrong.
	 */
	public void write(AspicArgumentationTheory<T> theory, File f) throws IOException{
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		for(InferenceRule<T> rule: theory) {
			writer.println(rule);
		}		
		writer.close();
	}
}

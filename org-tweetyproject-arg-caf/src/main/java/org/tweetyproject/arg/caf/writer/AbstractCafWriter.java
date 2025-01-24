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

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.parser.FileFormat;


/**
 * @author Sandra Hoffmann
 *
 */
public abstract class AbstractCafWriter {
	
	
	/**
	 * standard constructor
	 */
	public AbstractCafWriter() {
		super();
	}
	
	
	/**
	 * Retrieves the writer for the given file format.
	 * @param f some file format
	 * @return a writer or null if the format is not supported.
	 */
	public static AbstractCafWriter getWriter(FileFormat f){
		if(f.equals(FileFormat.TGF))
			return new CafTgfWriter();
		if(f.equals(FileFormat.APX))
			return new CafApxWriter();
		if(f.equals(FileFormat.CNF))
			return new CafCnfWriter();
		return null;
	}
	
	/**
	 * Writes the given file into an abstract argumentation framework
	 * @param caf an abstract argumentation framework
	 * @param f the file that will be overwritten. 
	 * @throws IOException for all errors concerning file reading/writing.
	 */
	public abstract void write(ConstrainedArgumentationFramework caf, File f) throws IOException;

}

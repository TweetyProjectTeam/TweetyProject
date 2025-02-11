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


import org.tweetyproject.arg.dung.parser.FileFormat;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * @author Sandra Hoffmann
 */
public abstract class AbstractEafWriter {
	
	/**
	 * standard constructor
	 */
	public AbstractEafWriter() {
		super();
	}
	
	
	/**
	 * Retrieves the writer for the given file format.
	 * @param f some file format
	 * @return a writer or null if the format is not supported.
	 */
	public static AbstractEafWriter getWriter(FileFormat f){
		if(f.equals(FileFormat.TGF))
			return new EafTgfWriter();
		if(f.equals(FileFormat.APX))
			return new EafApxWriter();
		if(f.equals(FileFormat.CNF))
			return new EafCnfWriter();
		return null;
	}
	
	/**
	 * Writes the given eaf into a file
	 * @param eaf an abstract argumentation framework
	 * @param f the file that will be overwritten. 
	 * @throws IOException for all errors concerning file reading/writing.
	 */
	public abstract void write(EpistemicArgumentationFramework eaf, File f) throws IOException;

}

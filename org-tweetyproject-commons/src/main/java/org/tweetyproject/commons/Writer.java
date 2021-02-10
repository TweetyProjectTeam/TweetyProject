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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons;

import java.io.*;

/**
 * This class represents an abstract writer for
 * writing objects into the file system. 
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public abstract class Writer {

	/**
	 * The object to be printed.
	 */
	protected Object input = null;
	
	/**
	 * Creates a new writer for the given object.
	 * @param obj an object.
	 */
	public Writer(Object obj){
		this.input = obj;
	}
	
	/**
	 * Creates a new empty writer.
	 */
	public Writer() {
	}
	
	/**
	 * Sets the object of this writer.
	 * @param obj some object
	 */
	public void setObjectToBePrinted(Object obj){
		this.input = obj;
	}
	
	/**
	 * Returns the object of this writer.
	 * @return the object of this writer.
	 */
	public Object getObjectToBePrinted(){
		return this.input;
	}
	
	/**
	 * Writes the object into a string.
	 * @return the string representing the object.
	 */
	public abstract String writeToString();
	
	/**
	 * Writes the object into the given file.
	 * @param filename the name of the file.
	 * @throws IOException if an IO issue occurs.
	 */
	public void writeToFile(String filename) throws IOException{
		String s = this.writeToString();
		try {
			File file = new File(filename);
			
			String newFilename = filename;
			//If the file already exists, create a new file instead of overwriting
			if (file.exists()) {
				String newFilenameFormat;
				int index = filename.lastIndexOf(".");
				if (index != -1) 
					newFilenameFormat = filename.substring(0, index) + "%03d" + filename.substring(index);
				else 
					newFilenameFormat = filename + "%03d";
				int fileNumber = 0;
				while (file.exists()) {
					newFilename = String.format(newFilenameFormat, fileNumber);
					file = new File(newFilename);
					fileNumber++;
				  }
			}
			
			//Write object to file
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(s);
			out.close();
			//System.out.println("Success: Wrote object to " + newFilename);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

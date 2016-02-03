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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons;

import java.io.*;

/**
 * This class represents an abstract writer for
 * writing objects into the file system. 
 * 
 * @author Matthias Thimm
 */
public abstract class Writer {

	/**
	 * 
	 */
	private Object obj;
	
	/**
	 * Creates a new writer for the given object.
	 * @param obj an object.
	 */
	public Writer(Object obj){
		this.obj = obj;
	}
	
	/**
	 * Sets the object of this writer.
	 */
	public void setObject(Object obj){
		this.obj = obj;
	}
	
	/**
	 * Returns the object of this writer.
	 * @return the object of this writer.
	 */
	public Object getObject(){
		return this.obj;
	}
	
	/**
	 * Writes the object into a string.
	 * @return the string representing the object.
	 */
	public abstract String writeToString();
	
	/**
	 * Writes the object into the given file.
	 * @param filename the name of the file.
	 */
	public void writeToFile(String filename) throws IOException{
		String s = this.writeToString();
		// TODO add check for overwriting and error handling
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		out.write(s);
		out.close();
	}
}

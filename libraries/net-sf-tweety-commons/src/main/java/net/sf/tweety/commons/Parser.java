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
package net.sf.tweety.commons;

import java.io.*;

/**
 * This class models an abstract parser for belief bases and formulas.
 * 
 * @author Matthias Thimm
 */
public abstract class Parser<T extends BeliefBase,S extends Formula> {

	/**
	 * Parses the file of the given filename into a belief base of the given type.
	 * @param filename the name of a file
	 * @return a belief base
	 * @throws FileNotFoundException if the file is not found 
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public T parseBeliefBaseFromFile(String filename) throws FileNotFoundException, IOException, ParserException{
		InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(filename));
		T bs = this.parseBeliefBase(reader);
		reader.close();
		return bs;
	}
	
	/**
	 * Parses the given text into a belief base of the given type.	 
	 * @param text a string
	 * @return a belief base.
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public T parseBeliefBase(String text) throws IOException, ParserException{
		return this.parseBeliefBase(new StringReader(text));
	}
	
	/**
	 * Parses the given reader into a belief base of the given type.
	 * @param reader a reader 
	 * @return a belief base
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public abstract T parseBeliefBase(Reader reader) throws IOException, ParserException;
	
	/**
	 * Parses the file of the given filename into a formula of the given type.
	 * @param filename the name of a file
	 * @return a formula
	 * @throws FileNotFoundException if the file is not found 
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public S parseFormulaFromFile(String filename) throws FileNotFoundException, IOException, ParserException{
		InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(filename));
		S f = this.parseFormula(reader);
		reader.close();
		return f;
	}
	
	/**
	 * Parses the given text into a formula of the given type.
	 * @param text a string
	 * @return a formula
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public S parseFormula(String text) throws IOException, ParserException{
		return this.parseFormula(new StringReader(text));
	}
	
	/**
	 * Parses the given reader into a formula of the given type.
	 * @param reader a reader
	 * @return a formula
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public abstract S parseFormula(Reader reader) throws IOException, ParserException;
	
	/**
	 * Checks whether the given string is a number.
	 * @param str some string
	 * @return "true" if the given string can be parsed as a number
	 */
	public static boolean isNumeric(String str){  
	  try{  
	    Double.parseDouble(str);  
	  }catch(NumberFormatException nfe) {
		  return false;  
	  }  
	  return true;  
	}
	
}

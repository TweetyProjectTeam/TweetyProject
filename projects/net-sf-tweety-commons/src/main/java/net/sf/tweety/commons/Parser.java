package net.sf.tweety.commons;

import java.io.*;

/**
 * This class models an abstract parser for belief bases and formulas.
 * 
 * @author Matthias Thimm
 */
public abstract class Parser<T extends BeliefBase> {

	/**
	 * Parses the file of the given filename into a belief base of the given type.
	 * @param filename the name of a file
	 * @return a belief base
	 * @throws FileNotFoundException if the file is not found 
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public T parseBeliefBaseFromFile(String filename) throws FileNotFoundException, IOException, ParserException{
		return this.parseBeliefBase(new InputStreamReader(new java.io.FileInputStream(filename)));
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
	public Formula parseFormulaFromFile(String filename) throws FileNotFoundException, IOException, ParserException{
		return this.parseFormula(new InputStreamReader(new java.io.FileInputStream(filename)));
	}
	
	/**
	 * Parses the given text into a formula of the given type.
	 * @param text a string
	 * @return a formula
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public Formula parseFormula(String text) throws IOException, ParserException{
		return this.parseFormula(new StringReader(text));
	}
	
	/**
	 * Parses the given reader into a formula of the given type.
	 * @param reader a reader
	 * @return a formula
	 * @throws Exception some parsing exceptions may be added here.
	 */
	public abstract Formula parseFormula(Reader reader) throws IOException, ParserException;
	
	
	
}

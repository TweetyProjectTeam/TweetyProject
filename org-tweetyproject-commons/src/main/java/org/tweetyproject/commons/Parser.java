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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models an abstract parser for belief bases and formulas.
 *
 * @param <T> the type of belief bases
 * @param <S> the type of formulas
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public abstract class Parser<T extends BeliefBase, S extends Formula> {
	
	/**
	 * Regular expression that contains symbols that appear in formulas and cannot be used as separators
	 * for belief bases.
	 */
	protected String illegalDelimitors = "[\\^\\|\\&!\\(\\)\\<\\>\\=\\^\\[\\]]|forall|exists ";

	/**
	 * Parses the file of the given filename into a belief base of the given type.
	 * 
	 * @param filename the name of a file
	 * @return a belief base
	 * @throws FileNotFoundException if the file is not found
	 * @throws IOException           if some IO issue occurred.
	 * @throws ParserException       some parsing exceptions may be added here.
	 */
	public T parseBeliefBaseFromFile(String filename) throws FileNotFoundException, IOException, ParserException {
		InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(filename));
		T bs = this.parseBeliefBase(reader);
		reader.close();
		return bs;
	}

	/**
	 * Parses the given text into a belief base of the given type.
	 * 
	 * @param text a string
	 * @return a belief base.
	 * @throws IOException     if some IO issue occurred.
	 * @throws ParserException some parsing exceptions may be added here.
	 */
	public T parseBeliefBase(String text) throws IOException, ParserException {
		return this.parseBeliefBase(new StringReader(text));
	}

	/**
	 * Parses the given reader into a belief base of the given type.
	 * 
	 * @param reader a reader
	 * @return a belief base
	 * @throws IOException     if some IO issue occurred.
	 * @throws ParserException some parsing exceptions may be added here.
	 */
	public abstract T parseBeliefBase(Reader reader) throws IOException, ParserException;

	/**
	 * Parses the file of the given filename into a list of belief bases of the given type. Belief
	 * bases are separated by three consecutive newline characters ("\n\n\n").
	 * 
	 * @param filename a string
	 * @return a list of belief bases in the order in which they appear in the input
	 *         string.
	 * @throws IOException if an IO error occurs
	 * @throws ParserException some parsing exception
	 */
	public List<T> parseListOfBeliefBasesFromFile(String filename) throws ParserException, IOException {
		String text = Files.readString(Path.of(filename));
		List<T> bs = this.parseListOfBeliefBases(text);
		return bs;
	}
	
	/**
	 * Parses the file of the given filename into a list of belief bases of the given type. Belief
	 * bases are separated by the given delimiter.
	 * 
	 * @param filename a string
	 * @param delimiter for separating belief bases
	 * @return a list of belief bases in the order in which they appear in the input
	 *         string.
	 * @throws IOException if an IO error occurs
	 * @throws ParserException some parsing exception
	 */
	public List<T> parseListOfBeliefBasesFromFile(String filename, String delimiter) throws ParserException, IOException {
		String text = Files.readString(Path.of(filename));
		List<T> bs = this.parseListOfBeliefBases(text, delimiter);
		return bs;
	}
	
	
	/**
	 * Parses the given text into a list of belief bases of the given type. Belief
	 * bases are separated by three consecutive newline characters ("\n\n\n").
	 * 
	 * @param text a string
	 * @return a list of belief bases in the order in which they appear in the input
	 *         string.
	 * @throws IOException if an IO error occurs
	 * @throws ParserException some parsing exception
	 */
	public List<T> parseListOfBeliefBases(String text) throws ParserException, IOException {
		String[] kbs_string = text.split("\n\n\n");
		ArrayList<T> kbs = new ArrayList<T>();
		for (String kb_string : kbs_string) {
			if (!kb_string.isBlank())
				kbs.add(this.parseBeliefBase(new StringReader(kb_string)));
		}
		return kbs;
	}
	
	/**
	 * Parses the given text into a list of belief bases of the given type. Belief
	 * bases are separated by the given delimiter.
	 * 
	 * @param text a string
	 * @param delimiter for separating belief bases
	 * @return a list of belief bases in the order in which they appear in the input
	 *         string.
	 * @throws IOException if an IO error occurs
	 * @throws ParserException some parsing exception
	 */
	public List<T> parseListOfBeliefBases(String text, String delimiter) throws ParserException, IOException {
		if (delimiter.matches(".*" + illegalDelimitors + ".*"))
			throw new IllegalArgumentException("The given delimiter is similar to characters that are likely to appear in formulas. Try using a more unique delimiter.");
		String[] kbs_string = text.split(delimiter);
		ArrayList<T> kbs = new ArrayList<T>();
		for (String kb_string : kbs_string) {
			if (!kb_string.isBlank())
				kbs.add(this.parseBeliefBase(new StringReader(kb_string)));
		}
		return kbs;
	}

	/**
	 * Parses the file of the given filename into a formula of the given type.
	 * 
	 * @param filename the name of a file
	 * @return a formula
	 * @throws FileNotFoundException if the file is not found
	 * @throws IOException           if some IO issue occurred.
	 * @throws ParserException       some parsing exceptions may be added here.
	 */
	public S parseFormulaFromFile(String filename) throws FileNotFoundException, IOException, ParserException {
		InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(filename));
		S f = this.parseFormula(reader);
		reader.close();
		return f;
	}

	/**
	 * Parses the given text into a formula of the given type.
	 * 
	 * @param text a string
	 * @return a formula
	 * @throws IOException     if some IO issue occurred.
	 * @throws ParserException some parsing exceptions may be added here.
	 */
	public S parseFormula(String text) throws IOException, ParserException {
		return this.parseFormula(new StringReader(text));
	}

	/**
	 * Parses the given reader into a formula of the given type.
	 * 
	 * @param reader a reader
	 * @return a formula
	 * @throws IOException     if some IO issue occurred.
	 * @throws ParserException some parsing exceptions may be added here.
	 */
	public abstract S parseFormula(Reader reader) throws IOException, ParserException;

	/**
	 * Checks whether the given string is a number.
	 * 
	 * @param str some string
	 * @return "true" if the given string can be parsed as a number
	 */
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}

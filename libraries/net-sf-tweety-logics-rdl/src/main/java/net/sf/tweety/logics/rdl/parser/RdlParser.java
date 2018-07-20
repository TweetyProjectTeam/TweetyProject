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
package net.sf.tweety.logics.rdl.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;
import net.sf.tweety.logics.rdl.syntax.DefaultTheory;

/**
 * This class implements a parser for default logic. 
 * @author Nils Geilen
 *
 */
public class RdlParser extends Parser<DefaultTheory> {

	/**
	 * parser to parse knowledge base
	 */
	private FolParser folparser;

	/**
	 * tokens for parsing defaults
	 */
	private final String DIV_COLON = "::", DIV_COMMA = ";", DIV_SLASH = "/";

	
	/**
	 * regexes for parsing a default and the justifications
	 */
	private final Pattern JUS_SPLIT = Pattern.compile("^([^;]+)" + DIV_COMMA + "(.*)$"),
			DEFAULT_SPLIT = Pattern.compile("^(.*)" + DIV_COLON + "(.*)" + DIV_SLASH + "(.*)$");

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public DefaultTheory parseBeliefBase(Reader reader) throws IOException, ParserException {
		folparser= new FolParser();
		BufferedReader br = new BufferedReader(reader);
		String str = "";
		String line;
		while ((line = br.readLine()) != null && !line.matches(".*"+DIV_COLON+".*"))
				str += line + "\n";
		FolBeliefSet facts = folparser.parseBeliefBase(str);
		LinkedList<DefaultRule> defaults = new LinkedList<>();
		do {
			if (!line.matches("^\\s*$"))
				defaults.add((DefaultRule) parseFormula(line));
		}while ((line = br.readLine()) != null);
		return new DefaultTheory(facts, defaults);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		Matcher matcher = DEFAULT_SPLIT.matcher(line);
		if (!matcher.matches())
			return folparser.parseFormula(line);
		FolFormula pre = new Tautology();
		if (!matcher.group(1).matches("^\\s*$"))
			pre = (FolFormula) parseFormula(matcher.group(1));
		FolFormula conc = (FolFormula) parseFormula(matcher.group(3));
		String jus = matcher.group(2);
		LinkedList<FolFormula> juslist = new LinkedList<>();
		while (true) {
			Matcher m = JUS_SPLIT.matcher(jus);
			if (!m.matches()) {
				juslist.add((FolFormula) parseFormula(jus));
				break;
			}
			jus = m.group(2);
			juslist.add((FolFormula) parseFormula(m.group(1)));
		}
		return new DefaultRule(pre, juslist, conc);

	}

}

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
 *  Copyright 2017 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.deductive.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.deductive.syntax.SimplePlLogicDeductiveKnowledgebase;
import net.sf.tweety.arg.deductive.syntax.SimplePlRule;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * 
 * @author Federico Cerutti <federico.cerutti@acm.org>
 *
 * Parses a simple logic knowledge base out of an input text.
 * Each line contains either a literal or a simple rule of the form
 * l1, ..., ln -> c
 */
public class SimplePlLogicParser
		extends
			Parser<SimplePlLogicDeductiveKnowledgebase,SimplePlRule> {

	private String symbolInf = "->", symbolComma = ",";
	private final Parser<? extends BeliefBase,?> formulaparser;

	public SimplePlLogicParser() {
		super();
		this.formulaparser = new PlParser();
	}

	/**
	 * Read an entire piece of text and send each line to the actual parser
	 * @param reader a reader
	 * 
	 * @return	the simple logic knowledge base read from the input
	 */
	@Override
	public SimplePlLogicDeductiveKnowledgebase parseBeliefBase(Reader reader)
			throws IOException, ParserException {

		SimplePlLogicDeductiveKnowledgebase kb = new SimplePlLogicDeductiveKnowledgebase();
		BufferedReader br = new BufferedReader(reader);

		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			Formula rule = parseFormula(line);
			if (rule != null)
				kb.add((SimplePlRule) rule);
		}
		return kb;
	}

	/**
	 * @see net.sf.tweety.commons.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public SimplePlRule parseFormula(Reader reader)
			throws IOException, ParserException {
		final Pattern RULE = Pattern.compile("(.*)(" + symbolInf + ")(.+)"),
				LITERAL = Pattern.compile("(.*)"),
				EMPTY = Pattern.compile("^\\s*$");

		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		if (line == null)
			return null;
		Matcher m = RULE.matcher(line);
		if (m.matches()) {
			SimplePlRule rule = new SimplePlRule();
			rule.setConclusion((PlFormula) formulaparser
					.parseFormula(m.group(3)));
			String str = m.group(1);
			if (!EMPTY.matcher(str).matches()) {
				String[] pres = str.split(symbolComma);
				for (String pre : pres)
					rule.addPremise((PlFormula) formulaparser
							.parseFormula(pre));
			}
			return rule;
		}
		m = LITERAL.matcher(line);
		if (m.matches()) {
			String str = m.group(1);
			if (!EMPTY.matcher(str).matches()){
				SimplePlRule rule = new SimplePlRule();
				rule.setConclusion((PlFormula) formulaparser.parseFormula(str));
				return rule;
			}
		}
		return null;
	}

}

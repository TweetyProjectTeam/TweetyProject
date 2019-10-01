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
package net.sf.tweety.arg.aba.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aba.syntax.AbaTheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.aba.syntax.Negation;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

/**
 * Parses a Assumption Based Argumentation System out of an input text. With
 * standard symbols, every line contains one of the following: <br>
 * &lt;rule&gt; ::= &lt;head&gt; '&lt;-' &lt;body&gt;? <br>
 * &lt;head&gt; ::= &lt;word&gt; <br>
 * &lt;body&gt; ::= 'true' | &lt;word&gt; (',' &lt;word&gt;)* <br>
 * &lt;assumption&gt; ::= &lt;word&gt; <br>
 * &lt;assumptions&gt; ::= '{' &lt;assumption&gt; (',' &lt;assumption&gt;)* '}'
 * <br>
 * with &lt;word&gt; in the theory's language.
 * 
 * 
 * @param <T> the type of formulas (language) that the ABA theory ranges over
 * @author Nils Geilen
 */
public class AbaParser<T extends Formula> extends Parser<AbaTheory<T>, Formula> {

	/**
	 * Used to parse formulae
	 */
	private final Parser<? extends BeliefBase, ? extends Formula> formulaparser;

	/**
	 * Symbols used for parsing rules. Note: symbolComma is used for separating
	 * assumption declarations. When using a fol-based parser, use a different
	 * symbolComma because otherwise the commas in fol formulas will cause errors.
	 */
	private String symbolTrue = "true", symbolArrow = "<-", symbolComma = ",";

	/**
	 * Creates a new ABA parser
	 * 
	 * @param formulaparser parses formulae of the language
	 */
	public AbaParser(Parser<? extends BeliefBase, ? extends Formula> formulaparser) {
		super();
		this.formulaparser = formulaparser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public AbaTheory<T> parseBeliefBase(Reader reader) throws IOException, ParserException {
		final Pattern COMMENT = Pattern.compile("^%.*"), EMPTY = Pattern.compile("^\\s*$"),
				ASSUMPTIONS = Pattern.compile("^\\s*\\{(.*)\\}\\s*$");
		AbaTheory<T> abat = new AbaTheory<>();
		BufferedReader br = new BufferedReader(reader);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			if (EMPTY.matcher(line).matches() || COMMENT.matcher(line).matches())
				continue;
			Matcher matcher = ASSUMPTIONS.matcher(line);
			if (matcher.matches()) {
				String[] asss = matcher.group(1).split(symbolComma);
				for (String ass : asss)
					abat.add(parseFormula(ass));
			} else
				abat.add(parseFormula(line));
		}
		return abat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.Parser#parseFormula(java.io.Reader)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		final Pattern RULE = Pattern.compile("(.+)" + symbolArrow + "(.*)"), TRUE = Pattern.compile("^\\s*$"),
				NEGATION = Pattern.compile("not(.+)=(.+)");

		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		if (line == null)
			return null;
		Matcher m = RULE.matcher(line);
		if (m.matches()) {
			InferenceRule<T> rule = new InferenceRule<>();
			String head = m.group(1), tail = m.group(2);
			rule.setConclusion((T) formulaparser.parseFormula(head));
			if (!TRUE.matcher(tail).matches()) {
				String[] pres = tail.split(symbolComma);
				for (String pre : pres)
					rule.addPremise((T) formulaparser.parseFormula(pre));
			}
			return rule;
		}

		m = NEGATION.matcher(line);
		if (m.matches()) {
			return new Negation<Formula>(formulaparser.parseFormula(m.group(1)),
					formulaparser.parseFormula(m.group(2)));
		}

		return new Assumption<>((T) formulaparser.parseFormula(line));

	}

	/**
	 * @return the symbolTrue
	 */
	public String getSymbolTrue() {
		return symbolTrue;
	}

	/**
	 * @param symbolTrue the symbolTrue to set
	 */
	public void setSymbolTrue(String symbolTrue) {
		this.symbolTrue = symbolTrue;
	}

	/**
	 * @return the symbolArrow
	 */
	public String getSymbolArrow() {
		return symbolArrow;
	}

	/**
	 * @param symbolArrow the symbolArrow to set
	 */
	public void setSymbolArrow(String symbolArrow) {
		this.symbolArrow = symbolArrow;
	}

	/**
	 * @return the symbolComma
	 */
	public String getSymbolComma() {
		return symbolComma;
	}

	/**
	 * @param symbolComma the symbolComma to set
	 */
	public void setSymbolComma(String symbolComma) {
		this.symbolComma = symbolComma;
	}

}

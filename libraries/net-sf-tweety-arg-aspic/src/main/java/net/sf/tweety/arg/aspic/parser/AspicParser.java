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
package net.sf.tweety.arg.aspic.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.order.SimpleAspicOrder;
import net.sf.tweety.arg.aspic.ruleformulagenerator.RuleFormulaGenerator;
import net.sf.tweety.arg.aspic.syntax.DefeasibleInferenceRule;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.syntax.StrictInferenceRule;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * Parses a Aspic Argumentation System out of an input text. Every line contains one of the following: 
 *		<order> 		   ::= <identifier> ( '<' <identifier> )+
 *		<ordinary premise> ::= '=>' '-'? <identifier>
 *		<axiom> 		   ::= '->' '-'? <identifier>
 *		<defeasible rule>  ::= ( <identifier> ':' )? <identifier> ( ',' <identifier> )* '=>' (-)? <identifier>
 *		<static rule>	   ::= <identifier> ( ',' <identifier> )* '->' (-)? <identifier>
 *
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class AspicParser <T extends Invertable> extends Parser<AspicArgumentationTheory<T>>{
	
	/**
	 * Used to parse formulae
	 */
	private final Parser<? extends BeliefBase> formulaparser;
	private RuleFormulaGenerator<T> rfg;
	
	private String symbolStrict = "->", 
			symbolDefeasible = "=>", 
			symbolComma = ",";
	
	/**
	 * Constructs a new instance
	 * @param formulaparser	parses the bodies and the heads of the ASPIC argumentation systems rules
	 * @param rfg	a generator, that transforms InferenceRule<T> into T
	 */
	public AspicParser(Parser<? extends BeliefBase> formulaparser, RuleFormulaGenerator<T> rfg) {
		super();
		this.formulaparser = formulaparser;
		this.rfg = rfg;
	}
	
	

	/**
	 * Sets a new symbol used for parsing strict function arrows
	 * @param symbolStrict	is the new symbol
	 */
	public void setSymbolStrict(String symbolStrict) {
		this.symbolStrict = symbolStrict;
	}


	


	/**
	 * Sets a new symbol used for parsing defeasible function arrows
	 * @param symbolStrict	is the new symbol
	 */
	public void setSymbolDefeasible(String symbolDefeasible) {
		this.symbolDefeasible = symbolDefeasible;
	}


	/**
	 * Sets a new symbol used for parsing parameter separators
	 * @param symbolStrict	is the new symbol
	 */
	public void setSymbolComma(String symbolComma) {
		this.symbolComma = symbolComma;
	}



	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	@SuppressWarnings(value = { "unchecked" })
	public AspicArgumentationTheory<T> parseBeliefBase(Reader reader) throws IOException, ParserException {
		final Pattern ORDER = Pattern.compile(".*<.*");
		
		AspicArgumentationTheory<T> as = new AspicArgumentationTheory<T>( rfg);
		
		BufferedReader br = new BufferedReader(reader);
		
		while(true) {
			String line = br.readLine();
			if(line==null)
				break;
			if (ORDER.matcher(line).matches()) {
				as.setOrder(parseSimpleOrder(line));
			} else {
				Formula rule = parseFormula(line);
				if(rule != null)
					as.addRule((InferenceRule<T>)rule);
			}
		}
		return as;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseFormula(java.io.Reader)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		final Pattern RULE = Pattern.compile("(.*)("+symbolStrict +"|"+symbolDefeasible +")(.+)"),
				RULE_ID = Pattern.compile("^\\s*([A-Za-z0-9]+)\\s*:(.*)"),
				EMPTY = Pattern.compile("^\\s*$");
		
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		if(line==null)
			return null;
		Matcher m = RULE.matcher(line);
		if(m.matches()) {
			InferenceRule<T> rule = 
					m.group(2).equals(symbolDefeasible)
							? new DefeasibleInferenceRule<>()
							: new StrictInferenceRule<>();
			rule.setConclusion((T)formulaparser.parseFormula(m.group(3)));
			String str = m.group(1);
			m = RULE_ID.matcher(str);
			if(m.matches()) {
				rule.setName(m.group(1));
				str = m.group(2);
			}
			if(!EMPTY.matcher(str).matches()){
				String[] pres = str.split(symbolComma);
				for(String pre:pres)
					rule.addPremise((T)formulaparser.parseFormula(pre));
			}
			return rule;
		}
		return null;
	}
	
	
	/**
	 * Extracts and Constructs a <code>SimpleAspicOrder</codeY> out of a string
	 * @param line	the source string
	 * @return	the parsed order
	 */
	public SimpleAspicOrder<T> parseSimpleOrder(String line) {
		List<String> rules = new ArrayList<>();
		String[] parts = line.split("<");
		for(String s:parts)
			rules.add(s.trim());
		return new SimpleAspicOrder<T>(rules);
	}
	

}

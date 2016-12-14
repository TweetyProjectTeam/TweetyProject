package net.sf.tweety.arg.aba.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aba.ABATheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.aba.syntax.Negation;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

/**
 * @author Nils Geilen
 * 
 * Parses a Assumption Based Argumentation System out of an input text. 
 * With standard symbols, every line contains one of the following:
 * 	<rule> ::= <head> '<-' <body>?
 * 			<head> ::= <word>
 * 			<body> ::= 'true' | <word> (',' <word>)*
 * 	<assumption> ::= <word>
 * 	<assumptions> ::= '{' <assumption> (',' <assumption>)* '}'
 * 	with <word> in the theory's language.
 *		
 * 
 * @param <T>	is the type of the language that the ABA theory ranges over 
 */
public class ABAParser<T extends Formula> extends Parser<ABATheory<T>> {

	/**
	 * Used to parse formulae
	 */
	private final Parser<? extends BeliefBase> formulaparser;

	/**
	 * Symbols used for parsing rules
	 */
	private String symbolTrue = "true", symbolArrow = "<-", symbolComma = ",";

	/**
	 * Create a new ABA parser
	 * @param formulaparser parses formulae of the language 
	 */
	public ABAParser(Parser<? extends BeliefBase> formulaparser	) {
		super();
		this.formulaparser = formulaparser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.commons.Parser#parseBeliefBase(java.io.Reader)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ABATheory<T> parseBeliefBase(Reader reader) throws IOException, ParserException {
		final Pattern COMMENT = Pattern.compile("^%.*"), 
				EMPTY = Pattern.compile("^\\s*$"),
				ASSUMPTIONS = Pattern.compile("^\\s*\\{(.*)\\}\\s*$");
		ABATheory<T> abat = new ABATheory<>();
		BufferedReader br = new BufferedReader(reader);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			if (EMPTY.matcher(line).matches() || COMMENT.matcher(line).matches())
				continue;
			Matcher matcher = ASSUMPTIONS.matcher(line);
			if(matcher.matches()) {
				String[] asss = matcher.group(1).split(symbolComma);
				for (String ass:asss)
					abat.add( parseFormula(ass));
			} else abat.add( parseFormula(line));
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
				NEGATION=Pattern.compile("not(.+)=(.+)");

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
			return new Negation(formulaparser.parseFormula(m.group(1)),formulaparser.parseFormula(m.group(2)));
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

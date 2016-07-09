package net.sf.tweety.arg.aspic.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aspic.AspicArgumentationSystem;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.aspic.syntax.AspicNegation;
import net.sf.tweety.arg.aspic.syntax.AspicWord;
import net.sf.tweety.arg.aspic.syntax.SimpleAspicOrder;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

/**
 * @author Nils Geilen
 * Parses a Aspic Argumentation System out of an input text. Every line contains one of the following: 
 *		<order> 		   ::= <identifier> ( '<' <identifier> )+
 *		<ordinary premise> ::= '=>' '-'? <identifier>
 *		<axiom> 		   ::= '->' '-'? <identifier>
 *		<defeasible rule>  ::= ( <identifier> ':' )? <identifier> ( ',' <identifier> )* '=>' (-)? <identifier>
 *		<static rule>	   ::= <identifier> ( ',' <identifier> )* '->' (-)? <identifier>
 */
public class AspicParser extends Parser<AspicArgumentationSystem>{
	
	
	/**
	 * static Patterns to parse lines  
	 */
	final static Pattern RULE = Pattern.compile("(.*)([=-]>)(.+)"),
			RULE_ID = Pattern.compile("(.*):(.*)"),
			//AXIOMS = Pattern.compile("^\\s*axioms:(.+)"),
			EMPTY = Pattern.compile("^\\s*$"),
			NOT = Pattern.compile("^-\\s*(\\w+)"),
			WORD = Pattern.compile("\\$?\\w+"),
			ORDER = Pattern.compile("^([^<]+)<(.+)");

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public AspicArgumentationSystem parseBeliefBase(Reader reader) throws IOException, ParserException {
		// TODO Auto-generated method stub
		AspicArgumentationSystem as = new AspicArgumentationSystem();
		
		BufferedReader br = new BufferedReader(reader);
		
		while(true) {
			String line = br.readLine();
			if(line==null)
				break;
			if (ORDER.matcher(line).matches()) {
				Collection<AspicFormula> rules = new ArrayList<>();
				while(true){
					Matcher m = ORDER.matcher(line);
					if(m.matches()) {
						rules.add(makeWord(m.group(1)));
						line = m.group(2);
					}else {
						rules.add(makeWord(line));
						break;
					}
				}
				as.setOrder(new SimpleAspicOrder(rules));
			} else {
				Formula rule = parseFormula(line);
				as.addRule((AspicInferenceRule)rule);
			}
		}
		as.expand();
		return as;
	}
	
	/**
	 * Constructs an Aspic Formula out of s
	 * @param s input string ::= '-'? <identifier>
	 * @return an Aspic formuls
	 * @throws ParserException
	 */
	private AspicFormula makeWord(String s) throws ParserException {
		s = s.trim();
		boolean negation = false;
		Matcher m = NOT.matcher(s);
		if(m.matches()) {
			negation = true;
			s = m.group(1);
		}
		if(WORD.matcher(s).matches())
			if(negation)
				return new  AspicNegation(new AspicWord(s));
			else
				return new AspicWord(s);
		else throw new ParserException("Non-word char in language");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		if(line==null)
			return null;
		Matcher m = RULE.matcher(line);
		if(m.matches()) {
			AspicInferenceRule rule = new AspicInferenceRule();
			rule.setDefeasible(m.group(2).equals("=>"));
			rule.setConclusion(makeWord(m.group(3)));
			String str = m.group(1);
			m = RULE_ID.matcher(str);
			if(m.matches()) {
				rule.setID(m.group(1).trim());
				str = m.group(2);
			}
			if(!EMPTY.matcher(str).matches()){
				String[] pres = str.split(",");
				for(String pre:pres)
					rule.addPremise(makeWord(pre));
			}
			return rule;
		}
		return null;
	}
	
	

}

package net.sf.tweety.arg.aspic.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aspic.AspicArgumentationSystem;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.aspic.syntax.AspicNegation;
import net.sf.tweety.arg.aspic.syntax.AspicWord;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

public class AspicParser extends Parser<AspicArgumentationSystem>{
	
	final Pattern RULE = Pattern.compile("(.*)([=-]>)(.+)"),
			RULE_ID = Pattern.compile("(.*):(.*)"),
			//AXIOMS = Pattern.compile("^\\s*axioms:(.+)"),
			EMPTY = Pattern.compile("^\\s*$"),
			NOT = Pattern.compile("^-\\s*(\\w+)"),
			WORD = Pattern.compile("\\$?\\w+");

	@Override
	public AspicArgumentationSystem parseBeliefBase(Reader reader) throws IOException, ParserException {
		// TODO Auto-generated method stub
		AspicArgumentationSystem as = new AspicArgumentationSystem();
		
		BufferedReader br = new BufferedReader(reader);
		
		while(true) {
			String line = br.readLine();
			if(line==null)
				break;
			Formula rule = parseFormula(line);
			as.addRule((AspicInferenceRule)rule);
			
		}
		as.expand();
		return as;
	}
	
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

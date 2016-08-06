package net.sf.tweety.arg.aspic.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.semantics.SimpleAspicOrder;
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
 * Parses a Aspic Argumentation System out of an input text. Every line contains one of the following: 
 *		<order> 		   ::= <identifier> ( '<' <identifier> )+
 *		<ordinary premise> ::= '=>' '-'? <identifier>
 *		<axiom> 		   ::= '->' '-'? <identifier>
 *		<defeasible rule>  ::= ( <identifier> ':' )? <identifier> ( ',' <identifier> )* '=>' (-)? <identifier>
 *		<static rule>	   ::= <identifier> ( ',' <identifier> )* '->' (-)? <identifier>
 */
public class AspicParser <T extends Invertable> extends Parser<AspicArgumentationTheory<T>>{
	
	private final Parser<? extends BeliefBase> formulaparser;
	
	private String symbolStrict = "->", 
			symbolDefeasible = "=>", 
			symbolComma = ",";
	
	public AspicParser(Parser<? extends BeliefBase> formulaparser) {
		super();
		this.formulaparser = formulaparser;
	}
	
	
	


	public void setSymbolStrict(String symbolStrict) {
		this.symbolStrict = symbolStrict;
	}





	public void setSymbolDefeasible(String symbolDefeasible) {
		this.symbolDefeasible = symbolDefeasible;
	}





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
		
		AspicArgumentationTheory<T> as = new AspicArgumentationTheory<T>();
		
		BufferedReader br = new BufferedReader(reader);
		
		while(true) {
			String line = br.readLine();
			if(line==null)
				break;
			if (ORDER.matcher(line).matches()) {
				Collection<String> rules = new ArrayList<>();
				String[] parts = line.split("<");
				for(String s:parts)
					rules.add(s.trim());
				as.setOrder(new SimpleAspicOrder<T>(rules));
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
	
	

}

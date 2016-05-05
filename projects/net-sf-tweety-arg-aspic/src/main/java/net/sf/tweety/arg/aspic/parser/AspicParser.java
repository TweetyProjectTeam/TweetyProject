package net.sf.tweety.arg.aspic.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aspic.AspicTheory;
import net.sf.tweety.arg.aspic.semantics.ArgumentationSystem;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.syntax.Word;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

public class AspicParser extends Parser<AspicTheory>{
	
	final Pattern RULE = Pattern.compile("(.+)([=-]>)(.+)"),
			AXIOMS = Pattern.compile("^\\s*axioms:(.+)"),
			NOT = Pattern.compile("^not\\s+(\\w+)"),
			WORD = Pattern.compile("\\w+");

	@Override
	public AspicTheory parseBeliefBase(Reader reader) throws IOException, ParserException {
		// TODO Auto-generated method stub
		Set<Word> kb = new HashSet<>();
		ArgumentationSystem as = new ArgumentationSystem();
		
		BufferedReader br = new BufferedReader(reader);
		
		while(true) {
			String line = br.readLine();
			if(line==null)
				break;
			Matcher m = RULE.matcher(line);
			if(m.matches()) {
				boolean defeasible = m.group(2).equals("=>");
				Word conclusion = makeWord(m.group(3));
				List<Word> prerequisites = new ArrayList<>();
				String[] pres = m.group(1).split(",");
				for(String pre:pres)
					prerequisites.add(makeWord(pre));
				InferenceRule rule = new InferenceRule(defeasible, conclusion, prerequisites);
				as.addRule(rule);
			} else {
				m = AXIOMS.matcher(line);
				boolean axioms = false;
				if(m.matches()) {
					axioms = true;
					line = m.group(1);
				}
				String[] prems = line.split(",");
				for(String prem: prems)
					kb.add(makeWord(prem).asAxiom(axioms));
			}
		}
		
		return new AspicTheory(as,kb);
	}
	
	private Word makeWord(String s) throws ParserException {
		s = s.trim();
		boolean negation = false;
		Matcher m = NOT.matcher(s);
		if(m.matches()) {
			negation = true;
			s = m.group(1);
		}
		if(WORD.matcher(s).matches())
			return new Word(s,negation);
		else throw new ParserException("Non-word char in language");
	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}

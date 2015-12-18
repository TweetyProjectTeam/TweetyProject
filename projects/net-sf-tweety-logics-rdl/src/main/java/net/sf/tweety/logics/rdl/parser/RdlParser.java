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
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

public class RdlParser extends Parser<DefaultTheory> {

	private final FolParser folparser = new FolParser();

	private final String DIV_COLON = "::", DIV_COMMA = ";", DIV_SLASH = "/";

	private final Pattern JUS_SPLIT = Pattern.compile("^([^;]+)" + DIV_COMMA + "(.*)$"),
			DEFAULT_SPLIT = Pattern.compile("^(.*)" + DIV_COLON + "(.*)" + DIV_SLASH + "(.*)$");

	@Override
	public DefaultTheory parseBeliefBase(Reader reader) throws IOException, ParserException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(reader);
		String str = "";
		String line;
		while ((line = br.readLine()) != null && !line.matches("--defaults--"))
			str += line + "\n";
		FolBeliefSet facts = folparser.parseBeliefBase(str);
		LinkedList<DefaultRule> defaults = new LinkedList<>();
		while ((line = br.readLine()) != null) {
			if (!line.matches("^\\s*$"))
				defaults.add((DefaultRule) parseFormula(line));
		}
		return new DefaultTheory(facts, defaults);
	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		Matcher matcher = DEFAULT_SPLIT.matcher(line);
		if (!matcher.matches())
			return folparser.parseFormula(line);
		FolFormula pre = null;
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

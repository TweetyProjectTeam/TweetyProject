package net.sf.tweety.arg.aba.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.aba.ABATheory;
import net.sf.tweety.arg.aba.syntax.ABARule;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.ruleformulagenerator.RuleFormulaGenerator;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class ABAParser<T extends Invertable> extends Parser<ABATheory<T>> {

	/**
	 * Used to parse formulae
	 */
	private final Parser<? extends BeliefBase> formulaparser;
	private RuleFormulaGenerator<T> rfg;

	private String symbolTrue = "true", symbolArrow = "<-", symbolComma = ",";

	public ABAParser(Parser<? extends BeliefBase> formulaparser, RuleFormulaGenerator<T> rfg) {
		super();
		this.formulaparser = formulaparser;
		this.rfg = rfg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ABATheory<T> parseBeliefBase(Reader reader) throws IOException, ParserException {
		ABATheory<T> abat = new ABATheory<>();
		BufferedReader br = new BufferedReader(reader);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			abat.add((ABARule<T>) this.parseFormula(line));
		}
		return abat;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		final Pattern RULE = Pattern.compile("(.+)" + symbolArrow + "(.*)"), TRUE = Pattern.compile("^\\s*$");

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
		} else {
			return new Assumption<Invertable>((T) formulaparser.parseFormula(line));
		}

	}

	public String getSymbolTrue() {
		return symbolTrue;
	}

	public void setSymbolTrue(String symbolTrue) {
		this.symbolTrue = symbolTrue;
	}

	public String getSymbolArrow() {
		return symbolArrow;
	}

	public void setSymbolArrow(String symbolArrow) {
		this.symbolArrow = symbolArrow;
	}

	public String getSymbolComma() {
		return symbolComma;
	}

	public void setSymbolComma(String symbolComma) {
		this.symbolComma = symbolComma;
	}

}

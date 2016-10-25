package net.sf.tweety.arg.aba;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.tweety.arg.aba.parser.ABAParser;
import net.sf.tweety.arg.aba.syntax.ABARule;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.ruleformulagenerator.PlFormulaGenerator;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class ABATest {

	@SuppressWarnings("unchecked")
	@Test
	public void ParserTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser, new PlFormulaGenerator());
		
		ABARule<PropositionalFormula> assumption = (ABARule<PropositionalFormula>)parser.parseFormula("a");
		ABARule<PropositionalFormula> rule_from_true = (ABARule<PropositionalFormula>)parser.parseFormula("a <-");
		ABARule<PropositionalFormula> two_params_rule = (ABARule<PropositionalFormula>)parser.parseFormula("b <- a, c");
		assertTrue(assumption.getConclusion().equals(rule_from_true.getConclusion()));
		assertTrue(assumption instanceof Assumption<?>);
		assertTrue(rule_from_true instanceof InferenceRule<?>);
		assertTrue(two_params_rule.getPremise().size() == 2);
		
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example1.aba");
		assertTrue(abat.getAssumptions().size() == 3);
		assertTrue(abat.getRules().size() == 4);
		
		System.out.println(abat.getAssumptions());
		System.out.println(abat.getRules());
		
		assertTrue(abat.getAssumptions().contains(new Assumption<PropositionalFormula>((PropositionalFormula)plparser.parseFormula("a"))));
		assertFalse(abat.getAssumptions().contains(new Assumption<PropositionalFormula>((PropositionalFormula)plparser.parseFormula("z"))));
		
		InferenceRule<PropositionalFormula> rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula)plparser.parseFormula("z"));
		rule.addPremise((PropositionalFormula)plparser.parseFormula("b"));
		rule.addPremise((PropositionalFormula)plparser.parseFormula("y"));
		assertTrue(abat.getRules().contains(rule));
	
		rule.setConclusion((PropositionalFormula)plparser.parseFormula("y"));
		rule.getPremise().clear();
		assertTrue(abat.getRules().contains(rule));
		
		rule.setConclusion((PropositionalFormula)plparser.parseFormula("y"));
		rule.getPremise().clear();
		rule.addPremise((PropositionalFormula)plparser.parseFormula("b"));
		assertFalse(abat.getRules().contains(rule));
		
	}

}

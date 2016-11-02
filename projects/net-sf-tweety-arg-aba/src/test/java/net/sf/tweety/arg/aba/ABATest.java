package net.sf.tweety.arg.aba;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import net.sf.tweety.arg.aba.parser.ABAParser;
import net.sf.tweety.arg.aba.semantics.ABAAttack;
import net.sf.tweety.arg.aba.syntax.ABARule;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.Deduction;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.ruleformulagenerator.PlFormulaGenerator;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class ABATest {

	@SuppressWarnings("unchecked")
	@Test
	public void ParserTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		
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
	
	@Test
	public void DeductionTest1() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example1.aba");
		
		Collection<Deduction<PropositionalFormula>> deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 7);
		
		InferenceRule<PropositionalFormula> rule = (InferenceRule<PropositionalFormula>)parser.parseFormula("z <- b,q");
		abat.add(rule);
		deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 7);
		
		rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula)plparser.parseFormula("b"));
		abat.add(rule);
		deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 10);
	}
	
	@Test
	public void DeductionTest2() throws Exception {
		PlParser plparser = new PlParser();
		ABATheory<PropositionalFormula> abat = new ABATheory<>();
		
		abat.addAssumption((PropositionalFormula)plparser.parseFormula("a"));
		Collection<Deduction<PropositionalFormula>> ds = abat.getAllDeductions();
		assertTrue(ds.size() == 1);
		Deduction<PropositionalFormula> deduction = ds.iterator().next();
		assertTrue(deduction.getConclusion().equals((PropositionalFormula)plparser.parseFormula("a")));
		assertTrue(deduction.getRules().size() == 0);
		
		InferenceRule<PropositionalFormula> rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula)plparser.parseFormula("b"));
		rule.addPremise((PropositionalFormula)plparser.parseFormula("a"));
		rule.addPremise((PropositionalFormula)plparser.parseFormula("c"));
		abat.add(rule);
		rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula)plparser.parseFormula("c"));
		abat.add(rule);
		
		deduction = null;
		for(Deduction<PropositionalFormula> d : abat.getAllDeductions())
			if (d.getConclusion().equals((PropositionalFormula)plparser.parseFormula("b")))
				deduction = d;
		assertFalse(deduction == null);
		
		assertTrue(deduction.getRules().size() == 2);
		assertTrue(deduction.getAssumptions().size() == 1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void AttackTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example1.aba");
		
		abat.add((Assumption<PropositionalFormula>)parser.parseFormula(" ! a"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 3);
		
		abat.add((ABARule<PropositionalFormula>)parser.parseFormula("! c <- b"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 4);
		
		abat.add((ABARule<PropositionalFormula>)parser.parseFormula("! c <- a"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 6);
	}
	
	
	@Test public void ToDungTheoryMethodTest() throws Exception {
		
	}

}

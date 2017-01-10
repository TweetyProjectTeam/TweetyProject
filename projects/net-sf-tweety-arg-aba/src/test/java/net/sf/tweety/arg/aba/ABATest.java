package net.sf.tweety.arg.aba;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.arg.aba.parser.ABAParser;
import net.sf.tweety.arg.aba.semantics.ABAAttack;
import net.sf.tweety.arg.aba.syntax.ABARule;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.Deduction;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class ABATest {
	
	@Before
	public void SetUp() {
		SatSolver.setDefaultSolver(new Sat4jSolver());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void ParserTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);

		ABARule<PropositionalFormula> assumption = (ABARule<PropositionalFormula>) parser.parseFormula("a");
		ABARule<PropositionalFormula> rule_from_true = (ABARule<PropositionalFormula>) parser.parseFormula("a <-");
		ABARule<PropositionalFormula> two_params_rule = (ABARule<PropositionalFormula>) parser
				.parseFormula("b <- a, c");
		assertTrue(assumption.getConclusion().equals(rule_from_true.getConclusion()));
		assertTrue(assumption instanceof Assumption<?>);
		assertTrue(rule_from_true instanceof InferenceRule<?>);
		assertTrue(two_params_rule.getPremise().size() == 2);

		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example1.aba");
		assertTrue(abat.getAssumptions().size() == 3);
		assertTrue(abat.getRules().size() == 4);

		assertTrue(abat.getAssumptions()
				.contains(new Assumption<PropositionalFormula>((PropositionalFormula) plparser.parseFormula("a"))));
		assertFalse(abat.getAssumptions()
				.contains(new Assumption<PropositionalFormula>((PropositionalFormula) plparser.parseFormula("z"))));

		InferenceRule<PropositionalFormula> rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula) plparser.parseFormula("z"));
		rule.addPremise((PropositionalFormula) plparser.parseFormula("b"));
		rule.addPremise((PropositionalFormula) plparser.parseFormula("y"));
		assertTrue(abat.getRules().contains(rule));

		rule.setConclusion((PropositionalFormula) plparser.parseFormula("y"));
		rule.getPremise().clear();
		assertTrue(abat.getRules().contains(rule));

		rule.setConclusion((PropositionalFormula) plparser.parseFormula("y"));
		rule.getPremise().clear();
		rule.addPremise((PropositionalFormula) plparser.parseFormula("b"));
		assertFalse(abat.getRules().contains(rule));

	}

	@Test
	public void DeductionTest1() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example1.aba");

		Collection<Deduction<PropositionalFormula>> deductions = abat.getAllDeductions();
		for(Deduction<?>d:deductions)
		System.out.println(d.getName());
		assertTrue(deductions.size() == 7);

		InferenceRule<PropositionalFormula> rule = (InferenceRule<PropositionalFormula>) parser
				.parseFormula("z <- b,q");
		abat.add(rule);
		deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 7);

		rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula) plparser.parseFormula("b"));
		abat.add(rule);
		deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 10);
	}

	@Test
	public void DeductionTest2() throws Exception {
		PlParser plparser = new PlParser();
		ABATheory<PropositionalFormula> abat = new ABATheory<>();

		abat.addAssumption((PropositionalFormula) plparser.parseFormula("a"));
		Collection<Deduction<PropositionalFormula>> ds = abat.getAllDeductions();
		assertTrue(ds.size() == 1);
		Deduction<PropositionalFormula> deduction = ds.iterator().next();
		assertTrue(deduction.getConclusion().equals((PropositionalFormula) plparser.parseFormula("a")));
		assertTrue(deduction.getRules().size() == 0);

		InferenceRule<PropositionalFormula> rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula) plparser.parseFormula("b"));
		rule.addPremise((PropositionalFormula) plparser.parseFormula("a"));
		rule.addPremise((PropositionalFormula) plparser.parseFormula("c"));
		abat.add(rule);
		rule = new InferenceRule<>();
		rule.setConclusion((PropositionalFormula) plparser.parseFormula("c"));
		abat.add(rule);

		deduction = null;
		for (Deduction<PropositionalFormula> d : abat.getAllDeductions())
			if (d.getConclusion().equals((PropositionalFormula) plparser.parseFormula("b")))
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
		abat.add(parser.parseFormula("not a=!a"));
		abat.add(parser.parseFormula("not !a=a"));
		abat.add(parser.parseFormula("not c=!c"));
		abat.add(parser.parseFormula("not !c=c"));

		abat.add((Assumption<PropositionalFormula>) parser.parseFormula(" ! a"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 3);

		abat.add((ABARule<PropositionalFormula>) parser.parseFormula("! c <- b"));
		System.out.println(ABAAttack.allAttacks(abat));
		assertTrue(ABAAttack.allAttacks(abat).size() == 4);

		abat.add((ABARule<PropositionalFormula>) parser.parseFormula("! c <- a"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 6);
	}

	@Test
	public void ToDungTheoryMethodTest() throws Exception {

	}

	@SuppressWarnings("unchecked")
	@Test
	public void ReasonerTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example2.aba");
		abat.add((ABARule<PropositionalFormula>)parser.parseFormula("!a<-"));
		abat.add(parser.parseFormula("not a=!a"));
		abat.add(parser.parseFormula("not !a=a"));
		assertTrue(abat.getAllDeductions().size()==7);
		System.out.println(abat.asDungTheory());
		FlatABAReasoner reasoner = new FlatABAReasoner(abat, Semantics.COMPLETE_SEMANTICS, Semantics.CREDULOUS_INFERENCE);
		Argument query = new Deduction<>("",(ABARule<PropositionalFormula> )parser.parseFormula("a"),new HashSet<>());
		Answer answer = reasoner.query(query);
		assertFalse(answer.getAnswerBoolean());
		System.out.println(reasoner.getExtensions());
		query = new Deduction<>("",(ABARule<PropositionalFormula> )parser.parseFormula("b"),new HashSet<>());
		answer = reasoner.query(query);
		assertTrue(answer.getAnswerBoolean());
		
	}
	
	@Test
	public void ClosureTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example2.aba");
		assertTrue(abat.isClosed(abat.getAssumptions()));
		assertTrue(abat.isFlat());
		abat.addAssumption((PropositionalFormula)plparser.parseFormula("r"));
		assertFalse(abat.isFlat());
	}
	
	@SuppressWarnings("unchecked")
	@Test 
	public void Example3 () throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example3.aba");
		assertFalse(abat.isFlat());
		
		GeneralABAReasoner<PropositionalFormula> abar = new CompleteReasoner<>(abat, Semantics.CREDULOUS_INFERENCE);
		Collection<Collection<Assumption<PropositionalFormula>>> complexts = abar.computeExtensions();
		
		GeneralABAReasoner<PropositionalFormula> grounded_reasoner = new GroundedReasoner<>(abat, Semantics.CREDULOUS_INFERENCE);
		Collection<Collection<Assumption<PropositionalFormula>>> groundedexts = grounded_reasoner.computeExtensions();
		assertTrue(groundedexts.size() == 1);
		
		Collection<Assumption<PropositionalFormula>> asss_c = new HashSet<>();
		asss_c.add((Assumption<PropositionalFormula>) parser.parseFormula("c"));
		assertTrue(abat.isClosed(asss_c));
		assertTrue(abat.isAdmissible(asss_c));
		assertTrue(complexts.contains(asss_c));
		
		Collection<Assumption<PropositionalFormula>> asss_0 = new HashSet<>();
		assertTrue(abat.isAdmissible(asss_0));
		assertTrue(complexts.contains(asss_0));
		assertTrue(groundedexts.contains(asss_0));
		
		Collection<Assumption<PropositionalFormula>> asss_b = new HashSet<>();
		asss_b.add((Assumption<PropositionalFormula>) parser.parseFormula("b"));
		assertFalse(abat.isClosed(asss_b));
		assertFalse(abat.isAdmissible(asss_b));
		assertFalse(complexts.contains(asss_b));
		
		Collection<Assumption<PropositionalFormula>> asss_ab = new HashSet<>();
		asss_ab.add((Assumption<PropositionalFormula>) parser.parseFormula("a"));
		asss_ab.add((Assumption<PropositionalFormula>) parser.parseFormula("b"));
		assertTrue(abat.isClosed(asss_ab));
		assertTrue(abat.isAdmissible(asss_ab));
		assertTrue(complexts.contains(asss_ab));
		
		
		
	}
	
	@Test 
	public void Example4 () throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example4.aba");
		assertFalse(abat.isFlat());
		
		
	}
	
	@Test 
	public void Example11 () throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example11.aba");
		
		assertTrue(abat.isFlat());
		
		DungTheory dt = abat.asDungTheory();
		assertTrue(dt.getNodes().size()==6);
		assertTrue(dt.getAttacks().size()==6);
		
		FlatABAReasoner reasoner = new FlatABAReasoner(abat, Semantics.COMPLETE_SEMANTICS, Semantics.CREDULOUS_INFERENCE);
System.out.println(reasoner.getExtensions());
	}

}

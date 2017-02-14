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
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Reasoner;
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

	@SuppressWarnings("unchecked")
	@Test
	public void DeductionTest1() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example1.aba");

		Collection<Deduction<PropositionalFormula>> deductions = abat.getAllDeductions();
		for (Deduction<?> d : deductions)
			System.out.println(d.getName());
		assertTrue(deductions.size() == 7);

		@SuppressWarnings("unchecked")
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
		abat.add((ABARule<PropositionalFormula>) parser.parseFormula("!a<-"));
		abat.add(parser.parseFormula("not a=!a"));
		abat.add(parser.parseFormula("not !a=a"));
		assertTrue(abat.getAllDeductions().size() == 7);
		assertTrue(abat.isFlat());
		Reasoner[] reasoners = { new FlatABAReasoner(abat, Semantics.COMPLETE_SEMANTICS, Semantics.CREDULOUS_INFERENCE),
				new CompleteReasoner<>(abat, Semantics.CREDULOUS_INFERENCE) };
		for (Reasoner reasoner : reasoners) {
			Assumption<?> query = (Assumption<?>) parser.parseFormula("a");
			Answer answer = reasoner.query(query);
			assertFalse(answer.getAnswerBoolean());
			query = (Assumption<?>) parser.parseFormula("b");
			answer = reasoner.query(query);
			assertTrue(answer.getAnswerBoolean());
		}
		assertTrue(((FlatABAReasoner)reasoners[0]).getExtensions().size() == ((GeneralABAReasoner<?>)reasoners[1]).computeExtensions().size());

	}

	@Test
	public void ClosureTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example2.aba");
		assertTrue(abat.isClosed(abat.getAssumptions()));
		assertTrue(abat.isFlat());
		abat.addAssumption((PropositionalFormula) plparser.parseFormula("r"));
		assertFalse(abat.isFlat());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void Example3() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example3.aba");
		assertFalse(abat.isFlat());

		GeneralABAReasoner<PropositionalFormula> abar = new CompleteReasoner<>(abat, Semantics.CREDULOUS_INFERENCE);
		Collection<Collection<Assumption<PropositionalFormula>>> complexts = abar.computeExtensions();
		
		Collection<Collection<Assumption<PropositionalFormula>>> prefexts = new PreferredReasoner<PropositionalFormula>(abat, Semantics.CREDULOUS_INFERENCE).computeExtensions();

		GeneralABAReasoner<PropositionalFormula> grounded_reasoner = new WellFoundedReasoner<>(abat,
				Semantics.CREDULOUS_INFERENCE);
		Collection<Collection<Assumption<PropositionalFormula>>> groundedexts = grounded_reasoner.computeExtensions();
		assertTrue(groundedexts.size() == 1);
		
		//System.out.println("exts"+complexts);
		

		Collection<Assumption<PropositionalFormula>> asss_c = new HashSet<>();
		asss_c.add((Assumption<PropositionalFormula>) parser.parseFormula("c"));
		assertTrue(abat.isClosed(asss_c));
		assertTrue(abat.isAdmissible(asss_c));
		assertTrue(prefexts.contains(asss_c));
	//	assertTrue(complexts.contains(asss_c));

		Collection<Assumption<PropositionalFormula>> asss_0 = new HashSet<>();
		assertTrue(abat.isAdmissible(asss_0));
		assertFalse(prefexts.contains(asss_0));
		//assertTrue(complexts.contains(asss_0));
		//assertTrue(groundedexts.contains(asss_0));

		Collection<Assumption<PropositionalFormula>> asss_b = new HashSet<>();
		asss_b.add((Assumption<PropositionalFormula>) parser.parseFormula("b"));
		assertFalse(abat.isClosed(asss_b));
		assertFalse(abat.isAdmissible(asss_b));
		assertFalse(prefexts.contains(asss_b));
		assertFalse(complexts.contains(asss_b));

		Collection<Assumption<PropositionalFormula>> asss_ab = new HashSet<>();
		asss_ab.add((Assumption<PropositionalFormula>) parser.parseFormula("a"));
		asss_ab.add((Assumption<PropositionalFormula>) parser.parseFormula("b"));
		assertTrue(abat.isClosed(asss_ab));
		assertTrue(abat.isAdmissible(asss_ab));
		assertTrue(prefexts.contains(asss_ab));
		assertTrue(complexts.contains(asss_ab));

		assertTrue(abat.isClosed(asss_c));
		assertTrue(abat.isConflictFree(asss_c));
		assertTrue(abat.attacks(asss_b, asss_c));
		assertFalse(abat.isClosed(asss_b));
		

	}

	@SuppressWarnings("unchecked")
	@Test
	public void Example4() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example4.aba");
		assertFalse(abat.isFlat());
		
		Collection<Collection<Assumption<PropositionalFormula>>> complexts = new CompleteReasoner<PropositionalFormula>(abat, Semantics.CREDULOUS_INFERENCE).computeExtensions();
		assertTrue(complexts.size() ==0);
		
		Collection<Collection<Assumption<PropositionalFormula>>> prefexts = new PreferredReasoner<PropositionalFormula>(abat, Semantics.CREDULOUS_INFERENCE).computeExtensions();

		
		Collection<Assumption<PropositionalFormula>> asss_a = new HashSet<>();
		asss_a.add((Assumption<PropositionalFormula>) parser.parseFormula("a"));
		assertFalse(complexts.contains(asss_a));
		assertTrue(prefexts.contains(asss_a));
	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void Example5() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example5.aba");
		assertFalse(abat.isFlat());
		
		Collection<Collection<Assumption<PropositionalFormula>>> complexts = new CompleteReasoner<PropositionalFormula>(abat, Semantics.CREDULOUS_INFERENCE).computeExtensions();
		assertTrue(complexts.size() ==2);
		Collection<Collection<Assumption<PropositionalFormula>>> wellfexts = new WellFoundedReasoner<PropositionalFormula>(abat, Semantics.CREDULOUS_INFERENCE).computeExtensions();
		assertTrue(complexts.size() ==2);
		
		Collection<Assumption<PropositionalFormula>> asss_ac = new HashSet<>();
		asss_ac.add((Assumption<PropositionalFormula>) parser.parseFormula("a"));
		asss_ac.add((Assumption<PropositionalFormula>) parser.parseFormula("c"));
		assertTrue(complexts.contains(asss_ac));
		
		Collection<Assumption<PropositionalFormula>> asss_bc = new HashSet<>();
		asss_bc.add((Assumption<PropositionalFormula>) parser.parseFormula("c"));
		asss_bc.add((Assumption<PropositionalFormula>) parser.parseFormula("b"));
		assertTrue(complexts.contains(asss_bc));
	
		Collection<Assumption<PropositionalFormula>> asss_c = new HashSet<>();
		asss_c.add((Assumption<PropositionalFormula>) parser.parseFormula("c"));
		assertTrue(wellfexts.contains(asss_c));
	}

	@Test
	public void Example11() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PropositionalFormula> parser = new ABAParser<>(plparser);
		ABATheory<PropositionalFormula> abat = parser.parseBeliefBaseFromFile("../../examples/aba/example11.aba");

		assertTrue(abat.isFlat());

		DungTheory dt = abat.asDungTheory();
		assertTrue(dt.getNodes().size() == 6);
		assertTrue(dt.getAttacks().size() == 6);

	}

}

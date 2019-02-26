/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
 package net.sf.tweety.arg.aba;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.arg.aba.parser.ABAParser;
import net.sf.tweety.arg.aba.reasoner.CompleteReasoner;
import net.sf.tweety.arg.aba.reasoner.FlatABAReasoner;
import net.sf.tweety.arg.aba.reasoner.GeneralABAReasoner;
import net.sf.tweety.arg.aba.reasoner.PreferredReasoner;
import net.sf.tweety.arg.aba.reasoner.WellFoundedReasoner;
import net.sf.tweety.arg.aba.semantics.ABAAttack;
import net.sf.tweety.arg.aba.semantics.AbaExtension;
import net.sf.tweety.arg.aba.syntax.ABARule;
import net.sf.tweety.arg.aba.syntax.ABATheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.Deduction;
import net.sf.tweety.arg.aba.syntax.InferenceRule;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlFormula;

public class ABATest {

	@Before
	public void SetUp() {
		SatSolver.setDefaultSolver(new Sat4jSolver());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void ParserTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);

		ABARule<PlFormula> assumption = (ABARule<PlFormula>) parser.parseFormula("a");
		ABARule<PlFormula> rule_from_true = (ABARule<PlFormula>) parser.parseFormula("a <-");
		ABARule<PlFormula> two_params_rule = (ABARule<PlFormula>) parser
				.parseFormula("b <- a, c");
		assertTrue(assumption.getConclusion().equals(rule_from_true.getConclusion()));
		assertTrue(assumption instanceof Assumption<?>);
		assertTrue(rule_from_true instanceof InferenceRule<?>);
		assertTrue(two_params_rule.getPremise().size() == 2);

		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example1.aba").getFile());
		assertTrue(abat.getAssumptions().size() == 3);
		assertTrue(abat.getRules().size() == 4);

		assertTrue(abat.getAssumptions()
				.contains(new Assumption<PlFormula>((PlFormula) plparser.parseFormula("a"))));
		assertFalse(abat.getAssumptions()
				.contains(new Assumption<PlFormula>((PlFormula) plparser.parseFormula("z"))));

		InferenceRule<PlFormula> rule = new InferenceRule<>();
		rule.setConclusion((PlFormula) plparser.parseFormula("z"));
		rule.addPremise((PlFormula) plparser.parseFormula("b"));
		rule.addPremise((PlFormula) plparser.parseFormula("y"));
		assertTrue(abat.getRules().contains(rule));

		rule.setConclusion((PlFormula) plparser.parseFormula("y"));
		rule.getPremise().clear();
		assertTrue(abat.getRules().contains(rule));

		rule.setConclusion((PlFormula) plparser.parseFormula("y"));
		rule.getPremise().clear();
		rule.addPremise((PlFormula) plparser.parseFormula("b"));
		assertFalse(abat.getRules().contains(rule));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void DeductionTest1() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example1.aba").getFile());

		Collection<Deduction<PlFormula>> deductions = abat.getAllDeductions();
		for (Deduction<?> d : deductions)
			System.out.println(d.getName());
		assertTrue(deductions.size() == 7);

		InferenceRule<PlFormula> rule = (InferenceRule<PlFormula>) parser
				.parseFormula("z <- b,q");
		abat.add(rule);
		deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 7);

		rule = new InferenceRule<>();
		rule.setConclusion((PlFormula) plparser.parseFormula("b"));
		abat.add(rule);
		deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 10);
	}

	@Test
	public void DeductionTest2() throws Exception {
		PlParser plparser = new PlParser();
		ABATheory<PlFormula> abat = new ABATheory<>();

		abat.addAssumption((PlFormula) plparser.parseFormula("a"));
		Collection<Deduction<PlFormula>> ds = abat.getAllDeductions();
		assertTrue(ds.size() == 1);
		Deduction<PlFormula> deduction = ds.iterator().next();
		assertTrue(deduction.getConclusion().equals((PlFormula) plparser.parseFormula("a")));
		assertTrue(deduction.getRules().size() == 0);

		InferenceRule<PlFormula> rule = new InferenceRule<>();
		rule.setConclusion((PlFormula) plparser.parseFormula("b"));
		rule.addPremise((PlFormula) plparser.parseFormula("a"));
		rule.addPremise((PlFormula) plparser.parseFormula("c"));
		abat.add(rule);
		rule = new InferenceRule<>();
		rule.setConclusion((PlFormula) plparser.parseFormula("c"));
		abat.add(rule);

		deduction = null;
		for (Deduction<PlFormula> d : abat.getAllDeductions())
			if (d.getConclusion().equals((PlFormula) plparser.parseFormula("b")))
				deduction = d;
		assertFalse(deduction == null);

		assertTrue(deduction.getRules().size() == 2);
		assertTrue(deduction.getAssumptions().size() == 1);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void AttackTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example1.aba").getFile());
		abat.add(parser.parseFormula("not a=!a"));
		abat.add(parser.parseFormula("not !a=a"));
		abat.add(parser.parseFormula("not c=!c"));
		abat.add(parser.parseFormula("not !c=c"));

		abat.add((Assumption<PlFormula>) parser.parseFormula(" ! a"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 3);

		abat.add((ABARule<PlFormula>) parser.parseFormula("! c <- b"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 4);

		abat.add((ABARule<PlFormula>) parser.parseFormula("! c <- a"));
		assertTrue(ABAAttack.allAttacks(abat).size() == 6);
	}

	@Test
	public void ToDungTheoryMethodTest() throws Exception {

	}

	@SuppressWarnings("unchecked")
	@Test
	public void ReasonerTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example2.aba").getFile());
		abat.add((ABARule<PlFormula>) parser.parseFormula("!a<-"));
		abat.add(parser.parseFormula("not a=!a"));
		abat.add(parser.parseFormula("not !a=a"));
		assertTrue(abat.getAllDeductions().size() == 7);
		assertTrue(abat.isFlat());
		List<GeneralABAReasoner<PlFormula>> reasoners = new LinkedList<>();  
		reasoners.add(new FlatABAReasoner<PlFormula>(Semantics.COMPLETE_SEMANTICS));
		reasoners.add(new CompleteReasoner<PlFormula>());
		for (GeneralABAReasoner<PlFormula> reasoner : reasoners) {
			Assumption<PlFormula> query = (Assumption<PlFormula>) parser.parseFormula("a");
			assertFalse(reasoner.query(abat, query, InferenceMode.CREDULOUS));
			query = (Assumption<PlFormula>) parser.parseFormula("b");
			assertTrue(reasoner.query(abat, query, InferenceMode.CREDULOUS));
		}
		assertTrue(((FlatABAReasoner<PlFormula>)reasoners.get(0)).getModels(abat).size() == ((GeneralABAReasoner<PlFormula>)reasoners.get(1)).getModels(abat).size());

	}

	@Test
	public void ClosureTest() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example2.aba").getFile());
		assertTrue(abat.isClosed(abat.getAssumptions()));
		assertTrue(abat.isFlat());
		abat.addAssumption((PlFormula) plparser.parseFormula("r"));
		assertFalse(abat.isFlat());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void Example3() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example3.aba").getFile());
		assertFalse(abat.isFlat());

		GeneralABAReasoner<PlFormula> abar = new CompleteReasoner<>();
		Collection<AbaExtension<PlFormula>> complexts = abar.getModels(abat);
		
		Collection<AbaExtension<PlFormula>> prefexts = new PreferredReasoner<PlFormula>().getModels(abat);

		GeneralABAReasoner<PlFormula> grounded_reasoner = new WellFoundedReasoner<>();
		Collection<AbaExtension<PlFormula>> groundedexts = grounded_reasoner.getModels(abat);
		assertTrue(groundedexts.size() == 1);
		
		//System.out.println("exts"+complexts);
		

		AbaExtension<PlFormula> asss_c = new AbaExtension<PlFormula>();
		asss_c.add((Assumption<PlFormula>) parser.parseFormula("c"));
		assertTrue(abat.isClosed(asss_c));
		assertTrue(abat.isAdmissible(asss_c));
		assertTrue(prefexts.contains(asss_c));
	//	assertTrue(complexts.contains(asss_c));

		AbaExtension<PlFormula> asss_0 = new AbaExtension<PlFormula>();
		assertTrue(abat.isAdmissible(asss_0));
		assertFalse(prefexts.contains(asss_0));
		//assertTrue(complexts.contains(asss_0));
		//assertTrue(groundedexts.contains(asss_0));

		AbaExtension<PlFormula> asss_b = new AbaExtension<PlFormula>();
		asss_b.add((Assumption<PlFormula>) parser.parseFormula("b"));
		assertFalse(abat.isClosed(asss_b));
		assertFalse(abat.isAdmissible(asss_b));
		assertFalse(prefexts.contains(asss_b));
		assertFalse(complexts.contains(asss_b));

		AbaExtension<PlFormula> asss_ab = new AbaExtension<PlFormula>();
		asss_ab.add((Assumption<PlFormula>) parser.parseFormula("a"));
		asss_ab.add((Assumption<PlFormula>) parser.parseFormula("b"));
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
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example4.aba").getFile());
		assertFalse(abat.isFlat());
		
		Collection<AbaExtension<PlFormula>> complexts = new CompleteReasoner<PlFormula>().getModels(abat);
		assertTrue(complexts.size() ==0);
		
		Collection<AbaExtension<PlFormula>> prefexts = new PreferredReasoner<PlFormula>().getModels(abat);

		
		AbaExtension<PlFormula> asss_a = new AbaExtension<PlFormula>();
		asss_a.add((Assumption<PlFormula>) parser.parseFormula("a"));
		assertFalse(complexts.contains(asss_a));
		assertTrue(prefexts.contains(asss_a));
	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void Example5() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example5.aba").getFile());
		assertFalse(abat.isFlat());
		
		Collection<AbaExtension<PlFormula>> complexts = new CompleteReasoner<PlFormula>().getModels(abat);
		assertTrue(complexts.size() ==2);
		Collection<AbaExtension<PlFormula>> wellfexts = new WellFoundedReasoner<PlFormula>().getModels(abat);
		assertTrue(complexts.size() ==2);
		
		AbaExtension<PlFormula> asss_ac = new AbaExtension<PlFormula>();
		asss_ac.add((Assumption<PlFormula>) parser.parseFormula("a"));
		asss_ac.add((Assumption<PlFormula>) parser.parseFormula("c"));
		assertTrue(complexts.contains(asss_ac));
		
		AbaExtension<PlFormula> asss_bc = new AbaExtension<PlFormula>();
		asss_bc.add((Assumption<PlFormula>) parser.parseFormula("c"));
		asss_bc.add((Assumption<PlFormula>) parser.parseFormula("b"));
		assertTrue(complexts.contains(asss_bc));
	
		AbaExtension<PlFormula> asss_c = new AbaExtension<PlFormula>();
		asss_c.add((Assumption<PlFormula>) parser.parseFormula("c"));
		assertTrue(wellfexts.contains(asss_c));
	}

	@Test
	public void Example11() throws Exception {
		PlParser plparser = new PlParser();
		ABAParser<PlFormula> parser = new ABAParser<>(plparser);
		ABATheory<PlFormula> abat = parser.parseBeliefBaseFromFile(ABATest.class.getResource("/example11.aba").getFile());

		assertTrue(abat.isFlat());

		DungTheory dt = abat.asDungTheory();
		assertTrue(dt.getNodes().size() == 6);
		assertTrue(dt.getAttacks().size() == 6);

	}

}

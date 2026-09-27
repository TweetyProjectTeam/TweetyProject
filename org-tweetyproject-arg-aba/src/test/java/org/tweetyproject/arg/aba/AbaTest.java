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
package org.tweetyproject.arg.aba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.tweetyproject.arg.aba.examples.AbaExample;
import org.tweetyproject.arg.aba.parser.AbaParser;
import org.tweetyproject.arg.aba.reasoner.AdmissibleReasoner;
import org.tweetyproject.arg.aba.reasoner.CompleteReasoner;
import org.tweetyproject.arg.aba.reasoner.ConflictFreeReasoner;
import org.tweetyproject.arg.aba.reasoner.FlatAbaReasoner;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.reasoner.IdealReasoner;
import org.tweetyproject.arg.aba.reasoner.PreferredReasoner;
import org.tweetyproject.arg.aba.reasoner.StableReasoner;
import org.tweetyproject.arg.aba.reasoner.WellFoundedReasoner;
import org.tweetyproject.arg.aba.semantics.AbaAttack;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaRule;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.aba.syntax.Deduction;
import org.tweetyproject.arg.aba.syntax.InferenceRule;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Test class for ABA.
 * 
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * @author Anna Gessler
 *
 */
public class AbaTest {

	@BeforeEach
	public void SetUp() {
		SatSolver.setDefaultSolver(new Sat4jSolver());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void PlParserTest() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);

		AbaRule<PlFormula> assumption = (AbaRule<PlFormula>) parser.parseFormula("a");
		AbaRule<PlFormula> rule_from_true = (AbaRule<PlFormula>) parser.parseFormula("a <-");
		AbaRule<PlFormula> two_params_rule = (AbaRule<PlFormula>) parser.parseFormula("b <- a, c");
		assertTrue(assumption.getConclusion().equals(rule_from_true.getConclusion()));
		assertTrue(assumption instanceof Assumption<?>);
		assertTrue(rule_from_true instanceof InferenceRule<?>);
		assertTrue(two_params_rule.getPremise().size() == 2);

		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example1.aba").getFile());
		assertTrue(abat.getAssumptions().size() == 3);
		assertTrue(abat.getRules().size() == 4);

		assertTrue(abat.getAssumptions().contains(new Assumption<PlFormula>((PlFormula) plparser.parseFormula("a"))));
		assertFalse(abat.getAssumptions().contains(new Assumption<PlFormula>((PlFormula) plparser.parseFormula("z"))));

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

	@Test
	public void FolParserTest() throws Exception {
		FolParser folparser = new FolParser();
		FolSignature sig = folparser.parseSignature("Male = {a,b}\n" + "Female = {c,d}\n" + "type(Pair(Male,Female))\n"
				+ "type(ContraryPair(Male,Female))\n" + "type(MPrefers(Male,Female,Female))\n"
				+ "type(WPrefers(Female,Male,Male))");
		folparser.setSignature(sig);
		AbaParser<FolFormula> parser = new AbaParser<FolFormula>(folparser);
		parser.setSymbolComma(";");
		AbaTheory<FolFormula> abat = parser
				.parseBeliefBaseFromFile(AbaExample.class.getResource("/smp_fol.aba").getFile());

		assertTrue(abat.getAssumptions().size() == 4);
		assertTrue(abat.getRules().size() == 20);
		assertTrue(abat.getNegations().size() == 4);
		assertTrue(abat.getAssumptions()
				.contains(new Assumption<FolFormula>((FolFormula) folparser.parseFormula("Pair(a,c)"))));

	}

	@Test
	public void FolDeductionsUseGroundAssumptions() throws Exception {
		FolParser folparser = new FolParser();
		folparser.setSignature(folparser.parseSignature("Male = {a,b}\n" + "Female = {c,d}\n" + "type(Pair(Male,Female))\n"
				+ "type(Likes(Male,Female))"));
		AbaParser<FolFormula> parser = new AbaParser<FolFormula>(folparser);
		parser.setSymbolComma(";");
		AbaTheory<FolFormula> abat = parser.parseBeliefBase("{Pair(A,B)}\nLikes(a,c) <-");

		FolFormula pair = (FolFormula) folparser.parseFormula("Pair(a,c)");
		assertTrue(abat.getAllDeductions().stream().anyMatch(d -> d.getConclusion().equals(pair)));
	}

	@Test
	public void FolGroundingUsesConstantsFromContraries() throws Exception {
		FolParser folparser = new FolParser();
		folparser.setSignature(folparser.parseSignature("Male = {a,b}\n" + "Female = {c,d}\n" + "type(Pair(Male,Female))\n"
				+ "type(Likes(Male,Female))"));
		AbaParser<FolFormula> parser = new AbaParser<FolFormula>(folparser);
		parser.setSymbolComma(";");
		AbaTheory<FolFormula> abat = parser.parseBeliefBase("{Pair(A,c)}\nnot Pair(a,c) = Likes(b,c)");

		FolFormula pair = (FolFormula) folparser.parseFormula("Pair(b,c)");
		assertTrue(abat.getAssumptions().contains(new Assumption<FolFormula>(pair)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void DeductionTest1() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example1.aba").getFile());

		Collection<Deduction<PlFormula>> deductions = abat.getAllDeductions();
		assertTrue(deductions.size() == 7);

		InferenceRule<PlFormula> rule = (InferenceRule<PlFormula>) parser.parseFormula("z <- b,q");
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
		AbaTheory<PlFormula> abat = new AbaTheory<>();

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
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example1.aba").getFile());
		abat.add(parser.parseFormula("not a=!a"));
		abat.add(parser.parseFormula("not !a=a"));
		abat.add(parser.parseFormula("not c=!c"));
		abat.add(parser.parseFormula("not !c=c"));

		abat.add((Assumption<PlFormula>) parser.parseFormula(" ! a"));
		assertTrue(AbaAttack.allAttacks(abat).size() == 3);

		abat.add((AbaRule<PlFormula>) parser.parseFormula("! c <- b"));
		assertTrue(AbaAttack.allAttacks(abat).size() == 4);

		abat.add((AbaRule<PlFormula>) parser.parseFormula("! c <- a"));
		assertTrue(AbaAttack.allAttacks(abat).size() == 6);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void ToDungTheoryMethodTest() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example2.aba").getFile());
		abat.add((AbaRule<PlFormula>) parser.parseFormula("!a<-"));
		abat.add(parser.parseFormula("not a=!a"));
		DungTheory dt = abat.asDungTheory();
		assertTrue(dt.getNodes().size()==7);
		assertTrue(dt.getAttacks().size()==2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void ReasonerTest() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example2.aba").getFile());
		abat.add((AbaRule<PlFormula>) parser.parseFormula("!a<-"));
		abat.add(parser.parseFormula("not a=!a"));
		abat.add(parser.parseFormula("not !a=a"));
		assertTrue(abat.getAllDeductions().size() == 7);
		assertTrue(abat.isFlat());
		List<GeneralAbaReasoner<PlFormula>> reasoners = new LinkedList<>();
		reasoners.add(new FlatAbaReasoner<PlFormula>(Semantics.COMPLETE_SEMANTICS));
		reasoners.add(new CompleteReasoner<PlFormula>());
		for (GeneralAbaReasoner<PlFormula> reasoner : reasoners) {
			Assumption<PlFormula> query = (Assumption<PlFormula>) parser.parseFormula("a");
			assertFalse(reasoner.query(abat, query, InferenceMode.CREDULOUS));
			query = (Assumption<PlFormula>) parser.parseFormula("b");
			assertTrue(reasoner.query(abat, query, InferenceMode.CREDULOUS));
		}
		assertTrue(((FlatAbaReasoner<PlFormula>) reasoners.get(0)).getModels(abat)
				.size() == ((GeneralAbaReasoner<PlFormula>) reasoners.get(1)).getModels(abat).size());

	}

	@Test
	public void ClosureTest() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example2.aba").getFile());

		assertTrue(abat.isClosed(abat.getAssumptions()));
		assertTrue(abat.isFlat());
		abat.addAssumption((PlFormula) plparser.parseFormula("r"));

		assertFalse(abat.isFlat());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void Example3() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example3.aba").getFile());

		assertFalse(abat.isFlat());

		GeneralAbaReasoner<PlFormula> abar = new CompleteReasoner<>();
		Collection<AbaExtension<PlFormula>> complexts = abar.getModels(abat);

		Collection<AbaExtension<PlFormula>> prefexts = new PreferredReasoner<PlFormula>().getModels(abat);

		GeneralAbaReasoner<PlFormula> grounded_reasoner = new WellFoundedReasoner<>();
		Collection<AbaExtension<PlFormula>> groundedexts = grounded_reasoner.getModels(abat);
		assertTrue(groundedexts.size() == 1);

		AbaExtension<PlFormula> asss_c = new AbaExtension<PlFormula>();
		asss_c.add((Assumption<PlFormula>) parser.parseFormula("c"));
		assertTrue(abat.isClosed(asss_c));
		assertTrue(abat.isAdmissible(asss_c));
		assertTrue(prefexts.contains(asss_c));
		// assertTrue(complexts.contains(asss_c));

		AbaExtension<PlFormula> asss_0 = new AbaExtension<PlFormula>();
		assertTrue(abat.isAdmissible(asss_0));
		assertFalse(prefexts.contains(asss_0));
		// assertTrue(complexts.contains(asss_0));
		// assertTrue(groundedexts.contains(asss_0));

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
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example4.aba").getFile());
		assertFalse(abat.isFlat());

		Collection<AbaExtension<PlFormula>> complexts = new CompleteReasoner<PlFormula>().getModels(abat);
		assertTrue(complexts.size() == 0);

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
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example5.aba").getFile());
		assertFalse(abat.isFlat());

		Collection<AbaExtension<PlFormula>> complexts = new CompleteReasoner<PlFormula>().getModels(abat);
		assertTrue(complexts.size() == 2);
		Collection<AbaExtension<PlFormula>> wellfexts = new WellFoundedReasoner<PlFormula>().getModels(abat);
		assertTrue(complexts.size() == 2);

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
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example11.aba").getFile());

		assertTrue(abat.isFlat());

		DungTheory dt = abat.asDungTheory();
		assertTrue(dt.getNodes().size() == 6);
		assertTrue(dt.getAttacks().size() == 6);

	}

	@Test
	public void MinimalSupportsMatchBruteForce() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		for (String file : new String[] { "example1", "example2", "example3", "example4", "example5", "example11" }) {
			AbaTheory<PlFormula> abat = parser
					.parseBeliefBaseFromFile(AbaTest.class.getResource("/" + file + ".aba").getFile());
			Map<PlFormula, Set<Set<Assumption<PlFormula>>>> supports = abat.getMinimalSupports();
			Set<Set<Assumption<PlFormula>>> subsets = SetTools.powerSet(new HashSet<>(abat.getAssumptions()));
			Set<PlFormula> formulas = new HashSet<>();
			for (InferenceRule<PlFormula> r : abat.getRules()) {
				formulas.add(r.getConclusion());
				formulas.addAll(r.getPremise());
			}
			for (Assumption<PlFormula> a : abat.getAssumptions()) {
				formulas.add(a.getConclusion());
				formulas.addAll(abat.getContraries(a.getConclusion()));
			}
			for (PlFormula f : formulas) {
				Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
				for (Set<Assumption<PlFormula>> s : subsets)
					if (abat.getDerivable(s).contains(f)
							&& subsets.stream().noneMatch(t -> s.containsAll(t) && !t.equals(s) && abat.getDerivable(t).contains(f)))
						expected.add(s);
				assertEquals(expected, supports.getOrDefault(f, Set.of()), file + ": " + f);
			}
		}
	}

	@Test
	public void ConflictFreeReasonerMatchesTheory() throws Exception {
		PlParser plparser = new PlParser();
		AbaParser<PlFormula> parser = new AbaParser<>(plparser);
		for (String file : new String[] { "example1", "example2", "example3", "example4", "example5", "example11" }) {
			AbaTheory<PlFormula> abat = parser
					.parseBeliefBaseFromFile(AbaTest.class.getResource("/" + file + ".aba").getFile());
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			for (Collection<Assumption<PlFormula>> ext : abat.getAllConflictFreeExtensions())
				expected.add(new HashSet<>(ext));
			Set<Set<Assumption<PlFormula>>> actual = new HashSet<>();
			for (AbaExtension<PlFormula> ext : new ConflictFreeReasoner<PlFormula>().getModels(abat))
				actual.add(new HashSet<>(ext));
			assertEquals(expected, actual, file);
		}
	}

	private List<AbaTheory<PlFormula>> comparisonTheories() throws Exception {
		AbaParser<PlFormula> parser = new AbaParser<>(new PlParser());
		List<AbaTheory<PlFormula>> theories = new LinkedList<>();
		for (String file : new String[] { "example1", "example2", "example3", "example4", "example5", "example11" })
			theories.add(parser.parseBeliefBaseFromFile(AbaTest.class.getResource("/" + file + ".aba").getFile()));
		// flat: a chain of attacks, and multi-premise rules with a cycle
		theories.add(parser.parseBeliefBase("{a0,a1,a2,a3,a4,a5}\nnot a0 = c0\nnot a1 = c1\nnot a2 = c2\nnot a3 = c3\n"
				+ "not a4 = c4\nnot a5 = c5\nc1 <- a0\nc2 <- a1\nc3 <- a2\nc4 <- a3\nc5 <- a4"));
		theories.add(parser.parseBeliefBase("{a,b,c,d}\nx <- a,b\ny <- c\nz <- d\nw <- x\n"
				+ "not a = y\nnot b = z\nnot c = w\nnot d = y"));
		return theories;
	}

	@Test
	public void AdmissibleReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			for (AbaExtension<PlFormula> ext : abat.getAllAdmissbleExtensions())
				expected.add(new HashSet<>(ext));
			Set<Set<Assumption<PlFormula>>> actual = new HashSet<>();
			for (AbaExtension<PlFormula> ext : new AdmissibleReasoner<PlFormula>().getModels(abat))
				actual.add(new HashSet<>(ext));
			assertEquals(expected, actual, abat.toString());
		}
	}

	@Test
	public void CompleteReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			l: for (AbaExtension<PlFormula> ext : abat.getAllAdmissbleExtensions()) {
				for (Assumption<PlFormula> a : abat.getAssumptions())
					if (!ext.contains(a) && abat.defends(ext, a))
						continue l;
				expected.add(new HashSet<>(ext));
			}
			Set<Set<Assumption<PlFormula>>> actual = new HashSet<>();
			for (AbaExtension<PlFormula> ext : new CompleteReasoner<PlFormula>().getModels(abat))
				actual.add(new HashSet<>(ext));
			assertEquals(expected, actual, abat.toString());
		}
	}

	@Test
	public void PreferredReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Collection<AbaExtension<PlFormula>> adm = abat.getAllAdmissbleExtensions();
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			for (AbaExtension<PlFormula> ext : adm)
				if (adm.stream().noneMatch(o -> o.containsAll(ext) && !ext.containsAll(o)))
					expected.add(new HashSet<>(ext));
			Set<Set<Assumption<PlFormula>>> actual = new HashSet<>();
			for (AbaExtension<PlFormula> ext : new PreferredReasoner<PlFormula>().getModels(abat))
				actual.add(new HashSet<>(ext));
			assertEquals(expected, actual, abat.toString());
		}
	}

	@Test
	public void StableReasonerMatchesDefinition() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			for (Collection<Assumption<PlFormula>> ext : abat.getAllExtensions())
				if (abat.isConflictFree(ext) && abat.isClosed(ext) && abat.getAssumptions().stream()
						.allMatch(a -> ext.contains(a) || abat.attacks(ext, Set.of(a))))
					expected.add(new HashSet<>(ext));
			Set<Set<Assumption<PlFormula>>> actual = new HashSet<>();
			for (AbaExtension<PlFormula> ext : new StableReasoner<PlFormula>().getModels(abat))
				actual.add(new HashSet<>(ext));
			assertEquals(expected, actual, abat.toString());
		}
	}

	@Test
	public void StableChain() throws Exception {
		AbaTheory<PlFormula> abat = comparisonTheories().get(6);
		Collection<AbaExtension<PlFormula>> stable = new StableReasoner<PlFormula>().getModels(abat);
		assertEquals(1, stable.size());
		Set<String> names = new HashSet<>();
		for (Assumption<PlFormula> a : stable.iterator().next())
			names.add(a.toString());
		assertEquals(Set.of("a0", "a2", "a4"), names);
	}

	@Test
	public void WellFoundedWithoutCompleteExtension() throws Exception {
		AbaParser<PlFormula> parser = new AbaParser<>(new PlParser());
		// the fact a is in every closed set, and derives its own contrary
		AbaTheory<PlFormula> abat = parser.parseBeliefBase("{a}\na <-\nx <- a\nnot a = x");
		assertTrue(new CompleteReasoner<PlFormula>().getModels(abat).isEmpty());
		assertTrue(new WellFoundedReasoner<PlFormula>().getModels(abat).isEmpty());
		assertEquals(null, new WellFoundedReasoner<PlFormula>().getModel(abat));
	}

	@Test
	public void IdealReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Collection<AbaExtension<PlFormula>> adm = abat.getAllAdmissbleExtensions();
			Set<Assumption<PlFormula>> prefIntersection = null;
			for (AbaExtension<PlFormula> ext : adm)
				if (adm.stream().noneMatch(o -> o.containsAll(ext) && !ext.containsAll(o))) {
					if (prefIntersection == null)
						prefIntersection = new HashSet<>(ext);
					else
						prefIntersection.retainAll(ext);
				}
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			if (prefIntersection != null) {
				Set<Assumption<PlFormula>> pi = prefIntersection;
				List<AbaExtension<PlFormula>> inside = adm.stream().filter(pi::containsAll).toList();
				for (AbaExtension<PlFormula> ext : inside)
					if (inside.stream().noneMatch(o -> o.containsAll(ext) && !ext.containsAll(o)))
						expected.add(new HashSet<>(ext));
			}
			Set<Set<Assumption<PlFormula>>> actual = new HashSet<>();
			for (AbaExtension<PlFormula> ext : new IdealReasoner<PlFormula>().getModels(abat))
				actual.add(new HashSet<>(ext));
			assertEquals(expected, actual, abat.toString());
		}
	}

}

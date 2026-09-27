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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
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
import org.tweetyproject.arg.aba.reasoner.AfReductionReasoner;
import org.tweetyproject.arg.aba.reasoner.AfReductionReasoner.SupportArgument;
import org.tweetyproject.arg.aba.reasoner.CompleteReasoner;
import org.tweetyproject.arg.aba.reasoner.ConflictFreeReasoner;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.reasoner.IdealReasoner;
import org.tweetyproject.arg.aba.reasoner.PreferredReasoner;
import org.tweetyproject.arg.aba.reasoner.SetafReductionReasoner;
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
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.arg.setaf.syntax.SetAttack;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.util.IncreasingSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;
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
	public void FolGroundingTest() throws Exception {
		FolParser folparser = new FolParser();
		folparser.setSignature(folparser.parseSignature("Male = {a,b}\n" + "Female = {c,d}\n" + "type(Pair(Male,Female))\n"
				+ "type(Likes(Male,Female))"));
		AbaParser<FolFormula> parser = new AbaParser<FolFormula>(folparser);
		parser.setSymbolComma(";");
		// deductions start from the ground instances of an assumption schema
		AbaTheory<FolFormula> abat = parser.parseBeliefBase("{Pair(A,B)}\nLikes(a,c) <-");
		FolFormula pairAC = (FolFormula) folparser.parseFormula("Pair(a,c)");
		assertTrue(abat.getAllDeductions().stream().anyMatch(d -> d.getConclusion().equals(pairAC)));
		// constants occurring only in contraries are used for grounding
		abat = parser.parseBeliefBase("{Pair(A,c)}\nnot Pair(a,c) = Likes(b,c)");
		assertTrue(abat.getAssumptions().contains(new Assumption<FolFormula>((FolFormula) folparser.parseFormula("Pair(b,c)"))));
	}

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

		abat = parser.parseBeliefBaseFromFile(AbaTest.class.getResource("/example11.aba").getFile());
		dt = abat.asDungTheory();
		assertTrue(dt.getNodes().size() == 6);
		assertTrue(dt.getAttacks().size() == 6);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void QueryTest() throws Exception {
		AbaParser<PlFormula> parser = new AbaParser<>(new PlParser());
		AbaTheory<PlFormula> abat = parser.parseBeliefBase("{a,b,c}\nnot a = x\nnot b = y\nnot c = z\nx <- b\ny <- a");
		GeneralAbaReasoner<PlFormula> reasoner = new PreferredReasoner<>();
		Assumption<PlFormula> a = (Assumption<PlFormula>) parser.parseFormula("a");
		Assumption<PlFormula> c = (Assumption<PlFormula>) parser.parseFormula("c");
		// preferred: {a, c} and {b, c}
		assertTrue(reasoner.query(abat, a, InferenceMode.CREDULOUS));
		assertFalse(reasoner.query(abat, a, InferenceMode.SKEPTICAL));
		assertTrue(reasoner.query(abat, c, InferenceMode.SKEPTICAL));
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
		// handbook Ex. 2.9; its stated complete sets {c} and {} do not contain the unattacked b they defend
		AbaTheory<PlFormula> abat = parser
				.parseBeliefBaseFromFile(AbaTest.class.getResource("/example3.aba").getFile());
		assertFalse(abat.isFlat());

		AbaExtension<PlFormula> asss_c = new AbaExtension<PlFormula>();
		asss_c.add((Assumption<PlFormula>) parser.parseFormula("c"));
		AbaExtension<PlFormula> asss_b = new AbaExtension<PlFormula>();
		asss_b.add((Assumption<PlFormula>) parser.parseFormula("b"));
		AbaExtension<PlFormula> asss_ab = new AbaExtension<PlFormula>();
		asss_ab.add((Assumption<PlFormula>) parser.parseFormula("a"));
		asss_ab.add((Assumption<PlFormula>) parser.parseFormula("b"));

		assertTrue(abat.isClosed(asss_c));
		assertTrue(abat.isConflictFree(asss_c));
		assertFalse(abat.isClosed(asss_b));
		assertTrue(abat.attacks(asss_b, asss_c));
		assertTrue(abat.isClosed(asss_ab));

		Set<Assumption<PlFormula>> c = new HashSet<>(asss_c), ab = new HashSet<>(asss_ab);
		assertEquals(Set.of(Set.of(), c, ab), asSets(new AdmissibleReasoner<PlFormula>().getModels(abat)));
		assertEquals(Set.of(c, ab), asSets(new PreferredReasoner<PlFormula>().getModels(abat)));
		assertEquals(Set.of(ab), asSets(new CompleteReasoner<PlFormula>().getModels(abat)));
		assertEquals(Set.of(ab), asSets(new WellFoundedReasoner<PlFormula>().getModels(abat)));
	}

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
		assertTrue(wellfexts.size() == 1);

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
	public void MinimalSupportsMatchBruteForce() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Map<PlFormula, Set<Set<Assumption<PlFormula>>>> supports = abat.getMinimalSupports();
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
				// smaller sets come first, so s is minimal iff no support found so far is inside it
				SubsetIterator<Assumption<PlFormula>> it = new IncreasingSubsetIterator<>(new HashSet<>(abat.getAssumptions()));
				while (it.hasNext()) {
					Set<Assumption<PlFormula>> s = it.next();
					if (abat.getDerivable(s).contains(f) && expected.stream().noneMatch(s::containsAll))
						expected.add(s);
				}
				assertEquals(expected, supports.getOrDefault(f, Set.of()), abat + ": " + f);
			}
		}
	}

	@Test
	public void ConflictFreeReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			for (Set<Assumption<PlFormula>> ext : subsets(abat))
				if (abat.isConflictFree(ext))
					expected.add(ext);
			assertEquals(expected, asSets(new ConflictFreeReasoner<PlFormula>().getModels(abat)), abat.toString());
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
		// an odd cycle; conflicts only visible through arguments outside an extension
		theories.add(parser.parseBeliefBase("{a,b,c}\nnb <- b\nnc <- c\nna <- a\nnot a = nb\nnot b = nc\nnot c = na"));
		theories.add(parser.parseBeliefBase("{b,c}\np <- b,c\nnot b = p\nnot c = zc"));
		theories.add(parser.parseBeliefBase("{b,c,d}\np <- b\nx <- b,d\nnot c = p\nnot b = zb\nnot d = zd"));
		theories.add(parser.parseBeliefBase("{a,b,c}\np <- b\nnot a = c\nnot c = p\nnot b = z"));
		// non-flat without complete extension: the fact a derives its own contrary
		theories.add(parser.parseBeliefBase("{a}\na <-\nx <- a\nnot a = x"));
		return theories;
	}

	@Test
	public void AdmissibleReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories())
			assertEquals(admissibleByDefinition(abat), asSets(new AdmissibleReasoner<PlFormula>().getModels(abat)),
					abat.toString());
	}

	private static List<Set<Assumption<PlFormula>>> subsets(AbaTheory<PlFormula> abat) {
		List<Set<Assumption<PlFormula>>> result = new LinkedList<>();
		SubsetIterator<Assumption<PlFormula>> it = new IncreasingSubsetIterator<>(new HashSet<>(abat.getAssumptions()));
		while (it.hasNext())
			result.add(it.next());
		return result;
	}

	// handbook Def. 2.8: closed, conflict-free, and attacking every closed attacker
	private static Set<Set<Assumption<PlFormula>>> admissibleByDefinition(AbaTheory<PlFormula> abat) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (Set<Assumption<PlFormula>> ext : subsets(abat))
			if (abat.isClosed(ext) && abat.isConflictFree(ext) && subsets(abat).stream()
					.noneMatch(att -> abat.isClosed(att) && abat.attacks(att, ext) && !abat.attacks(ext, att)))
				result.add(ext);
		return result;
	}

	private static Set<Set<Assumption<PlFormula>>> completeByDefinition(AbaTheory<PlFormula> abat) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (Set<Assumption<PlFormula>> ext : admissibleByDefinition(abat))
			if (abat.getAssumptions().stream().allMatch(a -> ext.contains(a) || subsets(abat).stream().anyMatch(
					att -> abat.isClosed(att) && abat.attacks(att, Set.of(a)) && !abat.attacks(ext, att))))
				result.add(ext);
		return result;
	}

	private static Set<Set<Assumption<PlFormula>>> maximal(Set<Set<Assumption<PlFormula>>> sets) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (Set<Assumption<PlFormula>> s : sets)
			if (sets.stream().noneMatch(o -> o.containsAll(s) && !s.containsAll(o)))
				result.add(s);
		return result;
	}

	@Test
	public void CompleteReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories())
			assertEquals(completeByDefinition(abat), asSets(new CompleteReasoner<PlFormula>().getModels(abat)),
					abat.toString());
	}

	@Test
	public void WellFoundedReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			// the intersection of all complete sets; none if there is no complete set
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			completeByDefinition(abat).stream().reduce((x, y) -> {
				Set<Assumption<PlFormula>> i = new HashSet<>(x);
				i.retainAll(y);
				return i;
			}).ifPresent(expected::add);
			assertEquals(expected, asSets(new WellFoundedReasoner<PlFormula>().getModels(abat)), abat.toString());
		}
	}

	@Test
	public void PreferredReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories())
			assertEquals(maximal(admissibleByDefinition(abat)),
					asSets(new PreferredReasoner<PlFormula>().getModels(abat)), abat.toString());
	}

	@Test
	public void StableReasonerMatchesDefinition() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
			for (Set<Assumption<PlFormula>> ext : subsets(abat))
				if (abat.isConflictFree(ext) && abat.isClosed(ext) && abat.getAssumptions().stream()
						.allMatch(a -> ext.contains(a) || abat.attacks(ext, Set.of(a))))
					expected.add(ext);
			assertEquals(expected, asSets(new StableReasoner<PlFormula>().getModels(abat)), abat.toString());
		}
	}

	@Test
	public void IdealReasonerMatchesTheory() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			// the maximal admissible sets inside every preferred set
			Set<Set<Assumption<PlFormula>>> adm = admissibleByDefinition(abat);
			Set<Assumption<PlFormula>> inAll = maximal(adm).stream().reduce((x, y) -> {
				Set<Assumption<PlFormula>> i = new HashSet<>(x);
				i.retainAll(y);
				return i;
			}).orElse(Set.of());
			Set<Set<Assumption<PlFormula>>> inside = new HashSet<>();
			for (Set<Assumption<PlFormula>> ext : adm)
				if (inAll.containsAll(ext))
					inside.add(ext);
			assertEquals(maximal(inside), asSets(new IdealReasoner<PlFormula>().getModels(abat)), abat.toString());
		}
	}

	@Test
	public void AfReductionMergesMinimalDerivations() throws Exception {
		AbaParser<PlFormula> parser = new AbaParser<>(new PlParser());
		// Lehtonen (SAFA 2026): the only argument from {a} derives {x, y}
		AbaTheory<PlFormula> abat = parser.parseBeliefBase("{a,b}\nx <- a,b\nx <- a\ny <- a");
		Map<Set<String>, Set<String>> actual = new HashMap<>();
		for (SupportArgument<PlFormula> arg : new AfReductionReasoner<PlFormula>(Semantics.CO).getArguments(abat)) {
			Set<String> support = new HashSet<>();
			for (Assumption<PlFormula> a : arg.getSupport())
				support.add(a.toString());
			Set<String> claims = new HashSet<>();
			for (PlFormula c : arg.getClaims())
				claims.add(c.toString());
			actual.put(support, claims);
		}
		assertEquals(Map.of(Set.of("a"), Set.of("a", "x", "y"), Set.of("b"), Set.of("b")), actual);
	}

	@Test
	public void AfReductionRejectsNonFlat() throws Exception {
		AbaParser<PlFormula> parser = new AbaParser<>(new PlParser());
		AbaTheory<PlFormula> abat = parser.parseBeliefBase("{a,b}\na <- b");
		assertThrows(IllegalArgumentException.class, () -> new AfReductionReasoner<PlFormula>(Semantics.CO).getArguments(abat));
	}

	@Test
	public void AfReductionMatchesDirectReasoners() throws Exception {
		Map<Semantics, GeneralAbaReasoner<PlFormula>> direct = Map.of(Semantics.ADM, new AdmissibleReasoner<>(),
				Semantics.CO, new CompleteReasoner<>(), Semantics.PR, new PreferredReasoner<>(), Semantics.ST,
				new StableReasoner<>(), Semantics.GR, new WellFoundedReasoner<>());
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			if (!abat.isFlat())
				continue;
			for (Map.Entry<Semantics, GeneralAbaReasoner<PlFormula>> e : direct.entrySet())
				assertEquals(asSets(e.getValue().getModels(abat)),
						asSets(new AfReductionReasoner<PlFormula>(e.getKey()).getModels(abat)),
						e.getKey() + ": " + abat);
		}
	}

	@Test
	public void AfReductionRejectsConflictFreeBasedSemantics() {
		for (Semantics s : new Semantics[] { Semantics.CF, Semantics.NA, Semantics.STG, Semantics.CF2, Semantics.WAD,
				Semantics.WPR, Semantics.UD, Semantics.SUD, Semantics.CG })
			assertThrows(IllegalArgumentException.class, () -> new AfReductionReasoner<PlFormula>(s), s.toString());
	}

	@Test
	public void SetafReductionMatchesDirectReasoners() throws Exception {
		Map<Semantics, GeneralAbaReasoner<PlFormula>> direct = Map.of(Semantics.CF, new ConflictFreeReasoner<>(),
				Semantics.ADM, new AdmissibleReasoner<>(), Semantics.CO, new CompleteReasoner<>(), Semantics.PR,
				new PreferredReasoner<>(), Semantics.ST, new StableReasoner<>(), Semantics.GR,
				new WellFoundedReasoner<>(), Semantics.ID, new IdealReasoner<>());
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			if (!abat.isFlat())
				continue;
			for (Map.Entry<Semantics, GeneralAbaReasoner<PlFormula>> e : direct.entrySet())
				assertEquals(asSets(e.getValue().getModels(abat)),
						asSets(new SetafReductionReasoner<PlFormula>(e.getKey()).getModels(abat)),
						e.getKey() + ": " + abat);
			// metalevel semantics must not hide conflicts
			Set<Set<Assumption<PlFormula>>> cf = asSets(new ConflictFreeReasoner<PlFormula>().getModels(abat));
			for (Semantics s : new Semantics[] { Semantics.WAD, Semantics.UD })
				assertTrue(cf.containsAll(asSets(new SetafReductionReasoner<PlFormula>(s).getModels(abat))),
						s + ": " + abat);
		}
	}

	@Test
	public void SetafReductionBuildsAssumptionAttacks() throws Exception {
		AbaParser<PlFormula> parser = new AbaParser<>(new PlParser());
		SetAf joint = new SetafReductionReasoner<PlFormula>(Semantics.CO)
				.getSetaf(parser.parseBeliefBase("{b,c}\np <- b,c\nnot b = p\nnot c = zc"));
		assertEquals(Set.of(new SetAttack(Set.of(new Argument("b"), new Argument("c")), new Argument("b"))),
				new HashSet<>(joint.getAttacks()));
		// q is a fact, so b is never accepted and drops out with its attacks
		SetAf fact = new SetafReductionReasoner<PlFormula>(Semantics.CO).getSetaf(parser.parseBeliefBase(
				"{a,b,c}\nr <- b,c\nq <-\np <- q,a\nnot a = r\nnot b = q\nnot c = p"));
		assertEquals(Set.of(new Argument("a"), new Argument("c")), new HashSet<>(fact));
		assertEquals(Set.of(new SetAttack(Set.of(new Argument("a")), new Argument("c"))),
				new HashSet<>(fact.getAttacks()));
	}

	@Test
	public void SetafReductionRejectsConflictFreeBasedMetalevelSemantics() {
		for (Semantics s : new Semantics[] { Semantics.NA, Semantics.CF2, Semantics.SCF2, Semantics.STG2 })
			assertThrows(IllegalArgumentException.class, () -> new SetafReductionReasoner<PlFormula>(s), s.toString());
	}

	private static Set<Set<Assumption<PlFormula>>> asSets(Collection<AbaExtension<PlFormula>> exts) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (AbaExtension<PlFormula> ext : exts)
			result.add(new HashSet<>(ext));
		return result;
	}

}

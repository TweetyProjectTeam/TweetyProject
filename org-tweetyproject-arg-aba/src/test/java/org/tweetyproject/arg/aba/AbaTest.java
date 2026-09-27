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

import org.junit.jupiter.api.Test;
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
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.syntax.SetAttack;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.util.IncreasingSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Test class for ABA.
 *
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * @author Anna Gessler
 */
public class AbaTest {

	private static final AbaParser<PlFormula> PARSER = new AbaParser<>(new PlParser());

	@Test
	public void ParserTest() throws Exception {
		AbaTheory<PlFormula> abat = file("example1");
		assertEquals(3, abat.getAssumptions().size());
		assertEquals(4, abat.getRules().size());
		// an empty body prints as true, so true parses back to an empty body
		assertTrue(theory("y <- true").getRules().iterator().next().getPremise().isEmpty());
		// no bare assumption lines, and sentences are atoms
		assertTrue(assertThrows(ParserException.class, () -> theory("{a}\nc")).getMessage().startsWith("Line 2"));
		assertThrows(ParserException.class, () -> theory("{a}\np && q <- a"));
		assertTrue(assertThrows(ParserException.class, () -> theory("{a}\nnot p = x")).getMessage().startsWith("Line 2"));

		FolParser folparser = new FolParser();
		folparser.setSignature(folparser.parseSignature("Male = {a,b}\nFemale = {c,d}\ntype(Pair(Male,Female))\n"
				+ "type(Likes(Male,Female))"));
		AbaParser<FolFormula> parser = new AbaParser<>(folparser);
		parser.setSymbolComma(";");
		// grounding also uses constants that only occur in contraries
		AbaTheory<FolFormula> fol = parser.parseBeliefBase("{Pair(A,c)}\nnot Pair(a,c) = Likes(b,c)");
		assertTrue(fol.getAssumptions().contains(new Assumption<>((FolFormula) folparser.parseFormula("Pair(b,c)"))));
	}

	@Test
	public void FlatnessTest() throws Exception {
		assertTrue(file("example2").isFlat());
		assertFalse(file("example3").isFlat());
		// flat although an assumption heads a rule: x is never derivable
		assertTrue(theory("{a}\na <- x").isFlat());
	}

	@Test
	public void MinimalSupportsMatchBruteForce() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Map<PlFormula, Set<Set<Assumption<PlFormula>>>> supports = abat.getMinimalSupports();
			for (PlFormula f : supports.keySet()) {
				// smaller sets come first, so s is minimal iff no support found so far is inside it
				Set<Set<Assumption<PlFormula>>> expected = new HashSet<>();
				for (Set<Assumption<PlFormula>> s : subsets(abat))
					if (abat.getDerivable(s).contains(f) && expected.stream().noneMatch(s::containsAll))
						expected.add(s);
				assertEquals(expected, supports.get(f), abat + ": " + f);
			}
		}
	}

	@Test
	public void DirectReasonersMatchDefinitions() throws Exception {
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			Set<Set<Assumption<PlFormula>>> cf = new HashSet<>(), st = new HashSet<>();
			for (Set<Assumption<PlFormula>> s : subsets(abat)) {
				if (abat.isConflictFree(s))
					cf.add(s);
				if (abat.isConflictFree(s) && abat.isClosed(s) && abat.getAssumptions().stream()
						.allMatch(a -> s.contains(a) || abat.attacks(s, Set.of(a))))
					st.add(s);
			}
			Set<Set<Assumption<PlFormula>>> adm = admissible(abat), co = complete(abat, adm), pr = maximal(adm);
			Set<Set<Assumption<PlFormula>>> wf = co.isEmpty() ? Set.of() : Set.of(intersection(co));
			Set<Assumption<PlFormula>> inAllPreferred = intersection(pr);
			Set<Set<Assumption<PlFormula>>> id = maximal(
					new HashSet<>(adm.stream().filter(inAllPreferred::containsAll).toList()));

			String msg = abat.toString();
			assertEquals(cf, sets(new ConflictFreeReasoner<PlFormula>().getModels(abat)), msg);
			assertEquals(adm, sets(new AdmissibleReasoner<PlFormula>().getModels(abat)), msg);
			assertEquals(co, sets(new CompleteReasoner<PlFormula>().getModels(abat)), msg);
			assertEquals(pr, sets(new PreferredReasoner<PlFormula>().getModels(abat)), msg);
			assertEquals(st, sets(new StableReasoner<PlFormula>().getModels(abat)), msg);
			assertEquals(wf, sets(new WellFoundedReasoner<PlFormula>().getModels(abat)), msg);
			assertEquals(id, sets(new IdealReasoner<PlFormula>().getModels(abat)), msg);
		}
	}

	@Test
	public void NonFlatExamples() throws Exception {
		// example3 is handbook Ex. 2.9; its stated complete sets {c} and {} miss the unattacked b they defend
		AbaTheory<PlFormula> ex3 = file("example3");
		assertEquals(names("", "c", "a b"), names(new AdmissibleReasoner<PlFormula>().getModels(ex3)));
		assertEquals(names("a b"), names(new CompleteReasoner<PlFormula>().getModels(ex3)));
		assertEquals(names("a b"), names(new WellFoundedReasoner<PlFormula>().getModels(ex3)));
		AbaTheory<PlFormula> ex4 = file("example4");
		assertEquals(names(), names(new CompleteReasoner<PlFormula>().getModels(ex4)));
		assertEquals(names("a"), names(new PreferredReasoner<PlFormula>().getModels(ex4)));
		AbaTheory<PlFormula> ex5 = file("example5");
		assertEquals(names("a c", "b c"), names(new CompleteReasoner<PlFormula>().getModels(ex5)));
		assertEquals(names("c"), names(new WellFoundedReasoner<PlFormula>().getModels(ex5)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void QueryTest() throws Exception {
		// preferred: {a, c} and {b, c}
		AbaTheory<PlFormula> abat = theory("{a,b,c}\nnot a = x\nnot b = y\nnot c = z\nx <- b\ny <- a");
		GeneralAbaReasoner<PlFormula> reasoner = new PreferredReasoner<>();
		Assumption<PlFormula> a = (Assumption<PlFormula>) PARSER.parseFormula("a");
		Assumption<PlFormula> c = (Assumption<PlFormula>) PARSER.parseFormula("c");
		assertTrue(reasoner.query(abat, a, InferenceMode.CREDULOUS));
		assertFalse(reasoner.query(abat, a, InferenceMode.SKEPTICAL));
		assertTrue(reasoner.query(abat, c, InferenceMode.SKEPTICAL));
	}

	@Test
	public void AfReductionMergesMinimalDerivations() throws Exception {
		// Lehtonen (SAFA 2026): the only argument from {a} derives {x, y}
		AbaTheory<PlFormula> abat = theory("{a,b}\nx <- a,b\nx <- a\ny <- a");
		Map<Set<String>, Set<String>> actual = new HashMap<>();
		for (SupportArgument<PlFormula> arg : new AfReductionReasoner<PlFormula>(Semantics.CO).getArguments(abat))
			actual.put(strings(arg.getSupport()), strings(arg.getClaims()));
		assertEquals(Map.of(Set.of("a"), Set.of("a", "x", "y"), Set.of("b"), Set.of("b")), actual);
	}

	@Test
	public void SetafReductionBuildsAssumptionAttacks() throws Exception {
		SetafReductionReasoner<PlFormula> reasoner = new SetafReductionReasoner<>(Semantics.CO);
		assertEquals(Set.of(new SetAttack(Set.of(new Argument("b"), new Argument("c")), new Argument("b"))),
				new HashSet<>(reasoner.getSetaf(theory("{b,c}\np <- b,c\nnot b = p\nnot c = zc")).getAttacks()));
		// q is a fact, so b is never accepted and drops out with its attacks
		assertEquals(Set.of(new SetAttack(Set.of(new Argument("a")), new Argument("c"))), new HashSet<>(reasoner
				.getSetaf(theory("{a,b,c}\nr <- b,c\nq <-\np <- q,a\nnot a = r\nnot b = q\nnot c = p")).getAttacks()));
	}

	@Test
	public void ReductionsMatchDirectReasoners() throws Exception {
		Map<Semantics, GeneralAbaReasoner<PlFormula>> direct = Map.of(Semantics.CF, new ConflictFreeReasoner<>(),
				Semantics.ADM, new AdmissibleReasoner<>(), Semantics.CO, new CompleteReasoner<>(), Semantics.PR,
				new PreferredReasoner<>(), Semantics.ST, new StableReasoner<>(), Semantics.GR,
				new WellFoundedReasoner<>(), Semantics.ID, new IdealReasoner<>());
		for (AbaTheory<PlFormula> abat : comparisonTheories()) {
			if (!abat.isFlat())
				continue;
			for (Map.Entry<Semantics, GeneralAbaReasoner<PlFormula>> e : direct.entrySet()) {
				Set<Set<Assumption<PlFormula>>> expected = sets(e.getValue().getModels(abat));
				String msg = e.getKey() + ": " + abat;
				assertEquals(expected, sets(new SetafReductionReasoner<PlFormula>(e.getKey()).getModels(abat)), msg);
				if (e.getKey() != Semantics.CF && e.getKey() != Semantics.ID)
					assertEquals(expected, sets(new AfReductionReasoner<PlFormula>(e.getKey()).getModels(abat)), msg);
			}
			// metalevel semantics of the SETAF reduction must not hide conflicts
			Set<Set<Assumption<PlFormula>>> cf = sets(new ConflictFreeReasoner<PlFormula>().getModels(abat));
			for (Semantics s : new Semantics[] { Semantics.WAD, Semantics.UD })
				assertTrue(cf.containsAll(sets(new SetafReductionReasoner<PlFormula>(s).getModels(abat))), s + ": " + abat);
		}
	}

	private static List<AbaTheory<PlFormula>> comparisonTheories() throws Exception {
		List<AbaTheory<PlFormula>> theories = new LinkedList<>();
		for (String name : new String[] { "example1", "example2", "example3", "example4", "example5", "example11" })
			theories.add(file(name));
		// a chain, multi-premise rules, an odd cycle
		theories.add(theory("{a0,a1,a2,a3}\nnot a0 = c0\nnot a1 = c1\nnot a2 = c2\nnot a3 = c3\nc1 <- a0\nc2 <- a1\n"
				+ "c3 <- a2"));
		theories.add(theory("{a,b,c,d}\nx <- a,b\ny <- c\nz <- d\nw <- x\nnot a = y\nnot b = z\nnot c = w\nnot d = y"));
		theories.add(theory("{a,b,c}\nnb <- b\nnc <- c\nna <- a\nnot a = nb\nnot b = nc\nnot c = na"));
		// conflicts only visible through arguments outside an extension
		theories.add(theory("{b,c}\np <- b,c\nnot b = p\nnot c = zc"));
		theories.add(theory("{b,c,d}\np <- b\nx <- b,d\nnot c = p\nnot b = zb\nnot d = zd"));
		theories.add(theory("{a,b,c}\np <- b\nnot a = c\nnot c = p\nnot b = z"));
		// non-flat without complete extension: the fact a derives its own contrary
		theories.add(theory("{a}\na <-\nx <- a\nnot a = x"));
		return theories;
	}

	// handbook Def. 2.8: closed, conflict-free, and attacking every closed attacker
	private static Set<Set<Assumption<PlFormula>>> admissible(AbaTheory<PlFormula> abat) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (Set<Assumption<PlFormula>> s : subsets(abat))
			if (abat.isClosed(s) && abat.isConflictFree(s) && subsets(abat).stream()
					.noneMatch(t -> abat.isClosed(t) && abat.attacks(t, s) && !abat.attacks(s, t)))
				result.add(s);
		return result;
	}

	private static Set<Set<Assumption<PlFormula>>> complete(AbaTheory<PlFormula> abat,
			Set<Set<Assumption<PlFormula>>> adm) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (Set<Assumption<PlFormula>> s : adm)
			if (abat.getAssumptions().stream().allMatch(a -> s.contains(a) || subsets(abat).stream()
					.anyMatch(t -> abat.isClosed(t) && abat.attacks(t, Set.of(a)) && !abat.attacks(s, t))))
				result.add(s);
		return result;
	}

	private static Set<Set<Assumption<PlFormula>>> maximal(Set<Set<Assumption<PlFormula>>> sets) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (Set<Assumption<PlFormula>> s : sets)
			if (sets.stream().noneMatch(o -> o.containsAll(s) && !s.containsAll(o)))
				result.add(s);
		return result;
	}

	private static Set<Assumption<PlFormula>> intersection(Set<Set<Assumption<PlFormula>>> sets) {
		return sets.stream().reduce((x, y) -> {
			Set<Assumption<PlFormula>> i = new HashSet<>(x);
			i.retainAll(y);
			return i;
		}).orElse(Set.of());
	}

	private static List<Set<Assumption<PlFormula>>> subsets(AbaTheory<PlFormula> abat) {
		List<Set<Assumption<PlFormula>>> result = new LinkedList<>();
		SubsetIterator<Assumption<PlFormula>> it = new IncreasingSubsetIterator<>(new HashSet<>(abat.getAssumptions()));
		while (it.hasNext())
			result.add(it.next());
		return result;
	}

	private static Set<Set<Assumption<PlFormula>>> sets(Collection<? extends Collection<Assumption<PlFormula>>> exts) {
		Set<Set<Assumption<PlFormula>>> result = new HashSet<>();
		for (Collection<Assumption<PlFormula>> ext : exts)
			result.add(new HashSet<>(ext));
		return result;
	}

	private static Set<Set<String>> names(Collection<? extends Collection<Assumption<PlFormula>>> exts) {
		Set<Set<String>> result = new HashSet<>();
		for (Collection<Assumption<PlFormula>> ext : exts)
			result.add(strings(ext));
		return result;
	}

	private static Set<Set<String>> names(String... exts) {
		Set<Set<String>> result = new HashSet<>();
		for (String ext : exts)
			result.add(ext.isEmpty() ? Set.of() : Set.of(ext.split(" ")));
		return result;
	}

	private static Set<String> strings(Collection<?> objects) {
		return new HashSet<>(objects.stream().map(Object::toString).toList());
	}

	private static AbaTheory<PlFormula> theory(String text) throws Exception {
		return PARSER.parseBeliefBase(text);
	}

	private static AbaTheory<PlFormula> file(String name) throws Exception {
		return PARSER.parseBeliefBaseFromFile(AbaTest.class.getResource("/" + name + ".aba").getFile());
	}
}

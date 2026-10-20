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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.reasoner.StronglyAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.serialisability.InitialModels;
import org.tweetyproject.serialisability.SelectionFunction;
import org.tweetyproject.serialisability.SerialisationSequence;
import org.tweetyproject.serialisability.SerialisationSystem;
import org.tweetyproject.serialisability.TerminationFunction;

/**
 * Tests for {@link AfExtensionScheme} and {@link AfLabelingScheme} with the
 * admissible serialisation basis.
 *
 * @author Lars Bengel
 */
public class AfSerialisationSchemeTest {

	/**
	 * @param arguments the argument names, one character each
	 * @param attacks   attacks of the form "ab" (a attacks b)
	 * @return the argumentation framework
	 */
	private static DungTheory af(String arguments, String... attacks) {
		DungTheory af = new DungTheory();
		for (char c : arguments.toCharArray())
			af.add(new Argument(String.valueOf(c)));
		for (String attack : attacks)
			af.addAttack(new Argument(attack.substring(0, 1)), new Argument(attack.substring(1, 2)));
		return af;
	}

	/**
	 * @param arguments the argument names, one character each
	 * @return the extension
	 */
	private static Extension<DungTheory> ext(String arguments) {
		Extension<DungTheory> result = new Extension<>();
		for (char c : arguments.toCharArray())
			result.add(new Argument(String.valueOf(c)));
		return result;
	}

	/**
	 * @return small random frameworks with up to six arguments
	 */
	private static List<DungTheory> randomFrameworks() {
		List<DungTheory> result = new ArrayList<>();
		for (int n = 1; n <= 6; n++) {
			for (double p : new double[] { 0.2, 0.4 }) {
				DefaultDungTheoryGenerator generator = new DefaultDungTheoryGenerator(n, p);
				generator.setSeed(42 * n);
				for (int i = 0; i < 5; i++)
					result.add(generator.next());
			}
		}
		return result;
	}

	/**
	 * @param alpha some selection function
	 * @param beta  some termination function
	 * @return the serialisation system for extensions
	 */
	private static SerialisationSystem<DungTheory, Extension<DungTheory>> system(
			SelectionFunction<DungTheory, Extension<DungTheory>> alpha,
			TerminationFunction<DungTheory, Extension<DungTheory>> beta) {
		return new SerialisationSystem<>(new AfExtensionScheme(), alpha, beta);
	}

	@Test
	void extensionSchemeMatchesReasoners() {
		Map<SerialisationSystem<DungTheory, Extension<DungTheory>>, AbstractExtensionReasoner> cases = Map.of(
				system(SelectionFunction.all(), TerminationFunction.always()), new SimpleAdmissibleReasoner(),
				system(SelectionFunction.all(), TerminationFunction.noUnattackedInitialModels()), new SimpleCompleteReasoner(),
				system(SelectionFunction.all(), TerminationFunction.noInitialModels()), new SimplePreferredReasoner(),
				system(SelectionFunction.all(), TerminationFunction.emptyFramework()), new SimpleStableReasoner(),
				system(SelectionFunction.unattacked(), TerminationFunction.noUnattackedInitialModels()), new SimpleGroundedReasoner(),
				system(SelectionFunction.unattacked(), TerminationFunction.always()), new StronglyAdmissibleReasoner());
		for (DungTheory af : randomFrameworks())
			for (Map.Entry<SerialisationSystem<DungTheory, Extension<DungTheory>>, AbstractExtensionReasoner> c : cases.entrySet())
				assertEquals(new HashSet<>(c.getValue().getModels(af)), c.getKey().getModels(af), af.toString());
	}

	@Test
	void reasonerMatchesReasoners() {
		Map<Semantics, AbstractExtensionReasoner> cases = Map.of(
				Semantics.ADM, new SimpleAdmissibleReasoner(),
				Semantics.CO, new SimpleCompleteReasoner(),
				Semantics.PR, new SimplePreferredReasoner(),
				Semantics.ST, new SimpleStableReasoner(),
				Semantics.GR, new SimpleGroundedReasoner(),
				Semantics.SAD, new StronglyAdmissibleReasoner());
		for (DungTheory af : randomFrameworks())
			for (Map.Entry<Semantics, AbstractExtensionReasoner> c : cases.entrySet())
				assertEquals(new HashSet<>(c.getValue().getModels(af)),
						new HashSet<>(new SerialisedExtensionReasoner(c.getKey()).getModels(af)), af.toString());
		assertEquals(Set.of(ext("df")), new HashSet<>(new SerialisedExtensionReasoner(Semantics.UC)
				.getModels(af("abcdef", "ab", "ba", "ac", "bc", "ce", "de", "ed", "ef", "fe"))));
		assertThrows(IllegalArgumentException.class, () -> new SerialisedExtensionReasoner(Semantics.SST));
	}

	@Test
	void unchallengedExamples() {
		SerialisationSystem<DungTheory, Extension<DungTheory>> uc = system(SelectionFunction.unattackedOrUnchallenged(),
				TerminationFunction.noUnattackedOrUnchallengedInitialModels());
		// [Thimm 2022, Ex. 9 and 10]
		DungTheory af2 = af("abcdef", "aa", "ab", "ba", "bc", "cd", "de", "ed", "ef", "fe", "ff");
		DungTheory af7 = af("abcdef", "ab", "ba", "ac", "bc", "ce", "de", "ed", "ef", "fe");
		assertEquals(Set.of(ext("b"), ext("be")), uc.getModels(af2));
		assertEquals(Set.of(ext("df")), uc.getModels(af7));
	}

	@Test
	void partition() {
		AfExtensionScheme scheme = new AfExtensionScheme();
		InitialModels<Extension<DungTheory>> im = scheme
				.partition(af("abcdef", "aa", "ab", "ba", "bc", "cd", "de", "ed", "ef", "fe", "ff"));
		assertTrue(im.getUnattacked().isEmpty());
		assertEquals(Set.of(ext("b"), ext("e")), im.getUnchallenged());
		assertTrue(im.getChallenged().isEmpty());

		im = scheme.partition(af("abc", "bc", "cb"));
		assertEquals(Set.of(ext("a")), im.getUnattacked());
		assertTrue(im.getUnchallenged().isEmpty());
		assertEquals(Set.of(ext("b"), ext("c")), im.getChallenged());
	}

	@Test
	void manyIsolatedArguments() {
		DungTheory af = af("abcdefghij");
		SerialisationSystem<DungTheory, Extension<DungTheory>> grounded = system(SelectionFunction.unattacked(),
				TerminationFunction.noUnattackedInitialModels());
		assertEquals(Set.of(ext("abcdefghij")), grounded.getModels(af));
	}

	/**
	 * Asserts that the totalised labeling models equal the labelings of the extension models
	 * @param af                 some argumentation framework
	 * @param labelingBeta       the termination function for labelings
	 * @param extensionBeta      the same termination function for extensions
	 */
	private static void assertLabelingsMatch(DungTheory af, TerminationFunction<DungTheory, Labeling> labelingBeta,
			TerminationFunction<DungTheory, Extension<DungTheory>> extensionBeta) {
		Set<Labeling> expected = new HashSet<>();
		for (Extension<DungTheory> e : system(SelectionFunction.all(), extensionBeta).getModels(af))
			expected.add(new Labeling(af, e));
		Set<Labeling> actual = new HashSet<>();
		for (Labeling l : new SerialisationSystem<>(new AfLabelingScheme(), SelectionFunction.<DungTheory, Labeling>all(),
				labelingBeta).getModels(af)) {
			assertFalse(l.containsValue(ArgumentStatus.UNDECIDED));
			actual.add(AfLabelingScheme.toTotalLabeling(af, l));
		}
		assertEquals(expected, actual, af.toString());
	}

	@Test
	void labelingModelsMatchExtensions() {
		for (DungTheory af : randomFrameworks()) {
			assertLabelingsMatch(af, TerminationFunction.always(), TerminationFunction.always());
			assertLabelingsMatch(af, TerminationFunction.noUnattackedInitialModels(),
					TerminationFunction.noUnattackedInitialModels());
		}
	}

	@Test
	void labelingUnionAndOrder() {
		AfLabelingScheme scheme = new AfLabelingScheme();
		Labeling aIn = new Labeling();
		aIn.put(new Argument("a"), ArgumentStatus.IN);
		Labeling bOut = new Labeling();
		bOut.put(new Argument("b"), ArgumentStatus.OUT);
		Labeling aOut = new Labeling();
		aOut.put(new Argument("a"), ArgumentStatus.OUT);
		Labeling union = scheme.union(aIn, bOut);
		assertEquals(2, union.size());
		assertEquals(union, scheme.union(bOut, aIn));
		assertEquals(aIn, scheme.union(aIn, scheme.empty(new DungTheory())));
		assertTrue(scheme.isLessOrEqual(aIn, union));
		assertFalse(scheme.isLessOrEqual(union, aIn));
		assertFalse(scheme.isLessOrEqual(aOut, union));
		assertThrows(IllegalArgumentException.class, () -> scheme.union(aIn, aOut));
		Labeling aUndec = new Labeling();
		aUndec.put(new Argument("a"), ArgumentStatus.UNDECIDED);
		assertThrows(IllegalArgumentException.class, () -> scheme.union(bOut, aUndec));
	}

	@Test
	void labelingSequencesIgnoreUndecidedArguments() {
		// x attacks itself and remains undecided
		SerialisationSystem<DungTheory, Labeling> admissible = new SerialisationSystem<>(new AfLabelingScheme(),
				SelectionFunction.all(), TerminationFunction.always());
		Set<SerialisationSequence<Labeling>> sequences = admissible.getSequences(af("ab", "ab"));
		assertEquals(sequences, admissible.getSequences(af("abx", "ab", "xx")));
		assertEquals(2, sequences.size());
	}
}

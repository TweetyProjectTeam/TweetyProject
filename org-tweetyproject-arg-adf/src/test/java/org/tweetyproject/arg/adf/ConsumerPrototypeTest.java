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
package org.tweetyproject.arg.adf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.D1;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.v;
import static org.tweetyproject.arg.adf.AdfTestSupport.vs;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.io.KppAdfParser;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleThreeValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.semantics.CharacteristicOperator;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Phase-2 consumer prototype (plan, phase 2): the two current consumers of the
 * ADF module, expressed against the new API, to validate names, conversions,
 * verification and iterator ownership before the API is frozen.
 * <ol>
 * <li>{@link CourseHelpers}: the semantic helpers of {@code ADFTaskGenerator}
 * in aig_course_tasks. Each one becomes a single call; the presentation
 * helpers (LaTeX, TikZ) stay in the course repository.</li>
 * <li>{@link WebAdapter}: the request path of org-tweetyproject-web
 * ({@code AbstractAdfReasonerFactory}, the callees, the timeout handling of
 * {@code RequestController}).</li>
 * </ol>
 * Findings: all helpers map one-to-one; the web answer format is kept by
 * {@link ThreeValuedInterpretation#toString()}; a web timeout cancels the
 * enumeration through thread interruption. The course text's claim that every
 * admissible interpretation is a three-valued model does not hold in general
 * (see {@code SimpleAdfReasonerTest.admissibleIsNotContainedInThreeValuedModels}).
 */
class ConsumerPrototypeTest {

	/** ADFTaskGenerator's semantic helpers on the new API. */
	static final class CourseHelpers {

		static boolean isMoreInformative(ThreeValuedInterpretation i1, ThreeValuedInterpretation i2) {
			return i1.leqInfo(i2);
		}

		static boolean isEquallyInformative(ThreeValuedInterpretation i1, ThreeValuedInterpretation i2) {
			return i1.equals(i2);
		}

		static boolean isIncomparable(ThreeValuedInterpretation i1, ThreeValuedInterpretation i2) {
			return i1.isIncomparable(i2);
		}

		static Collection<ThreeValuedInterpretation> getCompletions(ThreeValuedInterpretation v) {
			Set<ThreeValuedInterpretation> result = new HashSet<>();
			v.completions().forEachRemaining(result::add);
			return result;
		}

		static TruthValue evaluateCondition(ThreeValuedInterpretation v, PlFormula condition) {
			return v.evaluate(condition);
		}

		static boolean isThreeValuedModel(ThreeValuedInterpretation v, AbstractDialecticalFramework adf) {
			return new SimpleThreeValuedModelAdfReasoner().isModel(adf, v);
		}

		/** Γ_D(υ), called {@code evaluate} in the course code */
		static ThreeValuedInterpretation evaluate(ThreeValuedInterpretation v, AbstractDialecticalFramework adf) {
			return CharacteristicOperator.apply(adf, v);
		}

		static ThreeValuedInterpretation consensus(Collection<ThreeValuedInterpretation> vs) {
			return ThreeValuedInterpretation.consensus(vs);
		}

		/** SemanticsModelTask: previously getModels().contains(υ) */
		static boolean isModel(Semantics semantics, AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
			return AbstractAdfReasoner.getSimpleReasonerForSemantics(semantics).isModel(adf, v);
		}
	}

	/** org-tweetyproject-web's ADF request path on the new API. */
	static final class WebAdapter {

		/** arguments are named 1..n, conditions are k++ADF statements such as "ac(1,neg(2))" */
		static AbstractDialecticalFramework getAdf(int numberOfArguments, List<String> conditions) {
			StringBuilder s = new StringBuilder();
			for (int i = 1; i <= numberOfArguments; i++) {
				s.append("s(").append(i).append(").\n");
			}
			for (String condition : conditions) {
				s.append(condition).append(".\n");
			}
			return new KppAdfParser().parse(s.toString());
		}

		/** getModels with a timeout, as RequestController.runCallee does it */
		static String getModels(ExecutorService executor, Semantics semantics, AbstractDialecticalFramework adf, long timeout,
				TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			AbstractAdfReasoner reasoner = AbstractAdfReasoner.getSimpleReasonerForSemantics(semantics);
			try {
				Future<Collection<ThreeValuedInterpretation>> future = executor.submit(() -> reasoner.getModels(adf));
				return future.get(timeout, unit).toString().replace("[", "{").replace("]", "}");
			} finally {
				executor.shutdownNow(); // interrupts a running enumeration, which then closes itself
			}
		}
	}

	@Test
	void courseHelpersReproduceTheCourseExamples() {
		AbstractDialecticalFramework d1 = adf(D1);
		assertTrue(CourseHelpers.isMoreInformative(v(d1, "tuuf"), v(d1, "ttuf")));
		assertTrue(CourseHelpers.isIncomparable(v(d1, "ttuf"), v(d1, "tftf")));
		assertFalse(CourseHelpers.isEquallyInformative(v(d1, "ttuf"), v(d1, "tftf")));
		assertEquals(vs(d1, "tftf", "tfff", "tttf", "ttff"), CourseHelpers.getCompletions(v(d1, "tuuf")));
		assertEquals(TruthValue.UNDECIDED, CourseHelpers.evaluateCondition(v(d1, "tfuu"), d1.getAcceptanceCondition(new Argument("d"))));
		assertEquals(v(d1, "tftu"), CourseHelpers.evaluate(v(d1, "tfuu"), d1));
		assertEquals(v(d1, "tuuf"), CourseHelpers.consensus(List.of(v(d1, "ttuf"), v(d1, "tftf"))));
		assertTrue(CourseHelpers.isThreeValuedModel(v(d1, "uutu"), d1));
		assertTrue(CourseHelpers.isModel(Semantics.CO, d1, v(d1, "uutf")));
		assertFalse(CourseHelpers.isModel(Semantics.PR, d1, v(d1, "uutf")));
	}

	@Test
	void webAdapterKeepsTheAnswerFormat() throws Exception {
		AbstractDialecticalFramework adf = WebAdapter.getAdf(3, List.of("ac(1,neg(2))", "ac(2,neg(1))", "ac(3,c(v))"));
		assertEquals("{{t(2) t(3) f(1)}, {t(1) t(3) f(2)}}", WebAdapter.getModels(Executors.newSingleThreadExecutor(), Semantics.ST, adf, 10, TimeUnit.SECONDS));
		assertEquals("{{t(3) u(1) u(2)}}", WebAdapter.getModels(Executors.newSingleThreadExecutor(), Semantics.GR, adf, 10, TimeUnit.SECONDS));
	}

	@Test
	void webTimeoutCancelsTheEnumeration() throws InterruptedException {
		// 16 self-supporting arguments: 3^16 candidates for the brute-force complete reasoner
		List<String> conditions = new java.util.ArrayList<>();
		for (int i = 1; i <= 16; i++) {
			conditions.add("ac(" + i + "," + i + ")");
		}
		AbstractDialecticalFramework large = WebAdapter.getAdf(16, conditions);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		assertThrows(TimeoutException.class, () -> WebAdapter.getModels(executor, Semantics.CO, large, 200, TimeUnit.MILLISECONDS));
		assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS), "the enumeration did not stop after interruption");
	}

}

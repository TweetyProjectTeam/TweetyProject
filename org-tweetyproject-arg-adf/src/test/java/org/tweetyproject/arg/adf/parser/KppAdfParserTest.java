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
package org.tweetyproject.arg.adf.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tweetyproject.arg.adf.AdfTestSupport.D2;
import static org.tweetyproject.arg.adf.AdfTestSupport.adf;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomAdf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.writer.KppAdfWriter;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

class KppAdfParserTest {

	private final KppAdfParser parser = new KppAdfParser();
	private final KppAdfWriter writer = new KppAdfWriter();

	@Test
	void parsesAllConnectivesAndComments() {
		AbstractDialecticalFramework adf = parser.parse("% a comment\ns(a). s(b).\ns(c). % trailing\n"
				+ "ac(a, and(neg(b), c, c)). ac(b, xor(a)). ac(c, c(v)).");
		Proposition a = new Proposition("a"), b = new Proposition("b"), c = new Proposition("c");
		assertEquals(new Conjunction(List.of(new Negation(b), c, c)), adf.getAcceptanceCondition(new Argument("a")));
		assertEquals(new ExclusiveDisjunction(List.of(a)), adf.getAcceptanceCondition(new Argument("b")));
		assertEquals(new Tautology(), adf.getAcceptanceCondition(new Argument("c")));
	}

	@Test
	void readsTheBundledExample() throws IOException {
		Path example = Path.of("src/main/resources/adf_example.txt");
		AbstractDialecticalFramework adf = parser.parse(example);
		assertEquals(3, adf.size());
	}

	@Test
	void rejectsInvalidInput() {
		assertThrows(ParserException.class, () -> parser.parse("s(a). ac(a, b)."));
		assertThrows(ParserException.class, () -> parser.parse("s(a)."));
		assertThrows(ParserException.class, () -> parser.parse("s(a). ac(a, c(v)). ac(a, c(f))."));
		assertThrows(ParserException.class, () -> parser.parse("s(a). ac(b, c(v))."));
		assertThrows(ParserException.class, () -> parser.parse("s(a). ac(a, imp(a))."));
		assertThrows(ParserException.class, () -> parser.parse("s(a). ac(a, nand(a, a))."));
		assertThrows(ParserException.class, () -> parser.parse("s(a). ac(a, c(x))."));
		assertThrows(ParserException.class, () -> parser.parse("s(a) ac(a, a)."));
		assertThrows(ParserException.class, () -> parser.parse("s(a). ac(a, neg(a)"));
	}

	@Test
	void emptyAdf() {
		assertTrue(parser.parse("").isEmpty());
		assertEquals("", writer.write(new AbstractDialecticalFramework()));
	}

	@Test
	void writesBinaryConnectives() {
		AbstractDialecticalFramework adf = adf("s(a). ac(a, and(a, a, neg(a))).");
		assertEquals("s(a).\nac(a,and(a,and(a,neg(a)))).\n", writer.write(adf));
	}

	@Test
	void fileRoundTrip(@TempDir Path dir) throws IOException {
		Path file = dir.resolve("d2.kpp");
		writer.write(adf(D2), file);
		assertEquals(adf(D2).getNodes(), parser.parse(file).getNodes());
		assertTrue(Files.size(file) > 0);
	}

	@Test
	void parserMethodsOfCommons(@TempDir Path dir) throws IOException {
		Proposition a = new Proposition("a"), b = new Proposition("b");
		assertEquals(new Conjunction(List.of(a, new Negation(b))), parser.parseFormula("and(a, neg(b))"));
		assertThrows(ParserException.class, () -> parser.parseFormula("and(a, b) c"));
		Path file = dir.resolve("d2.kpp");
		writer.write(adf(D2), file);
		assertEquals(adf(D2), parser.parseBeliefBaseFromFile(file.toString()));
	}

	@Test
	void writerRejectsUnrepresentableNames() {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		adf.add(new Argument("a b"));
		assertThrows(IllegalArgumentException.class, () -> writer.write(adf));
	}

	/** round trips preserve Kleene evaluation of every condition under every interpretation */
	@Test
	void roundTripPreservesKleeneEvaluation() {
		Random rng = new Random(42L);
		for (int round = 0; round < 200; round++) {
			AbstractDialecticalFramework adf = randomAdf(rng, 1 + rng.nextInt(4), 4, 4);
			AbstractDialecticalFramework copy = parser.parse(writer.write(adf));
			assertEquals(adf.getNodes(), copy.getNodes());
			assertEquals(writer.write(copy), writer.write(parser.parse(writer.write(copy))));
			for (Iterator<ThreeValuedInterpretation> it = allInterpretations(adf); it.hasNext();) {
				ThreeValuedInterpretation v = it.next();
				for (Argument a : adf) {
					assertEquals(v.evaluate(adf.getAcceptanceCondition(a)), v.evaluate(copy.getAcceptanceCondition(a)));
				}
			}
		}
	}

	/** all 3^n interpretations, as completions over a doubled encoding would be clumsy here */
	private static Iterator<ThreeValuedInterpretation> allInterpretations(AbstractDialecticalFramework adf) {
		List<Argument> args = List.copyOf(adf.getNodes());
		int total = (int) Math.pow(3, args.size());
		return new Iterator<>() {
			int next = 0;

			@Override
			public boolean hasNext() {
				return next < total;
			}

			@Override
			public ThreeValuedInterpretation next() {
				ThreeValuedInterpretation v = ThreeValuedInterpretation.allUndecided(args);
				int code = next++;
				for (Argument a : args) {
					int digit = code % 3;
					code /= 3;
					if (digit > 0) {
						v = v.with(a, digit == 1 ? org.tweetyproject.arg.adf.semantics.TruthValue.TRUE
								: org.tweetyproject.arg.adf.semantics.TruthValue.FALSE);
					}
				}
				return v;
			}
		};
	}

}

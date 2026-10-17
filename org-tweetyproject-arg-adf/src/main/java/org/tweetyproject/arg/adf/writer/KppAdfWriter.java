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
package org.tweetyproject.arg.adf.writer;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.AcceptanceConditions;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * Writes ADFs in the k++ADF / DIAMOND text format read by {@link org.tweetyproject.arg.adf.parser.KppAdfParser}.
 * <p>
 * Conjunctions, disjunctions and exclusive disjunctions are written as nested
 * binary terms, so that the output is also readable by tools that only accept
 * binary connectives; a single operand is written on its own, an empty
 * conjunction as {@code c(v)} and an empty (exclusive) disjunction as
 * {@code c(f)}. All of these rewritings preserve classical and Kleene
 * evaluation.
 *
 * @author Lars Bengel
 */
public class KppAdfWriter {

	/**
	 * @param adf an ADF
	 * @return its k++ADF representation
	 * @throws IllegalArgumentException if an argument name cannot be represented
	 */
	public String write(AbstractDialecticalFramework adf) {
		StringBuilder s = new StringBuilder();
		for (Argument a : adf) {
			s.append("s(").append(name(a.getName())).append(").\n");
		}
		for (Argument a : adf) {
			s.append("ac(").append(name(a.getName())).append(',');
			write(adf.getAcceptanceCondition(a), s);
			s.append(").\n");
		}
		return s.toString();
	}

	/**
	 * @param adf    an ADF
	 * @param writer the target; it is flushed but not closed
	 * @throws IOException if writing fails
	 */
	public void write(AbstractDialecticalFramework adf, Writer writer) throws IOException {
		writer.write(write(adf));
		writer.flush();
	}

	/**
	 * @param adf  an ADF
	 * @param file the target file, written in UTF-8
	 * @throws IOException if writing fails
	 */
	public void write(AbstractDialecticalFramework adf, Path file) throws IOException {
		Files.writeString(file, write(adf), StandardCharsets.UTF_8);
	}

	private static void write(PlFormula f, StringBuilder s) {
		if (f instanceof Proposition) {
			s.append(name(((Proposition) f).getName()));
		} else if (f instanceof Tautology) {
			s.append("c(v)");
		} else if (f instanceof Contradiction) {
			s.append("c(f)");
		} else if (f instanceof Negation) {
			s.append("neg(");
			write(((Negation) f).getFormula(), s);
			s.append(')');
		} else if (f instanceof Conjunction) {
			writeNested("and", "c(v)", AcceptanceConditions.children(f), s);
		} else if (f instanceof Disjunction) {
			writeNested("or", "c(f)", AcceptanceConditions.children(f), s);
		} else if (f instanceof ExclusiveDisjunction) {
			writeNested("xor", "c(f)", AcceptanceConditions.children(f), s);
		} else if (f instanceof Implication || f instanceof Equivalence) {
			List<PlFormula> operands = AcceptanceConditions.children(f);
			s.append(f instanceof Implication ? "imp(" : "iff(");
			write(operands.get(0), s);
			s.append(',');
			write(operands.get(1), s);
			s.append(')');
		} else {
			throw new IllegalArgumentException("Unsupported connective: " + f.getClass().getSimpleName());
		}
	}

	private static void writeNested(String functor, String empty, List<PlFormula> operands, StringBuilder s) {
		if (operands.isEmpty()) {
			s.append(empty);
		} else if (operands.size() == 1) {
			write(operands.get(0), s);
		} else {
			s.append(functor).append('(');
			write(operands.get(0), s);
			s.append(',');
			writeNested(functor, empty, operands.subList(1, operands.size()), s);
			s.append(')');
		}
	}

	private static String name(String name) {
		if (name.isEmpty() || name.chars().anyMatch(c -> Character.isWhitespace(c) || "(),.%".indexOf(c) >= 0)) {
			throw new IllegalArgumentException("The argument name '" + name + "' cannot be written in k++ADF format.");
		}
		return name;
	}

}

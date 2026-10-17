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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.ParserException;
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
 * Parses ADFs in the k++ADF / DIAMOND text format, e.g.
 *
 * <pre>
 * s(a). s(b). s(c).
 * ac(a, neg(b)).
 * ac(b, c(v)).
 * ac(c, and(a, or(b, neg(a)))).
 * </pre>
 *
 * Every statement {@code s(x)} declares an argument and every
 * {@code ac(x, phi)} gives its acceptance condition. Conditions are built from
 * argument names, the constants {@code c(v)} (⊤) and {@code c(f)} (⊥), and the
 * connectives {@code neg/1}, {@code and}, {@code or}, {@code xor} (one or more
 * operands), {@code imp/2} and {@code iff/2}. Text after {@code %} up to the
 * end of a line is a comment. Every argument needs exactly one acceptance
 * condition, and conditions may only mention declared arguments.
 *
 * @author Lars Bengel
 */
public class KppAdfParser {

	/**
	 * @param text an ADF in k++ADF format
	 * @return the ADF
	 * @throws ParserException if the text is not a valid ADF
	 */
	public AbstractDialecticalFramework parse(String text) {
		try {
			return parse(new StringReader(text));
		} catch (IOException e) {
			throw new IllegalStateException(e); // cannot happen for a string
		}
	}

	/**
	 * @param file a file containing an ADF in k++ADF format (UTF-8)
	 * @return the ADF
	 * @throws IOException     if the file cannot be read
	 * @throws ParserException if the file does not contain a valid ADF
	 */
	public AbstractDialecticalFramework parse(Path file) throws IOException {
		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			return parse(reader);
		}
	}

	/**
	 * @param reader a reader providing an ADF in k++ADF format
	 * @return the ADF
	 * @throws IOException     if reading fails
	 * @throws ParserException if the input is not a valid ADF
	 */
	public AbstractDialecticalFramework parse(Reader reader) throws IOException {
		Tokenizer tokens = new Tokenizer(reader);
		Set<String> statements = new LinkedHashSet<>();
		Map<String, Term> conditions = new LinkedHashMap<>();
		while (tokens.peek() != null) {
			Term statement = term(tokens);
			tokens.expect(".");
			if (statement.is("s", 1) && statement.args.get(0).isName()) {
				statements.add(statement.args.get(0).name);
			} else if (statement.is("ac", 2) && statement.args.get(0).isName()) {
				String name = statement.args.get(0).name;
				if (conditions.put(name, statement.args.get(1)) != null) {
					throw new ParserException("Duplicate acceptance condition for " + name + ".");
				}
			} else {
				throw new ParserException("Expected s(x) or ac(x, phi), found " + statement + ".");
			}
		}
		Map<Argument, PlFormula> result = new LinkedHashMap<>();
		for (String name : statements) {
			Term condition = conditions.get(name);
			if (condition == null) {
				throw new ParserException("No acceptance condition for statement " + name + ".");
			}
			result.put(new Argument(name), formula(condition, statements));
		}
		for (String name : conditions.keySet()) {
			if (!statements.contains(name)) {
				throw new ParserException("Acceptance condition for undeclared statement " + name + ".");
			}
		}
		return new AbstractDialecticalFramework(result);
	}

	private static PlFormula formula(Term t, Set<String> statements) {
		if (t.isName()) {
			if (!statements.contains(t.name)) {
				throw new ParserException("Unknown statement " + t.name + " in acceptance condition.");
			}
			return new Proposition(t.name);
		}
		if (t.is("c", 1) && t.args.get(0).isName()) {
			switch (t.args.get(0).name) {
				case "v":
					return new Tautology();
				case "f":
					return new Contradiction();
				default:
					throw new ParserException("Unknown constant " + t + ", expected c(v) or c(f).");
			}
		}
		List<PlFormula> operands = new ArrayList<>();
		for (Term arg : t.args) {
			operands.add(formula(arg, statements));
		}
		switch (t.name) {
			case "neg":
				requireArity(t, 1, 1);
				return new Negation(operands.get(0));
			case "and":
				requireArity(t, 1, Integer.MAX_VALUE);
				return new Conjunction(operands);
			case "or":
				requireArity(t, 1, Integer.MAX_VALUE);
				return new Disjunction(operands);
			case "xor":
				requireArity(t, 1, Integer.MAX_VALUE);
				return new ExclusiveDisjunction(operands);
			case "imp":
				requireArity(t, 2, 2);
				return new Implication(operands.get(0), operands.get(1));
			case "iff":
				requireArity(t, 2, 2);
				return new Equivalence(operands.get(0), operands.get(1));
			default:
				throw new ParserException("Unknown connective " + t.name + ".");
		}
	}

	private static void requireArity(Term t, int min, int max) {
		if (t.args.size() < min || t.args.size() > max) {
			throw new ParserException("Wrong number of operands in " + t + ".");
		}
	}

	private static Term term(Tokenizer tokens) throws IOException {
		String name = tokens.next();
		if (name == null || isPunctuation(name)) {
			throw new ParserException("Expected a name, found " + (name == null ? "end of input" : name) + ".");
		}
		Term term = new Term(name);
		if ("(".equals(tokens.peek())) {
			tokens.next();
			term.args.add(term(tokens));
			while (",".equals(tokens.peek())) {
				tokens.next();
				term.args.add(term(tokens));
			}
			tokens.expect(")");
		}
		return term;
	}

	private static boolean isPunctuation(String token) {
		return token.length() == 1 && "(),.".contains(token);
	}

	/** a parsed term name(args...) or a plain name */
	private static final class Term {
		final String name;
		final List<Term> args = new ArrayList<>();

		Term(String name) {
			this.name = name;
		}

		boolean isName() {
			return args.isEmpty();
		}

		boolean is(String functor, int arity) {
			return name.equals(functor) && args.size() == arity;
		}

		@Override
		public String toString() {
			if (args.isEmpty()) {
				return name;
			}
			StringBuilder s = new StringBuilder(name).append('(');
			for (int i = 0; i < args.size(); i++) {
				s.append(i > 0 ? "," : "").append(args.get(i));
			}
			return s.append(')').toString();
		}
	}

	/** splits the input into names and the punctuation ( ) , . while skipping whitespace and comments */
	private static final class Tokenizer {
		private final Reader reader;
		private int lookahead;
		private String peeked;

		Tokenizer(Reader reader) throws IOException {
			this.reader = reader;
			this.lookahead = reader.read();
		}

		String peek() throws IOException {
			if (peeked == null) {
				peeked = read();
			}
			return peeked;
		}

		String next() throws IOException {
			String token = peek();
			peeked = null;
			return token;
		}

		void expect(String token) throws IOException {
			String found = next();
			if (!token.equals(found)) {
				throw new ParserException("Expected '" + token + "', found " + (found == null ? "end of input" : "'" + found + "'") + ".");
			}
		}

		private String read() throws IOException {
			while (lookahead != -1 && (Character.isWhitespace(lookahead) || lookahead == '%')) {
				if (lookahead == '%') {
					while (lookahead != -1 && lookahead != '\n') {
						lookahead = reader.read();
					}
				} else {
					lookahead = reader.read();
				}
			}
			if (lookahead == -1) {
				return null;
			}
			if ("(),.".indexOf(lookahead) >= 0) {
				String token = String.valueOf((char) lookahead);
				lookahead = reader.read();
				return token;
			}
			StringBuilder name = new StringBuilder();
			while (lookahead != -1 && !Character.isWhitespace(lookahead) && "(),.%".indexOf(lookahead) < 0) {
				name.append((char) lookahead);
				lookahead = reader.read();
			}
			return name.toString();
		}
	}

}

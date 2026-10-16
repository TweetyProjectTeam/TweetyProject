package org.tweetyproject.arg.adf.bench.driver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * A minimal k++ADF representation for the oracle, independent of both ADF
 * implementations: statements {@code s(a).} and {@code ac(a,phi).}, with
 * {@code c(v)}, {@code c(f)}, {@code neg}, n-ary {@code and}/{@code or} and
 * binary {@code imp}, {@code iff}, {@code xor}. Lines starting with {@code %}
 * are comments. Arguments are numbered in the order of their statements.
 */
final class KppAdf {

	/** formula nodes; atoms refer to argument indices */
	sealed interface Formula {}

	record Atom(int index) implements Formula {}

	record Const(boolean value) implements Formula {}

	record Not(Formula child) implements Formula {}

	/** {@code and}, {@code or} (at least one child), {@code imp}, {@code iff}, {@code xor} (two children) */
	record Op(String functor, List<Formula> children) implements Formula {}

	final List<String> names;

	final List<Formula> conditions;

	/** parents[a]: the sorted indices of the atoms occurring in the condition of a */
	final int[][] parents;

	KppAdf(List<String> names, List<Formula> conditions) {
		this.names = List.copyOf(names);
		this.conditions = List.copyOf(conditions);
		this.parents = new int[names.size()][];
		for (int a = 0; a < names.size(); a++) {
			TreeSet<Integer> atoms = new TreeSet<>();
			collectAtoms(conditions.get(a), atoms);
			parents[a] = atoms.stream().mapToInt(Integer::intValue).toArray();
		}
	}

	int size() {
		return names.size();
	}

	private static void collectAtoms(Formula f, TreeSet<Integer> atoms) {
		if (f instanceof Atom atom) {
			atoms.add(atom.index());
		} else if (f instanceof Not not) {
			collectAtoms(not.child(), atoms);
		} else if (f instanceof Op op) {
			op.children().forEach(child -> collectAtoms(child, atoms));
		}
	}

	static KppAdf parse(Path file) throws IOException {
		return parse(Files.readString(file));
	}

	static KppAdf parse(String text) {
		StringBuilder stripped = new StringBuilder();
		for (String line : text.split("\n")) {
			if (!line.strip().startsWith("%")) {
				stripped.append(line).append('\n');
			}
		}
		// read all statements first, since ac may precede the matching s
		List<String> names = new ArrayList<>();
		Map<String, Term> terms = new LinkedHashMap<>();
		for (String statement : stripped.toString().split("\\.")) {
			if (statement.isBlank()) {
				continue;
			}
			Term term = new TermParser(statement).parseStatement();
			if (term.functor().equals("s") && term.args().size() == 1 && term.args().get(0).args().isEmpty()) {
				String name = term.args().get(0).functor();
				if (names.contains(name)) {
					throw new IllegalArgumentException("Duplicate statement s(" + name + ")");
				}
				names.add(name);
			} else if (term.functor().equals("ac") && term.args().size() == 2 && term.args().get(0).args().isEmpty()) {
				String name = term.args().get(0).functor();
				if (terms.put(name, term.args().get(1)) != null) {
					throw new IllegalArgumentException("Duplicate acceptance condition for " + name);
				}
			} else {
				throw new IllegalArgumentException("Unexpected statement " + statement.strip());
			}
		}
		Map<String, Integer> index = new HashMap<>();
		for (int i = 0; i < names.size(); i++) {
			index.put(names.get(i), i);
		}
		for (String name : terms.keySet()) {
			if (!index.containsKey(name)) {
				throw new IllegalArgumentException("Acceptance condition for undeclared argument " + name);
			}
		}
		List<Formula> conditions = new ArrayList<>();
		for (String name : names) {
			Term term = terms.get(name);
			if (term == null) {
				throw new IllegalArgumentException("No acceptance condition for " + name);
			}
			conditions.add(toFormula(term, index));
		}
		return new KppAdf(names, conditions);
	}

	private static Formula toFormula(Term t, Map<String, Integer> index) {
		List<Formula> children = new ArrayList<>();
		for (Term arg : t.args()) {
			if (!t.functor().equals("c")) {
				children.add(toFormula(arg, index));
			}
		}
		int arity = t.args().size();
		switch (t.functor()) {
		case "c":
			if (arity == 1 && t.args().get(0).args().isEmpty() && t.args().get(0).functor().equals("v")) {
				return new Const(true);
			}
			if (arity == 1 && t.args().get(0).args().isEmpty() && t.args().get(0).functor().equals("f")) {
				return new Const(false);
			}
			throw new IllegalArgumentException("Unknown constant " + t);
		case "neg":
			requireArity(t, arity == 1);
			return new Not(children.get(0));
		case "and", "or":
			requireArity(t, arity >= 1);
			return new Op(t.functor(), children);
		case "imp", "iff", "xor":
			requireArity(t, arity == 2);
			return new Op(t.functor(), children);
		default:
			requireArity(t, arity == 0);
			Integer i = index.get(t.functor());
			if (i == null) {
				throw new IllegalArgumentException("Unknown atom " + t.functor());
			}
			return new Atom(i);
		}
	}

	private static void requireArity(Term t, boolean ok) {
		if (!ok) {
			throw new IllegalArgumentException("Wrong number of operands in " + t);
		}
	}

	private record Term(String functor, List<Term> args) {
		@Override
		public String toString() {
			return args.isEmpty() ? functor : functor + args;
		}
	}

	private static final class TermParser {

		private final String s;

		private int pos;

		TermParser(String s) {
			this.s = s;
		}

		Term parseStatement() {
			Term t = term();
			skipSpace();
			if (pos != s.length()) {
				throw new IllegalArgumentException("Unexpected input at '" + s.substring(pos).strip() + "'");
			}
			return t;
		}

		private Term term() {
			skipSpace();
			int start = pos;
			while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_')) {
				pos++;
			}
			if (start == pos) {
				throw new IllegalArgumentException("Name expected at '" + s.substring(pos).strip() + "'");
			}
			String functor = s.substring(start, pos);
			List<Term> args = new ArrayList<>();
			skipSpace();
			if (pos < s.length() && s.charAt(pos) == '(') {
				pos++;
				args.add(term());
				skipSpace();
				while (pos < s.length() && s.charAt(pos) == ',') {
					pos++;
					args.add(term());
					skipSpace();
				}
				if (pos >= s.length() || s.charAt(pos) != ')') {
					throw new IllegalArgumentException("')' expected in " + s.strip());
				}
				pos++;
			}
			return new Term(functor, args);
		}

		private void skipSpace() {
			while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
				pos++;
			}
		}
	}

}

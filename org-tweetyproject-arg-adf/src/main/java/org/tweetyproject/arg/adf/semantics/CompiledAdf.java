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
package org.tweetyproject.arg.adf.semantics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * An immutable, compiled snapshot of an ADF for brute-force reasoning.
 * <p>
 * Arguments are numbered 0..n-1 in the iteration order of the framework, and
 * interpretations are {@code byte[]}s holding {@link #U}, {@link #T} or
 * {@link #F} per argument. The acceptance conditions are compiled into an
 * array-based syntax tree with exactly the connective structure of the original
 * formulas, so Kleene evaluation is preserved.
 * <p>
 * This class is the shared evaluation core of {@link CharacteristicOperator}
 * and the {@code Simple*} reasoners; most users will not need it directly.
 * Methods that take a {@code byte[]} interpretation may modify it temporarily
 * but always restore it before returning; instances are therefore not safe for
 * concurrent use on the same array, but can be shared otherwise.
 *
 * @author Lars Bengel
 */
public final class CompiledAdf {

	/** undecided */
	public static final byte U = 0;
	/** true */
	public static final byte T = 1;
	/** false */
	public static final byte F = 2;

	private static final byte VAR = 0, TOP = 1, BOT = 2, NOT = 3, AND = 4, OR = 5, XOR = 6, IMP = 7, IFF = 8;

	private final String[] names;
	private final Map<String, Integer> index;

	// syntax tree: operator, children and (for VAR) argument index per node
	private final byte[] op;
	private final int[][] kids;
	private final int[] atom;

	private final int[] root;
	private final int[][] parents;

	private CompiledAdf(String[] names, Map<String, Integer> index, List<Byte> ops, List<int[]> kids,
			List<Integer> atoms, int[] root, int[][] parents) {
		this.names = names;
		this.index = index;
		this.op = new byte[ops.size()];
		this.kids = kids.toArray(new int[0][]);
		this.atom = new int[atoms.size()];
		for (int i = 0; i < op.length; i++) {
			op[i] = ops.get(i);
			atom[i] = atoms.get(i);
		}
		this.root = root;
		this.parents = parents;
	}

	/**
	 * Captures and compiles the current state of the given ADF.
	 *
	 * @param adf an ADF
	 * @return its compiled snapshot
	 */
	public static CompiledAdf of(AbstractDialecticalFramework adf) {
		List<Argument> arguments = new ArrayList<>(adf.getNodes());
		int n = arguments.size();
		String[] names = new String[n];
		Map<String, Integer> index = new HashMap<>();
		for (int i = 0; i < n; i++) {
			names[i] = arguments.get(i).getName();
			index.put(names[i], i);
		}
		Builder builder = new Builder(index);
		int[] root = new int[n];
		int[][] parents = new int[n][];
		for (int i = 0; i < n; i++) {
			PlFormula condition = adf.getAcceptanceCondition(arguments.get(i));
			root[i] = builder.compile(condition);
			Set<String> atoms = AcceptanceConditions.atomNames(condition);
			parents[i] = atoms.stream().mapToInt(index::get).toArray();
		}
		return new CompiledAdf(names, index, builder.ops, builder.kids, builder.atoms, root, parents);
	}

	private static final class Builder {
		final Map<String, Integer> index;
		final List<Byte> ops = new ArrayList<>();
		final List<int[]> kids = new ArrayList<>();
		final List<Integer> atoms = new ArrayList<>();

		Builder(Map<String, Integer> index) {
			this.index = index;
		}

		int compile(PlFormula f) {
			byte o;
			int a = -1;
			if (f instanceof Proposition) {
				Proposition p = (Proposition) f;
				o = VAR;
				a = index.get(p.getName());
			} else if (f instanceof Tautology) {
				o = TOP;
			} else if (f instanceof Contradiction) {
				o = BOT;
			} else if (f instanceof Negation) {
				o = NOT;
			} else if (f instanceof Conjunction) {
				o = AND;
			} else if (f instanceof Disjunction) {
				o = OR;
			} else if (f instanceof ExclusiveDisjunction) {
				o = XOR;
			} else if (f instanceof Implication) {
				o = IMP;
			} else if (f instanceof Equivalence) {
				o = IFF;
			} else {
				throw new IllegalArgumentException("Unsupported connective: " + f.getClass().getSimpleName());
			}
			List<PlFormula> children = AcceptanceConditions.children(f);
			int[] c = new int[children.size()];
			for (int i = 0; i < c.length; i++) {
				c[i] = compile(children.get(i));
			}
			ops.add(o);
			kids.add(c);
			atoms.add(a);
			return ops.size() - 1;
		}
	}

	// ------------------------------------------------------------------
	// domain and conversion
	// ------------------------------------------------------------------

	/**
	 * @return the number of arguments
	 */
	public int size() {
		return names.length;
	}

	/**
	 * @param i an argument index
	 * @return the name of the argument
	 */
	public String name(int i) {
		return names[i];
	}

	/**
	 * @param argument an argument of the ADF
	 * @return its index
	 * @throws IllegalArgumentException if the argument is not part of the ADF
	 */
	public int indexOf(Argument argument) {
		Integer i = index.get(argument.getName());
		if (i == null) {
			throw new IllegalArgumentException("Not an argument of this ADF: " + argument);
		}
		return i;
	}

	/**
	 * @param i an argument index
	 * @return the indices of the arguments occurring in its condition
	 */
	public int[] parents(int i) {
		return parents[i].clone();
	}

	/**
	 * @param v an interpretation over exactly the arguments of the ADF
	 * @return its array representation
	 * @throws IllegalArgumentException if the domains differ
	 */
	public byte[] encode(ThreeValuedInterpretation v) {
		if (v.size() != names.length) {
			throw new IllegalArgumentException("The interpretation is not defined over the arguments of the ADF.");
		}
		byte[] result = new byte[names.length];
		for (int i = 0; i < names.length; i++) {
			result[i] = encode(v.get(new Argument(names[i])));
		}
		return result;
	}

	/**
	 * @param v an interpretation in array representation
	 * @return the corresponding interpretation
	 */
	public ThreeValuedInterpretation decode(byte[] v) {
		Map<Argument, TruthValue> values = new LinkedHashMap<>();
		for (int i = 0; i < names.length; i++) {
			values.put(new Argument(names[i]), decode(v[i]));
		}
		return ThreeValuedInterpretation.of(values);
	}

	/**
	 * @param value a truth value
	 * @return its byte code
	 */
	public static byte encode(TruthValue value) {
		return switch (value) {
			case TRUE -> T;
			case FALSE -> F;
			case UNDECIDED -> U;
		};
	}

	/**
	 * @param value a byte code
	 * @return the truth value
	 */
	public static TruthValue decode(byte value) {
		return switch (value) {
			case T -> TruthValue.TRUE;
			case F -> TruthValue.FALSE;
			default -> TruthValue.UNDECIDED;
		};
	}

	// ------------------------------------------------------------------
	// evaluation
	// ------------------------------------------------------------------

	/**
	 * @param a an argument index
	 * @param v an interpretation
	 * @return the Kleene value of phi_a under v; for two-valued v this is the classical value
	 */
	public byte kleene(int a, byte[] v) {
		return eval(root[a], v);
	}

	private byte eval(int node, byte[] v) {
		int[] c = kids[node];
		switch (op[node]) {
			case VAR:
				return v[atom[node]];
			case TOP:
				return T;
			case BOT:
				return F;
			case NOT: {
				byte x = eval(c[0], v);
				return x == T ? F : x == F ? T : U;
			}
			case AND: {
				byte result = T;
				for (int k : c) {
					byte x = eval(k, v);
					if (x == F) {
						return F;
					} else if (x == U) {
						result = U;
					}
				}
				return result;
			}
			case OR: {
				byte result = F;
				for (int k : c) {
					byte x = eval(k, v);
					if (x == T) {
						return T;
					} else if (x == U) {
						result = U;
					}
				}
				return result;
			}
			case XOR: {
				boolean parity = false;
				for (int k : c) {
					byte x = eval(k, v);
					if (x == U) {
						return U;
					}
					parity ^= x == T;
				}
				return parity ? T : F;
			}
			case IMP: {
				byte x = eval(c[0], v);
				if (x == F) {
					return T;
				}
				byte y = eval(c[1], v);
				if (y == T) {
					return T;
				}
				return x == T ? y : U;
			}
			case IFF: {
				byte x = eval(c[0], v);
				if (x == U) {
					return U;
				}
				byte y = eval(c[1], v);
				if (y == U) {
					return U;
				}
				return x == y ? T : F;
			}
			default:
				throw new IllegalStateException();
		}
	}

	/**
	 * Computes Γ(v)(a), the consensus of phi_a over all completions of v. Only the
	 * undecided parents of a are completed, and if the Kleene value of phi_a is
	 * already t or f no completion is enumerated at all.
	 *
	 * @param a an argument index
	 * @param v an interpretation; restored before returning
	 * @return Γ(v)(a)
	 */
	public byte gamma(int a, byte[] v) {
		byte kleene = kleene(a, v);
		if (kleene != U) {
			return kleene;
		}
		boolean seenTrue = false, seenFalse = false;
		int[] open = undecidedParents(a, v);
		long completions = completionCount(open);
		try {
			for (long mask = 0; mask < completions; mask++) {
				assign(open, mask, v);
				if (kleene(a, v) == T) {
					seenTrue = true;
				} else {
					seenFalse = true;
				}
				if (seenTrue && seenFalse) {
					return U;
				}
			}
		} finally {
			for (int p : open) {
				v[p] = U;
			}
		}
		return seenTrue ? T : F;
	}

	/**
	 * Decides whether some completion w of v satisfies w(phi_a) = target.
	 *
	 * @param a      an argument index
	 * @param v      an interpretation; restored before returning
	 * @param target {@link #T} or {@link #F}
	 * @return true if such a completion exists
	 */
	public boolean hasCompletion(int a, byte[] v, byte target) {
		byte kleene = kleene(a, v);
		if (kleene != U) {
			return kleene == target;
		}
		int[] open = undecidedParents(a, v);
		long completions = completionCount(open);
		try {
			for (long mask = 0; mask < completions; mask++) {
				assign(open, mask, v);
				if (kleene(a, v) == target) {
					return true;
				}
			}
		} finally {
			for (int p : open) {
				v[p] = U;
			}
		}
		return false;
	}

	/**
	 * @param v an interpretation; not modified
	 * @return Γ(v)
	 */
	public byte[] gamma(byte[] v) {
		byte[] work = v.clone();
		byte[] result = new byte[v.length];
		for (int a = 0; a < v.length; a++) {
			result[a] = gamma(a, work);
		}
		return result;
	}

	/**
	 * @return the grounded interpretation, i.e. the least fixpoint of Γ, computed
	 *         by iterating Γ from the interpretation that maps everything to u
	 */
	public byte[] grounded() {
		byte[] v = new byte[names.length];
		while (true) {
			byte[] next = gamma(v);
			if (Arrays.equals(v, next)) {
				return v;
			}
			v = next;
		}
	}

	private int[] undecidedParents(int a, byte[] v) {
		int count = 0;
		for (int p : parents[a]) {
			if (v[p] == U) {
				count++;
			}
		}
		int[] open = new int[count];
		int i = 0;
		for (int p : parents[a]) {
			if (v[p] == U) {
				open[i++] = p;
			}
		}
		return open;
	}

	private static long completionCount(int[] open) {
		if (open.length > 62) {
			throw new IllegalArgumentException("Too many undecided parents for brute-force completion: " + open.length);
		}
		return 1L << open.length;
	}

	private static void assign(int[] open, long mask, byte[] v) {
		for (int i = 0; i < open.length; i++) {
			v[open[i]] = ((mask >>> i) & 1L) != 0 ? T : F;
		}
	}

}

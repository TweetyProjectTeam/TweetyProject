package org.tweetyproject.arg.adf.bench.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.tweetyproject.arg.adf.bench.common.Answers;
import org.tweetyproject.arg.adf.bench.driver.KppAdf.Atom;
import org.tweetyproject.arg.adf.bench.driver.KppAdf.Const;
import org.tweetyproject.arg.adf.bench.driver.KppAdf.Formula;
import org.tweetyproject.arg.adf.bench.driver.KppAdf.Not;
import org.tweetyproject.arg.adf.bench.driver.KppAdf.Op;

/**
 * Independent brute-force oracle for small ADFs (plan, section 4.2), written
 * directly from the definitions in ARCHITECTURE.md, "Semantics covered":
 * <ul>
 * <li>{@code MOD}: v two-valued and v(a) = v(phi_a);</li>
 * <li>{@code 3MOD}: v(a) = u or v(a) = v(phi_a) under Kleene evaluation;</li>
 * <li>{@code CF} (k++ADF): v(a) = t implies some completion of v satisfies
 * phi_a, v(a) = f implies some completion falsifies phi_a;</li>
 * <li>{@code NA}: ≤ᵢ-maximal CF;</li>
 * <li>{@code ADM}: v ≤ᵢ Γ(v); {@code CO}: v = Γ(v); {@code GR}: least fixpoint
 * of Γ, iterated from the all-undecided interpretation;</li>
 * <li>{@code PR}: ≤ᵢ-maximal ADM;</li>
 * <li>{@code ST}: two-valued model v such that the grounded interpretation of
 * the reduct D^v (Brewka et al. 2013, Def. 6: the arguments true in v, with
 * the arguments false in v replaced by ⊥) makes exactly those arguments
 * true.</li>
 * </ul>
 * Γ(v)(a) is the consensus of phi_a over all two-valued completions of v,
 * enumerated over the undecided parents of a. No optimisation (e.g. the Kleene
 * fast path) is applied, so the oracle shares no shortcut with either
 * implementation. All 3^n interpretations are checked, so it is meant for
 * n ≤ 10.
 */
final class Oracle {

	static final byte F = 0, T = 1, U = 2;

	static final List<String> SEMANTICS = List.of("MOD", "3MOD", "CF", "NA", "ADM", "CO", "GR", "PR", "ST");

	private final KppAdf adf;

	private final int n;

	Oracle(KppAdf adf) {
		this.adf = adf;
		this.n = adf.size();
	}

	/** @return the canonical models of the semantics, sorted */
	List<String> models(String semantics) {
		List<byte[]> models = switch (semantics) {
			case "MOD" -> filter(true, this::isTwoValuedModel);
			case "3MOD" -> filter(false, this::isThreeValuedModel);
			case "CF" -> filter(false, this::isConflictFree);
			case "NA" -> maximal(filter(false, this::isConflictFree));
			case "ADM" -> filter(false, this::isAdmissible);
			case "CO" -> filter(false, this::isComplete);
			case "GR" -> List.of(grounded());
			case "PR" -> maximal(filter(false, this::isAdmissible));
			case "ST" -> filter(true, this::isStable);
			default -> throw new IllegalArgumentException("Unknown semantics " + semantics);
		};
		List<String> canonical = new ArrayList<>();
		for (byte[] v : models) {
			canonical.add(canonical(v));
		}
		canonical.sort(null);
		return canonical;
	}

	String canonical(byte[] v) {
		List<String> t = new ArrayList<>(), f = new ArrayList<>();
		for (int a = 0; a < n; a++) {
			if (v[a] == T) {
				t.add(adf.names.get(a));
			} else if (v[a] == F) {
				f.add(adf.names.get(a));
			}
		}
		return Answers.canonical(t, f);
	}

	// ------------------------------------------------------------------
	// definitions
	// ------------------------------------------------------------------

	boolean isTwoValuedModel(byte[] v) {
		for (int a = 0; a < n; a++) {
			if (v[a] == U || kleene(adf.conditions.get(a), v) != v[a]) {
				return false;
			}
		}
		return true;
	}

	boolean isThreeValuedModel(byte[] v) {
		for (int a = 0; a < n; a++) {
			if (v[a] != U && kleene(adf.conditions.get(a), v) != v[a]) {
				return false;
			}
		}
		return true;
	}

	boolean isConflictFree(byte[] v) {
		for (int a = 0; a < n; a++) {
			if (v[a] == T && !canBe(a, v, true) || v[a] == F && !canBe(a, v, false)) {
				return false;
			}
		}
		return true;
	}

	boolean isAdmissible(byte[] v) {
		for (int a = 0; a < n; a++) {
			if (v[a] != U && gamma(a, v) != v[a]) {
				return false;
			}
		}
		return true;
	}

	boolean isComplete(byte[] v) {
		for (int a = 0; a < n; a++) {
			if (gamma(a, v) != v[a]) {
				return false;
			}
		}
		return true;
	}

	byte[] grounded() {
		byte[] v = new byte[n];
		Arrays.fill(v, U);
		while (true) {
			byte[] next = new byte[n];
			for (int a = 0; a < n; a++) {
				next[a] = gamma(a, v);
			}
			if (Arrays.equals(next, v)) {
				return v;
			}
			v = next;
		}
	}

	boolean isStable(byte[] v) {
		if (!isTwoValuedModel(v)) {
			return false;
		}
		// grounded interpretation of the reduct D^v: the arguments false in v stay
		// fixed at f (they are ⊥ in the reduct's conditions and not part of it),
		// the others start undecided and are updated with Γ of the reduct
		byte[] w = new byte[n];
		for (int a = 0; a < n; a++) {
			w[a] = v[a] == F ? F : U;
		}
		while (true) {
			byte[] next = w.clone();
			for (int a = 0; a < n; a++) {
				if (v[a] == T) {
					next[a] = gamma(a, w);
				}
			}
			if (Arrays.equals(next, w)) {
				break;
			}
			w = next;
		}
		for (int a = 0; a < n; a++) {
			if (v[a] == T && w[a] != T) {
				return false;
			}
		}
		return true;
	}

	/** v ≤ᵢ w: every argument decided in v has the same value in w */
	static boolean atMostAsInformative(byte[] v, byte[] w) {
		for (int a = 0; a < v.length; a++) {
			if (v[a] != U && v[a] != w[a]) {
				return false;
			}
		}
		return true;
	}

	// ------------------------------------------------------------------
	// Γ and evaluation
	// ------------------------------------------------------------------

	/** Γ(v)(a): t if every completion of v satisfies phi_a, f if none does, u otherwise */
	byte gamma(int a, byte[] v) {
		boolean canTrue = canBe(a, v, true), canFalse = canBe(a, v, false);
		return canTrue && canFalse ? U : canTrue ? T : F;
	}

	/** @return whether some two-valued completion of v gives phi_a the value */
	boolean canBe(int a, byte[] v, boolean value) {
		int[] parents = adf.parents[a];
		List<Integer> undecided = new ArrayList<>();
		for (int p : parents) {
			if (v[p] == U) {
				undecided.add(p);
			}
		}
		byte[] w = v.clone();
		for (long mask = 0; mask < 1L << undecided.size(); mask++) {
			for (int k = 0; k < undecided.size(); k++) {
				w[undecided.get(k)] = (mask >> k & 1) == 1 ? T : F;
			}
			if ((kleene(adf.conditions.get(a), w) == T) == value) {
				return true;
			}
		}
		return false;
	}

	/** strong Kleene evaluation; on two-valued inputs this is classical evaluation */
	static byte kleene(Formula f, byte[] v) {
		if (f instanceof Atom atom) {
			return v[atom.index()];
		}
		if (f instanceof Const c) {
			return c.value() ? T : F;
		}
		if (f instanceof Not not) {
			return not(kleene(not.child(), v));
		}
		Op op = (Op) f;
		List<Formula> children = op.children();
		switch (op.functor()) {
		case "and": {
			byte result = T;
			for (Formula child : children) {
				result = and(result, kleene(child, v));
			}
			return result;
		}
		case "or": {
			byte result = F;
			for (Formula child : children) {
				result = not(and(not(result), not(kleene(child, v))));
			}
			return result;
		}
		default: {
			byte l = kleene(children.get(0), v), r = kleene(children.get(1), v);
			switch (op.functor()) {
			case "imp":
				return not(and(l, not(r)));
			case "iff":
				return l == U || r == U ? U : l == r ? T : F;
			case "xor":
				return l == U || r == U ? U : l != r ? T : F;
			default:
				throw new IllegalStateException(op.functor());
			}
		}
		}
	}

	private static byte not(byte x) {
		return x == U ? U : x == T ? F : T;
	}

	private static byte and(byte x, byte y) {
		if (x == F || y == F) {
			return F;
		}
		return x == T && y == T ? T : U;
	}

	// ------------------------------------------------------------------
	// enumeration
	// ------------------------------------------------------------------

	private interface Check {
		boolean test(byte[] v);
	}

	/** all two-valued (or all three-valued) interpretations satisfying the check */
	private List<byte[]> filter(boolean twoValued, Check check) {
		List<byte[]> result = new ArrayList<>();
		int base = twoValued ? 2 : 3;
		long total = 1;
		for (int i = 0; i < n; i++) {
			total *= base;
		}
		byte[] v = new byte[n];
		for (long code = 0; code < total; code++) {
			long x = code;
			for (int a = 0; a < n; a++) {
				// digit 0 = f, 1 = t, 2 = u
				v[a] = (byte) (x % base);
				x /= base;
			}
			if (check.test(v)) {
				result.add(v.clone());
			}
		}
		return result;
	}

	/**
	 * The ≤ᵢ-maximal elements. Candidates are processed by decreasing number of
	 * decided arguments; a strictly more informative candidate has more decided
	 * arguments, and above it lies a maximal one that was processed earlier, so
	 * comparing with the maximal elements found so far suffices.
	 */
	private List<byte[]> maximal(List<byte[]> candidates) {
		List<byte[]> sorted = new ArrayList<>(candidates);
		sorted.sort(Comparator.comparingInt(Oracle::decided).reversed());
		List<byte[]> maximal = new ArrayList<>();
		for (byte[] v : sorted) {
			boolean dominated = false;
			for (byte[] m : maximal) {
				if (decided(m) > decided(v) && atMostAsInformative(v, m)) {
					dominated = true;
					break;
				}
			}
			if (!dominated) {
				maximal.add(v);
			}
		}
		return maximal;
	}

	private static int decided(byte[] v) {
		int count = 0;
		for (byte x : v) {
			if (x != U) {
				count++;
			}
		}
		return count;
	}

}

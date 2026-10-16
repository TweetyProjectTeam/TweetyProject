package org.tweetyproject.arg.adf.bench.driver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

class OracleTest {

	private static List<String> models(String kpp, String semantics) {
		return new Oracle(KppAdf.parse(kpp)).models(semantics);
	}

	// ------------------------------------------------------------------
	// semantic counterexamples from the architecture review
	// ------------------------------------------------------------------

	@Test
	void admissibleIsNotThreeValuedModel() {
		// phi_a = a, phi_b = a | -a: v = (u, t) is complete, but phi_b is u under Kleene evaluation
		String kpp = "s(a). s(b). ac(a,a). ac(b,or(a,neg(a))).";
		assertTrue(models(kpp, "CO").contains("t:{b} f:{}"));
		assertTrue(models(kpp, "ADM").contains("t:{b} f:{}"));
		assertFalse(models(kpp, "3MOD").contains("t:{b} f:{}"));
	}

	@Test
	void noncanonicalAdmissibleAfInterpretation() {
		// D_F of a -> b: (t, u) is admissible, but no canonical v_S of a Dung extension
		String kpp = "s(a). s(b). ac(a,c(v)). ac(b,neg(a)).";
		assertEquals(List.of("t:{a} f:{b}", "t:{a} f:{}", "t:{} f:{}"), models(kpp, "ADM"));
		assertEquals(List.of("t:{a} f:{b}"), models(kpp, "CO"));
	}

	// ------------------------------------------------------------------
	// examples checked with a second, separately written brute force
	// ------------------------------------------------------------------

	@Test
	void mutualAttack() {
		String kpp = "s(a). s(b). ac(a,neg(b)). ac(b,neg(a)).";
		assertEquals(List.of("t:{a} f:{b}", "t:{b} f:{a}", "t:{} f:{}"), models(kpp, "CO"));
		assertEquals(List.of("t:{} f:{}"), models(kpp, "GR"));
		assertEquals(List.of("t:{a} f:{b}", "t:{b} f:{a}"), models(kpp, "PR"));
		assertEquals(List.of("t:{a} f:{b}", "t:{b} f:{a}"), models(kpp, "ST"));
		assertEquals(List.of("t:{a} f:{b}", "t:{b} f:{a}"), models(kpp, "MOD"));
	}

	@Test
	void completeModelsOfGeneralInstance() {
		// bench/instances/co-scaling/general-n04-i3.kpp, on which the unfixed baseline crashes
		String kpp = "s(a0). s(a1). s(a2). s(a3). ac(a0,or(and(a2,a1),a1)). ac(a1,iff(neg(a3),or(a1,a3))). "
				+ "ac(a2,iff(xor(a0,a3),and(a2,a3))). ac(a3,a3).";
		assertEquals(Set.of("t:{} f:{}", "t:{} f:{a3}", "t:{} f:{a0,a1}", "t:{a2} f:{a0,a1}", "t:{a3} f:{a0,a1}",
				"t:{a2} f:{a0,a1,a3}", "t:{a3} f:{a0,a1,a2}", "t:{a0,a1} f:{a2,a3}", "t:{a2,a3} f:{a0,a1}"),
				Set.copyOf(models(kpp, "CO")));
	}

	@Test
	void stableReductKeepsEquivalence() {
		String kpp = "s(a). s(b). s(c). s(z). ac(a,c(v)). ac(b,c(v)). ac(z,c(f)). ac(c,iff(neg(a),neg(b))).";
		assertEquals(List.of("t:{a,b,c} f:{z}"), models(kpp, "ST"));
	}

	@Test
	void stableWithoutFalseArguments() {
		assertEquals(List.of("t:{a,b} f:{}"), models("s(a). s(b). ac(a,c(v)). ac(b,a).", "ST"));
	}

	@Test
	void stableRejectsUnsupportedCycle() {
		String kpp = "s(a). s(b). ac(a,b). ac(b,a).";
		assertEquals(List.of("t:{a,b} f:{}", "t:{} f:{a,b}"), models(kpp, "MOD"));
		assertEquals(List.of("t:{} f:{a,b}"), models(kpp, "ST"));
	}

	@Test
	void stableAllFalse() {
		assertEquals(List.of("t:{} f:{a,b}"), models("s(a). s(b). ac(a,c(f)). ac(b,and(a,b)).", "ST"));
	}

	@Test
	void conflictFreeIsSatisfiableRefutable() {
		// k++ADF: a = f needs a falsifying completion of a & b; b = t needs a satisfying one of -b | a
		String kpp = "s(a). s(b). ac(a,and(a,b)). ac(b,or(neg(b),a)).";
		Oracle oracle = new Oracle(KppAdf.parse(kpp));
		assertTrue(oracle.isConflictFree(new byte[] { Oracle.F, Oracle.U }));
		assertTrue(oracle.isConflictFree(new byte[] { Oracle.U, Oracle.T }));
		// a = t, b = f: phi_a = a & b is false under every completion
		assertFalse(oracle.isConflictFree(new byte[] { Oracle.T, Oracle.F }));
		assertEquals(List.of("t:{a,b} f:{}", "t:{} f:{a}"), models(kpp, "NA"));
	}

	@Test
	void emptyAdf() {
		for (String semantics : Oracle.SEMANTICS) {
			assertEquals(List.of("t:{} f:{}"), models("", semantics), semantics);
		}
	}

	// ------------------------------------------------------------------
	// evaluation and parsing
	// ------------------------------------------------------------------

	@Test
	void kleeneTruthTables() {
		byte f = Oracle.F, t = Oracle.T, u = Oracle.U;
		byte[] values = { f, t, u };
		// rows: left f, t, u; columns: right f, t, u
		Map<String, byte[][]> tables = Map.of(
				"and", new byte[][] { { f, f, f }, { f, t, u }, { f, u, u } },
				"or", new byte[][] { { f, t, u }, { t, t, t }, { u, t, u } },
				"imp", new byte[][] { { t, t, t }, { f, t, u }, { u, t, u } },
				"iff", new byte[][] { { t, f, u }, { f, t, u }, { u, u, u } },
				"xor", new byte[][] { { f, t, u }, { t, f, u }, { u, u, u } });
		for (Map.Entry<String, byte[][]> table : tables.entrySet()) {
			KppAdf adf = KppAdf.parse("s(x). s(y). ac(x," + table.getKey() + "(x,y)). ac(y,y).");
			for (int l = 0; l < 3; l++) {
				for (int r = 0; r < 3; r++) {
					assertEquals(table.getValue()[l][r], Oracle.kleene(adf.conditions.get(0), new byte[] { values[l], values[r] }),
							table.getKey() + "(" + l + "," + r + ")");
				}
			}
		}
		KppAdf neg = KppAdf.parse("s(x). ac(x,neg(x)).");
		assertEquals(t, Oracle.kleene(neg.conditions.get(0), new byte[] { f }));
		assertEquals(f, Oracle.kleene(neg.conditions.get(0), new byte[] { t }));
		assertEquals(u, Oracle.kleene(neg.conditions.get(0), new byte[] { u }));
	}

	@Test
	void parserAcceptsNaryConnectivesAndComments() {
		KppAdf adf = KppAdf.parse("% comment\nac(c, and(a, b, neg(a))).\ns(a). s(b). s(c).\nac(a,c(v)). ac(b, or(a)).");
		assertEquals(List.of("a", "b", "c"), adf.names);
		assertEquals(List.of("t:{a,b} f:{c}"), new Oracle(adf).models("GR"));
	}

	@Test
	void parserRejectsMalformedInput() {
		assertThrows(IllegalArgumentException.class, () -> KppAdf.parse("s(a). ac(a,b)."));
		assertThrows(IllegalArgumentException.class, () -> KppAdf.parse("s(a)."));
		assertThrows(IllegalArgumentException.class, () -> KppAdf.parse("s(a). ac(a,c(v)). ac(a,c(f))."));
		assertThrows(IllegalArgumentException.class, () -> KppAdf.parse("s(a). ac(a,imp(a))."));
		assertThrows(IllegalArgumentException.class, () -> KppAdf.parse("s(a). ac(a,c(x))."));
		assertThrows(IllegalArgumentException.class, () -> KppAdf.parse("s(a). ac(a,neg(a)"));
	}

	// ------------------------------------------------------------------
	// properties on random instances
	// ------------------------------------------------------------------

	@Test
	void propertiesOnRandomInstances() {
		Random rng = new Random(2026);
		for (int trial = 0; trial < 300; trial++) {
			String family = InstanceGenerator.FAMILIES.get(trial % InstanceGenerator.FAMILIES.size());
			int n = 1 + rng.nextInt(5);
			String kpp = InstanceGenerator.generate(family, n, rng);
			Oracle oracle = new Oracle(KppAdf.parse(kpp));
			Map<String, Set<String>> m = new java.util.HashMap<>();
			for (String semantics : Oracle.SEMANTICS) {
				m.put(semantics, new HashSet<>(oracle.models(semantics)));
			}
			String msg = kpp;
			assertTrue(m.get("CO").containsAll(m.get("MOD")) && m.get("PR").containsAll(m.get("MOD")), "MOD ⊆ PR ⊆ CO: " + msg);
			assertTrue(m.get("CO").containsAll(m.get("PR")), "PR ⊆ CO: " + msg);
			assertTrue(m.get("ADM").containsAll(m.get("CO")), "CO ⊆ ADM: " + msg);
			assertTrue(m.get("CF").containsAll(m.get("ADM")), "ADM ⊆ CF: " + msg);
			assertTrue(m.get("MOD").containsAll(m.get("ST")), "ST ⊆ MOD: " + msg);
			assertTrue(m.get("ADM").containsAll(m.get("3MOD")), "3MOD ⊆ ADM: " + msg);
			assertTrue(m.get("CF").containsAll(m.get("NA")), "NA ⊆ CF: " + msg);
			assertFalse(m.get("PR").isEmpty(), "PR ≠ ∅: " + msg);
			assertEquals(1, m.get("GR").size(), msg);
			assertTrue(m.get("CO").containsAll(m.get("GR")), "GR ⊆ CO: " + msg);

			List<byte[]> complete = interpretations(oracle, n, oracle::isComplete);
			byte[] grounded = oracle.grounded();
			for (byte[] v : complete) {
				assertTrue(Oracle.atMostAsInformative(grounded, v), "GR ≤ᵢ every CO: " + msg);
			}
			assertEquals(m.get("PR"), maximalCanonical(oracle, complete), "PR = maximal CO: " + msg);
		}
	}

	private interface Check {
		boolean test(byte[] v);
	}

	private static List<byte[]> interpretations(Oracle oracle, int n, Check check) {
		List<byte[]> result = new ArrayList<>();
		int total = (int) Math.pow(3, n);
		for (int code = 0; code < total; code++) {
			byte[] v = new byte[n];
			int x = code;
			for (int a = 0; a < n; a++) {
				v[a] = (byte) (x % 3);
				x /= 3;
			}
			if (check.test(v)) {
				result.add(v);
			}
		}
		return result;
	}

	/** maximal elements by pairwise comparison, independent of the oracle's sorted procedure */
	private static Set<String> maximalCanonical(Oracle oracle, List<byte[]> candidates) {
		Set<String> result = new HashSet<>();
		for (byte[] v : candidates) {
			boolean maximal = true;
			for (byte[] w : candidates) {
				if (!java.util.Arrays.equals(v, w) && Oracle.atMostAsInformative(v, w)) {
					maximal = false;
				}
			}
			if (maximal) {
				result.add(oracle.canonical(v));
			}
		}
		return result;
	}

}

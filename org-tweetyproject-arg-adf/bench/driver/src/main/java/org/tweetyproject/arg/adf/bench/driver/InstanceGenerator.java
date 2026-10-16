package org.tweetyproject.arg.adf.bench.driver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates benchmark instances in k++ADF format, independently of both ADF
 * implementations. All connectives are written as binary terms so that the old
 * parser can read every instance.
 * <ul>
 * <li>{@code af}: ADFs D_F of random AFs; every argument has 0-3 random
 * attackers (no self-attacks), phi_a = ∧ ¬b.</li>
 * <li>{@code bipolar}: every argument has 1-3 distinct parents (possibly
 * itself), each occurring exactly once as a supporting (b) or attacking (¬b)
 * literal in a random and/or tree; bipolar by construction.</li>
 * <li>{@code general}: every argument has 1-3 distinct parents and a random
 * formula of depth 2-3 over them with ∧, ∨, ¬, →, ↔ and ⊕; atoms may repeat,
 * but the two operands of a connective are never syntactically equal, because
 * the old parser rejects that. Typically not bipolar.</li>
 * </ul>
 * The seed of every instance is derived from the master seed, the family, the
 * size and the index, and is recorded in the manifest.
 */
public final class InstanceGenerator {

	public static final List<String> FAMILIES = List.of("af", "bipolar", "general");

	private InstanceGenerator() {}

	/** @return the manifest lines (file, family, n, index, seed) */
	public static List<String> generate(Path dir, long masterSeed, List<Integer> sizes, int perSize) throws IOException {
		Files.createDirectories(dir);
		List<String> manifest = new ArrayList<>();
		manifest.add("file\tfamily\tn\tindex\tseed");
		for (String family : FAMILIES) {
			for (int n : sizes) {
				for (int i = 0; i < perSize; i++) {
					long seed = masterSeed * 1_000_003L + family.hashCode() * 10_007L + n * 101L + i;
					String name = String.format("%s-n%02d-i%d.kpp", family, n, i);
					Files.writeString(dir.resolve(name), generate(family, n, new Random(seed)));
					manifest.add(name + "\t" + family + "\t" + n + "\t" + i + "\t" + seed);
				}
			}
		}
		Files.write(dir.resolve("manifest.tsv"), manifest);
		return manifest;
	}

	static String generate(String family, int n, Random rng) {
		StringBuilder s = new StringBuilder();
		for (int a = 0; a < n; a++) {
			s.append("s(a").append(a).append(").\n");
		}
		for (int a = 0; a < n; a++) {
			String condition = switch (family) {
				case "af" -> afCondition(a, n, rng);
				case "bipolar" -> bipolarCondition(n, rng);
				case "general" -> generalCondition(n, rng);
				default -> throw new IllegalArgumentException(family);
			};
			s.append("ac(a").append(a).append(',').append(condition).append(").\n");
		}
		return s.toString();
	}

	private static String afCondition(int a, int n, Random rng) {
		List<Integer> others = new ArrayList<>();
		for (int b = 0; b < n; b++) {
			if (b != a) {
				others.add(b);
			}
		}
		Collections.shuffle(others, rng);
		int k = Math.min(others.size(), rng.nextInt(4));
		if (k == 0) {
			return "c(v)";
		}
		List<String> literals = new ArrayList<>();
		for (int i = 0; i < k; i++) {
			literals.add("neg(a" + others.get(i) + ")");
		}
		return nest("and", literals);
	}

	private static String bipolarCondition(int n, Random rng) {
		List<String> literals = new ArrayList<>();
		for (int p : parents(n, rng)) {
			literals.add(rng.nextBoolean() ? "a" + p : "neg(a" + p + ")");
		}
		return randomTree(literals, rng);
	}

	/** joins the leaves with a random binary and/or tree */
	private static String randomTree(List<String> leaves, Random rng) {
		if (leaves.size() == 1) {
			return leaves.get(0);
		}
		int split = 1 + rng.nextInt(leaves.size() - 1);
		return (rng.nextBoolean() ? "and(" : "or(") + randomTree(leaves.subList(0, split), rng) + ","
				+ randomTree(leaves.subList(split, leaves.size()), rng) + ")";
	}

	private static String generalCondition(int n, Random rng) {
		List<Integer> parents = parents(n, rng);
		return formula(parents, 2 + rng.nextInt(2), rng);
	}

	private static String formula(List<Integer> atoms, int depth, Random rng) {
		if (depth == 0 || rng.nextInt(4) == 0) {
			return "a" + atoms.get(rng.nextInt(atoms.size()));
		}
		String[] binary = { "and", "or", "imp", "iff", "xor" };
		if (rng.nextInt(6) == 0) {
			return "neg(" + formula(atoms, depth - 1, rng) + ")";
		}
		// the baseline rejects syntactically equal operands of a binary connective, e.g. or(a1,a1)
		String left = formula(atoms, depth - 1, rng), right = formula(atoms, depth - 1, rng);
		for (int attempt = 0; right.equals(left) && attempt < 20; attempt++) {
			right = formula(atoms, depth - 1, rng);
		}
		if (right.equals(left)) {
			return left;
		}
		return binary[rng.nextInt(binary.length)] + "(" + left + "," + right + ")";
	}

	private static List<Integer> parents(int n, Random rng) {
		List<Integer> all = new ArrayList<>();
		for (int b = 0; b < n; b++) {
			all.add(b);
		}
		Collections.shuffle(all, rng);
		return new ArrayList<>(all.subList(0, Math.min(n, 1 + rng.nextInt(3))));
	}

	private static String nest(String functor, List<String> operands) {
		if (operands.size() == 1) {
			return operands.get(0);
		}
		return functor + "(" + operands.get(0) + "," + nest(functor, operands.subList(1, operands.size())) + ")";
	}

}
